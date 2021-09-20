package com.minhhop.easygolf.framework.dialogs;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.core.CoreDialog;
import com.minhhop.easygolf.listeners.OnComplete;

public class ConfirmDialog extends CoreDialog {

    private OnComplete mOnComplete;
    private String mContent;

    public ConfirmDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        findViewById(R.id.btYes).setOnClickListener(this);
        findViewById(R.id.btNo).setOnClickListener(this);
        if(!TextUtils.isEmpty(mContent)){
            TextView txtContent = findViewById(R.id.txtContent);
            txtContent.setText(mContent);
        }
    }

    public ConfirmDialog setOnConfirm(OnComplete onComplete){
        this.mOnComplete = onComplete;
        return this;
    }

    public ConfirmDialog setContent(String value){
        mContent = value;
        return this;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btYes:
                if(mOnComplete != null){
                    mOnComplete.complete();
                    dismiss();
                }
                break;
            case R.id.btNo:
                dismiss();
                break;
        }
    }

    @Override
    protected int resContentView() {
        return R.layout.dialog_confirm;
    }

    @Override
    protected boolean cancelTouchOutside() {
        return false;
    }
}
