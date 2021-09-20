package com.minhhop.easygolf.core;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.minhhop.easygolf.R;


/**
 * Created by pdypham on 4/4/18.
 */

public abstract class CoreDialog extends Dialog implements View.OnClickListener{

    public CoreDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resContentView());

        if(getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().setWindowAnimations(R.style.animDialog);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getWindow().setAttributes(lp);
            config(getWindow());
        }



        setCanceledOnTouchOutside(cancelTouchOutside());
        View btClose = findViewById(R.id.btClose);
        if(btClose != null) {
            btClose.setOnClickListener(this);
        }
        initView();
    }


    protected void config(Window window){}

    protected abstract void initView();
    protected abstract int resContentView();

    protected boolean cancelTouchOutside(){
        return false;
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btClose){
            dismiss();
            handlerActionClose();
        }
    }

    protected void handlerActionClose(){

    }
}
