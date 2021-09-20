package com.minhhop.easygolf.listeners;

public interface HandlerScoreCard {
    void putData(int strokes,int putts);
    void onSubmit(int strokes,int putts);
}
