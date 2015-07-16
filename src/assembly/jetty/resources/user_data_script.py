#!/usr/bin/python
import yaml
import urllib2

def get_validation_key(userdata):
    data = yaml.load(userdata)
    if not 'chefkey' in data:
        return None
    data = data['chefkey']
    print data
    f = open('/etc/validation.pem','w')
    f.write(data)
    f.close()

def get_user_data():
    try:
        h =  urllib2.urlopen('http://169.254.169.254/openstack/latest/user_data',None,30)
    except Exception, e:
        return None, None
    if h.getcode() != 200:
        return None, None

    return get_validation_key(h)

get_user_data()
