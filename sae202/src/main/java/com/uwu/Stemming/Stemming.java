package com.uwu.Stemming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Stemming {
    /*
     * Cette classe implémente l'algorithme de racinisation donnée dans le cahier des
     * charges.
     * 
     * Elle a été écrite par Maxime LOTTO dans le cadre du travail personnel de la SAE2.02
     */
    private static final Logger logger = LogManager.getLogger(Stemming.class);
    

    /**
     * Liste des voyelles utilisées dans l'algorithme de racinisation pour le français
     */
    private static final ArrayList<Character> vowels = new ArrayList<Character>(
            Arrays.asList('a', 'e', 'i', 'o', 'u', 'y', 'â', 'à',
                    'ë', 'é', 'ê', 'è', 'ï', 'î', 'ô', 'û', 'ù'));

    /** 
     * Regex utilisé pour trouver la région RV
     * il est calculé en fonction de la liste des voyelles
     * dans la fonction constructRegex
     */
    private Pattern RV_regex;
    /**
     * Regex qui trouve la première voyelle après une consonne
     * il est calculé en fonction de la liste des voyelles
     * dans la fonction constructRegex
     */
    private Pattern firstNonvowelsFollowingVowels;

    /**
     * Vérifie si un caractère est une voyelle (vowels est privé)
     * @param letter le caractère à vérifier
     * @return true si le caractère est une voyelle en français, false sinon
     */
    public static boolean isVowel(char letter) {
        return vowels.contains(letter);
    }

    /**
     * Le consigne est presque à chaque fois de trouver le suffixe le plus long dans la liste donnée
     * cette fonction le fait
     * @param word le mot à vérifier
     * @return le suffixe le plus long trouvé dans la liste, null si aucun suffixe n'est trouvé
     */
    public static String getLongestSuffix(List<String> suffixes, String word) {
        // si je l'initialise à null, on ne peut pas faire de .length() dessus
        // donc je l'initialise à une chaine vide
        String longestSuffix = "";
        // ? pourrait être remplacé par un .stream().filter().max()
        for (String suffix : suffixes) {
            if (word.endsWith(suffix) && suffix.length() > longestSuffix.length()) {
                longestSuffix = suffix;
            }
        }
        // si la chaine est vide, on retourne null
        return longestSuffix.length() > 0 ? longestSuffix : null;
    }

    /**
     * Cette fonction est utilisée pour remplacer le dernier suffixe trouvé dans un mot
     * @param string le mot dans lequel on veut remplacer le suffixe
     * @param toReplace le suffixe à remplacer
     * @param replacement la chaine de remplacement
     * @return le mot avec le suffixe remplacé
     */
    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }

    /**
     * constructeur par défaut
     * utilise la liste des voyelles par défaut
     */
    public Stemming() {
        constructRegex(vowels);
    }

    /**
     * Si on veut utiliser une liste de voyelles personnalisée (pas utilisé dans le projet)
     * @param vowels
     */
    public Stemming(ArrayList<Character> vowels) {
        constructRegex(vowels);
    }

    /**
     * Construit les regex utilisés dans l'algorithme de racinisation pour trouver la région RV, R1 et R2
     * @param vowels la liste des voyelles à utiliser
     * @return ne retourne rien, mais modifie les variables RV_regex et firstNonvowelsFollowingVowels
     */
    public void constructRegex(ArrayList<Character> vowels) {
        // cette fonction est utilisée pour construire les regex en fonction de la liste
        // des voyelles
        StringBuilder vowelsString = new StringBuilder();
        for (char vowel : vowels) {
            vowelsString.append(vowel);
        }

        // (?<=.) est une assertion positive de lookbehind qui vérifie qu'il y a un
        // caractère avant le groupe de capture
        // https://fr.javascript.info/regexp-lookahead-lookbehind
        // RV_regex = r'(?<=.)([aeiouyâàëéêèïîôûù])' (en python)
        RV_regex = Pattern.compile("(?<=.)([" + vowelsString.toString() + "])");

        // R1 est la région après la première consonne suivant une voyelle, ou la fin du
        // mot si il n'y a pas de consonne
        // R2 est la région après la première consonne suivant une voyelle dans R1, ou
        // la fin du mot si il n'y a pas de consonne
        firstNonvowelsFollowingVowels = Pattern
                .compile("[" + vowelsString.toString() + "]" + "[^" + vowelsString.toString() + "]");
    }

    /**
     * Etape préliminaire de l'algorithme de racinisation
     * Marque les voyelles qui ne sont pas considérées comme telles en les mettant en
     * majuscule car l'algo de racinisation ne match que sur les voyelles en minuscule
     * @param word
     * @return le mot marqué
     */
    public String markNonVowels(String word) {
        int letterIndex = 0;
        // plutot que de faire des .replace() à tout va sur le mot, on va le construire lettre par lettre
        StringBuilder markedWord = new StringBuilder();

        char lastLetter = ' '; // derniere lettre ajoutée au mot marqué (permet de ne pas devoir faire markedWord.to...)
        while (letterIndex < word.length()) {
            char letter = word.charAt(letterIndex); // lettre courante
            // la lettre suivante (si elle existe) sinon un espace
            char nextLetter = letterIndex < word.length() - 1 ? word.charAt(letterIndex + 1) : ' ';

            // rends le code ci-dessous plus lisible
            Boolean isPreviousLetterVowels = vowels.contains(lastLetter);
            Boolean isNextLetterVowels = vowels.contains(nextLetter);

            // on ne fait des opérations que si la lettre courante est pas une voyelle
            if (!vowels.contains(letter)) {
                // donc si la lettre courante n'est pas une voyelle, on l'ajoute au mot marqué tel quel
                lastLetter = letter;
            } else {
                if ((letter == 'u' || letter == 'i') && isPreviousLetterVowels && isNextLetterVowels) {
                    lastLetter = Character.toUpperCase(letter);
                } else if (letter == 'y' && (isPreviousLetterVowels || isNextLetterVowels)) {
                    lastLetter = Character.toUpperCase(letter);
                } else if (letter == 'u' && lastLetter == 'q') {
                    lastLetter = Character.toUpperCase(letter);
                } else if (letter == 'ë') {
                    // lastLetter est de type char, on ne peut y mettre une chaine de caractère
                    markedWord.append("H");
                    lastLetter = 'e';
                } else if (letter == 'ï') {
                    // lastLetter est de type char, on ne peut y mettre une chaine de caractère
                    markedWord.append("H");
                    lastLetter = 'i';
                } else {
                    // si aucune des conditions n'est remplie, on ajoute la lettre telle quelle
                    lastLetter = letter;
                }
            }

            markedWord.append(lastLetter);
            letterIndex += 1; // ne pas oublier d'incrémenter l'index de la lettre courante
        }

        return markedWord.toString();
    }

    /**
     * Etape préliminaire de l'algorithme de racinisation trouve l'index de la région RV
     * utilise la regex RV_regex
     * @param word le mot à traiter
     * @return l'index du début de la région RV // ! pas le substring de la région RV
     */
    public int find_RV(String word) {
        /*
         * If the word begins with two vowels, RV is the region after the third letter,
         * otherwise the region after the
         * first vowel not at the beginning of the word, or the end of the word if these
         * positions cannot be found.
         * (Exceptionally, par, col or tap, at the beginning of a word is also taken to
         * define RV as the region to their right.)
         */

        // return pos of the start of RV
        String firstThreeLetters = word.substring(0, 3);
        if (firstThreeLetters.equals("par") || firstThreeLetters.equals("col") || firstThreeLetters.equals("tap")) {
            return 3;
        } else if (vowels.contains(word.charAt(0)) && vowels.contains(word.charAt(1))) {
            return 3;
        } else {
            Matcher m = RV_regex.matcher(word);
            return m.find() ? m.end() : word.length();
        }
    }

    /**
     * Etape préliminaire de l'algorithme de racinisation trouve l'index de la région R1
     * utilise la regex firstNonvowelsFollowingVowels
     * 
     * @param word le mot à traiter
     * @return l'index du début la région R1 // ! pas le substring de la région R1
     */
    public int find_R1(String word) {
        /*
         * R1 is the region after the first non-vowel following a vowel, or the end of
         * the word if there is no such non-vowel.
         */

        // return pos of the start of R1
        Matcher m = firstNonvowelsFollowingVowels.matcher(word);
        return m.find() ? m.end() : word.length();
    }

    /**
     * Etape préliminaire de l'algorithme de racinisation trouve l'index de la région R2
     * utilise la regex firstNonvowelsFollowingVowels
     * @param word le mot à traiter
     * @param r1_start l'index du début de la région R1 (R2 se base sur le substring de R1)
     * @return l'index du début de la région R2 // ! pas le substring de la région R2
     */
    public int find_R2(String word, int r1_start) {
        /*
         * R2 is the region after the first non-vowel following a vowel in R1, or the
         * end
         * of the word if there is no such non-vowel.
         */

        // return pos of the start of R2
        Matcher m = firstNonvowelsFollowingVowels.matcher(word.substring(r1_start));
        return m.find() ? m.end() + r1_start : word.length();
    }

    /**
     * Fonction principale de l'algorithme de racinisation, elle appelle toutes les étapes ainsi que les étapes préliminaire
     * elle vérifie quel étape doit être appliquée
     * 
     * Elle se base sur les étapes de l'algorithme de racinisation écrites dans StemSteps (voir StemSteps.java)
     * 
     * @param word le mot à raciniser
     * @return le mot racinisé
     */
    public String stemm(String word) {
        word = word.toLowerCase().trim();

        // si le mot est trop court, on ne fait rien et on retourne le mot tel quel
        if (word.length() < 3) {
            return word;
        }

        // étapes préliminaires, il faut trouver les régions RV, R1 et R2
        // elles sont stockées dans un objet WordRV
        WordRV wordRV = new WordRV(word);
        wordRV.setWord(markNonVowels(wordRV.getWord()));

        wordRV.setRV_Start(find_RV(wordRV.getWord()));
        wordRV.setR1_Start(find_R1(wordRV.getWord()));
        wordRV.setR2_Start(find_R2(wordRV.getWord(), wordRV.getR1_Start()));

        // sert à vérrifier si l'étape 3 doit être appliquée
        String wordReferenceForStep3 = wordRV.getWord();

        // step 1
        // éffectue toutes les étapes de la liste step1
        for (IReplaceStep replaceStep : StemSteps.step1) {
            replaceStep.replace(wordRV);
        }

        // step 2
        // Do step 2a if either no ending was removed by step 1, or if one of endings
        // amment, emment, ment, ments was found.
        if(wordRV.isDoStep2a()) {
            wordReferenceForStep3 = wordRV.getWord();
            StemSteps.step2a.replace(wordRV);

            // Do step 2b if step 2a was done and removed an ending.
            if(wordRV.isFoundStep2aSuffixes()) {
                wordReferenceForStep3 = wordRV.getWord();
                StemSteps.step2b.replace(wordRV);
            }
        }

        // If the last step to be obeyed — either step 1, 2a or 2b — altered the word, do step 3
        if(!wordReferenceForStep3.equals(wordRV.getWord())) {
            StemSteps.step3.replace(wordRV);
        } else {
            // Alternatively, if the last step to be obeyed did not alter the word, do step 4
            StemSteps.step4.replace(wordRV);
        }

        // Always do steps 5 and 6.
        StemSteps.step5.replace(wordRV);
        StemSteps.step6.replace(wordRV);

        // step bonus
        StemSteps.stepFinally.replace(wordRV);

        return wordRV.getWord();
    }
}
