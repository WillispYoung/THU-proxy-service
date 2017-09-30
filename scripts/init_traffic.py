# python3
# flow file format: 
#    time: m-d,h:m:s  e.g. 03-04,17:30:20
#    traffic: count of bytes  e.g. 10000

import os
import re
import pymysql
import socket
import time

db = pymysql.connect(host="58.205.208.72", port=8779,
                     user="root", password="thuproxy",
                     database="thuproxy")
cur = db.cursor()
cur.execute("select * from thuproxy_proxyaccount")

for r in cur:
    user_type = r[2]
    expire_time = r[4]
    port = r[5]
    traffic = int(r[6] * 1024 * 1024)

    w = open("/proxy/flow/" + str(port) + ".flow", "a")
    now = time.strftime("%m-%d,%H:%M:%S", time.localtime(time.time()))
    
    w.write(now + "\n")
    w.write(str(traffic) + "\n")
    w.close()

cur.close()
db.close()