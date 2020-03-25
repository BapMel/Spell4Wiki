package com.manimaran.wikiaudio.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.manimaran.wikiaudio.R;
import com.manimaran.wikiaudio.constants.Urls;
import com.manimaran.wikiaudio.utils.PrefManager;

public class WiktionaryWebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_wiki);


        PrefManager pref = new PrefManager(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String word = bundle.getString("word");
            boolean isContributionMode = false;
            if (bundle.containsKey("is_contribution_mode")) {
                isContributionMode = bundle.getBoolean("is_contribution_mode");
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(word);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            String wikiUrl = String.format(Urls.WIKTIONARY_WEB, isContributionMode ? pref.getLanguageCodeSpell4Wiki() : pref.getLanguageCodeWiktionary(), word);
            loadPage(wikiUrl);
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadPage(String wikiUrl) {
        WebView mWebView = findViewById(R.id.webview);

        mWebView.loadUrl(wikiUrl);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);

        mWebView.setWebViewClient(new WebViewClient());
        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                findViewById(R.id.pb).setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                findViewById(R.id.pb).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

}
