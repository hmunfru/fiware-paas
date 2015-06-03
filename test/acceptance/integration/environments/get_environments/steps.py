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
from tools.tier import Tier
from tools.keystone_request import KeystoneRequest, get_token_value
from tools.utils import raw_httplib_request_to_python_dic
from tools.constants import NAME, DESCRIPTION, PRODUCTS, NETWORKS, PAAS,\
    TIER_IMAGE, KEYSTONE_URL, TENANT_ALT, USER_ALT, PASSWORD_ALT, VDC_ALT

dataset_utils = DatasetUtils()


@step(u'the paas manager is up and properly configured')
def the_paas_manager_is_up_and_properly_configured(step):
    pass  # Nothing to do here, the set up should be done by external means


@step(u'a list of tiers has been defined with data:')
def a_list_of_tiers_has_been_defined_with_data(step):
    world.tiers = []
    for row in step.hashes:
        data = dataset_utils.prepare_data(row)
        tier = Tier(data.get(NAME), world.config[PAAS][TIER_IMAGE])
        tier.parse_and_add_products(data.get(PRODUCTS))
        tier.parse_and_add_networks(data.get(NETWORKS))
        world.tiers.append(tier)


@step(u'an environment has already been created with data:')
def an_environment_has_already_been_created_with_data(step):
    data = dataset_utils.prepare_data(step.hashes[0])
    world.env_requests.add_environment(data.get(NAME), data.get(DESCRIPTION))


@step(u'an environment has already been created with the previous tiers and data:')
def an_environment_has_already_been_created_with_the_previous_tiers_and_data(step):
    data = dataset_utils.prepare_data(step.hashes[0])
    world.env_requests.add_environment(data.get(NAME), data.get(DESCRIPTION), world.tiers)


@step(u'no environments have been created yet')
def no_environments_have_been_created_yet(step):
    pass  # Nothing to do here, it should be assured by external means


@step(u'I request the list of existing environments')
def i_request_the_list_of_existing_environments(step):
    world.env_requests.get_environments(headers=world.headers)


@step(u'I receive an? "([^"]*)" response$')
def i_receive_a_response_of_type_with_items(step, response_type):
    status_code = http.status_codes[response_type]
    environment_request.check_get_environments_response(world.response, status_code)


@step(u'I receive an? "([^"]*)" response with "(\d)" items? in the list')
def i_receive_a_response_of_type_with_items(step, response_type, items_number):
    status_code = http.status_codes[response_type]
    environment_request.check_get_environments_response(world.response, status_code,
        int(items_number))


@step(u'I receive an? "([^"]*)" response with no content')
def i_receive_a_response_of_type_with_no_content(step, response_type):
    status_code = http.status_codes[response_type]
    environment_request.check_get_environments_response(world.response, status_code,
        0)


@step(u'there is an environment in the list with data:')
def there_is_an_environment_in_the_list_with_data(step):
    data = dataset_utils.prepare_data(step.hashes[0])
    environment_request.check_environment_in_list(world.response.environments,
        data.get(NAME), data.get(DESCRIPTION))


@step(u'the environment "(.*)" is not in the list')
def the_environment_is_not_in_the_list(step, environment_name):
    environment_request.check_if_environment_not_in_list(world.response.environments, environment_name)


@step(u'there is an environment in the list with "(\d)" tiers? and data:')
def there_is_an_environment_in_the_list_with_tiers_and_data(step, tiers_number):
    data = dataset_utils.prepare_data(step.hashes[0])
    environment_request.check_environment_in_list(world.response.environments,
        data.get(NAME), data.get(DESCRIPTION),
        int(tiers_number))


@step(u'a token requested by other user')
def a_token_requested_by_other_user(step):

    #Request new token for 'alternate' user
    keystone_request = KeystoneRequest(world.config[PAAS][KEYSTONE_URL], world.config[PAAS][TENANT_ALT],
                                       world.config[PAAS][USER_ALT], world.config[PAAS][PASSWORD_ALT])
    token = get_token_value(raw_httplib_request_to_python_dic(keystone_request.get_token()))
    world.headers.update({'X-Auth-Token': token})
    world.headers.update({'Tenant-Id': world.config[PAAS][VDC_ALT]})
