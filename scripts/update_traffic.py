import re
import os
import time
import pymysql
import socket

# 首先清空各个端口的流量
os.popen("iptables -Z INPUT")
os.popen("iptables -Z OUTPUT")

while True:
    # 每4小时执行一次操作
    now = time.strftime("%m-%d,%H:%M:%S", time.localtime(time.time()))
    hour = int(now.split(",")[1].split(":")[0])
    day = int(now.split(",")[0].split("-")[1])

    if hour % 4 != 0:
        time.sleep(1800)
        continue
    
    # 从数据库读取之前的结果
    record = dict()
    db = pymysql.connect(host="58.205.208.72", port=8779,
                         user="root", password="thuproxy",
                         database="thuproxy")
    cur = db.cursor()
    cur.execute("select * from thuproxy_proxyaccount")

    for r in cur:
        port = r[5]
        traffic = int(r[6] * 1024 * 1024)
        record[port] = traffic

    cur.close()
    db.close()
    print("fetch port traffic from database " + now + "!")

    # 更新流量
    pattern = re.compile(r' +')
    output = os.popen("iptables -L -v -n -x").read()
    lines = output.split("\n")

    for line in lines:
        tup = pattern.split(line)   
        if len(tup) == 10 or len(tup) == 11:
            try:
                port = int(tup[-1].split(":")[-1])
                if port in record:
                    record[port] += int(tup[1])
                else:
                    record[port] = int(tup[1])

            except ValueError:
                continue

    # 将结果输出到文件中
    for p in record:
        w = open("/proxy/flow/" + str(p) + ".flow", "a")
        w.write(now + "\n")
        w.write(str(record[p]) + "\n")
        w.close()

    print("output port traffic finished!")

    os.popen("iptables -Z INPUT")
    os.popen("iptables -Z OUTPUT")
    time.sleep(14400)