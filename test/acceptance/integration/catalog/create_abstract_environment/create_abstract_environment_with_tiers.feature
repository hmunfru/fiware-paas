# -*- coding: utf-8 -*-
Feature: Create an abstract environment with tiers

    As a fi-ware user
    I want to be able to create abstract environments with tiers
    so that I can clone them later

    @happy_path
    Scenario: Create abstract environment with one tier
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name       |
            | tiernameqa |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        Then I receive a "No Content" response

    Scenario: Create abstract environment with several tiers
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name        |
            | tiernameqa1 |
            | tiernameqa2 |
            | tiernameqa3 |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        Then I receive a "No Content" response

    Scenario: Create abstract environment with several tiers with different valid data
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name                    |
            | a                       |
            | tiernameqa-1            |
            | [STRING_WITH_LENGTH_30] |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        Then I receive a "No Content" response

    Scenario Outline: Create abstract environment with tiers with different invalid data
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name       |
            | <tiername> |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name   | description |
            | <name> | descqa      |
        Then I receive an "Bad Request" response
        
        Examples:
            | name    | tiername                |
            | nameqa1 | tiernameqa_1            |
            | nameqa2 | tiernameqa 1            |
            | nameqa3 | [STRING_WITH_LENGTH_31] |
            | nameqa4 |                         |
            | nameqa5 | [MISSING_PARAM]         |

    Scenario Outline: Create abstract environment with one tier with products
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name       | products   |
            | <tiername> | <products> |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name   | description |
            | <name> | descqa      |
        Then I receive a "No Content" response

        Examples:
            | name    | tiername    | products                 |
            | nameqa1 | tiernameqa1 | git=1.7                  |
            | nameqa2 | tiernameqa2 | git=1.7,mediawiki=1.17.0 |

    Scenario: Create abstract environment with several tiers with products
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name        | products         |
            | tiernameqa1 | git=1.7          |
            | tiernameqa2 | mediawiki=1.17.0 |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        Then I receive a "No Content" response

    Scenario Outline: Create abstract environment with one tier with networks
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name       | networks   |
            | <tiername> | <networks> |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name   | description |
            | <name> | descqa      |
        Then I receive a "No Content" response

        Examples:
            | name    | tiername    | networks      |
            | nameqa1 | tiernameqa1 | netqa1        |
            | nameqa2 | tiernameqa2 | netqa1,netqa2 |

    Scenario: Create abstract environment with several tiers with networks
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name        | networks |
            | tiernameqa1 | netqa1   |
            | tiernameqa2 | netqa2   |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        Then I receive a "No Content" response

    Scenario Outline: Create abstract environment with one tier with products and networks
        And a list of tiers has been defined with data:
            | name       | products   | networks   |
            | <tiername> | <products> | <networks> |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name   | description |
            | <name> | descqa      |
        Then I receive a "No Content" response

        Examples:
            | name    | tiername    | products                 | networks      |
            | nameqa1 | tiernameqa1 | git=1.7                  | netqa1        |
            | nameqa2 | tiernameqa2 | git=1.7,mediawiki=1.17.0 | netqa1,netqa2 |

    Scenario: Create abstract environment with several tiers with products and networks
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name        | products         | networks |
            | tiernameqa1 | git=1.7          | netqa1   |
            | tiernameqa2 | mediawiki=1.17.0 | netqa2   |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        Then I receive a "No Content" response


    @release_4_1
    Scenario Outline: Create abstract environment. Products with valid attributes
        Given the paas manager is up and properly configured
        And the product "<product_name>" with version "<product_version>" is created in SDC with attributes:
            | key               | value               | type              |
            | custom_att_01     | <attribute_value>   | <attribute_type>  |
        And the following instance attributes for product "<product_name>":
            | key               | value               | type              |
            | custom_att_01     | <attribute_value>   | <attribute_type> |
        And a list of tiers has been defined with data:
            | name        | products       |
            | tiernameqa1 | <paas_product> |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name        | description |
            | <env_name>  | descqa      |
        Then I receive a "No Content" response

        Examples:
        | product_name    | product_version   | attribute_type  | attribute_value                 | paas_product         | env_name    |
        | qa_paas_att_01  | 0.0.1             | Plain           | default_value                   | qa_paas_att_01=0.0.1 | qaenvatt01  |
        | qa_paas_att_02  | 0.0.1             | IP              | IP(192.168.1.1)                 | qa_paas_att_02=0.0.1 | qaenvatt02  |
        | qa_paas_att_03  | 0.0.1             | IPALL           | IPALL(192.168.1.1)              | qa_paas_att_03=0.0.1 | qaenvatt03  |
        | qa_paas_att_04  | 0.0.1             | IPALL           | IPALL(192.168.1.1,192.168.1.2)  | qa_paas_att_04=0.0.1 | qaenvatt04  |
        | qa_paas_att_05  | 0.0.1             | [MISSING_PARAM] | default_value                   | qa_paas_att_05=0.0.1 | qaenvatt05  |


    @release_4_1
    Scenario Outline: Create abstract environment. Products with invalid attribute type
        Given the paas manager is up and properly configured
        And the product "<product_name>" with version "<product_version>" is created in SDC with attributes:
            | key               | value               | type                  |
            | custom_att_01     | default_value_plain | <sdc_attribute_type>  |
        And the following instance attributes for product "<product_name>":
            | key               | value               | type                   |
            | custom_att_01     | default_value_plain | <paas_attribute_type>  |
        And a list of tiers has been defined with data:
            | name        | products       |
            | tiernameqa1 | <paas_product> |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name        | description |
            | <env_name>  | descqa      |
        Then I receive an "Bad Request" response

        Examples:
        | product_name     | product_version   | sdc_attribute_type | paas_attribute_type  | paas_product          | env_name    |
        | qa_paas_att_01a  | 0.0.1             | Plain              | Plai                 | qa_paas_att_01a=0.0.1 | qaenvatt01  |
        | qa_paas_att_02a  | 0.0.1             | IP                 | ip                   | qa_paas_att_02a=0.0.1 | qaenvatt02  |
        | qa_paas_att_03a  | 0.0.1             | IPALL              | ipall                | qa_paas_att_03a=0.0.1 | qaenvatt03  |
        | qa_paas_att_04a  | 0.0.1             | IPALL              | ip_al                | qa_paas_att_04a=0.0.1 | qaenvatt04  |
        | qa_paas_att_05a  | 0.0.1             | Plain              |                      | qa_paas_att_05a=0.0.1 | qaenvatt05  |


    @release_4_1
    Scenario Outline: Create abstract environment. Products with invalid attribute value
        Given the paas manager is up and properly configured
        And the product "<product_name>" with version "<product_version>" is created in SDC with attributes:
            | key               | value               | type              |
            | custom_att_01     | <attribute_value>   | <attribute_type>  |
        And the following instance attributes for product "<product_name>":
            | key               | value               | type                   |
            | custom_att_01     | <attribute_value>   | <attribute_type>  |
        And a list of tiers has been defined with data:
            | name        | products       |
            | tiernameqa1 | <paas_product> |
        When I request the creation of an abstract environment with the previous tiers and data:
            | name        | description |
            | <env_name>  | descqa      |
        Then I receive an "Bad Request" response

        Examples:
        | product_name     | product_version   | attribute_type  | attribute_value             | paas_product         | env_name    |
        | qa_paas_att_01b  | 0.0.1             | IP              | 192.168.1.1                 | qa_paas_att_01b=0.0.1 | qaenvatt01  |
        | qa_paas_att_02b  | 0.0.1             | IP              | (192.168.1.1)               | qa_paas_att_02b=0.0.1 | qaenvatt02  |
        | qa_paas_att_03b  | 0.0.1             | IP              | P(192.168.1.1)              | qa_paas_att_03b=0.0.1 | qaenvatt03  |
        | qa_paas_att_04b  | 0.0.1             | IP              | IPALL(192.168.1.1)          | qa_paas_att_04b=0.0.1 | qaenvatt04  |
        | qa_paas_att_05b  | 0.0.1             | IPALL           | 192.168.1.1                 | qa_paas_att_05b=0.0.1 | qaenvatt05  |
        | qa_paas_att_06b  | 0.0.1             | IPALL           | (192.168.1.1,192.168.1.2    | qa_paas_att_06b=0.0.1 | qaenvatt06  |
        | qa_paas_att_07b  | 0.0.1             | IPALL           | IP(192.168.1.1,192.168.1.2  | qa_paas_att_07b=0.0.1 | qaenvatt07  |
        | qa_paas_att_08b  | 0.0.1             | IPALL           | ALL(192.168.1.1,192.168.1.2 | qa_paas_att_08b=0.0.1 | qaenvatt08  |
