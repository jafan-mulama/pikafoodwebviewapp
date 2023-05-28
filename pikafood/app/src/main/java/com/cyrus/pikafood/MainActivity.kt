package com.cyrus.pikafood

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var progressBar: ProgressBar
    private lateinit var exitButton: Button

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        exitButton = findViewById(R.id.exitButton)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
            }
        }

        val settings = webView.settings
        settings.javaScriptEnabled = true // Enable JavaScript (optional)

        // Clear WebView cache
        webView.clearCache(true)

        webView.loadUrl("https://pikafood.co.ke/")

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    showExitConfirmationDialog()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        exitButton.setOnClickListener {
            showExitConfirmationDialog()
        }
        checkNetworkConnectivity()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit the app?")
            .setPositiveButton("Yes") { _, _ -> finish() }
            .setNegativeButton("No", null)
            .show()
    }
    private fun checkNetworkConnectivity() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return
        val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities)
        val isInternetAvailable = activeNetwork?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false

        if (!isInternetAvailable) {
            showConnectivityDialog()
        }
    }

    private fun showConnectivityDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setMessage("Please turn on data or connect to a Wi-Fi network.")
            .setPositiveButton("Settings") { _: DialogInterface, _: Int ->
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}