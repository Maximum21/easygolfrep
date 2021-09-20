package com.minhhop.easygolf.views.activities;


import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.adapter.SearchRuleAdapter;
import com.minhhop.easygolf.base.WozBaseActivity;
import com.minhhop.easygolf.services.DatabaseService;
import com.minhhop.easygolf.framework.common.Contains;

public class SearchRuleActivity extends WozBaseActivity {

    private SearchRuleAdapter mSearchRuleAdapter;
    private EditText mEditSearch;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_rule_search;
    }

    @Override
    protected void initView() {
        mSearchRuleAdapter = new SearchRuleAdapter(this);
        RecyclerView listResult = findViewById(R.id.listResult);
        listResult.setLayoutManager(new LinearLayoutManager(this));
        listResult.setAdapter(mSearchRuleAdapter);

        mEditSearch = findViewById(R.id.editSearch);
        String keyWord = getIntent().getStringExtra(Contains.EXTRA_KEY_WORD_SEARCH);
        if(!TextUtils.isEmpty(keyWord)){
            mSearchRuleAdapter.setDataList(DatabaseService.Companion.getInstance().searchKeyRulesGolf(keyWord));
            mEditSearch.setText(keyWord);
        }


        mEditSearch.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH && !TextUtils.isEmpty(mEditSearch.getText().toString())){

                mSearchRuleAdapter.setDataList(DatabaseService.Companion.getInstance().searchKeyRulesGolf(mEditSearch.getText().toString()));
                return true;
            }
            return false;
        });

        findViewById(R.id.actionSearch).setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.actionSearch:
                if(!TextUtils.isEmpty(mEditSearch.getText().toString())) {
                    Log.e("WOW","size: " + DatabaseService.Companion.getInstance().searchKeyRulesGolfV2("game").size());
                    mSearchRuleAdapter.setDataList(DatabaseService.Companion.getInstance().searchKeyRulesGolf(mEditSearch.getText().toString()));
                }
                break;
        }
    }


    @Override
    protected void loadData() {

    }
}
