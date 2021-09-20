package com.minhhop.easygolf.utils;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class TextViewUtils {

    public static void addBottomIcon(TextView textView, Drawable drawable, int imgWidth, int imgHeight){
        if(textView != null && drawable != null  && imgWidth > 0 && imgHeight > 0){
            drawable.setBounds(0,0,imgWidth,imgHeight);
            textView.setCompoundDrawables(null,null,null,drawable);
        }
    }
}
