package com.uwu;

import java.io.IOException;
import com.uwu.Conversion.PdfConverter;

public class App {

    public static void main(String[] args) {
        String pdfname = "dictionnaire_academie_francaise_5eme_edition";
        PdfConverter pdfConverter = new PdfConverter("HTMLTEST/", pdfname + ".pdf", "HTMLParsed/");
        pdfConverter.convert();
        String filePath = "HTMLParsed/" + pdfname + ".txt";
        String nomFichier = "./Serveur/files/frequences.csv";
        Analyse analyse = new Analyse(filePath);
        try {
            analyse.ecrireCSV(nomFichier); // Génère le fichier CSV
            System.out.println("Le fichier " + nomFichier + " a été généré avec succès."); // Affiche
                                                                                           // un
                                                                                           // message
                                                                                           // de
                                                                                           // succès
        } catch (IOException e) {
            System.err.println("Erreur lors de la génération du fichier CSV : " + e.getMessage()); // Affiche
                                                                                                   // un
                                                                                                   // message
                                                                                                   // d'erreur
            e.printStackTrace();
        }

    }
}
