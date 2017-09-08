import socket
import sys

port = sys.argv[1]
utype = sys.argv[2]

cmd = "addport@" + port + "," + utype
control = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
control.connect(("localhost", 4127))
control.send(bytes(cmd, encoding="utf-8"))
control.close()