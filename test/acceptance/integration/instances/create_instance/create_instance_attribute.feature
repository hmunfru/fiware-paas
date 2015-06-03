# -*- coding: utf-8 -*-
Feature: Create an instance of an environment in a tenant

    As a fi-ware user
    I want to be able to create instances of the environments in a tenant
    so that I can use them and work with them


    @happy_path @release_4_1
    Scenario: Create instance of an environment with two tier with products and attributes using chef
        Given the paas manager is up and properly configured
        And the product installator to be used is "chef"
        And the product "testing_paas_product_att_01" with version "0.0.1" is created in SDC with attributes:
            | key               | value               | type    |
            | custom_att_02     | default_value_plain | Plain   |
        And the product "testing_paas_product_att_02" with version "0.0.1" is created in SDC with attributes:
            | key               | value           | type    |
            | custom_att_01     | IP(tiernameqa1) | IP      |
        And a list of tiers has been defined with data:
            | name        | products                      |
            | tiernameqa1 | testing_paas_product_att_01=0.0.1    |
            | tiernameqa2 | testing_paas_product_att_02=0.0.1 |
        And an environment has already been created with the previous tiers and data:
            | name    | description |
            | env-qa | descqa       |
        When I request the creation of an instance of the environment "env-qa" using data:
            | name     | description    |
            | inst-qa | instancedescqa  |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status


    @release_4_1
    Scenario: Create instance of an environment with two tier with products and attributes using puppet (IP type)
        Given the paas manager is up and properly configured
        And the product installator to be used is "puppet"
        And the product "testing_paas_product_puppet" with version "0.0.1" is created in SDC with attributes:
            | key               | value               | type    |
            | custom_att_01     | default_value_plain | Plain   |
        And the product "testing_paas_product_puppet_02" with version "0.0.1" is created in SDC with attributes:
            | key               | value           | type    |
            | custom_att_01     | IP(tiernameqa1) | IP      |
        And a list of tiers has been defined with data:
            | name        | products                      |
            | tiernameqa1 | testing_paas_product_puppet=0.0.1    |
            | tiernameqa2 | testing_paas_product_puppet_02=0.0.1 |
        And an environment has already been created with the previous tiers and data:
            | name    | description |
            | env-qap | descqa       |
        When I request the creation of an instance of the environment "env-qap" using data:
            | name     | description    |
            | inst-qap | instancedescqa  |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status


    @release_4_1
    Scenario: Create instance of an environment with two tier with products and attributes using puppet (IPALL type)
        Given the paas manager is up and properly configured
        And the product installator to be used is "puppet"
        And the product "testing_paas_product_puppet" with version "0.0.1" is created in SDC with attributes:
            | key               | value               | type    |
            | custom_att_01     | default_value_plain | Plain   |
        And the product "testing_paas_product_puppet_02" with version "0.0.1" is created in SDC with attributes:
            | key               | value              | type    |
            | custom_att_01     | IPALL(tiernameqa1) | IPALL   |
        And a list of tiers has been defined with data:
            | name        | products                             |
            | tiernameqa1 | testing_paas_product_puppet=0.0.1    |
            | tiernameqa2 | testing_paas_product_puppet_02=0.0.1 |
        And an environment has already been created with the previous tiers and data:
            | name    | description |
            | env-qap3 | descqa       |
        When I request the creation of an instance of the environment "env-qap3" using data:
            | name     | description    |
            | inst-qap3 | instancedescqa  |
        Then I receive an "OK" response with a task
        And the task ends with "SUCCESS" status
