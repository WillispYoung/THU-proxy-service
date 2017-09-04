# python3

import geoip2.database
import sys

reader = geoip2.database.Reader("GeoLite2-City.mmdb")
ip = sys.argv[1]

res = reader.city(ip)
print("City: " + res.subdivisions.most_specific.name + ", " + res.city.name)
