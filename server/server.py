# https://www.afternerd.com/blog/python-http-server/
# Partial implementation of the skins system API from https://docs.ely.by/en/skins-system.html
from genericpath import exists
import http.server
import os
import socketserver
import json
import zlib
import re

PORT = 8080
REDIRECTS = {}

def findPath(type, hash, extension):
    keys = list(REDIRECTS[type].keys())
    values = list(REDIRECTS[type].values())
    redirect = keys[values.index(hash)]
    if len(extension) == 0:
        extension = ".png"
    return f"textures/{type}/{redirect}{extension}"

# https://stackoverflow.com/a/1761615/12245612
# https://stackoverflow.com/a/68883969/12245612
def crc32(fileName):
    prev = 0
    for eachLine in open(fileName,"rb"):
        prev = zlib.crc32(eachLine, prev)
    return "%X"%(prev & 0xFFFFFFFF)

def generateRedirect(type, name, extension):
    file = f'textures/{type}/{name}{extension}'
    if type not in REDIRECTS:
        REDIRECTS[type] = {}
    if name in REDIRECTS[type]:
        hash = REDIRECTS[type][name]
        if hash == crc32(file):
            return f"/redirect/{type}/{hash}{extension}"
        else:
            del REDIRECTS[type][name]

    if not exists(file):
        return None

    hash = crc32(file)
    if type not in REDIRECTS:
        REDIRECTS[type] = {}
    REDIRECTS[type][name] = hash
    return f"/redirect/{type}/{hash}{extension}"

class PlayerData(dict):
    def __init__(self, name, host):
        for i in os.listdir("textures/"):
            for j in os.listdir(f"textures/{i}"):
                found = re.findall("([^\"]*)(\.\w*)", j)[0]
                if found[0] == name:
                    self[i.upper()] = {}
                    redirect = generateRedirect(i, name, found[1])
                    self[i.upper()]["url"] = f"http://{host}{redirect}"

                    metadataFile = f'{os.getcwd()}/metadata/{i}/{name}.json'
                    if exists(metadataFile):
                        self[i.upper()]["metadata"] = json.load(open(metadataFile, 'rb'))

# https://stackoverflow.com/a/52531444/12245612
class Handler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        trimmedPath = self.path.lstrip("/").rstrip("/")
        found = re.findall("redirect/([^\"]*)/([^\"]*)(\.\w*)", trimmedPath)
        if trimmedPath.startswith("textures/"):
            name = trimmedPath.removeprefix("textures/")
            name = re.findall("(\w*)", name)[0]
            self.send_response(200)
            self.send_header("Content-type", "application/json")
            self.end_headers()
            self.wfile.write(bytes(json.dumps(PlayerData(name, self.headers["Host"])), "utf-8"))
        elif len(found) > 0:
            self.send_response(200)
            self.send_header("Content-type", "image/png")
            self.end_headers()
            self.wfile.write(open(findPath(*found[0]), 'rb').read())

with socketserver.TCPServer(("", PORT), Handler) as httpd:
    print("Serving at port", PORT)
    try:
        httpd.serve_forever()
    except(KeyboardInterrupt):
        httpd.shutdown()
        print("Closing server at port", PORT)