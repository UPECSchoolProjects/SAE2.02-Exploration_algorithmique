package com.uwu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

public class App {
    public static void main(String[] args) throws Exception {

        // HTMLConverter html = new HTMLConverter("HTMLTEST/Histoire de Neuilly par
        // lAbbé Bellanger - Wikisource.html", "HTMLParsed/", "prp-pages-output");
        // html.convert();
        // HTMLConverter html = new HTMLConverter("HTMLTEST/html6.html", "HTMLParsed/");

        // html.convert();

        // IConverter cf = ConversionFactory.getConverter("test.pdf");

        // cf.convert();

        // File test = new File("test.txt");
        // System.out.println(test);

        analyse analyseur = new analyse();
        List<String> motsVides = Arrays.asList("le", "la", "de", "du", "et", "ou", "mais", "donc", "pour", "par",
                "avec", "sans");
        String cheminFichier = "test.txt";
        try {
            List<Map.Entry<String, Integer>> listeFreq = analyseur.calculerFrequences(cheminFichier, motsVides);
            for (Map.Entry<String, Integer> entry : listeFreq) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
