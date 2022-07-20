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


def find_path(texture_type, redirect_hash, extension):
    keys = list(REDIRECTS[texture_type].keys())
    values = list(REDIRECTS[texture_type].values())
    redirect = keys[values.index(redirect_hash)]
    return f"textures/{texture_type}/{redirect}{extension or '.png'}"


# https://stackoverflow.com/a/1761615/12245612
# https://stackoverflow.com/a/68883969/12245612
def crc32(file_name):
    prev = 0
    for eachLine in open(file_name, "rb"):
        prev = zlib.crc32(eachLine, prev)
    return "%X" % (prev & 0xFFFFFFFF)


def generate_redirect(texture_type, name, extension):
    file = f'textures/{texture_type}/{name}{extension}'
    if texture_type not in REDIRECTS:
        REDIRECTS[texture_type] = {}
    if name in REDIRECTS[texture_type]:
        redirectHash = REDIRECTS[texture_type][name]
        if redirectHash == crc32(file):
            return f"/redirect/{texture_type}/{redirectHash}{extension}"
        del REDIRECTS[texture_type][name]

    if not exists(file):
        return None

    redirectHash = crc32(file)
    if texture_type not in REDIRECTS:
        REDIRECTS[texture_type] = {}
    REDIRECTS[texture_type][name] = redirectHash
    return f"/redirect/{texture_type}/{redirectHash}{extension}"


def create_player_data(name, host) -> dict:
    result = {}

    for i in os.listdir("textures/"):
        for j in os.listdir(f"textures/{i}"):
            found = re.findall(r"([^\"]*)(\.\w*)", j)[0]
            if found[0] == name:
                result[i.upper()] = {}
                redirect = generate_redirect(i, name, found[1])
                result[i.upper()]["url"] = f"http://{host}{redirect}"

                metadataFile = f'{os.getcwd()}/metadata/{i}/{name}.json'
                if exists(metadataFile):
                    with open(metadataFile, 'rb') as file:
                        result[i.upper()]["metadata"] = json.load(file)

    return result


# https://stackoverflow.com/a/52531444/12245612
class Handler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        strippedPath = self.path.lstrip("/").rstrip("/")
        redirectFound = re.findall(r"redirect/([^\"]*)/([^\"]*)(\.\w*)", strippedPath)

        if strippedPath.startswith("textures/"):
            name = strippedPath.removeprefix("textures/")
            name = re.findall(r"(\w*)", name)[0]
            self.send_response(200)
            self.send_header("Content-type", "application/json")
            self.end_headers()
            jsonStr = json.dumps(create_player_data(name, self.headers["Host"]))
            self.wfile.write(bytes(jsonStr, "utf-8"))
        elif redirectFound:
            self.send_response(200)
            self.send_header("Content-type", "image/png")
            self.end_headers()
            with open(find_path(*redirectFound[0]), 'rb') as file:
                self.wfile.write(file.read())
        else:
            self.send_error(404, "Not found")


def main(root_dir=os.getcwd()):
    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        print("Serving at port", PORT)
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            httpd.shutdown()
            print("Closing server at port", PORT)


if __name__ == "__main__":
    main()
