-adaptclassstrings
-allowaccessmodification
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontusemixedcaseclassnames
-ignorewarnings
-keepattributes *Annotation*
-mergeinterfacesaggressively
-optimizationpasses 3
-overloadaggressively
-renamesourcefileattribute
-repackageclasses

-keep @org.spongepowered.asm.mixin.Mixin class ** { *; }
-keep @org.spongepowered.asm.mixin.Mixin interface ** { *; }
-keep @org.spongepowered.asm.mixin.Mixin enum ** { *; }

-keep @net.zatrit.openmcskins.annotation.KeepClass class ** { *; }
-keepclassmembers class ** { @net.zatrit.openmcskins.annotation.KeepClassMember *; }

-keepclassmembers enum net.zatrit.openmcskins.** { *; }

-libraryjars <java.home>/jmods/java.base.jmod

-printmapping build/mappings.txt