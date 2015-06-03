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

__author__ = 'jfernandez'

import http
import json


class KeystoneRequest:
    def __init__(self, keystone_url, tenant, user, password, vdc=None):
        """
        Class constructor. Initializes class attributes.
        :param keystone_url: Keystone URL
        :param tenant: Fiware Tenant name
        :param user: Fiware User names
        :param password: Fiware Password
        :param vdc: TenantId
        """
        self.vdc = vdc
        self.keystone_url = keystone_url

        self.user = user
        self.password = password
        self.tenant = tenant

    def __get__token(self, url, payload):
        """
        Builds the request to get a token from Keystone
        :param url: Full URL to execute POST method
        :param payload: Body of the request
        :return: HTTPlib response
        """
        headers = {'Content-Type': 'application/json',
                   'Accept': "application/json"}
        return http.post(url, headers, payload)

    def get_token(self):
        """
        Executes request for getting the auth token from Keystone
        :return: HTTPlib response
        """
        url = "{}/{}".format(self.keystone_url, 'tokens')
        payload = {"auth": {"tenantName": self.tenant,
                            "passwordCredentials": {"username": self.user, "password": self.password}}}

        return self.__get__token(url, json.dumps(payload))


def get_token_value(body_response):
    """
    Gets token value from Keystone response
    :param body_response: Keystone response (/token)
    :return: Token ID
    """
    return body_response['access']['token']['id']


def get_public_endpoint_url_by_type(body_response, endpoint_type, region_name):
    """
    Get the public endpoint for service in a region by service TYPE
    :param body_response: Keystone response (/token)
    :param endpoint_type: Service type
    :param region_name: Name of the region
    :return: Public URL or None if not found
    """
    service_list = body_response['access']['serviceCatalog']

    public_url = None
    for service in service_list:
        if service['type'] == endpoint_type:
            for endpoint in service['endpoints']:
                if endpoint['region'] == region_name:
                    public_url = endpoint['publicURL']

    return public_url


def get_public_endpoint_url_by_name(body_response, endpoint_name, region_name):
    """
    Get the public endpoint for service in a region by service NAME
    :param body_response: Keystone response (/token)
    :param endpoint_name: Service name
    :param region_name: Name of the region
    :return: Public URL or None if not found
    """
    service_list = body_response['access']['serviceCatalog']

    public_url = None
    for service in service_list:
        if service['name'] == endpoint_name:
            for endpoint in service['endpoints']:
                if endpoint['region'] == region_name:
                    public_url = endpoint['publicURL']

    return public_url


def get_images_regions(body_response):
    """
    Gets the list of regions that have been found in the Image service with its public URL
    :param body_response: Keystone response (/token)
    :return: List of regions found with name and public URL
    """
    service_list = body_response['access']['serviceCatalog']

    regions = []
    for service in service_list:
        if service['type'] == 'image':
            for endpoint in service['endpoints']:
                regions.append({'region': endpoint['region'], 'public_url': endpoint['publicURL']})
    return regions
