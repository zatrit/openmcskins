# OpenMCSkins contribution guide

Hello, this is a guide to contributions

## Tips:

* All supported services are located in [Hosts](./src/main/java/net/zatrit/openmcskins/Hosts.java)
* If you want to add a new skins/capes service to this
  mod and its url is https://example.com/api/capes/[PlayerName].png,
  you can use [DirectResolver](./src/main/java/net/zatrit/openmcskins/resolvers/DirectResolver.java), like so:

```java
    EXAMPLE(d->new DirectResolver("https://example.com/api/capes/{name}.png",MinecraftProfileTexture.Type.CAPE))
```
