[![Build Status](https://api.travis-ci.org/unige-pinfo-2018/PInfo1-backend.svg?branch=master)](https://api.travis-ci.org/unige-pinfo-2018/PInfo1-backend.svg?branch=master)

Please edit the wildfly/standalone/configuration/standalone.xml in the appserver/wildfly docker to:

```
    <interface name="management">
        <any-address />
    </interface>
    <interface name="public">
        <any-address />
    </interface>
```
