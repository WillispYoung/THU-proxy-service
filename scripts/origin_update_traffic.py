# python3
# this script is meant to keep running forever.

import pymysql
import datetime
import time
import subprocess
import glob
import os
import re

files = glob.glob("/proxy/flow/*.flow")
record = dict()

for f in files:
    port = int(f.split("/")[-1].split(".")[0])
    last_traffic = os.popen("tail -n 1 " + f).read()
    record[port] = int(last_traffic)

while True:
    now = time.strftime("%m-%d,%H:%M:%S", time.localtime(time.time()))
    hour = int(now.split(",")[1].split(":")[0])

    # update roughly every 2 hours
    if hour % 2 != 0:
        time.sleep(1800)
        continue

    output = os.popen("iptables -L -v -n -x").read()
    lines = output.split("\n")

    i = 0
    p = re.compile(r' +')
    count = len(lines)
    while i < count:
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
    
    for p in record:
        w = open("/proxy/flow/" + str(p) + ".flow", "a")
        w.write(now + "\n")
        w.write(str(record[p]) + "\n")
        w.close()

    os.popen("iptables -Z INPUT")
    os.popen("iptables -Z OUTPUT")

    # sleep to next hour
    time.sleep(3600)