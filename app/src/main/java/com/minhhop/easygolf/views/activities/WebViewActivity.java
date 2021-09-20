package com.minhhop.easygolf.views.activities;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.WozBaseActivity;
import com.minhhop.easygolf.framework.common.Contains;

public class WebViewActivity extends WozBaseActivity {


    @Override
    protected int setLayoutView() {

        return R.layout.activity_webview;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView() {

        String url = getIntent().getStringExtra(Contains.EXTRA_URL_WEB_VIEW);
        if(!TextUtils.isEmpty(url)) {
            WebView webView = findViewById(R.id.webView);
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient());
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
        }

    }

    @Override
    protected void loadData() {

    }
}
