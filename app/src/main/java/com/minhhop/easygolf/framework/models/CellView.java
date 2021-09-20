package com.minhhop.easygolf.framework.models;

public class CellView  {
    private String name;
    private int par;
    private int score;

    public CellView(String name) {
        this.name = name;
    }

    public CellView(String name,int score ,int par) {
        this.name = name;
        this.par = par;
        this.score = score;
    }


    public String getName() {
        return name;
    }

    public int getPar() {
        return par;
    }

    public int getScore() {
        return score;
    }
}
