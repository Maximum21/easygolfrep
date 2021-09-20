package com.minhhop.easygolf.widgets.spotlight.target;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.widgets.spotlight.listener.OnGotIt;
import com.minhhop.easygolf.widgets.spotlight.listener.OnTargetStateChangedListener;
import com.minhhop.easygolf.widgets.spotlight.shape.Shape;

public class SimpleTarget extends Target {
    private SimpleTarget(Shape shape, PointF point, View overlay, long duration,
                         TimeInterpolator animation, OnTargetStateChangedListener listener) {
        super(shape, point, overlay, duration, animation, listener);
    }

    public static class Builder extends AbstractTargetBuilder<Builder, SimpleTarget> implements View.OnClickListener {

        @Override protected Builder self() {
            return this;
        }

        private CharSequence title;
        private CharSequence description;
        private PointF overlayPoint;
        private Float width = 0f;
        private OnGotIt mOnGotIt;

        public Builder(@NonNull Activity context) {
            super(context);
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }


        public Builder setWidth(Float value) {
            this.width = value;
            return this;
        }

        public Builder setDescription(CharSequence description) {
            this.description = description;
            return this;
        }

        public Builder setEventGotIt(OnGotIt onGotIt) {
            this.mOnGotIt = onGotIt;
            return this;
        }

        public Builder setOverlayPoint(PointF overlayPoint) {
            this.overlayPoint = overlayPoint;
            return this;
        }

        public Builder setOverlayPoint(float x, float y) {
            this.overlayPoint = new PointF(x, y);
            return this;
        }

        @Override public SimpleTarget build() {

            ViewGroup root = new FrameLayout(getContext());
            View overlay = getContext().getLayoutInflater().inflate(R.layout.layout_spotlight, root);

            TextView titleView = overlay.findViewById(R.id.title);
            TextView descriptionView = overlay.findViewById(R.id.description);
            LinearLayout layout = overlay.findViewById(R.id.container);
            layout.setPivotY(1f);
            overlay.findViewById(R.id.got_it).setOnClickListener(this);

            if (title != null) {
                titleView.setText(title);
            }
            if (description != null) {
                descriptionView.setText(description);
            }

            if (overlayPoint != null) {
                layout.setPadding((int) overlayPoint.x,0, (int) getContext().getResources().getDimension(R.dimen.d_15), 0);
                ViewTreeObserver vto = layout.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int width  = layout.getMeasuredWidth();
                        int height = layout.getMeasuredHeight();
                        layout.setY(overlayPoint.y - height*0.8f);
                    }
                });



            }
            return new SimpleTarget(shape, point, overlay, duration, animation, listener);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.got_it && mOnGotIt != null){
                mOnGotIt.gotIt();
            }
        }
    }
}
