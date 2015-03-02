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

from lettuce import step, world
from lettuce_tools.dataset_utils.dataset_utils import DatasetUtils
from tools import http
from tools import environment_request
from tools.constants import NAME, DESCRIPTION
from common_steps import sdc_product_provisioning_steps, paas_environment_provisioning

dataset_utils = DatasetUtils()


@step(u'a content-type header value "(.*)"')
def content_type_header_value(step, content_type):
    world.headers.update({'Content-Type': content_type})


@step(u'an accept header value "(.*)"')
def content_type_header_value(step, content_type):
    world.headers.update({'Accept': content_type})


@step(u'the authentication token "(.*)"')
def the_auth_token(step, token):
    world.headers.update({'X-Auth-Token': token})


@step(u'the authentication tenant-id "(.*)"')
def the_auth_token(step, tenant_id):
    world.headers.update({'Tenant-Id': tenant_id})


@step(u'I request the creation of an environment with data:')
def i_request_the_creation_of_an_environment_with_data(step):
    data = dataset_utils.prepare_data(step.hashes[0])
    world.env_requests.add_environment(data.get(NAME), data.get(DESCRIPTION), headers=world.headers)


@step(u'I receive an? "([^"]*)" response$')
def i_receive_an_http_response(step, response_type):
    status_code = http.status_codes[response_type]
    assert status_code == world.response.status, "HTTP status code is not the expected [{} != {}]"\
        .format(world.response.status, status_code)
