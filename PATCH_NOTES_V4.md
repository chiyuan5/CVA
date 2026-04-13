# PATCH NOTES V4

本次修改重点：

1. 将项目内兼容层 `thirdparty/free-reflection` 从旧的 `setHiddenApiExemptions` 反射实现切换为 `AndroidHiddenApiBypass`。
2. 在 `app/build.gradle` 增加 `dependenciesInfo` 关闭配置。
3. 为 `HCallbackProxy` 增加 `ActivityThread$H` 消息码读取 fallback，避免隐藏字段读取失败或返回空值时直接崩溃。
4. 为 `getTaskForActivity()` 增加容错，避免任务栈 ID 获取异常导致分身子进程直接退出。

关键改动文件：
- `thirdparty/free-reflection/build.gradle`
- `thirdparty/free-reflection/src/main/java/me/weishu/reflection/Reflection.java`
- `app/build.gradle`
- `Bcore/src/main/java/top/niunaijun/blackbox/fake/service/HCallbackProxy.java`
