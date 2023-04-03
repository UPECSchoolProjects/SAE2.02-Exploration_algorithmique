package com.uwu;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.uwu.Stemming.Stemming;

/**
 * Unit test for simple App.
 */
public class AnalyseTests {

    Map<String, AnalyseMot> exceptedMap = Map.ofEntries(Map.entry("pomm", new AnalyseMot("pomme", 0.5, 2)),
            Map.entry("arbre", new AnalyseMot("arbre", 0.5, 2)));

    @Test
    public void verifyAnalyse() {
        Analyse analyseur = new Analyse("TestsInputs/testFrequence.txt", Analyse.lire_mot_vides("mot_vide.txt"), new Stemming());

        Map<String, AnalyseMot> map;
        try {
            map = analyseur.calculerFrequences();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        for (Map.Entry<String, AnalyseMot> entry : map.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getKey() + " " + entry.getValue().getFrequence() + " " + entry.getValue().getOccurence());
            if(!exceptedMap.containsKey(entry.getKey()))
                throw new RuntimeException("La clé " + entry.getKey() + " n'est pas présente dans la map attendue");
            assertEquals(exceptedMap.get(entry.getKey()).getFrequence(),
                    entry.getValue().getFrequence(), 0);
            assertEquals(exceptedMap.get(entry.getKey()).getOccurence(),
                    entry.getValue().getOccurence(), 0);
        }
    }
}
