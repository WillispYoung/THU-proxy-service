import re
import os
import time

record = dict()

while True:
    now = time.strftime("%m-%d,%H:%M:%S", time.localtime(time.time()))
    hour = int(now.split(",")[1].split(":")[0])

    if hour % 2 != 0:
        time.sleep(1800)
        continue

    pattern = re.compile(r' +')
    output = os.popen("iptables -L -v -n -x").read()
    lines = output.split("\n")

    for line in lines:
        tup = pattern.split(line)
        if len(tup) == 10 or len(tup) == 11:
            try:
                port = int(tup[-1].split(":")[-1])
                if port in res:
                    record[port] += int(tup[1])
                else:
                    record[port] = int(tup[1])

                # w = open("/proxy/flow/" + str(port) + ".flow", "a")
                # w.write(now + "\n")
                # w.write(traffic + "\n")
                # print(str(port) + ": " + now + " " + traffic)
                # w.close()

            except ValueError:
                continue

    for p in record:
        w = open("/proxy/flow/" + str(p) + ".flow", "a")
        w.write(now + "\n")
        w.write(str(record[p]) + "\n")
        w.close()

    time.sleep(3600)