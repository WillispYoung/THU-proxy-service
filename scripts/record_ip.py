# python3

import sys

port = sys.argv[1]
ip = sys.argv[2]
time = sys.argv[3]

f = open("/proxy/ip/" + port + ".ip")
f.write(time + "\n")
f.write(ip + "\n")
f.close()