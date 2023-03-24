package com.uwu;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class analyse {

    public List<Map.Entry<String, Integer>> calculerFrequences(String cheminFichier, List<String> motsVides) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(cheminFichier));
        String ligne;
        StringBuilder sb = new StringBuilder();
        while ((ligne = br.readLine()) != null) {
            sb.append(ligne).append(" ");
        }
        br.close();

        // Diviser le texte en mots et compter leur fréquence
        Map<String, Integer> freq = new HashMap<>();
        String[] mots = sb.toString().split("\\W+");
        for (String mot : mots) {
            if (!motsVides.contains(mot)) {
                freq.put(mot, freq.getOrDefault(mot, 0) + 1);
            }
        }

        // Convertir la carte de fréquence en liste triée par ordre décroissant de fréquence
        List<Map.Entry<String, Integer>> listeFreq = new ArrayList<>(freq.entrySet());
        listeFreq.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        return listeFreq;
    }
}

