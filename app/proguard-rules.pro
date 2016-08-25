# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/robert/Documents/Android SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}



########      JACK AND JILL
#Common options:
-include
-basedirectory
-injars
-outjars #// only 1 output jar supported
-libraryjars
-dontoptimize  #// required: Jack does not optimize
-dontpreverify #// required: Jack does not preverify
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-forceprocessing
-keep
-keepclassmembers
-keepclasseswithmembers
-keepnames
-keepclassmembernames
-keepclasseswithmembernames
-printseeds
#Shrinking options:
-dontshrink
#Obfuscation options:
-dontobfuscate
-printmapping
-applymapping
-obfuscationdictionary
-classobfuscationdictionary
-packageobfuscationdictionary
-useuniqueclassmembernames
-dontusemixedcaseclassnames
-keeppackagenames
-flattenpackagehierarchy
-repackageclasses
-keepattributes
-adaptclassstrings
############   END JACK AND JILL