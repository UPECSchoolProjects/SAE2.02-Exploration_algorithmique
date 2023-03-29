package com.uwu;

public class AnalyseMot {

    private double frequence;
    private int occurence;

    public AnalyseMot(double frequence, int occurence) {
        this.frequence = frequence;
        this.occurence = occurence;
    }

    public double getFrequence() {
        return frequence;
    }

    public void setFrequence(double frequence) {
        this.frequence = frequence;
    }

    public void setOccurence(int occurence) {
        this.occurence = occurence;
    }

    public int getOccurence() {
        return occurence;
    }

}
