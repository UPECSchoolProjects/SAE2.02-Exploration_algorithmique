package com.uwu.Stemming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represente un mot et ses regions R1, R2 et RV
 * très utile pour le reste de l'implementation
 * 
 * @author Mélodie LOTTO
 */
public class WordRV {
    /*
     * Cette classe aide à l'implementation de l'algorithme de racinisation donnée
     * dans le cahier des
     * charges.
     * 
     * Elle a été écrite par Mélodie LOTTO dans le cadre du travail personnel de la
     * SAE2.02
     */
    /**
     * Listes des suffixes qui conditionnent l'execution de step2a
     * Do step 2a if either no ending was removed by step 1, or if one of endings
     * amment, emment, ment, ments was found.
     */
    static List<String> suffixesStep2a = Arrays.asList("amment", "emment", "ment", "ments");

    /**
     * Listes des parties de mot sur lesquelles on travaille
     * utile pour les fonctions qui doivent agir sur une partie de mot en
     * particulier
     */
    public enum Region {
        ALL, R1, R2, RV
    }

    /**
     * Le mot au moment de l'instantiation de l'objet (finalement inutile)
     */
    private String baseWord;
    /**
     * Le mot sur lequel on travaille
     */
    private String word;
    /**
     * La longueur du mot étant donné la frequence avec laquelle on l'utilise
     * j'ai décidé de la stocker
     */
    private int wordLength;
    /**
     * La position à partir de laquelle RV est défini
     */
    private int RV_Start;
    /**
     * La position à partir de laquelle R1 est défini
     */
    private int R1_Start;
    /**
     * La position à partir de laquelle R2 est défini
     */
    private int R2_Start;
    /**
     * String contenant le contenu de R1 (lazy evaluation dans le getter)
     */
    private String R1 = null;
    /**
     * String contenant le contenu de R2 (lazy evaluation dans le getter)
     */
    private String R2 = null;
    /**
     * String contenant le contenu de RV (lazy evaluation dans le getter)
     */
    private String RV = null;
    /**
     * Si il faut executer l'étape 2a
     */
    private boolean doStep2a;
    /**
     * Si on a trouvé un suffixe de step2a alors il faut forcément executer l'étape
     * 2b
     * d'ou l'utilité d'un flag (utilisé dans calculDoStep2a)
     */
    private boolean foundStep2aSuffixes; // si on a trouvé un suffixe de step2a

    /**
     * Constructeur de base utiliser dans la fonction principale du stemming
     * algorithm
     * 
     * @param word le mot à raciniser
     */
    public WordRV(String word) {
        this.baseWord = word;
        this.setWord(word);
        this.doStep2a = true;
        this.foundStep2aSuffixes = false;
        this.RV_Start = wordLength;
        this.R1_Start = wordLength;
        this.R2_Start = wordLength;
    }

    /**
     * Constructeur utilisé dans les tests unitaires
     * 
     * @param word
     * @param rV_Start
     * @param r1_Start
     * @param r2_Start
     */
    public WordRV(String word, int rV_Start, int r1_Start, int r2_Start) {
        this.baseWord = word;
        this.setWord(word);
        this.doStep2a = true;
        this.foundStep2aSuffixes = false;
        this.RV_Start = rV_Start;
        this.R1_Start = r1_Start;
        this.R2_Start = r2_Start;
    }

    /**
     * Constructeur utilisé dans les tests unitaires
     * 
     * @param word
     * @param rV_Start
     */
    public WordRV(String word, int rV_Start) {
        this.baseWord = word;
        this.setWord(word);
        this.doStep2a = true;
        this.foundStep2aSuffixes = false;
        this.RV_Start = rV_Start;
        this.R1_Start = wordLength;
        this.R2_Start = wordLength;
    }

    /**
     * Constructeur utilisé dans les tests unitaires
     * 
     * @param word
     * @param rv   le contenu de la region RV (RV_Start est calculé automatiquement)
     */
    public WordRV(String word, String rv) {
        this.baseWord = word;
        this.setWord(word);
        this.doStep2a = true;
        this.foundStep2aSuffixes = false;
        this.RV_Start = word.indexOf(rv);
        this.RV = rv;
        this.R1_Start = wordLength;
        this.R2_Start = wordLength;
    }

