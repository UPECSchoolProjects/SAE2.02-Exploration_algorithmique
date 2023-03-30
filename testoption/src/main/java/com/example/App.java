package com.example;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        Options options = new Options();

        // Création de l'option -o, ou --out-file, avec un argument et obligatoire
        Option outFileOption = Option.builder().option("f").longOpt("filename")
                .desc("fichier entrée").required().hasArgs().valueSeparator(',').build();

        // L'option est ajoutée à la liste
        options.addOption(outFileOption);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            String[] a = cmd.getOptionValues("f");
            for (String string : a) {
                System.out.println(string);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Hello World!");
    }
}
