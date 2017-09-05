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

    # no valid user
    if user_type == 0:
        continue

    expire = int(time.mktime(time.strptime(str(expire_time), "%Y-%m-%d")))
    now = int(time.time())

    if expire < now:
        continue

    cmd = "addport@" + str(port) + "," + str(user_type)
    control = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    control.connect(("localhost", 4127))
    control.send(bytes(cmd, encoding="utf-8"))
    control.close()


cur.close()
db.close()