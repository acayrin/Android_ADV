package me.acayrin.assignment.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import me.acayrin.assignment.R


class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var eUrlBar: TextView
    private lateinit var eBtnBack: ImageView
    private lateinit var eBtnShare: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        val destUrl = intent.extras!!.getString("url", "google.com")

        eUrlBar = findViewById(R.id.v_webview_url_bar)
        eBtnBack = findViewById(R.id.v_webview_back)
        eBtnShare = findViewById(R.id.v_webview_share)

        eUrlBar.text = destUrl
        eBtnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        eBtnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, destUrl)
            startActivity(Intent.createChooser(shareIntent, "Spread the words"))
        }

        // load web view
        loadWebView(destUrl)
    }

    private class Browser(val url: String) : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            view?.loadUrl(url)
            return true
        }
    }

    private fun loadWebView(destUrl: String) {
        webView = findViewById(R.id.v_webview)
        webView.webViewClient = Browser(destUrl)
        webView.settings.javaScriptEnabled = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.loadUrl(destUrl)
    }
}