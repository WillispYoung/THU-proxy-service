# python3
# flow file format: 
#    time: m-d,h:m:s  e.g. 3-4,17:30:20
#    traffic: count of bytes  e.g. 10000

import os

# output = os.popen("iptables -L -v -n -x").read()
# output = output.replace("\n", "\r\n")
# f = open("/root/THU-proxy-service/log/traffic.log", "w")
# f.write(output)
# f.close()

output = open("D:/CodeSpace/Python/traffic.log").read()
lines = output.split("\n")
record = dict()

i = 0
count = len(lines)
while i < count:
    if lines[i].startswith("Chain"):
        i += 2
        while i < count:
            l = lines[i]
            if len(l) == 0:
            	break
            t = l.split(" ")
            print(t[1], t[9])
            i += 1