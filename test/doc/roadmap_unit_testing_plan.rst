=================
Pegasus Test Plan
=================

This section describes the Test Plan designed for the new Pegasus (PaaS Manager) features in order to test
its functionality.

Test Plan for previous features can be found in the `Fiware Wiki <https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/PaaS_Management_-_Unit_Testing_Plan>`_

Release 4.1
---------------------------

The features involved in this release are:

+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Features                                                                                                                                                                                      |
+===============================================================================================================================================================================================+
| `FIWARE.Feature.Cloud.PaaSManager.BlueprintValidation <http://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.Feature.Cloud.PaaSManager.BlueprintValidation>`_               |
| `FIWARE.Feature.Cloud.PaaSManager.ConfigurationManagement <http://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.Feature.Cloud.PaaSManager.ConfigurationManagement>`_       |
| `FIWARE.Feature.Cloud.PaaSManager.GEIdentification <http://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.Feature.Cloud.PaaSManager.GEIdentification>`_                     |
| `FIWARE.Feature.Cloud.PaaSManager.MuranoMigration <http://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.Feature.Cloud.PaaSManager.MuranoMigration>`_                       |
+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+


**Catalog Test Cases**

You can execute component test cases for this release executing this command: ::

  $ lettuce_tools -ft add_abstract_tier.feature,create_abstract_environment_with_tiers.feature,add_tier.feature,create_environment_with_tiers.feature --tags=release_4_1

Test results:

- add_abstract_tier.feature ::
	
	1 feature (1 passed)
	17 scenarios (17 passed)

- create_abstract_environment_with_tiers.feature ::
	
	1 feature (1 passed)
	18 scenarios (18 passed)

- add_tier.feature ::
	
	1 feature (1 passed)
	17 scenarios (17 passed)

- create_environment_with_tiers.feature ::
	
	1 feature (1 passed)
	18 scenarios (18 passed)
	
**Provisioning Test Cases**

You can execute E2E test cases for this release executing this command: ::

  $ lettuce_tools -ft create_instance_nid.feature,create_instance_attribute.feature --tags=release_4_1

Test results:

- create_instance_nid.feature ::

	1 feature (1 passed)
	11 scenarios (11 passed)
	
- create_instance_attribute.feature ::
	
	1 feature (1 passed)
	3 scenarios (3 passed)
----------------------------

**FIWARE.Feature.Cloud.PaaSManager.GEIdentification** ::

   The VM should contain a metadata section with the GE Id. This is needed for some
   GEs in order to manage the GEs deployed in the Cloud. The GE identification 
   is called NID. 

Test Cases involved in this feature should validate that Pegasus is creating a NID metadata in each VM when blueprint
is deployed.
They are defined in:   

> `instances/create_instance/create_instance_nid.feature <../acceptance/integration/instances/create_instance/create_instance_nid.feature>`_:

- Scenario: Create instance of an environment with one tier without products
- Scenario: Create instance of an environment with several tiers without products
- Scenario: Create instance of an environment with one tier and one product (without nid metadata)
- Scenario Outline: Create instance of an environment with one tier and one product (with invalid nid metadata)
- Scenario: Create instance of an environment with one tier and one product (with valid nid metadata)
- Scenario: Create instance of an environment with several tiers with the same product (with valid nid metadata)
- Scenario: Create instance of an environment with several tiers and different products (with valid nid metadata)
- Scenario: Create instance of an environment with several tiers and only one product in the first tier (with valid nid metadata)
- Scenario: Create instance of an environment with one tier and two different products (with valid nid metadata)


**FIWARE.Feature.Cloud.PaaSManager.ConfigurationManagement** ::

	Complex Blueprint templates have software attributes dependences. 
	PaaS Manager should be able to resolve this complex configuration and translate it
	to recipes. Concretely, it means to resolve macros introduced by the user 
	or the portal. Some example can be IP VM, or a software attribute. 
   
Test Cases involved in this feature should validate that Pegasus is managing IP translation regarding type of the
product attribute (Plain, IP, IPALL) and the name of the tier.

> `catalog/add_abstract_tier/add_abstract_tier.feature <../acceptance/integration/catalog/add_abstract_tier/add_abstract_tier.feature>`_:

- Scenario Outline: Add tier to an abstract environment with valid product attributes
- Scenario Outline: Add tier to an abstract environment. Products with invalid attribute type
- Scenario Outline: Add tier to an abstract environment. Products with invalid attribute value

> `catalog/create_abstract_environment/create_abstract_environment_with_tiers.feature <../acceptance/integration/catalog/create_abstract_environment/create_abstract_environment_with_tiers.feature>`_:

- Scenario Outline: Create abstract environment. Products with valid attributes
- Scenario Outline: Create abstract environment. Products with invalid attribute type
- Scenario Outline: Create abstract environment. Products with invalid attribute value

> `catalog/add_tier/add_tier.feature <../test/acceptance/integration/catalog/add_tier/add_tier.feature>`_:

- Scenario Outline: Add tier to an environment with valid product attributes
- Scenario Outline: Add tier to an environment. Products with invalid attribute type
- Scenario Outline: Add tier to an environment. Products with invalid attribute value

> `catalog/create_environment/create_environment_with_tiers.feature <../acceptance/integration/catalog/create_environment/create_environment_with_tiers.feature>`_:

- Scenario Outline: Create environment. Products with valid attributes
- Scenario Outline: Create environment. Products with invalid attribute type
- Scenario Outline: Create environment. Products with invalid attribute value

> `instances/create_instance/create_instance_attribute.feature <../acceptance/integration/instances/create_instance/create_instance_attribute.feature>`_:

- Scenario: Create instance of an environment with two tiers with products and attributes using chef
- Scenario: Create instance of an environment with two tiers with products and attributes using puppet (IP type)
- Scenario: Create instance of an environment with two tiers with products and attributes using puppet (IPALL type)
