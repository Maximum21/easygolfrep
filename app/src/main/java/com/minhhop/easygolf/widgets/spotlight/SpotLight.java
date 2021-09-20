package com.minhhop.easygolf.widgets.spotlight;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.widgets.spotlight.listener.AbstractAnimatorListener;
import com.minhhop.easygolf.widgets.spotlight.listener.OnSpotlightStateChangedListener;
import com.minhhop.easygolf.widgets.spotlight.target.Target;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

public class SpotLight {

    @ColorRes
    private static final int DEFAULT_OVERLAY_COLOR = R.color.background_spotlight;
    private static final long DEFAULT_DURATION = 1000L;
    private static final TimeInterpolator DEFAULT_ANIMATION = new DecelerateInterpolator(2f);

    private static WeakReference<SpotLightView> spotlightViewWeakReference;
    private static WeakReference<Activity> contextWeakReference;
    private ArrayList<? extends Target> targets;
    private long duration = DEFAULT_DURATION;
    private TimeInterpolator animation = DEFAULT_ANIMATION;
    private OnSpotlightStateChangedListener spotlightListener;
    private int overlayColor = DEFAULT_OVERLAY_COLOR;
    private boolean isClosedOnTouchedOutside = true;

    private SpotLight(Activity activity) {
        contextWeakReference = new WeakReference<>(activity);
    }

    public static SpotLight with(@NonNull Activity activity) {
        return new SpotLight(activity);
    }

    private static Context getContext() {
        return contextWeakReference.get();
    }

    @Nullable
    private static SpotLightView getSpotlightView() {
        return spotlightViewWeakReference.get();
    }

    /**
     * sets {@link Target}s to SpotLight
     *
     * @param targets targets to show
     * @return the SpotLight
     */
    @SafeVarargs public final <T extends Target> SpotLight setTargets(@NonNull T... targets) {
        this.targets = new ArrayList<>(Arrays.asList(targets));

        return this;
    }

    /**
     * sets {@link Target}s to SpotLight
     *
     * @param targets targets as ArrayList to show
     * @return the SpotLight
     */
    public final <T extends Target> SpotLight setTargets(@NonNull ArrayList<T> targets) {
        this.targets = targets;

        return this;
    }

    /**
     * sets spotlight background color to SpotLight
     *
     * @param overlayColor background color to be used for the spotlight overlay
     * @return the SpotLight
     */
    public SpotLight setOverlayColor(@ColorRes int overlayColor) {
        this.overlayColor = overlayColor;
        return this;
    }

    /**
     * sets duration to {@link Target} Animation
     *
     * @param duration duration of Target Animation
     * @return the SpotLight
     */
    public SpotLight setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    /**
     * sets interpolator to {@link Target} Animation
     *
     * @param animation type of Target Animation
     * @return the SpotLight
     */
    public SpotLight setAnimation(TimeInterpolator animation) {
        this.animation = animation;
        return this;
    }

    /**
     * Sets {@link OnSpotlightStateChangedListener}
     *
     * @param listener OnSpotlightEndedListener of SpotLight
     * @return This SpotLight
     */
    public SpotLight setOnSpotlightStateListener(
            @NonNull final OnSpotlightStateChangedListener listener) {
        spotlightListener = listener;
        return this;
    }

    /**
     * Sets if SpotLight closes Target if touched outside
     *
     * @param isClosedOnTouchedOutside OnSpotlightEndedListener of SpotLight
     * @return This SpotLight
     */
    public SpotLight setClosedOnTouchedOutside(boolean isClosedOnTouchedOutside) {
        this.isClosedOnTouchedOutside = isClosedOnTouchedOutside;
        return this;
    }

    /**
     * Shows {@link SpotLightView}
     */
    public void start() {
        spotlightView();
    }

    /**
     * close the current {@link Target}
     */
    public void closeCurrentTarget() {
        finishTarget();
    }

    /**
     * close the {@link SpotLight}
     */
    public void closeSpotlight() {
        finishSpotlight();
    }

    /**
     * Creates the spotlight view and starts
     */
    @SuppressWarnings("unchecked") private void spotlightView() {
        if (getContext() == null) {
            throw new RuntimeException("context is null");
        }

        final View decorView = ((Activity) getContext()).getWindow().getDecorView();
        SpotLightView spotlightView =
                new SpotLightView(getContext(), overlayColor, () -> {
                    if (isClosedOnTouchedOutside) {
                        finishTarget();
                    }
                });
        spotlightViewWeakReference = new WeakReference<>(spotlightView);
        ((ViewGroup) decorView).addView(spotlightView);
        startSpotlight();
    }

    /**
     * show Target
     */
    @SuppressWarnings("unchecked") private void startTarget() {
        if (targets != null && targets.size() > 0 && getSpotlightView() != null) {
            final Target target = targets.get(0);
            SpotLightView spotlightView = getSpotlightView();
            spotlightView.removeAllViews();
            spotlightView.addView(target.getOverlay());
            spotlightView.turnUp(target, new AbstractAnimatorListener() {
                @Override public void onAnimationStart(Animator animation) {
                   if(target.getListener() != null)
                    target.getListener().onStarted(target);
                }
            });
        }
    }

    /**
     * show SpotLight
     */
    private void startSpotlight() {
        if (getSpotlightView() == null) return;
        getSpotlightView().startSpotlight(duration, animation, new AbstractAnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
                if (spotlightListener != null) spotlightListener.onStarted();
            }

            @Override public void onAnimationEnd(Animator animation) {
                startTarget();
            }
        });
    }

    /**
     * hide Target
     */
    @SuppressWarnings("unchecked") private void finishTarget() {
        if (targets != null && targets.size() > 0 && getSpotlightView() != null) {
            getSpotlightView().turnDown(new AbstractAnimatorListener() {
                @Override public void onAnimationEnd(Animator animation) {
                    if (!targets.isEmpty()) {
                        Target target = targets.remove(0);
                        if(target.getListener() != null)
                        target.getListener().onEnded(target);

                        if (targets.size() > 0) {
                            startTarget();
                        } else {
                            finishSpotlight();
                        }
                    }
                }
            });
        }
    }

    /**
     * hide SpotLight
     */
    private void finishSpotlight() {
        if (getSpotlightView() == null) return;
        getSpotlightView().finishSpotlight(duration, animation, new AbstractAnimatorListener() {
            @Override public void onAnimationEnd(Animator animation) {
                Activity activity = (Activity) getContext();
                if (activity != null) {
                    final View decorView = activity.getWindow().getDecorView();
                    ((ViewGroup) decorView).removeView(getSpotlightView());
                    if (spotlightListener != null) spotlightListener.onEnded();
                }
            }
        });
    }
}