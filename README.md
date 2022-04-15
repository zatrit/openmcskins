# OpenMCCapes

## What is it?

This is a lightweight mod that allows skins and capes to be use in various ways.

### Where can this mod download skins and capes from?

* From [Official Mojang Skin System](https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape) via AuthLib
* From [ely.by](https://ely.by) API
* From [SimpleHTTP server](./server/server.py) with an API similar to ely.by
* From local storage

## How to configure this?

The mod's configuration is located at **[minecraft game location]/config/openmcskins.yml**.
You can edit it with any text editor (e.g. Notepad++).

### Here are the possible options in the config:

### hosts

This option accepts a list of sources where the mod tries to load skins from.

###### Types of sources:
* !mojang [SECURE | INSECURE] - official Mojang skin system, accepts SECURE and INSECURE values, it is recommended to use SECURE
* !server [IP] - any server that implements the ely.by API
* !elyby - ely.by skin system
* !local [Directory] - directory where the mod will look for skins

### resolvingTimeout

Time in seconds allocated to search for skins in the [specified sources](#hosts)

### Config example:

```yaml
hosts:
 - !server 'http://127.0.0.1:8080'
 - !elyby
 - !local C:\MySkins
 - !mojang 'SECURE'
resolvingTimeout: 5
```

## Why are there errors in README.md ? (if any)
Because I translated it with Google Translator because I don't speak English well