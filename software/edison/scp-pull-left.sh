#!/bin/bash

export TARGETHOST=sk8s-aaaa-1.local

echo "Pulling webserver files from target host ($TARGETHOST)"

ssh-keygen -R 192.168.42.1

scp root@$TARGETHOST:/usr/lib/edison_config_tools/edison-config-server.js ./web/edison-config-server.js

scp -r root@$TARGETHOST:/usr/lib/edison_config_tools/public ./web/public

