package com.uwu;

/**
 * Classe permettant de stocker les informations sur un mot
 */
public class AnalyseMot {

    private double frequence; // fréquence du mot dans le texte
    private int occurence; // nombre d'occurence du mot dans le texte
    private String mot; // mot analysé (sa racine)

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
