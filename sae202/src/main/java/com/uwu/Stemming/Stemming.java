package com.uwu.Stemming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stemming {
    /*
     * Cette classe implémente l'algorithme de racinisation donnée dans le cahier des
     * charges.
     * 
     * Elle a été écrite par Maxime LOTTO dans le cadre du travail personnel de la SAE2.02
     */

    private static final ArrayList<Character> vowels = new ArrayList<Character>(
            Arrays.asList('a', 'e', 'i', 'o', 'u', 'y', 'â', 'à',
                    'ë', 'é', 'ê', 'è', 'ï', 'î', 'ô', 'û', 'ù'));

    private Pattern RV_regex;
    private Pattern firstNonvowelsFollowingVowels;

    public static boolean isVowel(char letter) {
        return vowels.contains(letter);
    }

    public static String getLongestSuffix(List<String> suffixes, String word) {
        String longestSuffix = "";
        for (String suffix : suffixes) {
            if (word.endsWith(suffix) && suffix.length() > longestSuffix.length()) {
                longestSuffix = suffix;
            }
        }
        return longestSuffix.length() > 0 ? longestSuffix : null;
    }

    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }

    public Stemming() {
        constructRegex(vowels);
    }

    public Stemming(ArrayList<Character> vowels) {
        constructRegex(vowels);
    }

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

    public String markNonVowels(String word) {
        int letterIndex = 0;
        StringBuilder markedWord = new StringBuilder();

        char lastLetter = ' '; // derniere lettre ajoutée au mot marqué
        while (letterIndex < word.length()) {
            char letter = word.charAt(letterIndex);
            char nextLetter = letterIndex < word.length() - 1 ? word.charAt(letterIndex + 1) : ' ';

            Boolean isPreviousLetterVowels = vowels.contains(lastLetter);
            Boolean isNextLetterVowels = vowels.contains(nextLetter);

            if (!vowels.contains(letter)) {
                lastLetter = letter;
            } else {
                if ((letter == 'u' || letter == 'i') && isPreviousLetterVowels && isNextLetterVowels) {
                    lastLetter = Character.toUpperCase(letter);
                } else if (letter == 'y' && (isPreviousLetterVowels || isNextLetterVowels)) {
                    lastLetter = Character.toUpperCase(letter);
                } else if (letter == 'u' && lastLetter == 'q') {
                    lastLetter = Character.toUpperCase(letter);
                } else if (letter == 'ë') {
                    markedWord.append("H");
                    lastLetter = 'e';
                } else if (letter == 'ï') {
                    markedWord.append("H");
                    lastLetter = 'i';
                } else {
                    lastLetter = letter;
                }
            }

            markedWord.append(lastLetter);
            letterIndex += 1;
        }

        return markedWord.toString();
    }

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

    public int find_R1(String word) {
        /*
         * R1 is the region after the first non-vowel following a vowel, or the end of
         * the word if there is no such non-vowel.
         */

        // return pos of the start of R1
        Matcher m = firstNonvowelsFollowingVowels.matcher(word);
        return m.find() ? m.end() : word.length();
    }

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

    public String stemm(String word) {
        WordRV wordRV = new WordRV(word);
        wordRV.setWord(markNonVowels(wordRV.getWord()));

        wordRV.setRV_Start(find_RV(wordRV.getWord()));
        wordRV.setR1_Start(find_R1(wordRV.getWord()));
        wordRV.setR2_Start(find_R2(wordRV.getWord(), wordRV.getR1_Start()));

        String wordReferenceForStep3 = wordRV.getWord();

        // step 1
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