    /**
     * Constructeur utilisé dans les tests unitaires
     * 
     * @param word
     * @param r1   le contenu de la region R1 (R1_Start est calculé automatiquement)
     * @param r2   le contenu de la region R2 (R2_Start est calculé automatiquement)
     */
    public WordRV(String word, String r1, String r2) {
        this.baseWord = word;
        this.setWord(word);
        this.doStep2a = true;
        this.foundStep2aSuffixes = false;
        this.RV_Start = wordLength;
        this.R1_Start = word.indexOf(r1);
        this.R1 = r1;
        this.R2_Start = word.indexOf(r2);
        this.R2 = r2;
        this.RV_Start = wordLength;
    }

    /**
     * Prends en paramètre l'enum Region et retourne l'index de début de la region
     * vérifie que l'index est bien dans la longueur du mot (le contraire peut arriver car le mot peut beaucoup changer)
     * théoriquement le controle n'est pas nécessaire mais je préfère le laisser
     *  @param r la region dont on veut l'index de début
     */
    public int getSearchIndex(Region r) {
        int index;
        switch (r) {
            case ALL:
                index = 0;
                break;
            case R1:
                index = this.getR1_Start();
                break;
            case R2:
                index = this.getR2_Start();
                break;
            case RV:
                index = this.getRV_Start();
                break;
            default:
                index = 0;
                break;
        }
        // check if index is in word length
        return index > wordLength ? wordLength : index;
    }

    /**
     * Fonction très utile pour remplacer un suffixe par une chaine de caractère
     * Elle prend en paramètre la region dans laquelle chercher le suffixe, une liste de suffixes et la chaine de caractère de remplacement
     * Elle est très utilisé dans les étapes 1 et 2
     * 
     * @param r
     * @param suffixes
     * @param replacement
     * @return renvoie true si le suffixe a été trouvé et remplacé sinon false
     */
    public boolean replaceSuffix(Region r, List<String> suffixes, String replacement) {

        String searchWord = word.substring(getSearchIndex(r));
        String longestSuffix = Stemming.getLongestSuffix(suffixes, searchWord);
        if (longestSuffix != null) {
            this.word = Stemming.replaceLast(word, longestSuffix, replacement);
            this.wordLength = word.length();
            // ? pas très propre
            this.calculDoStep2a(longestSuffix);
            return true;
        }
        return false;
    }

