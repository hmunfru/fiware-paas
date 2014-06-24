#!/usr/bin/env python
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



'''
Created on 16/04/2013

@author: henar
'''
from xml.etree.ElementTree import tostring
from tools import utils
from tools.enviornmentrequest import EnvironmentRequest
from tools.environment_instance import EnvironmentInstance
from tools.application_instance import ApplicationInstance
from tools.application_instance import Artifact
from tools.enviornment_instance_request import EnvironmentInstanceRequest
from xml.etree.ElementTree import tostring


domine = "130.206.80.112"

vm_fqn = 'fqn9'
vm_ip = '130.206.80.114'
product_name = 'test'
product_version = '0.1'
vdc = 'test3'
org = 'FIWARE'
environment_name = 'environment_name3'
application_name = 'application_name6'
application_version = '1.0'
artifact_name = 'dd'

config = {}
execfile("sdc.conf", config)

g = EnvironmentRequest(config['keystone_url'], config['paasmanager_url'], config['tenant'], config['user'],
    config['password'],
    config['vdc'], config['image'], config['sdc_url'])

instance_request = EnvironmentInstanceRequest(config['keystone_url'], config['paasmanager_url'], config['tenant'],
    config['user'], config['password'],
    config['vdc'], config['sdc_url'])

environment_name = 'dd'
blueprintname = 'dddd'
application = 'dddd'
network='dffdd'

print('Create a blueprint Template tomcat/mysql: ')
#g.add_environment(environment_name, 'description')

tier_name = 'tomcat5'
#g.add_tier_environment_network(environment_name, tier_name, "Trento", "tomcat=6", network)
print("  OK")
tier_name = 'mysql'
#g.add_tier_environment(environment_name,tier_name, "mysql=1.2.4")
print("  OK")

env = g.get_environment(environment_name)

print('Deploy an environment Instance' + blueprintname )
blueprint_instance = EnvironmentInstance(blueprintname, 'description', env, 'INIT')
#instance_request.add_blueprint_instance(blueprint_instance)
print ('OK')

print('Deploy an application Instance' + application )

application_instance = ApplicationInstance(application, "2.0")
artifact = Artifact("mywar", "path", "tomcat=6")
application_instance.add_artifact(artifact)

print tostring(application_instance.to_xml())
instance_request.add_application_instance(blueprintname, application_instance)
print ('OK')

#resource_environment_instance = "/paasmanager/rest/envInst/org/"+org+"/vdc/"+vdc+"/environmentInstance"
#environmentDto =  utils.createEnvironmentDto (environment_name,product_name, product_version)
#print('Deploy an environment ' + environment_name )  
#task = utils.doRequestHttpOperation(domine,resource_environment_instance, 'POST',tostring(environmentDto))
#status = utils.processTask (domine,task)
#print ("  " + status)
