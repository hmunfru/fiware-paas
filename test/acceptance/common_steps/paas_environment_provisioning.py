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

from lettuce import world
from lettuce_tools.dataset_utils.dataset_utils import DatasetUtils

from tools.tier import Tier
from tools.constants import NAME, PAAS, TIER_IMAGE, PRODUCTS, NETWORKS, TIER_REQUEST_REGION, TIER_REQUEST_IMAGE

dataset_utils = DatasetUtils()


def process_following_instance_attributes(step, product_name):
    """
    Lettuce step. Adds to the world the instance attributes to create the product data for the environment.
     Attributes will be defined in the step dataset and they will be added to the list paas_product_list_with_attributes
     after being processed.
    :param step: Lettuce step data.
    :param product_name: Name of the product to add attributes
    :return:
    """
    paas_instance_attributes = dict()
    paas_instance_attributes['name'] = product_name

    attribute_list = list()
    for dataset_row in step.hashes:
        attribute_list.append(dataset_utils.prepare_data(dataset_row))
    paas_instance_attributes['attributes'] = attribute_list
    world.paas_product_list_with_attributes.append(paas_instance_attributes)


def process_the_list_of_tiers(step):
    """
    Lettuce step. This function parses dataset to prepare each tier data.
     Tiers list will be returned with the tiers parsed from dataset
    :param step: Lettuce step with all data about tiers
    :return: List of processed tiers from step data
    """
    tier_list = list()
    for row in step.hashes:
        data = dataset_utils.prepare_data(row)
        tier = Tier(data.get(NAME), world.config[PAAS][TIER_IMAGE])
        tier.parse_and_add_products(data.get(PRODUCTS))

        if TIER_REQUEST_IMAGE in data:
            tier.tier_image = data.get(TIER_REQUEST_IMAGE)

        if TIER_REQUEST_REGION in data:
            tier.region = data.get(TIER_REQUEST_REGION)

        # For each product, check if there are defined attributes
        for paas_product_with_attributes in world.paas_product_list_with_attributes:
            for attribute in paas_product_with_attributes['attributes']:
                attribute_type = attribute['type'] if 'type' in attribute else None
                tier.add_attribute_to_product(paas_product_with_attributes['name'], attribute['key'],
                                              attribute['value'], attribute_type)

        tier.parse_and_add_networks(data.get(NETWORKS))
        tier_list.append(tier)

    return tier_list
