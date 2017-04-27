-optimizationpasses 5
-useuniqueclassmembernames
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!class/unboxing/enum
-printmapping proguard.map
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

#do not proguard android-support-v4.jar
#-libraryjars ./libs/android-support-v4.jar
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-dontnote android.support.**

#do not proguard org.apache.http.legacy.jar
-dontwarn android.net.compatibility.**
-dontwarn android.net.http.**
-dontwarn com.android.internal.http.multipart.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**
-dontwarn android.databinding.**
-keep class android.net.compatibility.**{*;}
-keep class android.net.http.**{*;}
-keep class com.android.internal.http.multipart.**{*;}
-keep class org.apache.commons.**{*;}
-keep class org.apache.http.**{*;}
-dontnote org.apache.http.**
-dontnote android.net.http.**
-dontnote org.apache.commons.**


#android class
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.support.v4.app.FragmentActivity
-keep public class * extends android.support.v4.app.Fragment
-keep public class android.support.**{*;}

#project file

-keepclasseswithmembers public class com.bemetoy.bp.plugin.car.ui.**{*;}
-keepattributes Signature
-keep class com.unity3d.** { *; }
-keep class org.fmod.** { *; }
-keep class bitter.jnibridge.** {*;}


#Fresco
-keep class com.facebook.drawee.** {*;}
-keep class com.facebook.cache.** {*;}
-keep class com.facebook.common.** {*;}
-keep class com.facebook.datasource.** {*;}
-keep class com.facebook.imagepipeline.** {*;}
-keep class com.facebook.imageutils.** {*;}
-dontwarn com.facebook.**

#百度
-keep class com.baidu.** {*;}
-keep class vi.com.gdi.bgl.android.**{*;}
-keep class com.baidu.location.**{*;}

#腾讯bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#Volley
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.**
-dontwarn com.android.volley.toolbox.**

#PB
-keep class com.google.protobuf.** {*;}
-keep public class * extends com.google.protobuf.** {*;}
-keep class com.bemetoy.bp.autogen.protocol.** {*;}

#存储
-keep public class com.bemetoy.bp.autogen.** {*;}

#Customer View.
-keepclasseswithmembers public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#Customer View.
-keepclassmembernames public class * extends java.lang.Object {
    public static final String TAG;
    public final static String TAG;
}

#不混淆JNI
-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
