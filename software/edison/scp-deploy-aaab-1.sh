#!/bin/bash

export TARGETHOST=sk8s-aaab-1.local

echo "Deploying to target host ($TARGETHOST)"

ssh-keygen -R 192.168.42.1

ssh root@$TARGETHOST 'rm -fr ~/sk8flair && rm -fr ~/src && mkdir -p ~/sk8flair/profiles && mkdir -p ~/tests && mkdir -p ~/src'

# Deploy the bluetooth configuration files
scp ./bluetooth/main-left.conf root@$TARGETHOST:/etc/bluetooth/main.conf
scp ./bluetooth/bluetooth.conf root@$TARGETHOST:/etc/dbus-1/system.d/bluetooth.conf

# Deploy the started files
scp ./startup/sk8flair.service root@$TARGETHOST:/lib/systemd/system/sk8flair.service

# Deploy the render configuration
scp ./src/profiles/*.* root@$TARGETHOST:/home/root/sk8flair/profiles/

# Deploy the service script
scp ./bluetooth/sk8flair_service_d.py root@$TARGETHOST:/home/root/sk8flair/sk8flair_service_d
ssh root@$TARGETHOST 'chmod +x /home/root/sk8flair/sk8flair_service_d'

# Deploy the application last so as not to confuse the debugger
scp ./Debug/Sk8Flair root@$TARGETHOST:/home/root/sk8flair/Sk8Flair
ssh root@$TARGETHOST 'chmod +x /home/root/sk8flair/Sk8Flair'

# Deploy the test files
scp ./tests/*.* root@$TARGETHOST:/home/root/tests/

# Deploy the source code so we can debug the app
scp ./src/*.* root@$TARGETHOST:/home/root/src/


#ssh root@$TARGETHOST 'reboot'