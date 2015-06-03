# -*- coding: utf-8 -*-
Feature: Get the environments in a tenant

    As a fi-ware user
    I want to be able to get the list of environments in a tenant
    so that I know the environments I can use to create new instances 

    # IMPORTANT NOTE: These scenarios will not be executed unless the list of
    #                 environments in the tenant used for the tests is empty  
    @happy_path
    Scenario: Get a list with just one environment
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description |
            | nameqa | descqa      |
        When I request the list of existing environments
        Then I receive an "OK" response with "1" item in the list
        And there is an environment in the list with data:
            | name   | description |
            | nameqa | descqa      |
        
    Scenario: Get an empty list of environments
        Given the paas manager is up and properly configured
        And no environments have been created yet
        When I request the list of existing environments
        Then I receive an "OK" response with no content
        
    Scenario: Get a list with several environments
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name    | description |
            | nameqa1 | descqa1     |
        And an environment has already been created with data:
            | name    | description |
            | nameqa2 | descqa2     |
        And an environment has already been created with data:
            | name    | description |
            | nameqa3 | descqa3     |
        When I request the list of existing environments
        Then I receive an "OK" response with "3" items in the list
        And there is an environment in the list with data:
            | name    | description |
            | nameqa1 | descqa1     |
        And there is an environment in the list with data:
            | name    | description |
            | nameqa2 | descqa2     |
        And there is an environment in the list with data:
            | name    | description |
            | nameqa3 | descqa3     |
        
    Scenario: Get a list with many environments with different valid data
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name                    | description              |
            | a                       | descqa                   |
        And an environment has already been created with data:
            | name                    | description              |
            | nameqa-1                | descqa                   |
        And an environment has already been created with data:
            | name                    | description              |
            | [STRING_WITH_LENGTH_30] | descqa                   |
        And an environment has already been created with data:
            | name                    | description              |
            | accountsqa1             | a                        |
        And an environment has already been created with data:
            | name                    | description              |
            | accountsqa2             | descqa-1                 |
        And an environment has already been created with data:
            | name                    | description              |
            | accountsqa3             | Symbols: +*=._,;"@#%)/?! |
        And an environment has already been created with data:
            | name                    | description              |
            | accountsqa4             | Non-ASCII: á.é.í.ñ       |
        And an environment has already been created with data:
            | name                    | description              |
            | accountsqa5             | [STRING_WITH_LENGTH_150] |
        When I request the list of existing environments
        Then I receive an "OK" response with "8" items in the list
        And there is an environment in the list with data:
            | name                    | description              |
            | a                       | descqa                   |
        And there is an environment in the list with data:
            | name                    | description              |
            | nameqa-1                | descqa                   |
        And there is an environment in the list with data:
            | name                    | description              |
            | [STRING_WITH_LENGTH_30] | descqa                   |
        And there is an environment in the list with data:
            | name                    | description              |
            | accountsqa1             | a                        |
        And there is an environment in the list with data:
            | name                    | description              |
            | accountsqa2             | descqa-1                 |
        And there is an environment in the list with data:
            | name                    | description              |
            | accountsqa3             | Symbols: +*=._,;"@#%)/?! |
        And there is an environment in the list with data:
            | name                    | description              |
            | accountsqa4             | Non-ASCII: á.é.í.ñ       |
        And there is an environment in the list with data:
            | name                    | description              |
            | accountsqa5             | [STRING_WITH_LENGTH_150] |


    Scenario: A Tenant creates an environment and other Tenant gets the list of environments of the first one
        Given the paas manager is up and properly configured
        And   an environment has already been created with data:
              | name        | description |
              | envqaother  | descqa      |
        And   a token requested by other user
        When  I request the list of existing environments
        Then  I receive an "Forbidden" response
