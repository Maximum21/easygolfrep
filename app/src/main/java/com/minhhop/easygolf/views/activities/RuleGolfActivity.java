package com.minhhop.easygolf.views.activities;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.adapter.RuleGolfAdapter;
import com.minhhop.easygolf.base.WozBaseActivity;
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS;
import com.minhhop.easygolf.framework.models.RuleGolf;
import com.minhhop.easygolf.services.ApiService;
import com.minhhop.easygolf.services.DatabaseService;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RuleGolfActivity extends WozBaseActivity {

    private RuleGolfAdapter mRuleGolfAdapter;
    private EditText mEditSearch;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_rule_golf;
    }

    @Override
    protected void initView() {
        RecyclerView listRule = findViewById(R.id.listRule);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listRule.setLayoutManager(layoutManager);
//        mRuleGolfAdapter = new RuleGolfAdapter(this,listRule);

        listRule.setAdapter(mRuleGolfAdapter);

        mEditSearch = findViewById(R.id.editSearch);
        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRuleGolfAdapter.setDataList(DatabaseService.Companion.getInstance().searchKeyRulesGolfV2(mEditSearch.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    protected void loadData() {
        List<RuleGolf> rulesGolf = DatabaseService.Companion.getInstance().getRulesGolf();
        if(rulesGolf.size() > 0){
            mRuleGolfAdapter.setDataList(rulesGolf);
            hideLoading();
        }else {
            showLoading();
            ApiService.Companion.getInstance().getGolfService().getRuleGolf()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> DatabaseService.Companion.getInstance().saveRulesGolf(result, () -> {
                        mRuleGolfAdapter.setDataList(result);
                        hideLoading();
                    }), throwable -> {
                        // show back button
                        hideLoading();
                        new AlertDialogIOS(RuleGolfActivity.this,
                                throwable.getLocalizedMessage(),null).show();
                    });
        }


    }

}
