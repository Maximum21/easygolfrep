package com.minhhop.easygolf.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.minhhop.easygolf.R;

/**
 * Created by pdypham on 3/25/18.
 */

public abstract class CoreActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(setLayoutView());
        addView();

//        init Tool Bar
        setToolBar();

        initView();

        loadData();
    }

    protected void addView(){

    }

    protected void setToolBar(){


        Toolbar mToolBar = findViewById(R.id.toolBarHome);

        if(mToolBar != null){
            mToolBar.setTitle("");
            setSupportActionBar(mToolBar);

            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);

                actionBar.setTitle(null);


            }

        }else {

            mToolBar = findViewById(R.id.toolBarBack);
            if(mToolBar != null){
                setSupportActionBar(mToolBar);

                ActionBar actionBar = getSupportActionBar();
                if(actionBar != null) {
                    actionBar.setDisplayHomeAsUpEnabled(true);

                    if(setIconToolbar() != 0){
                        actionBar.setHomeAsUpIndicator(setIconToolbar());
                    }

                    actionBar.setTitle(getTitleToolBar());

                }


                mToolBar.setNavigationOnClickListener(v -> onBackPressed());

            }


        }

//        TextView titleToolBar = findViewById(R.id.titleToolBar);

//        if(titleToolBar != null){
//
//            if(TextUtils.isEmpty(getTitleToolBar())){
//                titleToolBar.setVisibility(View.GONE);
//            }else {
//                titleToolBar.setText(getTitleToolBar());
//            }
//        }



    }

    @Override
    public void onClick(View v) {

    }


    protected boolean isHideSoftKeyBoardTouchOutSide(){
        return true;
    }




    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(isHideSoftKeyBoardTouchOutSide()){
            View viewFocus = getWindow().getCurrentFocus();
            if(viewFocus instanceof EditText){
                    int[] screenLocation = new int[2];
                viewFocus.getLocationOnScreen(screenLocation);

                float x = event.getRawX() + viewFocus.getLeft() - screenLocation[0];
                float y = event.getRawY() + viewFocus.getTop() - screenLocation[1];

                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    if(x < viewFocus.getLeft() || x >= viewFocus.getRight()
                            || y < viewFocus.getTop() || y >= viewFocus.getBottom()){

                        InputMethodManager iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(iMM != null) {
                            hideSoftKeyboard();
                            handlerSoftKeyBoard();

                        }

                    }else {
                        handlerSoftKeyBoard();
                    }
                }


            }
        }

        return super.dispatchTouchEvent(event);
    }


    protected void hideSoftKeyboard(){

        View viewFocus = getCurrentFocus();

        InputMethodManager iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(iMM != null && viewFocus != null) {
            iMM.hideSoftInputFromWindow(viewFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            handlerSoftKeyBoard();
        }
    }

    protected void handlerSoftKeyBoard(){ }

    protected String getTitleToolBar(){
        return null;
    }

    protected abstract int setLayoutView();

    protected abstract void initView();


    protected abstract void loadData();


    protected void startActivity(Class cl){
        startActivity(new Intent(CoreActivity.this,cl));
    }

    protected void startActivity(Class cl, int flag){
        Intent intent = new Intent(CoreActivity.this,cl);
        intent.addFlags(flag);
        startActivity(intent);
    }


    protected void startActivityForResult(Class cl, int requestCode){
        startActivityForResult(new Intent(CoreActivity.this,cl),requestCode);
    }

    protected void startActivityForResult(Class cl,  int requestCode,Bundle bundle){

        Intent intent = new Intent(CoreActivity.this,cl);
        intent.putExtras(bundle);

        startActivityForResult(intent,requestCode);
    }


    protected void startActivity(Class cl, Bundle bundle){

        Intent intent = new Intent(CoreActivity.this,cl);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    protected int setIconToolbar(){
        return 0;
    }
}
