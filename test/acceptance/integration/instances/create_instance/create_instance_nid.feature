# -*- coding: utf-8 -*-
Feature: Create an instance of an environment in a tenant

    As a fi-ware user
    I want to be able to create instances of the environments in a tenant with NID configurations
    so that I can use them and work with them


    @release_4_1
    Scenario: Create instance of an environment with one tier without products
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name   |
            | tierqa |
        And an environment has already been created with the previous tiers and data:
            | name  | description |
            | envqa | descqa      |
        When I request the creation of an instance of the environment "envqa" using data:
            | name   | description    |
            | instqa | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        And the created instances have not NID metadata


    @release_4_1
    Scenario: Create instance of an environment with several tiers without products
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name      |
            | tierqa1   |
            | tierqa2   |
            | tierqa3   |
        And an environment has already been created with the previous tiers and data:
            | name  | description |
            | envqa | descqa      |
        When I request the creation of an instance of the environment "envqa" using data:
            | name   | description    |
            | instqa | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        And the created instances have not NID metadata


    @release_4_1
    Scenario: Create instance of an environment with one tier and one product (without nid metadata)
        Given the paas manager is up and properly configured
        And the product "testing_paas_product_no_nid" with version "0.0.1" is created in SDC
        And a list of tiers has been defined with data:
            | name       | products                          |
            | tiernameqa | testing_paas_product_no_nid=0.0.1 |
        And an environment has already been created with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        When I request the creation of an instance of the environment "nameqa" using data:
            | name           | description    |
            | instancenameqa | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        And the created instances have not NID metadata


    @release_4_1
    Scenario Outline: Create instance of an environment with one tier and one product (with invalid nid metadata)
        Given the paas manager is up and properly configured
        And the product "<product>" with version "0.0.1" is created in SDC with metadatas:
            | key       | value     |
            | <nid_key> | 12invalid |
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
        And the created instances have not NID metadata

        Examples:
        | env_name | inst_name | product                    | nid_key   | paas_product                      |
        | envqa1   | instqa1   |testing_paas_product_no_nid | ni        | testing_paas_product_no_nid=0.0.1 |
        | envqa2   | instqa2   |testing_paas_product_no_nid | NID       | testing_paas_product_no_nid=0.0.1 |


    @happy_path @release_4_1
    Scenario: Create instance of an environment with one tier and one product (with valid nid metadata)
        Given the paas manager is up and properly configured
        And the product "testing_paas_product_nid_01" with version "0.0.1" is created in SDC with metadatas:
            | key | value  |
            | nid | 123456 |
        And a list of tiers has been defined with data:
            | name       | products                          |
            | tiernameqa | testing_paas_product_nid_01=0.0.1 |
        And an environment has already been created with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        When I request the creation of an instance of the environment "nameqa" using data:
            | name           | description    |
            | instancenameqa | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        And the created instances have the correct NID metadata value


    @release_4_1
    Scenario: Create instance of an environment with several tiers with the same product (with valid nid metadata)
        Given the paas manager is up and properly configured
        And the product "testing_paas_product_nid_01" with version "0.0.1" is created in SDC with metadatas:
            | key | value  |
            | nid | 123456 |
        And a list of tiers has been defined with data:
            | name    | products                          |
            | tierqa1 | testing_paas_product_nid_01=0.0.1 |
            | tierqa2 | testing_paas_product_nid_01=0.0.1 |
        And an environment has already been created with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        When I request the creation of an instance of the environment "nameqa" using data:
            | name    | description    |
            | instqa  | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        And the created instances have the correct NID metadata value


    @release_4_1
    Scenario: Create instance of an environment with several tiers and different products (with valid nid metadata)
        Given the paas manager is up and properly configured
        And the product "testing_paas_product_nid_01" with version "0.0.1" is created in SDC with metadatas:
            | key | value  |
            | nid | 123456 |
        And the product "testing_paas_product_nid_02" with version "0.0.1" is created in SDC with metadatas:
            | key | value  |
            | nid | 789012 |
        And a list of tiers has been defined with data:
            | name    | products                                  |
            | tierqa1 | testing_paas_product_nid_01=0.0.1 |
            | tierqa2 | testing_paas_product_nid_02=0.0.1 |
        And an environment has already been created with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        When I request the creation of an instance of the environment "nameqa" using data:
            | name   | description    |
            | instqa | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        And the created instances have the correct NID metadata value


    @release_4_1
    Scenario: Create instance of an environment with several tiers and only one product in the first tier (with valid nid metadata)
        Given the paas manager is up and properly configured
        And the product "testing_paas_product_nid_01" with version "0.0.1" is created in SDC with metadatas:
            | key | value  |
            | nid | 123456 |
        And a list of tiers has been defined with data:
            | name    | products                          |
            | tierqa1 | testing_paas_product_nid_01=0.0.1 |
            | tierqa2 |                                   |
        And an environment has already been created with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        When I request the creation of an instance of the environment "nameqa" using data:
            | name    | description    |
            | instqa  | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        And the created instances have the correct NID metadata value


    @release_4_1
    Scenario: Create instance of an environment with one tier and two different products (with valid nid metadata)
        Given the paas manager is up and properly configured
        And the product "testing_paas_product_nid_01" with version "0.0.1" is created in SDC with metadatas:
            | key | value  |
            | nid | 123456 |
        And the product "testing_paas_product_nid_02" with version "0.0.1" is created in SDC with metadatas:
            | key | value  |
            | nid | 789012 |
        And a list of tiers has been defined with data:
            | name   | products                                                            |
            | tierqa | testing_paas_product_nid_01=0.0.1,testing_paas_product_nid_02=0.0.1 |
        And an environment has already been created with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        When I request the creation of an instance of the environment "nameqa" using data:
            | name    | description    |
            | instqa  | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        And the created instances have the correct NID metadata value
