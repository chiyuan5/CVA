# ============================================================
# ChiyuanVA — consumer-rules.pro
# 随 AAR 发布给消费方（宿主 App）的 ProGuard 规则。
# 当宿主 App 开启混淆时，这些规则会自动应用，
# 确保宿主继承/调用 Bcore 的代码不被破坏。
# ============================================================

# ── 公开 API — 宿主继承/调用，保留完整类名和方法签名 ────────────
# 关键：ClientConfiguration 是宿主必须 override 的抽象基类，
# 若不保留则宿主子类的 override 方法在运行时找不到父类方法。
-keep public abstract class com.chiyuan.va.app.configuration.ClientConfiguration {
    public *;
    protected *;
}
-keep public class com.chiyuan.va.app.configuration.AppLifecycleCallback {
    public *;
    protected *;
}
-keep public class com.chiyuan.va.app.configuration.** { public *; protected *; }

# ChiyuanVACore 主入口
-keep public class com.chiyuan.va.ChiyuanVACore { public *; protected *; }

# 公开接口
-keep public interface com.chiyuan.va.** { *; }

# 实体类（Parcelable）
-keep class com.chiyuan.va.entity.** { *; }

# 反射框架
-keep class com.chiyuan.va.reflection.** { *; }
-keep @com.chiyuan.va.reflection.annotation.BClass class * { *; }
-keep @com.chiyuan.va.reflection.annotation.BClassName class * { *; }

# 依赖库
-keep class mirror.** { *; }
-keep class black.**  { *; }
-keep class android.** { *; }
-keep class com.android.** { *; }
