package com.minhhop.easygolf.utils;

import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;

import androidx.annotation.NonNull;

import com.minhhop.easygolf.widgets.URLSpanNoUnderline;

public class TextViewUtil extends URLSpan {

    public TextViewUtil(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }

    public static void removeUnderLine(Spannable pText){
        URLSpan[] spans = pText.getSpans(0,pText.length(),URLSpan.class);
        for (URLSpan span :
                spans) {
            int start = pText.getSpanStart(span);
            int end = pText.getSpanEnd(span);
            pText.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            pText.setSpan(span,start,end,0);
        }
    }


//    public class URLSpanNoUnderline extends URLSpan {
//        public URLSpanNoUnderline(String url) {
//            super(url);
//        }
//        @Override public void updateDrawState(TextPaint ds) {
//            super.updateDrawState(ds);
//            ds.setUnderlineText(false);
//        }
//    }
}
