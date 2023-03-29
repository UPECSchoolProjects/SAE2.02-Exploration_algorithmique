package com.uwu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AnalyseTests 
{
    Map<String, Integer> exceptedMap = Map.ofEntries(
        Map.entry("je", 2),
        Map.entry("vais", 2)
    );


    @Test
    public void verifyAnalyse()
    {
        Analyse analyseur = new Analyse("TestsInputs/testFrequence.txt");

        Map<String, Integer> map = analyseur.calculerFrequences();

        assertEquals(map, exceptedMap);
    }
}
