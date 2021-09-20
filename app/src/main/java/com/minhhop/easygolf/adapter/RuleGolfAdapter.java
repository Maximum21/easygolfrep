package com.minhhop.easygolf.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;
import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter;
import com.minhhop.easygolf.core.CoreRecyclerViewHolder;
import com.minhhop.easygolf.framework.models.RuleGolf;

public class RuleGolfAdapter extends BaseRecyclerViewAdapter<RuleGolf> {

    private final ExpansionLayoutCollection expansionsCollection = new ExpansionLayoutCollection();
    private RecyclerView mRecyclerView;
    public RuleGolfAdapter(Context context,RecyclerView recyclerView) {
        super(context);
        expansionsCollection.openOnlyOne(true);
        mRecyclerView = recyclerView;
    }

    @Override
    protected int setLayout(int viewType) {
        return R.layout.item_rule_golf;
    }


    @Override
    protected CoreRecyclerViewHolder setViewHolder(View viewRoot, int viewType) {
        return new RuleGolfHolder(viewRoot);
    }


    @Override
    public void onBindViewHolder(@NonNull CoreRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof RuleGolfHolder){

            RuleGolf eRule = mDataList.get(position);

            RuleGolfHolder target = (RuleGolfHolder) holder;
            expansionsCollection.add(target.expansionLayout);
            target.expansionLayout.collapse(false);

            target.txtIndex.setText(formatIndexItem(eRule.getIndex()));
            target.txtName.setText(eRule.getTitle());
            String targetTitle = formatIndexItem(eRule.getIndex()) + "   " + eRule.getTitle();
            target.mItemRuleGolfAdapter.setDataList(eRule.getRules(),eRule.getIndex(),targetTitle);
            target.expansionLayout.addListener((expansionLayout, expanded) -> {
                if(expanded){
                    mRecyclerView.smoothScrollToPosition(position);
                }
            });
        }
    }

    private String formatIndexItem(int index){
        String result;
        if(index < 10){
            result = "0" + index;
        }else {
            result = String.valueOf(index);
        }

        result+= ".";

        return result;
    }

    private final static class RuleGolfHolder extends CoreRecyclerViewHolder{

        private ExpansionLayout expansionLayout;

        private TextView txtIndex;
        private TextView txtName;
        private View diverView;

        private ItemRuleGolfAdapter mItemRuleGolfAdapter;

        private RuleGolfHolder(View itemView) {
            super(itemView);

            RecyclerView listItem = itemView.findViewById(R.id.list_item);
            mItemRuleGolfAdapter = new ItemRuleGolfAdapter(itemView.getContext());
            listItem.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            listItem.setAdapter(mItemRuleGolfAdapter);
            
            txtIndex = itemView.findViewById(R.id.txtIndex);
            txtName = itemView.findViewById(R.id.txtName);
            diverView = itemView.findViewById(R.id.diverView);
            expansionLayout = itemView.findViewById(R.id.expansionLayout);
            expansionLayout.addListener((expansionLayout, expanded) -> {
                if(!expanded){
                    diverView.setVisibility(View.VISIBLE);
                }else {
                    diverView.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}
