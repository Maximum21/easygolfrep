package com.minhhop.easygolf.views.activities;

import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.WozBaseActivity;
import com.minhhop.easygolf.presentation.policy.PolicyTermsActivity;
import com.minhhop.easygolf.utils.AppUtil;
import com.minhhop.easygolf.framework.common.Contains;
import com.minhhop.easygolf.utils.TextViewUtil;

public class AboutActivity extends WozBaseActivity {

    private TextView mTxtLastUpdate;
    private TextView mTxtCurrentVersion;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        mTxtLastUpdate = findViewById(R.id.txtLastUpdate);
        TextView mTxtSize = findViewById(R.id.txtSize);
        mTxtCurrentVersion = findViewById(R.id.txtCurrentVersion);

        mTxtSize.setText(AppUtil.getSizeInApp());

        findViewById(R.id.privacyPolicy).setOnClickListener(this);

        TextView txtSendFeedBack = findViewById(R.id.txtSendFeedBack);
        txtSendFeedBack.setText(Html.fromHtml("<a href=\"mailto:ask@me.it\">" + getString(R.string.send_feedback) +"</a>"));
        txtSendFeedBack.setMovementMethod(LinkMovementMethod.getInstance());
        TextViewUtil.removeUnderLine((Spannable) txtSendFeedBack.getText());

        TextView txtLinkWeb = findViewById(R.id.txtLinkWeb);
        txtLinkWeb.setText(Html.fromHtml(getString(R.string.link_website)));
        txtLinkWeb.setMovementMethod(LinkMovementMethod.getInstance());
        TextViewUtil.removeUnderLine((Spannable) txtLinkWeb.getText());

    }

    @Override
    protected void loadData() {
        mTxtLastUpdate.setText(AppUtil.getLastTimeUpdate(this, "MMM dd, yyyy"));
        mTxtCurrentVersion.setText(AppUtil.getLastVersion(this));
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.privacyPolicy) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Contains.EXTRA_POLICY, true);
            startActivity(PolicyTermsActivity.class, bundle);
        }
    }
}
