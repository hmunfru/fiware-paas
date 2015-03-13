# -*- coding: utf-8 -*-
Feature: Deploy a product in an instance with open_port and open_ports metadata values

    As a fi-ware user
    I want to be able to deploy blueprints and install products with open_port and open_ports metadatas
    so that security groups and rules are created dynamically based on these metadata values of products.

    @skip @CLAUDIA-4847 @slow
    Scenario Outline: Create and launch a Blueprint with one product with open_ports metadatas
        Given the paas manager is up and properly configured
        And the product "<product>" with version "0.0.1" is created in SDC with metadatas:
            | key        | value            |
            | open_ports | <metadata_value> |
        And a list of tiers has been defined with data:
            | name       | products       |
            | tiernameqa | <paas_product> |
        And an environment has already been created with the previous tiers and data:
            | name       | description |
            | <env_name> | descqa      |
        When I request the creation of an instance of the environment "<env_name>" using data:
            | name        | description    |
            | <inst_name> | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        And the created security group has rules with "TCP" ports "<metadata_value>"

        Examples:
        | env_name    | inst_name    | product             | paas_product              | metadata_value      |
        | envqamet1   | instqamet1   | testing_metadata_01 | testing_metadata_01=0.0.1 |                     |
        | envqamet2   | instqamet2   | testing_metadata_01 | testing_metadata_01=0.0.1 | 8080                |
        | envqamet3   | instqamet3   | testing_metadata_01 | testing_metadata_01=0.0.1 | 8080-8090           |
        | envqamet4   | instqamet4   | testing_metadata_01 | testing_metadata_01=0.0.1 | 55 8080-8090        |
        | envqamet5   | instqamet5   | testing_metadata_01 | testing_metadata_01=0.0.1 | 8080-8090 55        |
        | envqamet6   | instqamet6   | testing_metadata_01 | testing_metadata_01=0.0.1 | 55 65 8080-8090 88  |


    @slow
    Scenario Outline: Create and launch a Blueprint with one product with open_ports_udp metadatas
        Given the paas manager is up and properly configured
        And the product "<product>" with version "0.0.1" is created in SDC with metadatas:
            | key            | value            |
            | open_ports_udp | <metadata_value> |
        And a list of tiers has been defined with data:
            | name       | products       |
            | tiernameqa | <paas_product> |
        And an environment has already been created with the previous tiers and data:
            | name       | description |
            | <env_name> | descqa      |
        When I request the creation of an instance of the environment "<env_name>" using data:
            | name        | description    |
            | <inst_name> | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        And the created security group has rules with "UDP" ports "<metadata_value>"

        Examples:
        | env_name    | inst_name    | product             | paas_product              | metadata_value      |
        | envqamet1   | instqamet1   | testing_metadata_02 | testing_metadata_02=0.0.1 |                     |
        | envqamet2   | instqamet2   | testing_metadata_02 | testing_metadata_02=0.0.1 | 8080                |
        | envqamet3   | instqamet3   | testing_metadata_02 | testing_metadata_02=0.0.1 | 8080-8090           |
        | envqamet4   | instqamet4   | testing_metadata_02 | testing_metadata_02=0.0.1 | 55 8080-8090        |
        | envqamet5   | instqamet5   | testing_metadata_02 | testing_metadata_02=0.0.1 | 8080-8090 55        |
        | envqamet6   | instqamet6   | testing_metadata_02 | testing_metadata_02=0.0.1 | 55 65 8080-8090 88  |

    @skip @CLAUDIA-4847 @slow
    Scenario Outline: Create and launch a Blueprint with one product with open_ports and open_ports_udp metadatas
        Given the paas manager is up and properly configured
        And the product "<product>" with version "0.0.1" is created in SDC with metadatas:
            | key            | value            |
            | open_ports     | <open_ports> |
            | open_ports_udp | <open_ports_udp> |
        And a list of tiers has been defined with data:
            | name       | products       |
            | tiernameqa | <paas_product> |
        And an environment has already been created with the previous tiers and data:
            | name       | description |
            | <env_name> | descqa      |
        When I request the creation of an instance of the environment "<env_name>" using data:
            | name        | description    |
            | <inst_name> | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        And the created security group has rules with "TCP" ports "<open_ports>"
        And the created security group has rules with "UDP" ports "<open_ports_udp>"

        Examples:
        | env_name    | inst_name    | product             | paas_product              | open_ports_udp | open_ports |
        | envqamet1   | instqamet1   | testing_metadata_03 | testing_metadata_03=0.0.1 | 23-29          | 8080-8090  |
        | envqamet2   | instqamet2   | testing_metadata_03 | testing_metadata_03=0.0.1 | 40-1080        | 135-2556   |
