package com.uwu;

public class AnalyseMot {

    private double frequence;
    private int occurence;
    private String mot;

    public AnalyseMot(String mot, double frequence, int occurence) {
        this.frequence = frequence;
        this.occurence = occurence;
        this.mot = mot;
    }

    public String getMot() {
        return mot;
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
