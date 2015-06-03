# -*- coding: utf-8 -*-
Feature: Add a tier to an environment in a tenant

    As a fi-ware user
    I want to be able to add tiers to the environments in a tenant
    so that they become more functional and useful

    @happy_path
    Scenario: Add tier to an environment without tiers
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description |
            | nameqa | descqa      |
        When I request the addition of a tier to the environment "nameqa" with data:
            | name       |
            | tiernameqa |
        Then I receive a "No Content" response
        
    Scenario: Add tier to an environment with tiers
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description |
            | nameqa | descqa      |
        And a tier has already been added to the environment "nameqa" with data:
            | name       |
            | tiernameqa |
        When I request the addition of a tier to the environment "nameqa" with data:
            | name        |
            | newtiername |
        Then I receive a "No Content" response
        
    Scenario Outline: Add tier with different valid data to an environment
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description |
            | <name> | descqa      |
        When I request the addition of a tier to the environment "<name>" with data:
            | name       |
            | <tiername> |
        Then I receive a "No Content" response
        
        Examples:
            | name    | tiername                |
            | nameqa1 | a                       |
            | nameqa2 | nameqa-1                |
            | nameqa3 | [STRING_WITH_LENGTH_30] |

    Scenario Outline: Add tier with different invalid data to an environment
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description |
            | <name> | descqa      |
        When I request the addition of a tier to the environment "<name>" with data:
            | name       |
            | <tiername> |
        Then I receive a "Bad Request" response
        
        Examples:
            | name    | tiername                |
            | nameqa1 | tiernameqa_1            |
            | nameqa2 | tiernameqa 1            |
            | nameqa3 | [STRING_WITH_LENGTH_31] |
            | nameqa4 |                         |
            | nameqa5 | [MISSING_PARAM]         |

    Scenario: Add tier with the name of an already existing tier to an environment
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description |
            | nameqa | descqa      |
        And a tier has already been added to the environment "nameqa" with data:
            | name       |
            | tiernameqa |
        When I request the addition of a tier to the environment "nameqa" with data:
            | name       |
            | tiernameqa |
        Then I receive a "Conflict" response
        
    Scenario Outline: Add tier with products to an environment
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description |
            | <name> | descqa      |
        When I request the addition of a tier to the environment "<name>" with data:
            | name       | products   |
            | <tiername> | <products> |
        Then I receive a "No Content" response

        Examples:
            | name    | tiername    | products                 |
            | nameqa1 | tiernameqa1 | git=1.7                  |
            | nameqa2 | tiernameqa2 | git=1.7,mediawiki=1.17.0 |
        
    Scenario Outline: Add tier with networks to an environment
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description |
            | <name> | descqa      |
        When I request the addition of a tier to the environment "<name>" with data:
            | name       | networks   |
            | <tiername> | <networks> |
        Then I receive a "No Content" response

        Examples:
            | name    | tiername    | networks      |
            | nameqa1 | tiernameqa1 | netqa1        |
            | nameqa2 | tiernameqa2 | netqa1,netqa2 |
        
    Scenario Outline: Add tier with products and networks to an environment
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description |
            | <name> | descqa      |
        When I request the addition of a tier to the environment "<name>" with data:
            | name       | products   | networks   |
            | <tiername> | <products> | <networks> |
        Then I receive a "No Content" response

        Examples:
            | name    | tiername    | products                 | networks      |
            | nameqa1 | tiernameqa1 | git=1.7                  | netqa1        |
            | nameqa2 | tiernameqa2 | git=1.7,mediawiki=1.17.0 | netqa1,netqa2 |

    Scenario: Add tier to non existent environment
        Given the paas manager is up and properly configured
        And there is no environment with name "nameqa" already created
        When I request the addition of a tier to the environment "nameqa" with data:
            | name       |
            | tiernameqa |
        Then I receive a "Not Found" response


   @release_4_1
   Scenario Outline: Add tier to an environment with valid product attributes
        Given the paas manager is up and properly configured
        And the product "<product_name>" with version "<product_version>" is created in SDC with attributes:
            | key               | value               | type              |
            | custom_att_01     | <attribute_value>   | <attribute_type>  |
        And the following instance attributes for product "<product_name>":
            | key               | value               | type              |
            | custom_att_01     | <attribute_value>   | <attribute_type>  |
        And an environment has already been created with data:
            | name       | description |
            | <env_name> | descqa  |
        When I request the addition of a tier to the environment "<env_name>" with data:
            | name    | products       |
            | qa-test | <paas_product> |
        Then I receive a "No Content" response

        Examples:
        | product_name    | product_version   | attribute_type  | attribute_value   | paas_product         | env_name    |
        | qa_paas_att_01  | 0.0.1             | Plain           | default_value     | qa_paas_att_01=0.0.1 | qaenvatt01  |
        | qa_paas_att_02  | 0.0.1             | IP              | IP(tiername)      | qa_paas_att_02=0.0.1 | qaenvatt02  |
        | qa_paas_att_03  | 0.0.1             | IPALL           | IPALL(tiername)   | qa_paas_att_03=0.0.1 | qaenvatt03  |
        | qa_paas_att_04  | 0.0.1             | [MISSING_PARAM] | default_value     | qa_paas_att_04=0.0.1 | qaenvatt04  |


    @release_4_1
    Scenario Outline: Add tier to an environment. Products with invalid attribute type
        Given the paas manager is up and properly configured
        And the product "<product_name>" with version "<product_version>" is created in SDC with attributes:
            | key               | value               | type                  |
            | custom_att_01     | default_value_plain | <sdc_attribute_type>  |
        And the following instance attributes for product "<product_name>":
            | key               | value               | type                   |
            | custom_att_01     | default_value_plain | <paas_attribute_type>  |
        And an environment has already been created with data:
            | name   | description |
            | <env_name> | descqa      |
        When I request the addition of a tier to the environment "<env_name>" with data:
            | name    | products   |
            | qa-test | <paas_product> |
       Then I receive an "Bad Request" response

        Examples:
        | product_name     | product_version   | sdc_attribute_type | paas_attribute_type  | paas_product          | env_name    |
        | qa_paas_att_01a  | 0.0.1             | Plain              | Plai                 | qa_paas_att_01a=0.0.1 | qaenvatt01  |
        | qa_paas_att_02a  | 0.0.1             | IP                 | ip                   | qa_paas_att_02a=0.0.1 | qaenvatt02  |
        | qa_paas_att_03a  | 0.0.1             | IPALL              | ipall                | qa_paas_att_03a=0.0.1 | qaenvatt03  |
        | qa_paas_att_04a  | 0.0.1             | IPALL              | ip_al                | qa_paas_att_04a=0.0.1 | qaenvatt04  |
        | qa_paas_att_05a  | 0.0.1             | Plain              |                      | qa_paas_att_05a=0.0.1 | qaenvatt05  |


    @release_4_1
    Scenario Outline: Add tier to an environment. Products with invalid attribute value
        Given the paas manager is up and properly configured
        And the product "<product_name>" with version "<product_version>" is created in SDC with attributes:
            | key               | value               | type              |
            | custom_att_01     | <attribute_value>   | <attribute_type>  |
        And the following instance attributes for product "<product_name>":
            | key               | value               | type              |
            | custom_att_01     | <attribute_value>   | <attribute_type>  |
        And an environment has already been created with data:
            | name   | description |
            | <env_name> | descqa      |
        When I request the addition of a tier to the environment "<env_name>" with data:
            | name    | products   |
            | qa-test | <paas_product> |
        Then I receive an "Bad Request" response

        Examples:
        | product_name     | product_version   | attribute_type  | attribute_value             | paas_product          | env_name    |
        | qa_paas_att_01b  | 0.0.1             | IP              | 192.168.1.1                 | qa_paas_att_01b=0.0.1 | qaenvatt01  |
        | qa_paas_att_02b  | 0.0.1             | IP              | (192.168.1.1)               | qa_paas_att_02b=0.0.1 | qaenvatt02  |
        | qa_paas_att_03b  | 0.0.1             | IP              | P(192.168.1.1)              | qa_paas_att_03b=0.0.1 | qaenvatt03  |
        | qa_paas_att_04b  | 0.0.1             | IP              | IPALL(192.168.1.1)          | qa_paas_att_04b=0.0.1 | qaenvatt04  |
        | qa_paas_att_05b  | 0.0.1             | IPALL           | 192.168.1.1                 | qa_paas_att_05b=0.0.1 | qaenvatt05  |
        | qa_paas_att_06b  | 0.0.1             | IPALL           | (192.168.1.1,192.168.1.2    | qa_paas_att_06b=0.0.1 | qaenvatt06  |
        | qa_paas_att_07b  | 0.0.1             | IPALL           | IP(192.168.1.1,192.168.1.2  | qa_paas_att_07b=0.0.1 | qaenvatt07  |
        | qa_paas_att_08b  | 0.0.1             | IPALL           | ALL(192.168.1.1,192.168.1.2 | qa_paas_att_08b=0.0.1 | qaenvatt08  |
