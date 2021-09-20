package com.minhhop.easygolf.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter;
import com.minhhop.easygolf.core.CoreRecyclerViewHolder;
import com.minhhop.easygolf.framework.models.RuleGolfItem;
import com.minhhop.easygolf.framework.common.Contains;
import com.minhhop.easygolf.views.activities.RuleGolfDetailActivity;

import java.util.List;

public class ItemRuleGolfAdapter extends BaseRecyclerViewAdapter<RuleGolfItem> {

    private int mMainIndex;
    private String mTitle;

    ItemRuleGolfAdapter(Context context) {
        super(context);
    }

    @Override
    protected int setLayout(int viewType) {
        return R.layout.item_expansion_rule_golf;
    }


    @Override
    protected CoreRecyclerViewHolder setViewHolder(View viewRoot, int viewType) {
        return new ItemRuleGolfHolder(viewRoot);
    }



    public void setDataList(List<RuleGolfItem> itemList,int index,String title) {
        this.mMainIndex = index;
        this.mTitle = title;
        super.setDataList(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull CoreRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ItemRuleGolfHolder){

            RuleGolfItem eRule = mDataList.get(position);

            ItemRuleGolfHolder target = (ItemRuleGolfHolder) holder;
            if(position == 0){
                target.txtIndex.setVisibility(View.INVISIBLE);
                target.txtName.setTextColor(getContext().getResources().getColor(R.color.colorLinker));
            }

            target.txtIndex.setText(formatIndexItem(eRule.getIndex()));
            target.txtName.setText(eRule.getTitle());
        }
    }

    private String formatIndexItem(int index){
        return mMainIndex + "." + index;
    }

    private class ItemRuleGolfHolder extends CoreRecyclerViewHolder{


        private TextView txtIndex;
        private TextView txtName;

        private ItemRuleGolfHolder(View itemView) {
            super(itemView);

            txtIndex = itemView.findViewById(R.id.txtIndex);
            txtName = itemView.findViewById(R.id.txtName);

            itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString(Contains.EXTRA_TITLE_RULE,mTitle);

                String targetTitle = txtIndex.getText().toString() + "   " + txtName.getText().toString();
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
