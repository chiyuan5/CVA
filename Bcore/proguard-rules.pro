# ============================================================
# ChiyuanVA ProGuard 规则
# ★ 反检测：移除对 com.chiyuan.va.proxy.** 的 -keep，
#   让壳组件类名被混淆，进一步消除静态特征。
#   仅保留反射必须访问的类和注解处理器相关类。
# ============================================================

# 反射框架 — 必须保留
-keep class com.chiyuan.va.reflection.** { *; }
-keep @com.chiyuan.va.reflection.annotation.BClass class * { *; }
-keep @com.chiyuan.va.reflection.annotation.BClassName class * { *; }
-keep @com.chiyuan.va.reflection.annotation.BClassNameNotProcess class * { *; }
-keepclasseswithmembernames class * {
    @com.chiyuan.va.reflection.annotation.BField.*         <methods>;
    @com.chiyuan.va.reflection.annotation.BFieldNotProcess.*     <methods>;
    @com.chiyuan.va.reflection.annotation.BFieldSetNotProcess.*  <methods>;
    @com.chiyuan.va.reflection.annotation.BFieldCheckNotProcess.* <methods>;
    @com.chiyuan.va.reflection.annotation.BMethod.*        <methods>;
    @com.chiyuan.va.reflection.annotation.BStaticField.*   <methods>;
    @com.chiyuan.va.reflection.annotation.BStaticMethod.*  <methods>;
    @com.chiyuan.va.reflection.annotation.BMethodCheckNotProcess.* <methods>;
    @com.chiyuan.va.reflection.annotation.BConstructor.*   <methods>;
    @com.chiyuan.va.reflection.annotation.BConstructorNotProcess.* <methods>;
}

# AIDL Stub — Binder 框架通过类名反射查找，必须保留
-keep class com.chiyuan.va.core.system.SystemCallProvider { *; }
-keep class com.chiyuan.va.fake.provider.FileProvider { *; }

# AppConfig / 实体类 — Parcelable，必须保留字段名
-keep class com.chiyuan.va.entity.** { *; }

# 公开 API — 宿主通过 API 调用，必须保留
-keep class com.chiyuan.va.ChiyuanVACore { public *; }
-keep interface com.chiyuan.va.** { *; }

# Android 系统组件 — 必须保留（系统通过类名实例化）
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.job.JobService

# ★ 不再 -keep com.chiyuan.va.proxy.**
#   壳 Activity/Service/Provider 的类名由 ProxyManifest 动态生成，
#   混淆后 manifest 中 android:name 对应混淆后的类名，
#   在构建脚本中用 aapt + mapping.txt 同步替换即可。
#
# 注意：若构建流水线尚未集成 manifest 自动重写，可临时保留：
# -keep class com.chiyuan.va.proxy.** { *; }

# mirror / black 反射辅助类
-keep class mirror.** { *; }
-keep class black.**  { *; }
-keep class android.** { *; }
-keep class com.android.** { *; }
