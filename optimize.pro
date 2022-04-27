-ignorewarnings
-renamesourcefileattribute
-adaptclassstrings
-repackageclasses
-allowaccessmodification
-mergeinterfacesaggressively
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-keepattributes *Annotations*

-keep @net.zatrit.openmcskins.annotation.KeepClass class ** { *; }
-keepclassmembers class ** { @net.zatrit.openmcskins.annotation.DontObfuscate *; }

-keep @net.zatrit.openmcskins.annotation.KeepClass enum ** { *; }
-keepclassmembers enum ** { @net.zatrit.openmcskins.annotation.DontObfuscate *; }

-keep enum org.yaml.snakeyaml.nodes.NodeId { *; }

-libraryjars <java.home>/jmods/java.base.jmod

-printmapping build/mappings.txt