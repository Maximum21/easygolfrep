package com.minhhop.easygolf.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter;
import com.minhhop.easygolf.core.CoreRecyclerViewHolder;
import com.minhhop.easygolf.framework.models.entity.UserEntity;
import com.minhhop.easygolf.listeners.EventContact;
import com.squareup.picasso.Picasso;

public class FriendChatAdapter extends BaseRecyclerViewAdapter<UserEntity> {
    private EventContact mEventContact;

    public FriendChatAdapter(Context context,EventContact eventContact) {
        super(context);
        this.mEventContact = eventContact;
    }

    @Override
    protected int setLayout(int viewType) {
        return R.layout.item_friend_chat;
    }


    @Override
    protected CoreRecyclerViewHolder setViewHolder(View viewRoot, int viewType) {
        return new FriendChatHolder(viewRoot);
    }


    @Override
    public void onBindViewHolder(@NonNull CoreRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof FriendChatHolder){
            FriendChatHolder target = (FriendChatHolder) holder;
            UserEntity data = mDataList.get(position);
            Picasso.get().load(data.getAvatar())
                    .placeholder(R.drawable.ic_icon_user_default)
                    .error(R.drawable.ic_icon_user_default)
                    .into(target.mImgAvatar);
            String name = data.getFullName();
            target.mTxtName.setText(name);
            target.mTxtPhone.setText(data.getPhoneNumber());
        }
    }

    private class FriendChatHolder extends CoreRecyclerViewHolder{
        private ImageView mImgAvatar;
        private TextView mTxtName;
        private TextView mTxtPhone;

        private FriendChatHolder(View itemView) {
            super(itemView);
            mImgAvatar = itemView.findViewById(R.id.avatar);
            mTxtName = itemView.findViewById(R.id.txtName);
            mTxtPhone = itemView.findViewById(R.id.txtPhone);

            itemView.setOnClickListener(v -> mEventContact.onClick(mDataList.get(getAdapterPosition())));

        }
    }
}
