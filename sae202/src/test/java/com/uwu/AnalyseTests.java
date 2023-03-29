package com.uwu;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AnalyseTests {

    Map<String, AnalyseMot> exceptedMap = Map.ofEntries(Map.entry("pomme", new AnalyseMot(0.5, 2)),
            Map.entry("arbre", new AnalyseMot(0.5, 2)));

    @Test
    public void verifyAnalyse() {
        Analyse analyseur = new Analyse("TestsInputs/testFrequence.txt");

        Map<String, AnalyseMot> map;
        try {
            map = analyseur.calculerFrequences();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, AnalyseMot> entry : map.entrySet()) {
            assertEquals(exceptedMap.get(entry.getKey()).getFrequence(),
                    entry.getValue().getFrequence(), 0);
            assertEquals(exceptedMap.get(entry.getKey()).getOccurence(),
                    entry.getValue().getOccurence(), 0);
        }
    }
}
