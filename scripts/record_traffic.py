# python3
# flow file format: 
#    time: m-d,h:m:s  e.g. 03-04,17:30:20
#    traffic: count of bytes  e.g. 10000

import os
import re

output = os.popen("iptables -L -v -n -x").read()
output = output.replace("\n", "\r\n")
# f = open("/root/THU-proxy-service/log/traffic.log", "w")
# f.write(output)
# f.close()

# output = open("D:/CodeSpace/Python/traffic.log").read()
lines = output.split("\n")
record = dict()
for l in lines:
    print(l)

return

i = 0
p = re.compile(r' +')
count = len(lines)
while i < count:
    print(lines[i])
    if lines[i].startswith("Chain"):
        i += 2
        while i < count:
            l = lines[i]
            if len(l) == 0:
                break
            t = p.split(l)
            traffic = int(t[2])
            port = int(t[-1].split(":")[-1])
            if port in record:
                record[port] += traffic
            else:
                record[port] = traffic
            i += 1
    i += 1

for k in record:
    print(k, record[k])