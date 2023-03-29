package com.uwu;

import java.io.IOException;

public class App {

    public static void main(String[] args) {  
        String filePath = "HTMLParsed/html6-parsed.txt";
        String nomFichier = "frequences.csv";
        Analyse analyse = new Analyse(filePath); 
        try {
            analyse.ecrireCSV(nomFichier); // Génère le fichier CSV
            System.out.println("Le fichier " + nomFichier + " a été généré avec succès."); // Affiche un message de succès
        } catch (IOException e) {
            System.err.println("Erreur lors de la génération du fichier CSV : " + e.getMessage()); // Affiche un message d'erreur
            e.printStackTrace();
        }
    }
}
