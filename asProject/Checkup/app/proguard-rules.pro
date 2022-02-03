-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontpreverify
-ignorewarnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-allowaccessmodification
-repackageclasses ''
-verbose
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class android.support.** {*;}
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
#-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
-keep public class com.meetvr.aicamera.personal.util.ImageSelectUtils


-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    public *;
}

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
     public static final android.os.Parcelable$Creator *;
     public *;
     public static class *;
     private *;
     *** get*();
     void set*(***);
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    public *;
    private *;
    *** get*();
    void set*(***);
}
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
-keepclassmembers class com.ljd.example.JSInterface {
    <methods>;
}

-dontwarn com.suchengkeji.android.confusiondemo.md.**
-keep class com.suchengkeji.android.confusiondemo.md.** { *; }
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**


#
# ----------------------------- 第三方 -----------------------------
#
-dontwarn com.orhanobut.logger.**
-keep class com.orhanobut.logger.**{*;}
-keep interface com.orhanobut.logger.**{*;}

-dontwarn com.google.gson.**
-keep class com.google.gson.**
-keep interface com.google.gson.**{*;}

-dontwarn com.moxiang.common.**
-keep class com.moxiang.common.** { *;}
#=====================================项目中的特殊类=============================================
#美摄
-keep class com.cdv.** { *;}
-keep class com.meicam.** { *;}

#ijk播放器
-keep class com.dueeeke.videoplayer.** { *;}
-keep class tv.danmaku.ijk.media.player.** { *;}
-keep class org.greenrobot.** { *;}



#EventBus,需要特别注意
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
<init>(java.lang.Throwable);
}

-keep class de.greenrobot.event.** {*;}
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}

#不需要混淆的类
-keep public class com.meetvr.aicamera.dymode.managers.EffectPlatform{
   public *;
   private *;
}

-keep class com.xiaoyi.yicamera.mp4recorder2.** { *;}
-keep class com.meetvr.aicamera.cameraclip.** { *;}
-keep class com.ss.android.ugc.effectmanager.** { *;}
-keep class * extends com.framwork.base.view.MOBaseActivity
-keep public class com.meetvr.aicamera.utils.Constants
-keep public class com.meetvr.aicamera.profession.Constant{
        *;
}

#友盟
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class **.R$*{
   public static final int *;
}

-dontwarn com.hjq.permissions.**