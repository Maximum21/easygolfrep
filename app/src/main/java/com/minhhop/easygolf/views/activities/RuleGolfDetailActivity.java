package com.minhhop.easygolf.views.activities;

import android.annotation.SuppressLint;
import android.text.Html;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.WozBaseActivity;
import com.minhhop.easygolf.framework.models.RuleGolfItem;
import com.minhhop.easygolf.framework.common.Contains;
import com.minhhop.easygolf.widgets.PicassoImageSVGGetter;

public class RuleGolfDetailActivity extends WozBaseActivity {

    @Override
    protected int setLayoutView() {
        return R.layout.activity_detail_rule_golf;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView() {
        RuleGolfItem mRuleGolf = (RuleGolfItem) getIntent().getSerializableExtra(Contains.EXTRA_RULE);
        if(mRuleGolf != null) {

            TextView txtTitleParent = findViewById(R.id.txtTitleParent);
            String valueParent = getIntent().getStringExtra(Contains.EXTRA_TITLE_RULE);
            txtTitleParent.setText(valueParent);

            TextView txtTitle = findViewById(R.id.txtTitle);
            String valueChildTitle = getIntent().getStringExtra(Contains.EXTRA_TITLE_RULE_CHILD);
            txtTitle.setText(valueChildTitle);

            TextView txtContent = findViewById(R.id.txtContent);
            txtContent.setText(Html.fromHtml(mRuleGolf.getContent(),
                    new PicassoImageSVGGetter(txtContent, getResources()), null));

            String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/open_sans_regular.ttf\")}body {font-family: MyFont;font-size: medium;text-align: justify;}</style></head><body>";

            WebView webview = findViewById(R.id.webViewContent);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDefaultFontSize(13);

            webview.loadDataWithBaseURL("", pish + " " + mRuleGolf.getContent(), "text/html", "UTF-8", "");
        }
    }


    @Override
    protected void loadData() {

    }
}
