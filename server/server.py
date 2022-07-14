# https://www.afternerd.com/blog/python-http-server/
# Partial implementation of the skins system
# API from https://docs.ely.by/en/skins-system.html
from genericpath import exists
import http.server
import os
import socketserver
import json
import zlib
import re

PORT = 8080
REDIRECTS = {}


def findPath(textureType, redirectHash, extension):
    keys = list(REDIRECTS[textureType].keys())
    values = list(REDIRECTS[textureType].values())
    redirect = keys[values.index(redirectHash)]
    return f"textures/{textureType}/{redirect}{extension or ".png"}"


# https://stackoverflow.com/a/1761615/12245612
# https://stackoverflow.com/a/68883969/12245612
def crc32(fileName):
    prev = 0
    for eachLine in open(fileName, "rb"):
        prev = zlib.crc32(eachLine, prev)
    return "%X" % (prev & 0xFFFFFFFF)


def generateRedirect(textureType, name, extension):
    file = f'textures/{textureType}/{name}{extension}'
    if textureType not in REDIRECTS:
        REDIRECTS[textureType] = {}
    if name in REDIRECTS[textureType]:
        redirectHash = REDIRECTS[textureType][name]
        if redirectHash == crc32(file):
            return f"/redirect/{textureType}/{redirectHash}{extension}"
        del REDIRECTS[textureType][name]

    if not exists(file):
        return None

    redirectHash = crc32(file)
    if textureType not in REDIRECTS:
        REDIRECTS[textureType] = {}
    REDIRECTS[textureType][name] = redirectHash
    return f"/redirect/{textureType}/{redirectHash}{extension}"


class PlayerData(dict):
    def __init__(self, name, host):
        super().__init__()
        for i in os.listdir("textures/"):
            for j in os.listdir(f"textures/{i}"):
                found = re.findall(r"([^\"]*)(\.\w*)", j)[0]
                if found[0] == name:
                    self[i.upper()] = {}
                    redirect = generateRedirect(i, name, found[1])
                    self[i.upper()]["url"] = f"http://{host}{redirect}"

                    metadataFile = f'{os.getcwd()}/metadata/{i}/{name}.json'
                    if exists(metadataFile):
                        with open(metadataFile, 'rb') as file:
                            self[i.upper()]["metadata"] = json.load(file)


# https://stackoverflow.com/a/52531444/12245612
class Handler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        trimmedPath = self.path.lstrip("/").rstrip("/")
        found = re.findall(r"redirect/([^\"]*)/([^\"]*)(\.\w*)", trimmedPath)
        if trimmedPath.startswith("textures/"):
            name = trimmedPath.removeprefix("textures/")
            name = re.findall(r"(\w*)", name)[0]
            self.send_response(200)
            self.send_header("Content-type", "application/json")
            self.end_headers()
            jsonStr = json.dumps(PlayerData(name, self.headers["Host"]))
            self.wfile.write(bytes(jsonStr, "utf-8"))
        elif len(found) > 0:
            self.send_response(200)
            self.send_header("Content-type", "image/png")
            self.end_headers()
            with open(findPath(*found[0]), 'rb') as file:
                self.wfile.write(file.read())
        else:
            self.send_error(404, "Not found")


with socketserver.TCPServer(("", PORT), Handler) as httpd:
    print("Serving at port", PORT)
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        httpd.shutdown()
        print("Closing server at port", PORT)
