package com.uwu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Analyse {
    private static final Logger logger = LogManager.getLogger(App.class);

    static private java.util.Map<String, String> exceptedMap =
            java.util.Map.ofEntries(java.util.Map.entry("é", "e"), java.util.Map.entry("è", "e"),
                    java.util.Map.entry("ê", "e"), java.util.Map.entry("à", "a"),
                    java.util.Map.entry("â", "a"), java.util.Map.entry("î", "i"),
                    java.util.Map.entry("ï", "i"), java.util.Map.entry("ô", "o"),
                    java.util.Map.entry("û", "u"), java.util.Map.entry("ù", "u"),
                    java.util.Map.entry("ç", "c"), java.util.Map.entry("œ", "oe"),
                    java.util.Map.entry("æ", "ae"), java.util.Map.entry("ë", "e"),
                    java.util.Map.entry("ü", "u"), java.util.Map.entry("ÿ", "y"),
                    java.util.Map.entry("Æ", "Ae"), java.util.Map.entry("Ø", "Oe"),
                    java.util.Map.entry("Å", "Aa"), java.util.Map.entry("Þ", "Th"),
                    java.util.Map.entry("ß", "ss")
                    );
    ArrayList<String> motsVides = new ArrayList<String>();
    private String filePath;

    public Analyse(String filePath) {
        this.filePath = filePath;
        try {
            this.lire_mot_vides();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lire_mot_vides() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("mot_vide.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            motsVides.add(line);
        }
    }

    public Map<String, Integer> calculerFrequences() throws IOException { // Renvoie une Map avec
                                                                          // les mots et leur
                                                                          // fréquence
        Map<String, Integer> freq = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) { // Ouvre le fichier
            String ligne;
            while ((ligne = br.readLine()) != null) { // Lit le fichier ligne par ligne
                String[] mots = ligne.split("\\W+"); // Sépare la ligne en mots
                for (String mot : mots) {
                    mot = mot.toLowerCase(); // Met le mot en minuscule
                    mot = mot.replaceAll("[^a-z]", ""); // Supprime les caractères non alphabétiques
                    char[] mot_array = mot.toCharArray(); // Convertit le mot en tableau de
                                                          // caractères
                    for (int i = 0; i < mot_array.length; i++) { // Parcourt le tableau de
                                                                 // caractères
                        if (exceptedMap.containsKey(String.valueOf(mot_array[i]))) { // Vérifie si
                                                                                     // le caractère
                                                                                     // est dans la
                                                                                     // Map
                            mot_array[i] = exceptedMap.get(String.valueOf(mot_array[i])).charAt(0); // Remplace
                                                                                                    // le
                                                                                                    // caractère
                        }
                    }
                    mot = new String(mot_array); // Convertit le tableau de caractères en String
                    logger.trace("mot: " + mot + " - length: " + mot.length());
                    if ((!motsVides.contains(mot)) && mot.length() > 0) { // Vérifie que le mot n'est
                                                                        // pas vide
                        freq.put(mot, freq.getOrDefault(mot, 0) + 1); // Incrémente la fréquence du
                                                                      // mot
                    }
                }
            }
        }
        logger.trace("freq: " + freq);
        return freq;
    }

    //
    public void ecrireCSV(String nomFichier) throws IOException { // Génère un fichier CSV avec les
                                                                  // mots et leur
                                                                  // fréquence
        try (FileWriter writer = new FileWriter(nomFichier)) {
            writer.append("MOT");
            writer.append(";");
            writer.append("FREQUENCE");
            writer.append("\n");

            Map<String, Integer> freq = calculerFrequences();
            for (Map.Entry<String, Integer> entry : freq.entrySet()) { // Parcourt la Map
                writer.append(entry.getKey());
                writer.append(";");
                writer.append(entry.getValue().toString());
                writer.append("\n");
            }
        }
    }
}
