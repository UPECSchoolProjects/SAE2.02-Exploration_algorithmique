package com.uwu;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Analyse {
    private static final Logger logger = LogManager.getLogger(Analyse.class);

    public static final List<String> motsVides = new ArrayList<String>(Arrays.asList("a", "b"));

    String filePath;

    public Analyse(String filePath) {
        this.filePath = filePath;
    }

    public String readFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.filePath));
        String ligne;
        StringBuilder sb = new StringBuilder();
        while ((ligne = br.readLine()) != null) {
            sb.append(ligne).append(" ");
        }
        br.close();

        return sb.toString();
    }

    public Map<String, Integer> calculerFrequences() {

        String text;
        try {
            text = this.readFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        // Diviser le texte en mots et compter leur fréquence
        Map<String, Integer> freq = new HashMap<>();
        String[] mots = text.split("\\W+");
        for (String mot : mots) {
            if (!motsVides.contains(mot)) {
                freq.put(mot, freq.getOrDefault(mot, 0) + 1);
            }
        }

        // Convertir la carte de fréquence en liste triée par ordre décroissant de fréquence
        List<Map.Entry<String, Integer>> listeFreq = new ArrayList<>(freq.entrySet());
        listeFreq.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // reconverti la liste en map
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : listeFreq) {
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }
}

