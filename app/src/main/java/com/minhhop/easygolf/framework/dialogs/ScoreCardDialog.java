package com.minhhop.easygolf.framework.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.core.CoreDialog;
import com.minhhop.easygolf.framework.models.entity.UserEntity;
import com.minhhop.easygolf.listeners.HandlerScoreCard;
import com.minhhop.easygolf.services.CircleTransform;
import com.minhhop.easygolf.services.DatabaseService;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class ScoreCardDialog extends CoreDialog {

    private TextView mTxtStrokes;
    private TextView mTxtPutts;
    private TextView mTxtScore;

    private int mValueStroke;
    private int mValuePutt;

    private int mValueHole;
    private int mValuePar;
    private int mValueSL;



    private HandlerScoreCard mHandlerScoreCard;

    public ScoreCardDialog(@NonNull Context context, int valueStroke,
                           int valuePutt, int valueHole, int valuePar, int valueSL,String userName,String avatar) {

        super(context);
        if(valueStroke == -1){
            this.mValueStroke = valuePar;
        }else {
            this.mValueStroke = valueStroke + valuePutt;
        }

        this.mValuePutt = valuePutt;

        this.mValueHole = valueHole;
        this.mValuePar = valuePar;
        this.mValueSL = valueSL;

    }

    @Override
    protected void initView() {

        mTxtStrokes = findViewById(R.id.txtStrokes);
        mTxtPutts = findViewById(R.id.txtPutts);
        mTxtScore = findViewById(R.id.txtScore);

        TextView mTxtHole = findViewById(R.id.txtHole);
        mTxtHole.setText(String.valueOf(mValueHole));

        TextView mTxtPar = findViewById(R.id.txtPar);
        mTxtPar.setText(String.valueOf(mValuePar));

        TextView mTxtSL = findViewById(R.id.txtSL);
        mTxtSL.setText(String.valueOf(mValueSL));



        updateScore();

        findViewById(R.id.btMinusStroke).setOnClickListener(this);
        findViewById(R.id.btPlusStroke).setOnClickListener(this);
        findViewById(R.id.btMinusPutt).setOnClickListener(this);
        findViewById(R.id.btPlusPutt).setOnClickListener(this);
        findViewById(R.id.btSubmit).setOnClickListener(this);


        TextView txtName = findViewById(R.id.txtName);
        ImageView imgAvatar = findViewById(R.id.imgAvatar);

        UserEntity userEntity = DatabaseService.Companion.getInstance().getCurrentUserEntity();
        if(userEntity != null) {
            txtName.setText(userEntity.getFullName());
            Picasso.get().load(userEntity.getAvatar())
                    .transform(new CircleTransform())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imgAvatar);
        }
    }

    public ScoreCardDialog setHandlerData(HandlerScoreCard handlerScoreCard){
        this.mHandlerScoreCard = handlerScoreCard;
        return this;
    }


    @Override
    protected int resContentView() {
        return R.layout.dialog_add_score_card;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btClose:
                if(mHandlerScoreCard != null){
                    mHandlerScoreCard.putData(mValueStroke,mValuePutt);
                }
                dismiss();
                break;
            case R.id.btSubmit:
                if(mHandlerScoreCard != null){
                    mHandlerScoreCard.onSubmit(mValueStroke,mValuePutt);
                }
                dismiss();
                break;
            case R.id.btMinusStroke:
                if(mValueStroke > 0){
                    mValueStroke --;
                }
                break;

            case R.id.btPlusStroke:
                mValueStroke ++;
                break;

            case R.id.btMinusPutt:
                if(mValuePutt > 0){
                    mValuePutt --;
                }
                break;

            case R.id.btPlusPutt:
                mValuePutt ++;
                break;
        }

        updateScore();
    }



    private void updateScore(){
        if (mValuePutt >= mValueStroke){
            mValueStroke = mValuePutt + 1;
        }
        mTxtStrokes.setText(String.valueOf(mValueStroke));
        mTxtPutts.setText(String.valueOf(mValuePutt));

        mTxtScore.setText(getScore());
    }

    private String getScore(){

        String result;
        int score = mValueStroke;
        if(score == mValuePar){
            result = "Par";

        }else {

            if(score < mValuePar){
                if(score == mValuePar - 1){
                    result = "Birdie";

                }else {
                    if(score == mValuePar - 2){
                        result = "Eagle";

                    }else {
                        result = String.valueOf(score);
                    }
                }

            }else {
                if(score == mValuePar + 1){
                    result = "Bogey";

                }else {
                    result = (score - mValuePar) +"Bogey";
                }
            }


        }

        return result;
    }
}
