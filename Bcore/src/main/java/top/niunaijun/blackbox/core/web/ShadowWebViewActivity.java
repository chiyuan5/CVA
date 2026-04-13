package top.niunaijun.blackbox.core.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.niunaijun.blackbox.R;

public class ShadowWebViewActivity extends AppCompatActivity {
    public static final String EXTRA_ORIGINAL_INTENT = "_B_|_shadow_web_original_intent_";
    public static final String EXTRA_TARGET_PACKAGE = "_B_|_shadow_web_target_pkg_";
    public static final String EXTRA_TARGET_CLASS = "_B_|_shadow_web_target_cls_";
    private static final String TAG = "ShadowWebViewActivity";

    private WebView webView;
    private ProgressBar progressBar;
    private TextView statusView;
    private Button openExternalButton;
    private Button closeButton;
    private String targetUrl;

    public static Intent createIntent(String hostPackage, @Nullable Intent original) {
        Intent intent = new Intent();
        intent.setClassName(hostPackage, ShadowWebViewActivity.class.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_ORIGINAL_INTENT, original);
        if (original != null && original.getComponent() != null) {
            intent.putExtra(EXTRA_TARGET_PACKAGE, original.getComponent().getPackageName());
            intent.putExtra(EXTRA_TARGET_CLASS, original.getComponent().getClassName());
        }
        return intent;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shadow_webview_core);

        webView = findViewById(R.id.shadow_webview);
        progressBar = findViewById(R.id.shadow_web_progress);
        statusView = findViewById(R.id.shadow_web_status);
        openExternalButton = findViewById(R.id.shadow_web_open_external);
        closeButton = findViewById(R.id.shadow_web_close);

        final Intent originalIntent = getParcelableIntentExtra(getIntent(), EXTRA_ORIGINAL_INTENT);
        targetUrl = UrlExtractor.extract(originalIntent);

        if (getSupportActionBar() != null) {
            String title = originalIntent != null && originalIntent.getComponent() != null
                    ? originalIntent.getComponent().getShortClassName() : "Web";
            getSupportActionBar().setTitle(title);
        }

        openExternalButton.setOnClickListener(v -> {
            if (targetUrl != null) {
                openExternal(targetUrl);
            }
        });
        closeButton.setOnClickListener(v -> finish());

        if (targetUrl == null || targetUrl.trim().isEmpty()) {
            statusView.setText("未从目标 Intent 中解析到网页地址。\n\n目标类："
                    + getIntent().getStringExtra(EXTRA_TARGET_CLASS)
                    + "\n\nExtras:\n"
                    + UrlExtractor.dumpExtras(originalIntent));
            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
            openExternalButton.setVisibility(View.GONE);
            return;
        }

        statusView.setText(targetUrl);
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUserAgentString(settings.getUserAgentString() + " BlackBoxShadowWeb/2.0");

        GenericJavascriptBridge bridge = new GenericJavascriptBridge();
        webView.addJavascriptInterface(bridge, "javascript");
        webView.addJavascriptInterface(bridge, "KRJavascriptInterface");
        webView.addJavascriptInterface(bridge, "Android");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                statusView.setText(url != null ? url : targetUrl);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request != null && request.getUrl() != null ? request.getUrl().toString() : null;
                return handleSpecialUrl(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleSpecialUrl(url);
            }
        });

        Log.d(TAG, "Loading url: " + targetUrl + " from " + (originalIntent != null ? originalIntent.getComponent() : null));
        webView.loadUrl(targetUrl);
    }

    @SuppressWarnings("deprecation")
    @Nullable
    private static Intent getParcelableIntentExtra(Intent intent, String key) {
        if (intent == null) return null;
        try {
            return (Intent) intent.getParcelableExtra(key);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private boolean handleSpecialUrl(String url) {
        if (url == null || url.trim().isEmpty()) return false;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            openExternal(url);
            return true;
        }
        return false;
    }

    private void openExternal(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            statusView.setText("无法打开外部浏览器：" + url + "\n" + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }

    public class GenericJavascriptBridge {
        @JavascriptInterface public void close() { runOnUiThread(() -> finish()); }
        @JavascriptInterface public void back() { runOnUiThread(() -> onBackPressed()); }
        @JavascriptInterface public void open(String url) {
            if (url != null && !url.trim().isEmpty()) {
                runOnUiThread(() -> {
                    if (!handleSpecialUrl(url)) {
                        webView.loadUrl(url);
                    }
                });
            }
        }
        @JavascriptInterface public void postMessage(String message) { Log.d(TAG, "postMessage=" + message); }
        @JavascriptInterface public void callback(String message) { Log.d(TAG, "callback=" + message); }
        @JavascriptInterface public String getDeviceInfo() { return "{}"; }
        @JavascriptInterface public String getOaid() { return ""; }
        @JavascriptInterface public String getOAID() { return ""; }
        @JavascriptInterface public String getUserInfo() { return "{}"; }
    }

    private static class UrlExtractor {
        private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\\s\"'<>]+", Pattern.CASE_INSENSITIVE);
        private static final String[] COMMON_KEYS = new String[] {
                "url", "URL", "link", "web_url", "webUrl", "openUrl", "pageUrl", "h5Url", "jumpUrl", "targetUrl", "loadUrl"
        };

        @Nullable
        static String extract(@Nullable Intent intent) {
            if (intent == null) return null;
            String dataString = intent.getDataString();
            String fromString = extractFromString(dataString);
            if (fromString != null) return fromString;
            for (String key : COMMON_KEYS) {
                String extra = intent.getStringExtra(key);
                String url = extractFromString(extra);
                if (url != null) return url;
            }
            return extractFromBundle(intent.getExtras());
        }

        static String dumpExtras(@Nullable Intent intent) {
            return dumpBundle(intent != null ? intent.getExtras() : null);
        }

        @Nullable
        private static String extractFromBundle(@Nullable Bundle bundle) {
            if (bundle == null) return null;
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                Object value = bundle.get(key);
                String url = extractFromAny(value);
                if (url != null) return url;
            }
            return null;
        }

        @Nullable
        private static String extractFromAny(@Nullable Object value) {
            if (value == null) return null;
            if (value instanceof String) return extractFromString((String) value);
            if (value instanceof CharSequence) return extractFromString(value.toString());
            if (value instanceof Uri) return extractFromString(value.toString());
            if (value instanceof Intent) return extract((Intent) value);
            if (value instanceof Bundle) return extractFromBundle((Bundle) value);
            if (value instanceof ArrayList) {
                for (Object item : (ArrayList<?>) value) {
                    String url = extractFromAny(item);
                    if (url != null) return url;
                }
                return null;
            }
            if (value instanceof Object[]) {
                for (Object item : (Object[]) value) {
                    String url = extractFromAny(item);
                    if (url != null) return url;
                }
                return null;
            }
            return extractFromString(String.valueOf(value));
        }

        @Nullable
        private static String extractFromString(@Nullable String raw) {
            if (raw == null) return null;
            String trimmed = raw.trim();
            if (trimmed.isEmpty()) return null;
            Matcher matcher = URL_PATTERN.matcher(trimmed);
            return matcher.find() ? matcher.group() : null;
        }

        static String dumpBundle(@Nullable Bundle bundle) {
            if (bundle == null) return "<empty>";
            StringBuilder sb = new StringBuilder();
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                sb.append(String.format(Locale.US, "%s = %s", key, value)).append('\n');
            }
            return sb.length() == 0 ? "<empty>" : sb.toString();
        }
    }
}
