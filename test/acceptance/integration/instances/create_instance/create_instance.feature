# -*- coding: utf-8 -*-
Feature: Create an instance of an environment in a tenant

    As a fi-ware user
    I want to be able to create instances of the environments in a tenant
    so that I can use them and work with them


    @happy_path
    Scenario: Create instance of an environment with one tier
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


    Scenario: Create instance of an environment with several tiers
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


    @env_dependant
    Scenario: Create instance of an environment with several tiers in different regions
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name      | region | image                                | networks | products                   |
            | tierqatr  | Trento | 6e2519b8-1b36-4669-b6fe-ed2e77815b9f | netqatr  | testing_paas_product=0.0.1 |
            | tierqasp  | Spain  | 422128fe-02a2-44ca-b9a7-67ec69809e5e | netqasp  | testing_paas_product=0.0.1 |
        And an environment has already been created with the previous tiers and data:
            | name     | description |
            | envqareg | descqa      |
        When I request the creation of an instance of the environment "envqareg" using data:
            | name      | description    |
            | instqareg | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status


    @env_dependant
    Scenario: Create instance of an environment with several tiers in different regions
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name      | region | image                                |
            | tierqasp  | Spain  | 422128fe-02a2-44ca-b9a7-67ec69809e5e |
            | tierqatr  | Trento | 6e2519b8-1b36-4669-b6fe-ed2e77815b9f |
        And an environment has already been created with the previous tiers and data:
            | name     | description |
            | envqareg | descqa      |
        When I request the creation of an instance of the environment "envqareg" using data:
            | name      | description    |
            | instqareg | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status

    Scenario Outline: Create instance of an environment with tiers with different valid data (1/3)
    # Split in three parts due to OpenStack limits that cannot be easily overridden
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name                    |
            | a                       |
            | tiernameqa-1            |
            | [STRING_WITH_LENGTH_30] |
        And an environment has already been created with the previous tiers and data:
            | name      | description |
            | <envname> | <envdesc>   |
        When I request the creation of an instance of the environment "<envname>" using data:
            | name   | description    |
            | <name> | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        
        Examples:
            | name    | envname                 | envdesc                  |
            | instqa1 | a                       | descqa                   |
            | instqa2 | nameqa-1                | descqa                   |
            | instqa3 | [STRING_WITH_LENGTH_30] | descqa                   |


    Scenario Outline: Create instance of an environment with tiers with different valid data (2/3)
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name                    |
            | a                       |
            | tiernameqa-1            |
            | [STRING_WITH_LENGTH_30] |
        And an environment has already been created with the previous tiers and data:
            | name      | description |
            | <envname> | <envdesc>   |
        When I request the creation of an instance of the environment "<envname>" using data:
            | name   | description    |
            | <name> | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        
        Examples:
            | name            | envname                 | envdesc                  |
            | instancenameqa4 | nameqa1                 | a                        |
            | instancenameqa5 | nameqa2                 | descqa-1                 |
            | instancenameqa6 | nameqa3                 | Symbols: +*=._,;"@#%)/?! |


    Scenario Outline: Create instance of an environment with tiers with different valid data (3/3)
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name                    |
            | a                       |
            | tiernameqa-1            |
            | [STRING_WITH_LENGTH_30] |
        And an environment has already been created with the previous tiers and data:
            | name      | description |
            | <envname> | <envdesc>   |
        When I request the creation of an instance of the environment "<envname>" using data:
            | name   | description    |
            | <name> | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        
        Examples:
            | name            | envname                 | envdesc                  |
            | instancenameqa7 | nameqa4                 | Non-ASCII: á.é.í.ñ       |
            | instancenameqa8 | nameqa5                 | [STRING_WITH_LENGTH_256] |


    Scenario Outline: Create instance with different valid data of an environment with tiers
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name       |
            | tiernameqa |
        And an environment has already been created with the previous tiers and data:
            | name      | description |
            | <envname> | descqa      |
        When I request the creation of an instance of the environment "<envname>" using data:
            | name   | description   |
            | <name> | <description> |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
        
        Examples:
            | name                    | description              | envname |
            | a                       | descqa                   | nameqa1 |
            | instancenameqa-1        | descqa                   | nameqa2 |
            | [STRING_WITH_LENGTH_30] | descqa                   | nameqa3 |
            | instancenameqa1         | a                        | nameqa4 |
            | instancenameqa2         | descqa-1                 | nameqa5 |
            | instancenameqa3         | Symbols: +*=._,;"@#%)/?! | nameqa6 |
            | instancenameqa4         | Non-ASCII: á.é.í.ñ       | nameqa7 |
            | instancenameqa5         | [STRING_WITH_LENGTH_256] | nameqa8 |


    Scenario: Create instance with different invalid data of an environment with tiers
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name       |
            | tiernameqa |
        And an environment has already been created with the previous tiers and data:
            | name      | description |
            | <envname> | descqa      |
        When I request the creation of an instance of the environment "<envname>" using data:
            | name   | description   |
            | <name> | <description> |
        Then I receive a "Bad Request" response
        
        Examples:
            | name                    | description              | envname |
            | nameqa_1                | descqa                   | nameqa1 |
            | [STRING_WITH_LENGTH_31] | descqa                   | nameqa2 |
            |                         | descqa                   | nameqa3 |
            | [MISSING_PARAM]         | descqa                   | nameqa4 |
            | instancenameqa1         | [STRING_WITH_LENGTH_257] | nameqa5 |
            | instancenameqa2         |                          | nameqa6 |
            | instancenameqa3         | [MISSING_PARAM]          | nameqa7 |


    @happy_path
    Scenario Outline: Create instance of an environment with one tier with products
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name       | products   |
            | <tiername> | <products> |
        And an environment has already been created with the previous tiers and data:
            | name      | description |
            | <envname> | descqa      |
        When I request the creation of an instance of the environment "<envname>" using data:
            | name   | description    |
            | <name> | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status

        Examples:
            | name            | envname | tiername    | products                    |
            | instancenameqa  | nameqa  | tiernameqa  | testing_paas_product=0.0.1  |


    @slow
    Scenario: Create instance of an environment with several tiers with products
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name        | products                   |
            | tiernameqa1 | testing_paas_product=0.0.1 |
            | tiernameqa2 | testing_paas_product=0.0.1 |
        And an environment has already been created with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        When I request the creation of an instance of the environment "nameqa02" using data:
            | name           | description    |
            | instancenameqa | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status


    @happy_path
    Scenario Outline: Create instance of an environment with one tier with networks
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name       | networks   |
            | <tiername> | <networks> |
        And an environment has already been created with the previous tiers and data:
            | name      | description |
            | <envname> | descqa      |
        When I request the creation of an instance of the environment "<envname>" using data:
            | name   | description    |
            | <name> | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status

        Examples:
            | name            | envname | tiername    | networks      |
            | instancenameqa1 | nameqa1 | tiernameqa1 | netqa1        |
            | instancenameqa2 | nameqa2 | tiernameqa2 | netqa1,netqa2 |


    Scenario: Create instance of an environment with several tiers with networks
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name        | networks |
            | tiernameqa1 | netqa1   |
            | tiernameqa2 | netqa2   |
        And an environment has already been created with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        When I request the creation of an instance of the environment "nameqa" using data:
            | name           | description    |
            | instancenameqa | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status


    Scenario Outline: Create instance of an environment with one tier with products and networks
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name       | products   | networks   |
            | <tiername> | <products> | <networks> |
        And an environment has already been created with the previous tiers and data:
            | name      | description |
            | <envname> | descqa      |
        When I request the creation of an instance of the environment "<envname>" using data:
            | name   | description    |
            | <name> | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status

        Examples:
            | name            | envname | tiername    | products                                                 | networks      |
            | instancenameqa1 | nameqa1 | tiernameqa1 | testing_paas_product=0.0.1                               | netqa1        |
            | instancenameqa2 | nameqa2 | tiernameqa2 | testing_paas_product=0.0.1,testing_paas_product_02=0.0.1 | netqa1,netqa2 |


    @slow
    Scenario: Create instance of an environment with tiers with products and networks
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name        | products                      | networks |
            | tiernameqa1 | testing_paas_product=0.0.1    | netqa1   |
            | tiernameqa2 | testing_paas_product_02=0.0.1 | netqa2   |
        And an environment has already been created with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        When I request the creation of an instance of the environment "nameqa" using data:
            | name           | description    |
            | instancenameqa | instancedescqa |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status


    Scenario: Create instance with the same name of an already existing one
        Given the paas manager is up and properly configured
        And a list of tiers has been defined with data:
            | name       |
            | tiernameqa |
        And an environment has already been created with the previous tiers and data:
            | name   | description |
            | nameqa | descqa      |
        And an instance of the environment "nameqa" has already been created using data:
            | name           | description    |
            | instancenameqa | instancedescqa |
        When I request the creation of an instance of the environment "nameqa" using data:
            | name           | description    |
            | instancenameqa | instancedescqa |
        Then I receive a "Conflict" response


    Scenario: Create instance of an environment without tiers
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description |
            | nameqa | descqa      |
        When I request the creation of an instance of the environment "nameqa" using data:
            | name       | description |
            | instanceqa | instdescqa  |
        Then I receive a "Bad Request" response
