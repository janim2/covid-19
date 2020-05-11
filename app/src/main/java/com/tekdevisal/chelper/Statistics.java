package com.tekdevisal.chelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Statistics extends AppCompatActivity {

    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        getSupportActionBar().setTitle("Covaid | STATS");

        browser = findViewById(R.id.webview);
        browser.setWebViewClient(new MyBrowser());

        browser.getSettings().setBuiltInZoomControls(false);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setSupportZoom(false);
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        browser.getSettings().setAllowFileAccess(true);
        browser.getSettings().setDomStorageEnabled(true);

        browser.loadUrl("https://www.worldometers.info/coronavirus/");
//        browser.loadUrl("https://ghanahealthservice.org/covid19");
//        browser.loadUrl("https://www.coronatracker.com/country/ghana/");
    }

    private static class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
