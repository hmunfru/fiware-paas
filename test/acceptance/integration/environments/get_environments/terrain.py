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

from lettuce import world, after, before
from tools import environment_request
from tools.environment_request import EnvironmentRequest
from tools.constants import PAAS, KEYSTONE_URL, PAASMANAGER_URL, TENANT, USER,\
    PASSWORD, VDC, SDC_URL


@before.each_feature
def before_each_scenario(feature):
    world.env_requests = EnvironmentRequest(world.config[PAAS][KEYSTONE_URL],
        world.config[PAAS][PAASMANAGER_URL],
        world.config[PAAS][TENANT],
        world.config[PAAS][USER],
        world.config[PAAS][PASSWORD],
        world.config[PAAS][VDC],
        world.config[PAAS][SDC_URL])

    # Check that the initial list of environments is empty before launching the tests
    world.env_requests.get_environments()
    environment_request.check_get_environments_response(world.response, 200, 0)


@after.each_scenario
def after_each_scenario(scenario):
    # Delete the environments created in the scenario.
    environment_request.delete_created_environments()
