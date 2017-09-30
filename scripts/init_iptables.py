# python3

import pymysql
import time
import os

db = pymysql.connect(host="58.205.208.72", port=8779,
                     user="root", password="thuproxy",
                     database="thuproxy")
cur = db.cursor()
cur.execute("select * from thuproxy_proxyaccount")

for r in cur:
    user_type = r[2]
    expire_time = r[4]
    port = r[5]

    # no valid user
    if user_type == 0:
        continue

    expire = int(time.mktime(time.strptime(str(expire_time), "%Y-%m-%d")))
    now = int(time.time())

    if expire < now:
        continue

    os.system("iptables -A INPUT -p tcp --dport " + str(port))
    os.system("iptables -A OUTPUT -p tcp --sport " + str(port))

cur.close()
db.close()
