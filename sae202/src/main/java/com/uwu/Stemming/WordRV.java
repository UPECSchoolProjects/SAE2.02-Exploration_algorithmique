package com.uwu.Stemming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordRV {
    /*
     * Cette classe aide à l'implementation de l'algorithme de racinisation donnée
     * dans le cahier des
     * charges.
     * 
     * Elle a été écrite par Maxime LOTTO dans le cadre du travail personnel de la
     * SAE2.02
     */
    static List<String> suffixesStep2a = Arrays.asList("amment", "emment", "ment", "ments");

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
    private boolean doStep2a;
    private boolean foundStep2aSuffixes;
    private boolean step2aFoundSuffixes;

    public WordRV(String word) {
        this.baseWord = word;
        this.setWord(word);
        this.doStep2a = true;
        this.foundStep2aSuffixes = false;
        this.step2aFoundSuffixes = false;
        this.RV_Start = wordLength;
        this.R1_Start = wordLength;
        this.R2_Start = wordLength;
    }

    public WordRV(String word, int rV_Start, int r1_Start, int r2_Start) {
        this.baseWord = word;
        this.setWord(word);
        this.doStep2a = true;
        this.foundStep2aSuffixes = false;
        this.step2aFoundSuffixes = false;
        this.RV_Start = rV_Start;
        this.R1_Start = r1_Start;
        this.R2_Start = r2_Start;
    }

    public WordRV(String word, int rV_Start) {
        this.baseWord = word;
        this.setWord(word);
        this.doStep2a = true;
        this.foundStep2aSuffixes = false;
        this.step2aFoundSuffixes = false;
        this.RV_Start = rV_Start;
        this.R1_Start = wordLength;
        this.R2_Start = wordLength;
    }

    public WordRV(String word, String rv) {
        this.baseWord = word;
        this.setWord(word);
        this.doStep2a = true;
        this.foundStep2aSuffixes = false;
        this.step2aFoundSuffixes = false;
        this.RV_Start = word.indexOf(rv);
        this.RV = rv;
        this.R1_Start = wordLength;
        this.R2_Start = wordLength;
    }

    public WordRV(String word, String r1, String r2) {
        this.baseWord = word;
        this.setWord(word);
        this.doStep2a = true;
        this.foundStep2aSuffixes = false;
        this.step2aFoundSuffixes = false;
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
            this.calculDoStep2a(longestSuffix);
            return true;
        }
        return false;
    }

    public void calculDoStep2a(String suffix) {
        if (this.foundStep2aSuffixes) {
            return;
        } else {
            if (suffixesStep2a.contains(suffix)) {
                this.doStep2a = true;
                this.foundStep2aSuffixes = true;
                return;
            }
        }

        this.doStep2a = false;
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

    public boolean isInRegion(Region r, int index) {
        switch (r) {
            case ALL:
                return true;
            case R1:
                return index >= this.getR1_Start();
            case R2:
                return index >= this.getR2_Start();
            case RV:
                return index >= this.getRV_Start();
            default:
                return false;
        }
    }

    public boolean deleteIfPrecededBySuffix(Region r, List<String> suffixes, String replacement,
            List<Character> authorizedPrecedentLetter, boolean deleteIfnotPrecededByAuthorizedLetter, Region precedentLetterRegion) {
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
            int index = searchWord.lastIndexOf(longestSuffix);
            int precedentIndex = index - 1;
            if (precedentIndex < 0) {
                return false;
            }
            char precedentLetter = searchWord.charAt(precedentIndex);

            if (precedentLetterRegion != null) {
                if (!this.isInRegion(precedentLetterRegion, precedentIndex)) {
                    return false;
                }
            }

            if(deleteIfnotPrecededByAuthorizedLetter) {
                // si on doit supprimer si le caractère précédent n'est pas dans la liste des caractères autorisés
                // alors on retourne faux si le caractère précédent est dans la liste des caractères autorisés
                if (!authorizedPrecedentLetter.contains(precedentLetter)) {
                    return false;
                }
            } else {
                // si on doit supprimer si le caractère précédent est dans la liste des caractères autorisés
                // alors on retourne faux si le caractère précédent n'est pas dans la liste des caractères autorisés
                if (authorizedPrecedentLetter.contains(precedentLetter)) {
                    return false;
                }
            }
            this.word = Stemming.replaceLast(word, longestSuffix, replacement);
            this.wordLength = word.length();
            this.calculDoStep2a(longestSuffix);
            return true;
        }
        return false;
    }


    public void deleteLastLetter() {
        if (this.wordLength == 0) {
            return;
        }
        this.word = this.word.substring(0, this.word.length() - 1);
        this.wordLength = this.word.length();
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

    public boolean isDoStep2a() {
        return this.doStep2a;
    }

    public void setDoStep2a(boolean doStep2a) {
        this.doStep2a = doStep2a;
    }

    public boolean isFoundStep2aSuffixes() {
        return this.foundStep2aSuffixes;
    }

    public void setFoundStep2aSuffixes(boolean foundStep2aSuffixes) {
        this.foundStep2aSuffixes = foundStep2aSuffixes;
    }

}
