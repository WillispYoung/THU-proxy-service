# python3

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
    traffic = r[7]

    w = open("/proxy/flow/" + str(port) + "/flow")
    now = time.strftime("%m-%d,%H:%M:%S", time.localtime(time.time()))


    


cur.close()
db.close()