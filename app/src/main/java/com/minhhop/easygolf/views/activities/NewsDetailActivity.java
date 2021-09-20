package com.minhhop.easygolf.views.activities;

import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.WozBaseActivity;
import com.minhhop.easygolf.framework.models.News;
import com.minhhop.easygolf.utils.AppUtil;
import com.minhhop.easygolf.framework.common.Contains;
import com.minhhop.easygolf.widgets.PicassoImageGetter;
import com.squareup.picasso.Picasso;

public class NewsDetailActivity extends WozBaseActivity {


    @Override
    protected int setLayoutView() {
        return R.layout.activity_detail_news;
    }

    @Override
    protected void initView() {
        News mNews = (News) getIntent().getSerializableExtra(Contains.EXTRA_NEWS);
        if(mNews != null) {
            ImageView imgView = findViewById(R.id.imgView);
            Picasso.get().load(mNews.getPhoto())
                    .into(imgView);
            CollapsingToolbarLayout txtTags = findViewById(R.id.txtTags);
            txtTags.setTitle(mNews.getTags());

            TextView txtName = findViewById(R.id.txtName);
            TextView txtDate = findViewById(R.id.txtDate);
            txtName.setText(mNews.getTitle());
            txtDate.setText(AppUtil.fromISO8601UTC(mNews.getDate(), "MMM dd, yyyy"));

            TextView txtContent = findViewById(R.id.txtContent);
            txtContent.setText(Html.fromHtml(mNews.getContent(), new PicassoImageGetter(txtContent, getResources()), null));
        }
    }

    @Override
    protected void loadData() {
    }
}
