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
-overloadaggressively
-dontusemixedcaseclassnames

-keep @org.spongepowered.asm.mixin.Mixin class ** { *; }
-keep @org.spongepowered.asm.mixin.Mixin interface ** { *; }

-keep @net.zatrit.openmcskins.annotation.KeepClass class ** { *; }
-keepclassmembers class ** { @net.zatrit.openmcskins.annotation.KeepClassMember *; }

-keepclassmembers enum net.zatrit.openmcskins.** { public *; }
-keep enum org.yaml.snakeyaml.nodes.NodeId { *; }

-libraryjars <java.home>/jmods/java.base.jmod

-printmapping build/mappings.txt