package com.minhhop.easygolf.widgets;

import android.text.TextPaint;
import android.text.style.URLSpan;

public class URLSpanNoUnderline extends URLSpan {

    public URLSpanNoUnderline(String url) {
        super(url);
    }

    public void updateDrawState(TextPaint p_DrawState){
        super.updateDrawState(p_DrawState);
        p_DrawState.setUnderlineText(false);
    }
}
