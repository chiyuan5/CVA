# PATCH NOTES V6

## WebView 子进程修复
- 对系统/开放包服务（尤其是 `com.android.webview`）的 `bindService/startService/stopService` 改为直通系统，不再错误走虚拟服务代理。
- 修复 Chromium/WebView 沙箱子进程 `ChildProcessConnection.start failed` 导致的白屏/一直加载。

## WebView 数据目录修复
- `WebView.setDataDirectorySuffix()` 改为使用更安全的后缀格式，避免带 `:` 的目录名。
- 在应用绑定阶段预创建 WebView 相关数据/缓存目录，减少 `simple_index_file` / `Code Cache` 初始化失败。
