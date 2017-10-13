import socket
import sys
import os

port = sys.argv[1]
utype = sys.argv[2]

cmd = "addport@" + port + "," + utype
control = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
control.connect(("localhost", 4127))
control.send(bytes(cmd, encoding="utf-8"))
control.close()

os.popen("iptables -A OUTPUT -p tcp --sport " + port)
print("open port " + port + " with type " + utype + " done!")