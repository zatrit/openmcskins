## Here are all the config options:

### hosts

This option accepts a list of sources from which the mod tries to load skins.

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
* !direct [CAPE | SKIN]:[URL] - download skin or cape from url directly, by replacing ``{name}`` by player name
  and ``{id}`` by player UUID
* !tlauncher - TLauncher API
* !mantle - Mantle API

### resolvingTimeout

Time in seconds allocated to search for skins in the [specified sources](#hosts)

### fullErrorMessage

Print full error message on error of ``true``

### offlineMode

Download skins by names instead of UUID's if ``true``

### hashingAlgorithm

Algorithm, that using for generate skin file name

### ignoreAnimatedCapes

Ignore animated capes from custom server and MinecraftCapes if ``true``

### forceIcons

Force render player icon near name in player list if ``true``

### cosmetics

Render player Optifine/Cloaks+/Mantle cosmetics
(requires [CEM](https://www.curseforge.com/minecraft/mc-mods/custom-entity-models-cem)) if ``true``

### ears

Render player ears (requires [Manningham Mills](https://github.com/Chocohead/Fabric-ASM/releases/tag/v2.0)) if ``true``

### Config example:

```yaml
ignoreAnimatedCapes: false
hashingAlgorithm: SHA318
fullErrorMessage: true
resolvingTimeout: 5
offlineMode: false
forceIcons: true
cosmetics: true
ears: true
# DON'T USE TOO MANY HOSTS
hosts:
  - !direct 'CAPE:http://example.com/api/{name}?uuid={id}'
  - !server 'http://example.com/api'
  - !cosmetica 'NO_THIRD_PARTY'
  - !local 'C:\MySkins'
  - !minecraftcapes
  - !tlauncher
  - !optifine
  - !labymod
  - !fivezig
  - !mantle
  - !mojang
  - !elyby
```