# Patch Notes V8

- Added host-side `ShadowWebViewActivity` to proxy problematic in-app WebView activities.
- Redirects explicit launches of `KRWebViewActivity` / `WebViewActivity` to host-side shell before virtualization.
- Collapses `:WebView` process names back to main package process for activities/services/providers matching WebView patterns.
- Host-side shell scans original intent extras for URL, enables JS/DOM storage, and provides an external-browser fallback.
