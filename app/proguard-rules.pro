# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/xiaoxuli/Library/Android/sdk/tools/proguard/proguard-android.txt
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
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keepattributes *Annotation*

-keep class com.zleida.entity.** { *; }
-dontwarn com.zleida.entity.**

-keep class com.baidu.** { *; }
-dontwarn com.baidu.**

-keep class vi.com.gdi.bgl.android.**{*;}
-dontwarn vi.com.gdi.bgl.android.**

-keepattributes Signature
-keepattributes InnerClasses
-keepattributes InnerClasses,EnclosingMethod


-keepclasseswithmembernames class * {
    native <methods>;
}

-keep public class com.zleida.R$*{
    public static final int *;
}

-dontnote org.apache.http.**