# -*- coding: utf-8 -*-
# Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U
#
# This file is part of FI-WARE project.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
#
# You may obtain a copy of the License at:
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#
# See the License for the specific language governing permissions and
# limitations under the License.
#
# For those usages not covered by the Apache version 2.0 License please
# contact with opensource@tid.es

import urllib
import time

__author__ = 'henar'
import httplib
from xml.dom.minidom import parseString
from urlparse import urlparse
import sys
import json
import mimetypes

status_codes = {"OK": 200,
                "No Content": 204,
                "Bad Request": 400,
                "Unauthorized": 401,
                "Forbidden": 403,
                "Not Found": 404,
                "Conflict": 409,
                "Not acceptable": 406,
                "Unsupported media type": 415,
                "Internal Server Error": 500}

MAX_CHECK_TIMES_FOR_TASK_EXECUTION = 200


def post_multipart(host, port, selector, fields, files):
    content_type, body = encode_multipart_formdata(fields, files)
    h = httplib.HTTP(host, port)
    h.putrequest('POST', selector)
    h.putheader('content-type', content_type)
    h.putheader('content-length', str(len(body)))
    h.endheaders()
    h.send(body)
    errcode, errmsg, headers = h.getreply()
    print errcode
    return h.file.read()


def encode_multipart_formdata(fields, files):
    LIMIT = '100'
    dd = '\r\n'
    L = []
    for (key, value) in fields:
        L.append('--' + LIMIT)
        L.append('Content-Disposition: form-data; name="%s"' % key)
        L.append('')
        L.append(value)
    print files
    for (filename, value) in files:
        L.append('--' + LIMIT)
        L.append('Content-Disposition: form-data; name="%s"; filename="%s"' % (filename, filename))
        L.append('Content-Type: %s' % get_content_type(filename))
        L.append('')
        L.append(value)
    L.append('--' + LIMIT + '--')
    L.append('')
    print L
    body = dd.join(L)
    content_type = 'multipart/form-data; boundary=%s' % LIMIT
    return content_type, body


def get_content_type(filename):
    return mimetypes.guess_type(filename)[0] or 'application/octet-stream'


def __do_http_req(method, url, headers, payload):
    #print "##### REQUEST #####"
    #print method, url
    #print "Headers: ", headers
    #print payload, "\n"

    parsed_url = urlparse(url)
    final_uri = parsed_url.path
    if parsed_url.query is not None and len(parsed_url.query) != 0:
        final_uri += "?" + parsed_url.query

    if 'https' in parsed_url[0]:
        con = httplib.HTTPSConnection(parsed_url.netloc)
    else:
        con = httplib.HTTPConnection(parsed_url.netloc)
    con.request(method, final_uri, payload, headers)
    return con.getresponse()

    ##
    ## Metod que hace el HTTP-GET
    ##


def get(url, headers):
    return __do_http_req("GET", url, headers, None)


def delete(url, headers):
    return __do_http_req("DELETE", url, headers, None)


    ##
    ## Metod que hace el HTTP-PUT
    ##


def put(url, headers, payload=None):
    return __do_http_req("PUT", url, headers, payload)


    ##
    ## Metod que hace el HTTP-POST
    ##


def post(url, headers, payload=None):
    return __do_http_req("POST", url, headers, payload)


def get_token(keystone_url, tenant, user, password):
    # url="%s/%s" %(keystone_url,"v2.0/tokens")
    #print keystone_url
    headers = {'Content-Type': 'application/json',
               'Accept': "application/xml"}
    payload = {"auth": {"tenantName": tenant, "passwordCredentials": {"username": user, "password": password}}}

    #print json.dumps(payload)
    response = post(keystone_url, headers, json.dumps(payload))
    data = response.read()

    ## Si la respuesta es la adecuada, creo el diccionario de los datos en JSON.
    if response.status != 200:
        print 'error to obtain the token ' + str(response.status)
        sys.exit(1)
    else:
        dom = parseString(data)
        try:
            result = (dom.getElementsByTagName('token'))[0]
            var = result.attributes["id"].value

            return var
        except:
            print ("Error in the processing environment")
            sys.exit(1)


def wait_for_task(task_data, headers):
    try:
        href = task_data["href"]
        status = task_data["status"]
    except:
        assert False, "No task information received: %s" % (task_data)

    i = 0
    while status == 'RUNNING' and i < MAX_CHECK_TIMES_FOR_TASK_EXECUTION:
        time.sleep(5)
        task_data = json.loads(get_task_data(href, headers))
        status = task_data["status"]
        print "WAITING for task execution ({}). STATUS: {}".format(i, status)
        i += 1
    return task_data


def get_task_data(url, headers):
    response = get(url, headers)

    assert response.status == 200,\
    'Unexpected status code getting the task data: %d' % (response.status)

    return response.read()
