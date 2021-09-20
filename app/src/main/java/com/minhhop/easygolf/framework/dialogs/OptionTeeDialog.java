package com.minhhop.easygolf.framework.dialogs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.core.CoreDialog;
import com.minhhop.easygolf.framework.models.Tee;

import java.util.List;

public class OptionTeeDialog extends CoreDialog {

    private List<Tee> mTees;
    private RecyclerView mListOption;

    public OptionTeeDialog(@NonNull Context context, List<Tee> tees) {
        super(context);
        this.mTees = tees;
    }

    @Override
    protected void initView() {

    }


    @Override
    protected int resContentView() {
        return R.layout.dialog_picker_tee_flag;
    }


}
