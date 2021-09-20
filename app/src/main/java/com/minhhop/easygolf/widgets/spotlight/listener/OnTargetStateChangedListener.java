package com.minhhop.easygolf.widgets.spotlight.listener;

import com.minhhop.easygolf.widgets.spotlight.target.Target;

public interface OnTargetStateChangedListener<T extends Target> {
    /**
     * Called when Target is started
     */
    void onStarted(T target);

    /**
     * Called when Target is started
     */
    void onEnded(T target);
}
