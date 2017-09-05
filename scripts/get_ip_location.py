# python3

import geoip2.database
import sys

reader = geoip2.database.Reader("GeoLite2-City.mmdb")
ip = sys.argv[1]
res = reader.city(ip)

if 'zh-CN' in res.city.names:
    print(res.city.names['zh-CN'])
else:
    print(res.city.name)
