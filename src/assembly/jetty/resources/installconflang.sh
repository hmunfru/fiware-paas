#!/bin/sh

if test -f "/etc/lsb-release" && grep -q DISTRIB_ID /etc/lsb-release; then
  platform=`grep DISTRIB_ID /etc/lsb-release | cut -d "=" -f 2 | tr '[A-Z]' '[a-z]'`
  platform_version=`grep DISTRIB_RELEASE /etc/lsb-release | cut -d "=" -f 2`
elif test -f "/etc/debian_version"; then
  platform="debian"
  platform_version=`cat /etc/debian_version`
elif test -f "/etc/redhat-release"; then
  platform=`sed 's/^\(.\+\) release.*/\1/' /etc/redhat-release | tr '[A-Z]' '[a-z]'`
  echo $platform
  platform_version=`sed 's/^.\+ release \([.0-9]\+\).*/\1/' /etc/redhat-release`
  echo $platform_version
fi

#
# NOTE: platform manging in the install.sh is DEPRECATED
#
# - install.sh should be true to ohai and should not remap
#   platform or platform versions.
#
# - remapping platform and mangling platform version numbers is
#   now the complete responsibility of the server-side endpoints
#

major_version=`echo $platform_version | cut -d. -f1`
case $platform in
  # FIXME: should remove this case statement completely
  "el")
    # FIXME:  "el" is deprecated, should use "redhat"
    platform_version=$major_version
    ;;
  "debian")
    # FIXME: remove client-side yolo here
    case $major_version in
      "5") platform_version="6";;  # FIXME: need to port this "reverse-yolo" into platform.rb
      "6") platform_version="6";;
      "7") platform_version="6";;
    esac
    ;;
  "freebsd")
    platform_version=$major_version
    ;;
  "sles")
    platform_version=$major_version
    ;;
  "suse")
    platform_version=$major_version
    ;;
esac

if test "x$platform_version" = "x"; then
  echo "Unable to determine platform version!"
  report_bug
  exit 1
fi

if test "x$platform" = "xsolaris2"; then
  # hack up the path on Solaris to find wget, pkgadd
  PATH=/usr/sfw/bin:/usr/sbin:$PATH
  export PATH
fi

hostname_var=`hostname`
hostname_ip=`hostname  -I | cut -f1 -d' '`
echo "$hostname_ip $hostname_var" >> /etc/hosts

CHEF_SERVER=$1
HOSTNAME_VAR=$hostname_var
VALIDATION=$3
PUPPET_SERVER=$2
echo $PUPPET_SERVER
echo $CHEF_SERVER
echo $HOSTNAME_VAR
echo $VALIDATION

curl -L http://repositories.testbed.fi-ware.org/webdav/user_data_script.py | python
### installing chef
mkdir /etc/chef
mkdir /var/log/chef
curl -L https://www.opscode.com/chef/install.sh | bash 
OHAI_TIME_DIR="$(find / -name ohai_time.rb)"
sed -i 's/ohai_time Time.now.to_f/ohai_time Time.now/' ${OHAI_TIME_DIR}
echo 'log_level              :info
log_location           "/var/log/chef.log"
ssl_verify_mode        :verify_none
validation_client_name "chef-validator"
validation_key         "/etc/chef/validation.pem"
client_key             "/etc/chef/client.pem"
chef_server_url        "'${CHEF_SERVER}'"
environment            "_default"
node_name              "'${HOSTNAME_VAR}'"
json_attribs           "/etc/chef/firstboot.json"
file_cache_path        "/var/cache/chef"
file_backup_path       "/var/backups/chef"
pid_file               "/var/run/chef/client.pid"
Chef::Log::Formatter.show_time = true' > /etc/chef/client.rb

#sed -i 's/{CHEF_URL}/$CHEF_SERVER/g' /etc/chef/client.rb
#sed -i 's/{HOSTNAME}/"${HOSTNAME_VAR}"/g' /etc/chef/client.rb

echo '{}' > /etc/chef/firstboot.json
cp $VALIDATION /etc/chef/validation.pem 
chef-client -d -i 60 -s 6 -L /var/log/chef/client.log &


#### installing puppet
#### repository
echo ${platform}
if [ ${platform:0:6} == 'centos' ]; then
    echo $platform_version
    if [[ ${platform_version:0:1} = "6" ]]; then 
        rpm -ivh https://yum.puppetlabs.com/puppetlabs-release-el-6.noarch.rpm
    fi
    if [[ ${platform_version:0:1} = "7" ]]; then
        rpm -ivh https://yum.puppetlabs.com/puppetlabs-release-el-7.noarch.rpm
    fi
    if [[ ${platform_version:0:1} = "5" ]]; then
       rpm -ivh https://yum.puppetlabs.com/puppetlabs-release-el-5.noarch.rpm
    fi
    yum -y install puppet
else
    apt-get -y install puppet
fi

echo "[main]
rundir = /var/run/puppet
logdir = /var/log/puppet
ssldir = $vardir/ssl

[agent]
classfile = $vardir/classes.txt
server = "'${PUPPET_SERVER}'"
runinterval = 60
pluginsync = True
localconfig = $vardir/localconfig">text

PUPPET_CONF=`cat text`

echo $PUPPET_CONF
if [ ${platform:0:6} == 'centos' ]; then
    if [[ ${platform_version:0:1} = "7" ]]; then
        echo '[main]
        rundir = /var/run/puppet
        logdir = /var/log/puppet
        ssldir = $vardir/ssl

        [agent]
        classfile = $vardir/classes.txt
        server = "'${PUPPET_SERVER}'"
        runinterval = 60
        pluginsync = True
        localconfig = $vardir/localconfig' > /etc/puppet/puppet.conf
#        cd /opt/puppetlabs/puppet/bin/
        puppet agent --enable
        service puppet restart
    fi
    if [[ ${platform_version:0:1} = "6" ]]; then
        echo '[main]
        rundir = /var/run/puppet
        logdir = /var/log/puppet
        ssldir = $vardir/ssl
    
        [agent]
        classfile = $vardir/classes.txt
        server = "'${PUPPET_SERVER}'"
        runinterval = 60
        pluginsync = True
        localconfig = $vardir/localconfig' > /etc/puppet/puppet.conf       
        puppet agent --enable
        service puppet restart
    fi
else
    echo '[main]
        rundir = /var/run/puppet
        logdir = /var/log/puppet
        ssldir = $vardir/ssl
    
        [agent]
        classfile = $vardir/classes.txt
        server = "'${PUPPET_SERVER}'"
        runinterval = 60
        pluginsync = True
        localconfig = $vardir/localconfig' > /etc/puppet/puppet.conf

     echo 'START=yes
           DAEMON_OPTS=""' > /etc/default/puppet

        puppet agent --enable
        service puppet restart
fi

