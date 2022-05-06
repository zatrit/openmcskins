# OpenMCSkins

<a href="https://www.curseforge.com/minecraft/mc-mods/openmcskins"><img src="https://cf.way2muchnoise.eu/versions/620015_latest.svg"></a>

## What is it?

This is a mod that allows skins and capes to be use in various ways.

### Dependencies

* [Cloth config](https://www.curseforge.com/minecraft/mc-mods/cloth-config)

### Where can this mod download skins and capes from?

* From [Official Mojang Skin System](https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape) via AuthLib
* From [ely.by API](https://docs.ely.by/en/skins-system.html)
* From [SimpleHTTP server](./server/) with an API similar to ely.by
* From local storage
* From [Optifine](https://optifine.net/home) cape system
* From [LabyMod](https://www.labymod.net/en) capes system
* From [5Zig](https://5zigreborn.eu/) capes system
* From [Cosmetica](https://cosmetica.cc/) capes system
* From [MinecraftCapes](https://minecraftcapes.net/) capes system (no ears yet)

### How to configure this

This mod using a [Cloth config](https://www.curseforge.com/minecraft/mc-mods/cloth-config) for configuration, and you
can access OpenMCSkins options from **"Skin customization..."** options

### How to edit config manually

The mod's configuration is located at **[minecraft game location]/config/openmcskins.yml**. You can edit it with any
text editor (e.g. Notepad++).

### Here are the possible options in the config:

### hosts

This option accepts a list of sources where the mod tries to load skins from.

###### Types of sources:

* !mojang - official Mojang API
* !server [IP] - any server that implements the ely.by API
* !local [Directory] - directory where the mod will look for skins
* !elyby - ely.by API
* !optifine - Optifine API
* !labymod - LabyMod API
* !fivezig - 5zig API
* !cosmetica [NO_THIRD_PARTY | ALLOW_THIRD_PARTY] - Cosmetica API
* !minecraftcapes - MinecraftCapes API

### resolvingTimeout

Time in seconds allocated to search for skins in the [specified sources](#hosts)

### fullErrorMessage

Print full error message on error of ``true``

### offlineMode

Download skins by names instead of UUID's if ``true``

### hashingAlgorithm

Algorithm, that using for generate skin file name

### Config example:

```yaml
hosts:
  - !minecraftcapes
  - !cosmetica 'NO_THIRD_PARTY'
  - !labymod
  - !fivezig
  - !optifine
  - !server 'http://127.0.0.1:8080'
  - !elyby
  - !local C:\MySkins
  - !mojang
resolvingTimeout: 5
offlineMode: false
fullErrorMessage: true
hashingAlgorithm: SHA318
```

## Why are there errors in README.md ? (if any)

Because I translated it with Google Translator because I don't speak English well
