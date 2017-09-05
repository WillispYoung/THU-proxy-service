# python3
# flow file format: 
#    time: m-d,h:m:s  e.g. 3-4,17:30:20
#    traffic: coutn of bits  e.g. 10000

import subprocess

proc = subprocess.Popen("iptables -L -v -n -x", stdout=subprocess.PIPE, shell=True)
output = proc.stdout.read()

f = open("/root/THU-proxy-service/log/traffic.log")
f.write(str(output))
f.close()