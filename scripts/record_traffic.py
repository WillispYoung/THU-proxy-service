# python3
# flow file format: 
#    time: m-d,h:m:s  e.g. 3-4,17:30:20
#    traffic: coutn of bits  e.g. 10000

import os

output = os.popen("iptables -L -v -n -x").read()
output = output.replace("\n", "\r\n")
f = open("/root/THU-proxy-service/log/traffic.log")
f.write(output)
f.close()