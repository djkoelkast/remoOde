package nl.didier.mooderemote

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * Simple fullscreen WebView wrapper around a moOde audio player web UI.
 * The moOde IP address is stored locally and can be changed at any time
 * via the gear icon in the toolbar.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var prefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "mooderemote"
        private const val KEY_IP = "moode_ip"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        progressBar = findViewById(R.id.progressBar)
        webView = findViewById(R.id.webView)
        setupWebView()

        val savedIp = prefs.getString(KEY_IP, null)
        if (savedIp.isNullOrBlank()) {
            promptForIp(firstRun = true)
        } else {
            loadMoode(savedIp)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            mediaPlaybackRequiresUserGesture = false
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                Toast.makeText(
                    this@MainActivity,
                    "Kan moOde niet bereiken op dit IP. Controleer het adres.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility =
                    if (newProgress in 1..99) ProgressBar.VISIBLE else ProgressBar.GONE
            }
        }
    }

    private fun loadMoode(ip: String) {
        val url = if (ip.startsWith("http://") || ip.startsWith("https://")) {
            ip
        } else {
            "http://$ip"
        }
        webView.loadUrl(url)
    }

    private fun promptForIp(firstRun: Boolean) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = "bijv. 192.168.1.50"
            prefs.getString(KEY_IP, null)?.let { setText(it) }
        }

        val builder = AlertDialog.Builder(this)
            .setTitle("IP-adres van moOde")
            .setView(input)
            .setCancelable(!firstRun)
            .setPositiveButton("Opslaan") { _, _ ->
                val value = input.text.toString().trim()
                if (value.isNotBlank()) {
                    prefs.edit().putString(KEY_IP, value).apply()
                    loadMoode(value)
                } else if (firstRun) {
                    promptForIp(firstRun = true)
                }
            }

        if (!firstRun) {
            builder.setNegativeButton("Annuleren", null)
        }

        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            promptForIp(firstRun = false)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // Laat de hardware-terugknop eerst binnen de moOde-webinterface werken
        // (bijv. terug uit een submenu), en pas daarna de app sluiten.
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
