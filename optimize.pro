-ignorewarnings
-renamesourcefileattribute
-adaptclassstrings
-repackageclasses
-allowaccessmodification
-mergeinterfacesaggressively
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-keepattributes *Annotations*
-optimizationpasses 5

-keep @net.zatrit.openmcskins.annotation.KeepClass class ** { *; }
-keepclassmembers class ** { @net.zatrit.openmcskins.annotation.KeepClassMember *; }

-keep @net.zatrit.openmcskins.annotation.KeepClass enum ** { *; }
-keepclassmembers enum ** { @net.zatrit.openmcskins.annotation.KeepClassMember *; }

-keep enum org.yaml.snakeyaml.nodes.NodeId { *; }

-libraryjars <java.home>/jmods/java.base.jmod

-printmapping build/mappings.txt