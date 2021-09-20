package com.minhhop.easygolf.framework.dialogs;

import android.content.Context;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.core.CoreDialog;

public class InputNoteDialog extends CoreDialog {

    private EditText editNotes;

    public InputNoteDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        editNotes = findViewById(R.id.editNotes);
        editNotes.setText("");
    }



    @Override
    protected int resContentView() {
        return R.layout.dialog_input_notes;
    }

    @Override
    protected boolean cancelTouchOutside() {
        return true;
    }
}
