package com.uwu;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.uwu.Stemming.Stemming;
import com.uwu.Stemming.WordRV;

/**
 * Unit test for simple App.
 */
public class StemmingTest {
    Map<String, String> test_mark_non_vowels = Map.ofEntries(
            Map.entry("jouer", "joUer"),
            Map.entry("ennuie", "ennuIe"),
            Map.entry("yeux", "Yeux"),
            Map.entry("quand", "qUand"),
            Map.entry("croyiez", "croYiez"));

    @Test
    public void testMarkNonVowels() {
        Stemming stemmer = new Stemming();

        for (Map.Entry<String, String> entry : test_mark_non_vowels.entrySet()) {
            String word = entry.getKey();
            String expected = entry.getValue();
            String result = stemmer.markNonVowels(word);
            assertEquals(expected, result);
        }
    }

    List<WordRV> test_RV = Arrays.asList(
            new WordRV("aimer", "er"),
            new WordRV("adorer", "rer"),
            new WordRV("voler", "ler"),
            new WordRV("tapis", "is"));

    @Test
    public void testRV() {
        Stemming stemmer = new Stemming();

        for (WordRV entry : test_RV) {
            String word = entry.getBaseWord();
            String expected = entry.getRV();
            int result = stemmer.find_RV(word);
            assertEquals(expected, word.substring(result));
        }
    }

    List<WordRV> test_R1_R2 = Arrays.asList(
            new WordRV("fameusement", "eusement", "ement"),
            new WordRV("beautiful", "iful", "ul"),
            new WordRV("beauty", "y", ""),
            new WordRV("animadversion", "imadversion", "adversion"),
            new WordRV("sprinkled", "kled", ""),
            new WordRV("eucharist", "harist", "ist"));

    @Test
    public void testR1R2() {
        Stemming stemmer = new Stemming();

        for (WordRV entry : test_R1_R2) {
            String word = entry.getBaseWord();
            String expectedR1 = entry.getR1();
            String expectedR2 = entry.getR2();
            int resultR1 = stemmer.find_R1(word);
            int resultR2 = stemmer.find_R2(word, resultR1);

            assertEquals(expectedR1, word.substring(resultR1));
            assertEquals(expectedR2, word.substring(resultR2));
        }
    }

    Map<String, String> test_step1 = Map.ofEntries(
            Map.entry("importance", "import"),
            Map.entry("adaptation", "adapt"),
            Map.entry("électrication", "électr")
    );

    @Test
    public void testStep1() {
        Stemming stemmer = new Stemming();

        for (Map.Entry<String, String> entry : test_step1.entrySet()) {
            String word = entry.getKey();
            String expected = entry.getValue();
            String result = stemmer.stemm(word);
            assertEquals(expected, result);
        }
    }
}
