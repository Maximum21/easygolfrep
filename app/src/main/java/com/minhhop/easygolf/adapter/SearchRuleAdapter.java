package com.minhhop.easygolf.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter;
import com.minhhop.easygolf.core.CoreRecyclerViewHolder;
import com.minhhop.easygolf.framework.models.RuleGolfItem;
import com.minhhop.easygolf.framework.common.Contains;
import com.minhhop.easygolf.views.activities.RuleGolfDetailActivity;

public class SearchRuleAdapter extends BaseRecyclerViewAdapter<RuleGolfItem> {


    public SearchRuleAdapter(Context context) {
        super(context);
    }

    @Override
    protected int setLayout(int viewType) {
        return R.layout.item_search_rule;
    }


    @Override
    public void onBindViewHolder(@NonNull CoreRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof SearchRuleHolder){

            RuleGolfItem e = mDataList.get(position);

            SearchRuleHolder target = (SearchRuleHolder) holder;
            target.txtContent.setText(Html.fromHtml(e.getContent()));
            target.txtTitle.setText(e.getTitle());
        }
    }

    @Override
    protected CoreRecyclerViewHolder setViewHolder(View viewRoot, int viewType) {
        return new SearchRuleHolder(viewRoot);
    }

    private class SearchRuleHolder extends CoreRecyclerViewHolder{
        private TextView txtContent;
        private TextView txtTitle;

        private SearchRuleHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtContent = itemView.findViewById(R.id.txtContent);

            itemView.findViewById(R.id.actionContinueReading).setOnClickListener(v -> {

                Bundle bundle = new Bundle();
                bundle.putString(Contains.EXTRA_TITLE_RULE,"");

                String targetTitle = "" + "   " + txtTitle.getText().toString();
                bundle.putString(Contains.EXTRA_TITLE_RULE_CHILD,targetTitle);

                RuleGolfItem temp = mDataList.get(getAdapterPosition());
                RuleGolfItem eRuleGolf = new RuleGolfItem();
                eRuleGolf.setIndex(temp.getIndex());
                eRuleGolf.setId(temp.getId());
                eRuleGolf.setContent(temp.getContent());
                eRuleGolf.setSlug(temp.getSlug());

                bundle.putSerializable(Contains.EXTRA_RULE,eRuleGolf);
                Intent intent = new Intent(getContext(),RuleGolfDetailActivity.class);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            });
        }
    }
}
