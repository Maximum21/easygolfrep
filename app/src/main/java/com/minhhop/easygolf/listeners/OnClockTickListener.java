package com.minhhop.easygolf.listeners;
import android.text.format.Time;

public interface OnClockTickListener {
    public void OnSecondTick(Time currentTime);
    public void OnMinuteTick(Time currentTime);
}
