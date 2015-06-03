==============================
Pegasus API - Acceptance Tests
==============================
This project contains the Pegasus (PaaS Manager) acceptance tests (component, integration and E2E testing).
All test cases have been defined using Gherkin that it is a Business Readable, Domain Specific Language that lets you
describe software’s behaviour without detailing how that behaviour is implemented.
Gherkin has the purpose of serving documentation of test cases.


Test case implementation has been performed using `Python <http://www.python.org/>`_ and
`Lettuce <http://lettuce.it/>`_.


Test environment
----------------

**Prerequisites**

- Python 2.7 or newer (2.x) (https://www.python.org/downloads/)
- pip (https://pypi.python.org/pypi/pip)
- virtualenv (https://pypi.python.org/pypi/virtualenv)
- Pegasus (`Download Pegasus (PaaS Manager) <http://catalogue.fi-ware.org/enablers/paas-manager-pegasus/downloads>`_)

**Test case execution using virtualenv**

1. Create a virtual environment somewhere *(virtualenv ~/venv)*
#. Activate the virtual environment *(source ~/venv/bin/activate)*
#. Go to *test/acceptance* folder in the project
#. Install the requirements for the acceptance tests in the virtual environment *(pip install -r requirements.txt --allow-all-external)*


Test structure and prerequisites
---------------------------------

Pegasus (PaaS Manager) will need to be deployed in a FIWARE environment to be tested.

Acceptance tests have two type of test cases:

- **Catalog test cases**: They try to test Pegasus catalog and they do not need additional prerequisites.
- **Instance test cases**: Those test cases try to test the Blueprint deployment.

**NOTE**: For launching blueprints you will need the full OpenStack architecture available, the product releases 
registered in (`Sagitta <http://catalogue.fi-ware.org/enablers/software-deployment-configuration-sagitta>`_)
and Chef-Server/Puppet-Master must be ready to install the software requested.


Configuration file
------------------
Some configuration is needed before test case execution. This configuration is set in the *properties.json* file.

All configuration values will be 'strings'.

**Environment configuration example** ::

    "paas": {
        "keystone_url": "http://130.206.80.57:4731/v2.0",
        "sdc_url": "https://130.206.81.126:8443/sdc/rest",
        "paasmanager_url": "https://130.206.81.133:8443/paasmanager/rest",
        "glance_url": "",
        "nova_url": "http://130.206.80.57:8774/v2/***************",
        "vdc": "***************",
        "tenant": "***************",
        "user": "***************",
        "password": "***************",
        "tier_image": "422128fe-02a2-44ca-b9a7-67ec69809e5e",
        "tier_num_min": "1",
        "tier_num_max": "1",
        "tier_num_initial": "1",
        "tier_flavour": "2",
        "tier_keypair": "default",
        "tier_floatingip": "false",
        "tier_region": "Spain"
    }

You will need setup a valid configuration properties for the environment (*keystone_url*) and region (*tier_region*)
you are going to use.

- You need a valid FIWARE credentials for the configured *keystone_url*: User, Password and TenantId.
- You can get the public endpoints configuration (*_url*) requesting for that to the Service Catalog service (Keystone).
- You can find and create the Tier configuration values using the FIWARE Portal:
Images (to set a valid *tier_image* id), Flavors (to set a valid *tier_flavor* id),
Security/Keypairs (to set a *tier_keypair*)
- You do not need change *tier_num_* and *tier_floatingip*) values.


Tests execution
---------------
- Go to the *test/acceptance* folder of the project if not already on it or.
- Run *lettuce_tools --tags=-skip*. This command will execute all acceptance tests (see available params with the -h option)
