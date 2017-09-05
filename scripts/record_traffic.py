# python3
# flow file format: 
#    time: m-d,h:m:s  e.g. 3-4,17:30:20
#    traffic: coutn of bits  e.g. 10000

import subprocess

proc = subprocess.Popen("iptables -L -v -n -x", stdout=subprocess.PIPE, shell=True)
output = proc.stdout.read()
output = output.replace("\n", "\r\n")

print(output)