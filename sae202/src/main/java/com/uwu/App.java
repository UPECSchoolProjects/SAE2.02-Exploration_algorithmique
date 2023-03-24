package com.uwu;

import com.uwu.Conversion.ConversionFactory;
import com.uwu.Conversion.HTMLConverter;
import com.uwu.Conversion.IConverter;
import com.uwu.Conversion.ConversionFactory;
import com.uwu.Conversion.IConverter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        // HTMLConverter html = new HTMLConverter("HTMLTEST/Histoire de Neuilly par
        // lAbbé Bellanger - Wikisource.html", "HTMLParsed/", "prp-pages-output");
        // html.convert();
        HTMLConverter html = new HTMLConverter("HTMLTEST/", "html6.html", "HTMLParsed/", "text");

        html.convert();

        // IConverter cf = ConversionFactory.getConverter("test.pdf");

        // cf.convert();

        // File test = new File("test.txt");
        // System.out.println(test);

        // analyse analyseur = new analyse();
        // List<String> motsVides = Arrays.asList("le", "la", "de", "du", "et", "ou",
        // "mais", "donc", "pour", "par", "avec", "sans");
        // String cheminFichier = "test.txt";
        // try {
        // List<Map.Entry<String, Integer>> listeFreq =
        // analyseur.calculerFrequences(cheminFichier, motsVides);
        // for (Map.Entry<String, Integer> entry : listeFreq) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // }

        // } catch (IOException e) {
        // e.printStackTrace();
        // }

    }
}
