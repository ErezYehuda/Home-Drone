#!/bin/bash
. /home/pi/.profile 
workon cv

ssh -c blowfish -fN -R 8086:localhost:8081 root@45.79.173.164 


python ./RP.py