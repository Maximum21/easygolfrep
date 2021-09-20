package com.minhhop.easygolf.framework.dialogs;

import android.content.Context;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.BaseDialog;

import java.util.List;

public class AlertDialogIOS extends BaseDialog implements View.OnClickListener {

    Button mBtDone;
    TextView mTxtContent;
    LinearLayout mLayoutView;

    private String content;
    private OnCancelListener onCancelListener;

    public AlertDialogIOS(@NonNull Context context, String content, OnCancelListener onCancelListener) {
        super(context);
        this.onCancelListener = onCancelListener;
        this.content = content;
    }


    @Override
    protected void initView() {
        mBtDone = findViewById(R.id.bt_done);
        mTxtContent = findViewById(R.id.txt_content);
        mLayoutView = findViewById(R.id.layout_view);

        mTxtContent.setText(content);
        mBtDone.setOnClickListener(this);
    }

    @Override
    protected int resContentView() {
        return R.layout.dialog_alert_ios;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (onCancelListener != null) {
            onCancelListener.onCancel(this);
            dismiss();
        } else {
            dismiss();
        }
    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}