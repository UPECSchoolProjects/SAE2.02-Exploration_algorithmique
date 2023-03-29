package com.uwu;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AnalyseTests {
    Map<String, Integer> exceptedMap = Map.ofEntries(Map.entry("pomme", 2), Map.entry("arbre", 2));


    @Test
    public void verifyAnalyse() {
        Analyse analyseur = new Analyse("TestsInputs/testFrequence.txt");

        Map<String, Integer> map;
        try {
            map = analyseur.calculerFrequences();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        assertEquals(map, exceptedMap);
    }
}
