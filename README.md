# FI-WARE PaaS Manager 
[![Build Status](https://travis-ci.org/telefonicaid/fiware-paas.svg)](https://travis-ci.org/telefonicaid/fiware-paas) [![Coverage Status](https://coveralls.io/repos/jesuspg/fiware-paas/badge.png?branch=develop)](https://coveralls.io/r/jesuspg/fiware-paas?branch=develop) [![help stackoverflow](http://b.repl.ca/v1/help-stackoverflow-orange.png)](http://www.stackoverflow.com)


This is the repository of the PaaS Manager developed in the FI-WARE and 4CaaSt project. The PaaS Manager GE provides a
new layer over the IaaS layer (Openstack) in the aim of easing the task of deploying applications on a Cloud infrastructure. 
Therefore, it orchestrates the provisioning of the required virtual resources at IaaS level, and then, the installation and configuration 
of the whole software stack of the application by the SDC GE, taking into account the underlying virtual infrastructure. 
It provides a flexible mechanism to perform the deployment, enabling multiple deployment architectures: 
everything in a single VM or server, several VMs or servers, or elastic architectures based on load balancers and different software tiers.


## Requirements
In order to execute the PaaS Manager, it is needed to have previously installed the following software:
- Tomcat 7.X.X 
- PostgreSQL 

#### Building Requirements
It is a a maven application, it is just need to execute mvn clean install

## Installation instruction (for CentOS)
### Database configuration
    
    $ yum install postgresql postgresql-server postgresql-contrib


Type the following commands to install the postgresql as service and start it

    chkconfig --add postgresql
    chkconfig postgresql on
    service postgresql initdb
    service postgresql start

Create the DB :
Connect as postgres user to the PostgreSQL server and set the password for user postgres using alter user as below:

    $ su - postgres
    postgres$ psql postgres postgres;
    psql (8.4.13)
    Type "help" for help.
    postgres=# alter user postgres with password 'postgres';
    Create the database
    postgres=# create database paasmanager;
    postgres=# grant all privileges on database paasmanager to postgres;

Create tables:
Download sql files from [here](/migrations/src/main/resources)

    postgres=# \i db-initial.sql
    postgres=# \i db-changelog.sql

    exit quit "\q" and then "exit"

Edit file /var/lib/pgsql/data/pg_hba.conf and set authentication method to md5:

    # TYPE  DATABASE    USER        CIDR-ADDRESS          METHOD
    # "local" is for Unix domain socket connections only
    local   all         all                               md5
    local   all         postgres                          md5
    # IPv4 local connections:
    host    all         all         0.0.0.0/0          md5
Edit file /var/lib/pgsql/data/postgresql.conf and set listen addresses to 0.0.0.0:

    listen_addresses = '0.0.0.0'
Reload configuration

    $ service postgresql reload

[edit] Apache Tomcat configuration
### Tomcat configuration
Install Tomcat 7 together with standard Tomcat samples, documentation, and management web apps:

    $ yum install tomcat7-webapps tomcat7-docs-webapp tomcat7-admin-webapps
Start/Stop/Restart Tomcat 7 as a service. startp:
    
    $ sudo service tomcat7 start
stop:

    $ sudo service tomcat7 stop
restart:

    $ sudo service tomcat7 restart
Add Tomcat 7 service to the autostart

    $ sudo chkconfig tomcat7 on


Once the prerequisites are satisfied, you shall create the context file as $CATALINA_HOME/conf/Catalina/localhost/paasmanager.xml (substituting PATH_TO_WEBAPP to the corresponding directory):

    <Context path="/paasmanager" docBase="PATH_TO_WEBAPP" reloadable="true" debug="5">
      <Resource name="jdbc/paasmanager" auth="Container" type="javax.sql.DataSource" driverClassName="org.postgresql.Driver"
       url="jdbc:postgresql://localhost:5432/paasmanager"
       username="postgres" password="postgres"
       maxActive="20" maxIdle="10" maxWait="-1"/> 
    </Context>
    
Include the library postgresql-8.4-702.jdbc4.jar in $CATALINA_HOME/lib
Configure the profile fiware in the catalina.properties. So that, open the file $CATALINA_HOME/conf/catalina.properties and write at the end
 spring.profiles.active=fiware

Start tomcat

    $ sudo service tomcat7 start

#### Acceptance tests


#### References
* [FIWARE.OpenSpecification.Cloud.PaaS](http://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.Cloud.PaaS)
* [PaaS_Open_RESTful_API_Specification_(PRELIMINARY)](http://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/PaaS_Open_RESTful_API_Specification_(PRELIMINARY))
* [PaaS_Manager_-_Installation_and_Administration_Guide](http://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/PaaS_Manager_-_Installation_and_Administration_Guide)