    /**
     * Vérfie si le suffixe passé en paramètre est présent dans la liste des suffixes de l'étape 2a
     * Si oui, on passe doStep2a à true
     * 
     * sert à savoir si on doit faire l'étape 2a ou non
     * 
     * @param suffix
     */
    public void calculDoStep2a(String suffix) {
        // si on a déjà trouvé un suffixe de l'étape 2a, il faut forcément faire l'étape 2a
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

    /**
     * Vérifie pour chaque région si le mot se termine par la chaine de caractère passée en paramètre
     * Si oui, on ajoute la région à la liste des régions retournées
     * 
     * utilisé dans l'étape 1
     * 
     * @param str
     * @return la liste des régions dans lesquelles le mot se termine par la chaine de caractère passée en paramètre
     */
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

    /**
     * Vérifie si un index se trouve dans une région passée en paramètre
     * @param r
     * @param index
     * @return true si l'index se trouve dans la région, false sinon
     */
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

    /**
     *  Utile pour ""ion" delete if in R2 and preceded by s or t" 
     *  Utilisé dans l'étape 4
     * 
     *  Vérifie si un suffixe est présent dans la région r 
     *  ET si le caractère précédent est dans une liste de caractères passée en paramètre 
     *  ET si le caractère précédent est dans la deuxième région passée en paramètre
     *  Un peu spécifique mais peut être réutilisé
     * @param r la région dans laquelle chercher le suffixe
     * @param suffixes la liste des suffixes à chercher
     * @param replacement la chaine de caractère de remplacement du suffixe
     * @param authorizedPrecedentLetter la liste des caractères demandés pour le caractère précédent
     * @param deleteIfnotPrecededByAuthorizedLetter inverse la condition sur le caractère précédent, si true on supprime si le caractère précédent n'est pas dans la liste
     * @param precedentLetterRegion la région dans laquelle chercher le caractère précédent (Si tout le mot est dans la région, on peut mettre Region.ALL)
     * @return true si le suffixe et le caractère précédent est trouvé, false sinon
     */
    public boolean deleteIfPrecededBySuffix(Region r, List<String> suffixes, String replacement,
            List<Character> authorizedPrecedentLetter, boolean deleteIfnotPrecededByAuthorizedLetter,
            Region precedentLetterRegion) {

        String searchWord = word.substring(getSearchIndex(r));
        String longestSuffix = Stemming.getLongestSuffix(suffixes, searchWord);
        if (longestSuffix != null) {
            int index = searchWord.lastIndexOf(longestSuffix);
            int precedentIndex = index - 1;
            // si le caractère précédent n'existe pas, on retourne faux
            if (precedentIndex < 0) {
                return false;
            }
            char precedentLetter = searchWord.charAt(precedentIndex);

            if (precedentLetterRegion != null) {
                if (!this.isInRegion(precedentLetterRegion, precedentIndex)) {
                    return false;
                }
            }

            if (deleteIfnotPrecededByAuthorizedLetter) {
                // si on doit supprimer si le caractère précédent n'est pas dans la liste des
                // caractères autorisés
                // alors on retourne faux si le caractère précédent est dans la liste des
                // caractères autorisés
                if (!authorizedPrecedentLetter.contains(precedentLetter)) {
                    return false;
                }
            } else {
                // si on doit supprimer si le caractère précédent est dans la liste des
                // caractères autorisés
                // alors on retourne faux si le caractère précédent n'est pas dans la liste des
                // caractères autorisés
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

    /**
     * comme son nom l'indique, supprime le dernier caractère du mot actuel
     * @return ne retourne rien
     */
    public void deleteLastLetter() {
        if (this.wordLength == 0) {
            return;
        }
        this.word = this.word.substring(0, this.word.length() - 1);
        this.wordLength = this.word.length();
    }
 
    // -----------------------------
    // GETTERS AND SETTERS
    // Certains getters font du lazy loading
    // -----------------------------

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
        // le mot pourrait avoir été modifié, on vérifie que l'index ne dépasse pas la longueur du mot
        if (this.RV_Start > this.wordLength) {
            this.RV_Start = this.wordLength;
        }
        return this.RV_Start;
    }

    public void setRV_Start(int rV_Start) {
        // si l'index est modifié, on supprime le cache de la région
        this.RV = null;
        this.RV_Start = rV_Start;
    }

    public int getR1_Start() {
        // le mot pourrait avoir été modifié, on vérifie que l'index ne dépasse pas la longueur du mot
        if (this.R1_Start > this.wordLength) {
            this.R1_Start = this.wordLength;
        }
        return this.R1_Start;
    }

    public void setR1_Start(int r1_Start) {
        // si l'index est modifié, on supprime le cache de la région
        this.R1 = null;
        this.R1_Start = r1_Start;
    }

    public int getR2_Start() {
        // le mot pourrait avoir été modifié, on vérifie que l'index ne dépasse pas la longueur du mot
        if (this.R2_Start > this.wordLength) {
            this.R2_Start = this.wordLength;
        }
        return this.R2_Start;
    }

    public void setR2_Start(int r2_Start) {
        // si l'index est modifié, on supprime le cache de la région
        this.R2 = null;
        this.R2_Start = r2_Start;
    }

    public String getR1() {
        // si la région n'a pas été calculée, on la calcule (lazy loading)
        if (this.R1 == null) {
            // le mot pourrait avoir été modifié, on vérifie que l'index ne dépasse pas la longueur du mot
            if (this.R1_Start > this.wordLength) {
                this.R1_Start = this.wordLength;
            }
            this.R1 = this.word.substring(this.R1_Start);
        }
        return this.R1;
    }

    public String getR2() {
        // si la région n'a pas été calculée, on la calcule (lazy loading)
        if (this.R2 == null) {
            // le mot pourrait avoir été modifié, on vérifie que l'index ne dépasse pas la longueur du mot
            if (this.R2_Start > this.wordLength) {
                this.R2_Start = this.wordLength;
            }
            this.R2 = this.word.substring(this.R2_Start);
        }
        return this.R2;
    }

    public String getRV() {
        // si la région n'a pas été calculée, on la calcule (lazy loading)
        if (this.RV == null) {
            // le mot pourrait avoir été modifié, on vérifie que l'index ne dépasse pas la longueur du mot
            if (this.RV_Start > this.wordLength) {
                this.RV_Start = this.wordLength;
            }
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
