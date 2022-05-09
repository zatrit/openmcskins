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
* From [Cloaks+](http://cloaksplus.com) capes system
* From TLauncher skins system
* Directly from *anywhere*

### How to configure this

This mod using a [Cloth config](https://www.curseforge.com/minecraft/mc-mods/cloth-config) for configuration, and you
can access OpenMCSkins options from **"Skin customization..."** options

The host list contains all hosts as strings, like ``{type}`` or ``{type}: {value}``

### How to edit config manually

The mod's configuration is located at **[minecraft game location]/config/openmcskins.toml**. You can edit it with any
text editor (e.g. Notepad++).

### Here are the possible options in the config:

### hosts

This option accepts a list of sources where the mod tries to load skins from.

###### Types of sources:

* MOJANG - official Mojang API
* SERVER - any server that implements the ely.by API (as value accepts URL)
* LOCAL - directory where the mod will look for skins
* ELYBY - ely.by API
* OPTIFINE - Optifine API
* LABYMOD - LabyMod API
* FIVEZIG - 5zig API
* COSMETICA - Cosmetica API (as value accepts NO_THIRD_PARTY or ALLOW_THIRD_PARTY)
* MINECRAFTCAPES - MinecraftCapes API
* CLOAKSPLUS - Cloaks+ API
* TLAUNCHER - TLaucnher API
* DIRECT - download skin or cape from url directly, by replacing ``{name}`` by player name
  and ``{id}`` by player UUID (as value accepts string like ``[SKIN | CAPE]:[URL]``)

### resolvingTimeout

Time in seconds allocated to search for skins in the specified sources

### fullErrorMessage

Print full error message on error of ``true``

### offlineMode

Download skins by names instead of UUID's if ``true``

### hashingAlgorithm

Algorithm, that using for generate skin file name

### ignoreAnimatedCapes

Ignore animated capes from custom server and MinecraftCapes if ``true``

### Config example:

```toml
resolvingTimeout = 5
fullErrorMessage = true
offlineMode = true
ignoreAnimatedCapes = false
hashingAlgorithm = "SHA384"

[[hosts]]
type = "COSMETICA"

[[hosts]]
type = "CLOAKSPLUS"

[[hosts]]
type = "MINECRAFTCAPES"

[[hosts]]
type = "SERVER"
value = "http://example.com/api"

[[hosts]]
type = "OPTIFINE"

[[hosts]]
type = "ELYBY"

[[hosts]]
type = "MOJANG"
```

## Why are there errors in README.md ? (if any)

Because I translated it with Google Translator because I don't speak English well
