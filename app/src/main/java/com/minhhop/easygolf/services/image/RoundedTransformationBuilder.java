package com.minhhop.easygolf.services.image;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

import com.squareup.picasso.Transformation;

import java.util.Arrays;

public final class RoundedTransformationBuilder {
    private final DisplayMetrics mDisplayMetrics;
    private float[] mCornerRadius = new float[]{0,0,0,0};
    private boolean mOval = false;
    private float mBorderWidth = 0;
    private ColorStateList mBorderColor =
            ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;


    public RoundedTransformationBuilder() {
        this.mDisplayMetrics = Resources.getSystem().getDisplayMetrics();
    }


    public RoundedTransformationBuilder cornerRadius(float radius){
        mCornerRadius[Corner.TOP_LEFT] = radius;
        mCornerRadius[Corner.TOP_RIGHT] = radius;
        mCornerRadius[Corner.BOTTOM_LEFT] = radius;
        mCornerRadius[Corner.BOTTOM_RIGHT] = radius;
        return this;
    }


    public RoundedTransformationBuilder cornerRadiusDp(float radius){
        return cornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,radius,mDisplayMetrics));
    }

    public RoundedTransformationBuilder oval(boolean oval) {
        mOval = oval;
        return this;
    }

    public Transformation build(){
        return new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                Bitmap transformed = RoundedDrawable.fromBitmap(source)
                        .setScaleType(mScaleType)
                        .setCornerRadius(mCornerRadius[0],mCornerRadius[1],
                                mCornerRadius[2],mCornerRadius[3])
                        .toBitmap();
                if (!source.equals(transformed)) {
                    source.recycle();
                }
                return transformed;
            }

            @Override
            public String key() {
                return "r:" + Arrays.toString(mCornerRadius)
                        + "b:" + mBorderWidth
                        + "c:" + mBorderColor
                        + "o:" + mOval;
            }
        };
    }
}
