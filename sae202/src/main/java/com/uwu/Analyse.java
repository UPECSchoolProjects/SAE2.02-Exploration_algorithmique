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

public class Analyse {

    ArrayList<String> motsVides = new ArrayList<String>();
    private String filePath;

    public Analyse(String filePath) {
        this.filePath = filePath;
        try {
            this.lire_mot_vides();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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

    public Map<String, Integer> calculerFrequences() throws IOException { // Renvoie une Map avec les mots et leur
                                                                          // fréquence
        Map<String, Integer> freq = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) { // Ouvre le fichier
            String ligne;
            while ((ligne = br.readLine()) != null) { // Lit le fichier ligne par ligne
                String[] mots = ligne.split("\\W+"); // Sépare la ligne en mots
                for (String mot : mots) {
                    mot = mot.toLowerCase();  minuscule
                    char[] mot_array = mot.toCharArray();               
                    if (!motsVides.contains(mot) && mot.length() > 1)              // Vérifie que le mot n'est pas vide
                        freq.put(mot, freq.getOrDefault(mot, 0) + 1);  // Incrémente la fréquence du mot
                    }
                }
            }
        }

     

     
                                                                  // 
    publi

    
            
            Map<String, Integer> freq = calculerFrequences();
            for (Map.Entry<String, Integer> entry : freq.entrySet()) { // Parcourt la Map
                writer.append(entry.getKey());
                writer.append(";");
                writer.append(entry.getValue().toString());
                writer.append("\n");
            }
        }
 


