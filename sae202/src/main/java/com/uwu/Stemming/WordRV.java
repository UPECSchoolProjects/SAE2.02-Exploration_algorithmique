package com.uwu.Stemming;

import java.util.ArrayList;
import java.util.List;

public class WordRV {
     /*
     * Cette classe aide à l'implementation de l'algorithme de racinisation donnée dans le cahier des
     * charges.
     * 
     * Elle a été écrite par Maxime LOTTO dans le cadre du travail personnel de la SAE2.02
     */

    public enum Region {
        ALL, R1, R2, RV
    }

    private String baseWord;
    private String word;
    private int wordLength;
    private int RV_Start;
    private int R1_Start;
    private int R2_Start;
    private String R1 = null;
    private String R2 = null;
    private String RV = null;

    public WordRV(String word) {
        this.baseWord = word;
        this.setWord(word);
        this.RV_Start = wordLength;
        this.R1_Start = wordLength;
        this.R2_Start = wordLength;
    }

    public WordRV(String word, int rV_Start, int r1_Start, int r2_Start) {
        this.baseWord = word;
        this.setWord(word);
        this.RV_Start = rV_Start;
        this.R1_Start = r1_Start;
        this.R2_Start = r2_Start;
    }

    public WordRV(String word, int rV_Start) {
        this.baseWord = word;
        this.setWord(word);
        this.RV_Start = rV_Start;
        this.R1_Start = wordLength;
        this.R2_Start = wordLength;
    }

    public WordRV(String word, String rv) {
        this.baseWord = word;
        this.setWord(word);
        this.RV_Start = word.indexOf(rv);
        this.RV = rv;
        this.R1_Start = wordLength;
        this.R2_Start = wordLength;
    }

    public WordRV(String word, String r1, String r2) {
        this.baseWord = word;
        this.setWord(word);
        this.RV_Start = wordLength;
        this.R1_Start = word.indexOf(r1);
        this.R1 = r1;
        this.R2_Start = word.indexOf(r2);
        this.R2 = r2;
        this.RV_Start = wordLength;
    }

    public boolean replaceSuffix(Region r, List<String> suffixes, String replacement) {

        int searchIndex;
        switch (r) {
            case ALL:
                searchIndex = 0;
                break;
            case R1:
                searchIndex = this.getR1_Start();
                break;
            case R2:
                searchIndex = this.getR2_Start();
                break;
            case RV:
                searchIndex = this.getRV_Start();
                break;
            default:
                searchIndex = 0;
                break;
        }


        String searchWord = word.substring(searchIndex);
        String longestSuffix = Stemming.getLongestSuffix(suffixes, searchWord);
        if (longestSuffix != null) {
            this.word = Stemming.replaceLast(word, longestSuffix, replacement);
            this.wordLength = word.length();
            return true;
        }
        return false;
    }

    public ArrayList<Region> endsWithRegion(String str) {
        ArrayList<Region> regions = new ArrayList<Region>();
        if (this.word.endsWith(str)) {
            regions.add(Region.ALL);
        }
        if (this.getR1().endsWith(str)) {
            regions.add(Region.R1);
        }
        if (this.getR2().endsWith(str)) {
            regions.add(Region.R2);
        }
        if (this.getRV().endsWith(str)) {
            regions.add(Region.RV);
        }
        return regions;
    }

    public String getBaseWord() {
        return this.baseWord;
    }

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
        this.wordLength = word.length();
    }

    public int getWordLength() {
        return this.wordLength;
    }

    public int getRV_Start() {
        return this.RV_Start;
    }

    public void setRV_Start(int rV_Start) {
        this.RV = null;
        this.RV_Start = rV_Start;
    }

    public int getR1_Start() {
        return this.R1_Start;
    }

    public void setR1_Start(int r1_Start) {
        this.R1 = null;
        this.R1_Start = r1_Start;
    }

    public int getR2_Start() {
        return this.R2_Start;
    }

    public void setR2_Start(int r2_Start) {
        this.R2 = null;
        this.R2_Start = r2_Start;
    }

    public String getR1() {
        if (this.R1 == null) {
            this.R1 = this.word.substring(this.R1_Start);
        }
        return this.R1;
    }

    public String getR2() {
        if (this.R2 == null) {
            this.R2 = this.word.substring(this.R2_Start);
        }
        return this.R2;
    }

    public String getRV() {
        if (this.RV == null) {
            this.RV = this.word.substring(this.RV_Start);
        }
        return this.RV;
    }

}
