package com.uwu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.uwu.Conversion.ConversionFactory;
import com.uwu.Conversion.IConverter;
import com.uwu.Conversion.PdfConverter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {


        Options options = new Options();
        Option filesOptions = Option.builder().option("f").longOpt("filename").hasArgs()
                .valueSeparator(',').build();
        options.addOption(filesOptions);

        Option directory = Option.builder().option("d").longOpt("directory").hasArg().build();
        options.addOption(directory);

        Option outDir = Option.builder().option("o").longOpt("outDir").hasArg().required().build();
        options.addOption(outDir);

        Option AnalysisOutDir = Option.builder().option("ad").longOpt("analysisOutDir").hasArg().build();
        options.addOption(AnalysisOutDir);

        Option UnifiedFileNameOption = Option.builder().option("uf").longOpt("unifiedFileName").hasArg().build();
        options.addOption(UnifiedFileNameOption);

        // cette option permet de convertir tous les fichiers en un seul fichier
        Option unifiedOption = Option.builder().option("u").longOpt("unified").build();
        options.addOption(unifiedOption);

        // cette option permet de reconvertir les fichiers même s'ils ont déjà été convertis
        Option rebuildOption = Option.builder().option("r").longOpt("rebuild").build();
        options.addOption(rebuildOption);

        Option skipAnalysisOption = Option.builder().option("s").longOpt("skipAnalysis").build();
        options.addOption(skipAnalysisOption);

        Option verboseOption = Option.builder().option("v").longOpt("verbose").build();
        options.addOption(verboseOption);

        Option extraVerbose = Option.builder().option("vv").longOpt("extraVerbose").build();
        options.addOption(extraVerbose);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (!cmd.hasOption("directory") && !cmd.hasOption("filename")) {
                logger.info("Vous devez spécifier au moins un fichier ou un répertoire");
                System.exit(-1);
            }
        } catch (ParseException e) {
            // print help
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("myapp", "", options, "", true);
            logger.info("Cette commande n'est pas valide : " + e.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        if (cmd.hasOption("verbose")) {
            Logger logger = LogManager.getRootLogger();
            Configurator.setAllLevels(logger.getName(), Level.getLevel("DEBUG"));
            logger.info("Mode verbeux (DEBUG) activé");
        }

        if (cmd.hasOption("extraVerbose")) {
            Logger logger = LogManager.getRootLogger();
            Configurator.setAllLevels(logger.getName(), Level.getLevel("TRACE"));
            logger.info("Mode extra-verbeux (TRACE) activé");
        }

        File outDirFile = new File(cmd.getOptionValue("outDir"));
        if (!outDirFile.exists()) {
            logger.error("Le répertoire de sortie spécifié n'existe pas ("
                    + outDirFile.getAbsolutePath() + ")");
            System.exit(-1);
        }
        if (!outDirFile.isDirectory()) {
            logger.error("Le chemin de sortie spécifié n'est pas un répertoire("
                    + outDirFile.getAbsolutePath() + ")");
            System.exit(-1);
        }

        String[] files;
        String path = "";
        if (cmd.hasOption("filename")) {
            files = cmd.getOptionValues("filename");
        } else {
            // read all files in the directory
            File file = new File(cmd.getOptionValue("directory"));
            if (!file.exists()) {
                logger.error("Le répertoire spécifié n'existe pas");
                System.exit(-1);
            }
            if (!file.isDirectory()) {
                logger.error("Le chemin spécifié n'est pas un répertoire");
                System.exit(-1);
            }
            files = listFilesForFolder(file);
            path = file.getAbsolutePath() + File.separator;
        }

        boolean unified = cmd.hasOption("u");

        logger.debug(path);

        ArrayList<File> convertedFiles = new ArrayList<File>();
        for (String file : files) {
            // get path and filename separately
            if (cmd.hasOption("filename")) {
                path = file.substring(0, file.lastIndexOf(File.separator) + 1);
                file = file.substring(file.lastIndexOf(File.separator) + 1);
            }
            logger.debug("Path : " + path + " File : " + file);
            IConverter converter = ConversionFactory.getConverter(path, file, "",
                    outDirFile.getAbsolutePath() + File.separator);
            String filenameWithoutExtension = file.substring(0, file.lastIndexOf("."));
            if (!cmd.hasOption("r") && new File(outDirFile.getAbsolutePath() + File.separator
                    + filenameWithoutExtension + ".txt").exists()) {
                logger.info("Le fichier " + file + " a déjà été converti");
                continue;
            }
            try {
                convertedFiles.add(converter.convert());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cmd.hasOption("s")) {
                logger.info("Analyse des fichiers ignorée");
                System.exit(0);
            }
            String analysisOutDir = cmd.hasOption("ad") ? cmd.getOptionValue("ad") : outDirFile.getAbsolutePath();
            if(!analysisOutDir.endsWith(File.separator)) {
                analysisOutDir += File.separator;
            }

            // create dir if it doesn't exist
            File analysisDir = new File(analysisOutDir);
            if(!analysisDir.isDirectory()) {
                logger.error("Le chemin de sortie d'analyse spécifié n'est pas un répertoire !");
                System.exit(-1);
            }
            if (!analysisDir.exists()) {
                analysisDir.mkdir();
            }

            if (unified) {
                File unifiedFile = null;
                try {
                    String UnifiedFileName = cmd.hasOption("uf") ? cmd.getOptionValue("uf") : "unified";
                    UnifiedFileName = UnifiedFileName.replaceAll(".txt", "");
                    unifiedFile =
                            unifyFiles(convertedFiles, outDirFile.getAbsolutePath(), UnifiedFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (unifiedFile != null) {
                    logger.info("Fichier unifié créé : " + unifiedFile.getAbsolutePath());
                }

                logger.info("Analyse du fichier unifié...");

                Analyse analyseur = new Analyse(unifiedFile.getAbsolutePath());
                try {
                    analyseur.ecrireCSV(analysisOutDir + "unified.csv");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                logger.info("Analyse terminée");
                logger.info("Fichier CSV créé : " + analysisOutDir + "unified.csv");
            } else {
                int nbFile = convertedFiles.size();
                int compteur = 0;
                logger.info("Analyse de " + nbFile + " fichier(s)...");
                for (File f : convertedFiles) {

                    Analyse analyseur = new Analyse(f.getAbsolutePath());

                    String filenameWithoutExt =
                            f.getName().substring(0, f.getName().lastIndexOf("."));

                    try {
                        analyseur.ecrireCSV(analysisOutDir + filenameWithoutExt + ".csv");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    logger.info("Analyse de " + f.getName() + " terminée (" + compteur + "/"
                            + nbFile + ")");
                    logger.info("Fichier CSV créé : " + analysisOutDir + filenameWithoutExt + ".csv");
                    compteur++;
                }

                logger.info("Analyse terminée");
            }
        }
    }

    public static File unifyFiles(ArrayList<File> files, String outDir, String filename)
            throws IOException {
        // read file
        File file = new File(outDir + File.separator + filename + ".txt");

        // if file exists, delete it
        if (file.exists()) {
            file.delete();
        }

        // create file
        file.createNewFile();

        // create OutputStreamWriter
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        BufferedWriter bw = new BufferedWriter(osw);

        // write to file
        for (File f : files) {
            InputStream ips = new FileInputStream(f);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
            }
            br.close();
        }

        bw.close();
        return file;
    }

    public static String[] listFilesForFolder(final File folder) {
        ArrayList<String> files = new ArrayList<String>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                System.out.println(fileEntry.getName());
                files.add(fileEntry.getName());
            }
        }
        return files.toArray(new String[files.size()]);
    }
}
