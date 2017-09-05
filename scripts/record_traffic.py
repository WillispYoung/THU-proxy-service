# python3
# flow file format: 
#    time: m-d,h:m:s  e.g. 3-4,17:30:20
#    traffic: coutn of bits  e.g. 10000

import subprocess

proc = subprocess.Popen("iptables -L -v -n -c", stdout=subprocess.PIPE)
output = proc.stdout.read()

print output