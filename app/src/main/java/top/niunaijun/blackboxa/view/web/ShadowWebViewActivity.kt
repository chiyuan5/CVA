package top.niunaijun.blackboxa.view.web

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import top.niunaijun.blackboxa.R
import java.util.Locale
import java.util.regex.Pattern

class ShadowWebViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ORIGINAL_INTENT = "_B_|_shadow_web_original_intent_"
        const val EXTRA_TARGET_PACKAGE = "_B_|_shadow_web_target_pkg_"
        const val EXTRA_TARGET_CLASS = "_B_|_shadow_web_target_cls_"
        private const val TAG = "ShadowWebViewActivity"

        @JvmStatic
        fun createIntent(hostPackage: String, original: Intent?): Intent {
            return Intent().apply {
                setClassName(hostPackage, ShadowWebViewActivity::class.java.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(EXTRA_ORIGINAL_INTENT, original)
                putExtra(EXTRA_TARGET_PACKAGE, original?.component?.packageName)
                putExtra(EXTRA_TARGET_CLASS, original?.component?.className)
            }
        }
    }

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var statusView: TextView
    private lateinit var openExternalButton: Button
    private lateinit var closeButton: Button

    private var targetUrl: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shadow_webview)

        webView = findViewById(R.id.shadow_webview)
        progressBar = findViewById(R.id.shadow_web_progress)
        statusView = findViewById(R.id.shadow_web_status)
        openExternalButton = findViewById(R.id.shadow_web_open_external)
        closeButton = findViewById(R.id.shadow_web_close)

        val originalIntent: Intent? = intent.getParcelableExtra(EXTRA_ORIGINAL_INTENT)
        targetUrl = UrlExtractor.extract(originalIntent)

        supportActionBar?.title = originalIntent?.component?.shortClassName ?: "Web"

        openExternalButton.setOnClickListener {
            targetUrl?.let { url -> openExternal(url) }
        }
        closeButton.setOnClickListener { finish() }

        if (targetUrl.isNullOrBlank()) {
            statusView.text = buildString {
                append("未从目标 Intent 中解析到网页地址。\n\n")
                append("目标类：")
                append(intent.getStringExtra(EXTRA_TARGET_CLASS) ?: "unknown")
                append("\n\nExtras:\n")
                append(UrlExtractor.dumpExtras(originalIntent))
            }
            progressBar.visibility = View.GONE
            webView.visibility = View.GONE
            openExternalButton.visibility = View.GONE
            return
        }

        statusView.text = targetUrl
        CookieManager.getInstance().setAcceptCookie(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            setSupportZoom(true)
            builtInZoomControls = false
            displayZoomControls = false
            loadsImagesAutomatically = true
            mediaPlaybackRequiresUserGesture = false
            cacheMode = WebSettings.LOAD_DEFAULT
            javaScriptCanOpenWindowsAutomatically = true
            userAgentString = userAgentString + " BlackBoxShadowWeb/1.0"
        }

        webView.addJavascriptInterface(GenericJavascriptBridge(), "javascript")
        webView.addJavascriptInterface(GenericJavascriptBridge(), "KRJavascriptInterface")
        webView.addJavascriptInterface(GenericJavascriptBridge(), "Android")

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress >= 100) View.GONE else View.VISIBLE
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                statusView.text = url ?: targetUrl
                progressBar.visibility = View.VISIBLE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                return handleSpecialUrl(url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return handleSpecialUrl(url)
            }
        }

        Log.d(TAG, "Loading url: $targetUrl from ${originalIntent?.component}")
        webView.loadUrl(targetUrl!!)
    }

    private fun handleSpecialUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            openExternal(url)
            return true
        }
        return false
    }

    private fun openExternal(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            statusView.text = "无法打开外部浏览器：$url\n${e.message}"
        }
    }

    override fun onBackPressed() {
        if (::webView.isInitialized && webView.visibility == View.VISIBLE && webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.removeAllViews()
            webView.destroy()
        }
        super.onDestroy()
    }

    inner class GenericJavascriptBridge {
        @JavascriptInterface fun close() = runOnUiThread { finish() }
        @JavascriptInterface fun back() = runOnUiThread { onBackPressed() }
        @JavascriptInterface fun open(url: String?) {
            if (!url.isNullOrBlank()) {
                runOnUiThread {
                    if (!handleSpecialUrl(url)) {
                        webView.loadUrl(url)
                    }
                }
            }
        }
        @JavascriptInterface fun postMessage(message: String?) { Log.d(TAG, "postMessage=$message") }
        @JavascriptInterface fun callback(message: String?) { Log.d(TAG, "callback=$message") }
        @JavascriptInterface fun getDeviceInfo(): String = "{}"
        @JavascriptInterface fun getOaid(): String = ""
        @JavascriptInterface fun getOAID(): String = ""
        @JavascriptInterface fun getUserInfo(): String = "{}"
    }

    private object UrlExtractor {
        private val URL_PATTERN = Pattern.compile("https?://[^\\s\"'<>]+", Pattern.CASE_INSENSITIVE)
        private val COMMON_KEYS = listOf(
            "url", "URL", "link", "web_url", "webUrl", "openUrl", "pageUrl", "h5Url", "jumpUrl", "targetUrl", "loadUrl"
        )

        fun extract(intent: Intent?): String? {
            if (intent == null) return null
            intent.dataString?.let { extractFromString(it)?.let { url -> return url } }
            for (key in COMMON_KEYS) {
                intent.getStringExtra(key)?.let { extractFromString(it)?.let { url -> return url } }
            }
            return extractFromBundle(intent.extras)
        }

        fun dumpExtras(intent: Intent?): String {
            return dumpBundle(intent?.extras)
        }

        private fun extractFromBundle(bundle: Bundle?): String? {
            if (bundle == null) return null
            for (key in bundle.keySet()) {
                when (val value = bundle.get(key)) {
                    is String -> extractFromString(value)?.let { return it }
                    is CharSequence -> extractFromString(value.toString())?.let { return it }
                    is Uri -> extractFromString(value.toString())?.let { return it }
                    is Intent -> extract(value)?.let { return it }
                    is Bundle -> extractFromBundle(value)?.let { return it }
                    is ArrayList<*> -> value.forEach { item -> extractFromAny(item)?.let { return it } }
                    is Array<*> -> value.forEach { item -> extractFromAny(item)?.let { return it } }
                    is Parcelable -> extractFromString(value.toString())?.let { return it }
                    else -> extractFromString(value?.toString())?.let { return it }
                }
            }
            return null
        }

        private fun extractFromAny(value: Any?): String? = when (value) {
            null -> null
            is String -> extractFromString(value)
            is CharSequence -> extractFromString(value.toString())
            is Uri -> extractFromString(value.toString())
            is Intent -> extract(value)
            is Bundle -> extractFromBundle(value)
            else -> extractFromString(value.toString())
        }

        private fun extractFromString(raw: String?): String? {
            if (raw.isNullOrBlank()) return null
            val trimmed = raw.trim()
            val matcher = URL_PATTERN.matcher(trimmed)
            return if (matcher.find()) matcher.group() else null
        }

        private fun dumpBundle(bundle: Bundle?): String {
            if (bundle == null) return "<empty>"
            val lines = mutableListOf<String>()
            for (key in bundle.keySet()) {
                val value = bundle.get(key)
                lines += String.format(Locale.US, "%s = %s", key, value)
            }
            return if (lines.isEmpty()) "<empty>" else lines.joinToString("\n")
        }
    }
}
