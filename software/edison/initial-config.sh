#!/bin/bash

# Before running this script you need to flash a new build onto the edison board
# and then connect the board to a wifi connections using the command:
#     configure_edison --wifi

export TARGETHOST=192.168.1.9

ssh-keygen-R $TARGETHOST

echo "Initializing to target host ($TARGETHOST)"

cat ~/.ssh/id_rsa.pub | ssh root@$TARGETHOST 'mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys'

ssh root@$TARGETHOST 'echo "src/gz all http://repo.opkg.net/edison/repo/all" > /etc/opkg/base-feeds.conf'
ssh root@$TARGETHOST 'echo "src/gz edison http://repo.opkg.net/edison/repo/edison" > /etc/opkg/base-feeds.conf'
ssh root@$TARGETHOST 'echo "src/gz core2-32 http://repo.opkg.net/edison/repo/core2-32" > /etc/opkg/base-feeds.conf'

ssh root@$TARGETHOST 'echo "src mraa-upm  http://iotdk.intel.com/repos/1.1/intelgalactic" > /etc/opkg/mraa-upm.conf'

ssh root@$TARGETHOST 'opkg update'

ssh root@$TARGETHOST 'opkg install mraa'
ssh root@$TARGETHOST 'opkg install libxml2'

ssh root@$TARGETHOST 'rm /etc/localtime'
ssh root@$TARGETHOST 'ln -s /usr/share/zoneinfo/America/Los_Angeles /etc/localtime'

ssh root@$TARGETHOST 'ln -s /usr/lib/libmraa.so.1 /usr/lib/libmraa.so.0'

ssh-keygen -R $TARGETHOST


# ========================================== TIMEZONE CONFIGURATION ==========================================
# root@Edison:~# rm /etc/localtime
# root@Edison:~# ln -s /usr/share/zoneinfo/America/Los_Angeles /etc/localtime
# root@Edison:~# ls -l /etc/localtime


