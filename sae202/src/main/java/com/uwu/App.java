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
import java.nio.file.Path;
import java.nio.file.Paths;
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
import com.uwu.Stemming.Stemming;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {


        Options options = new Options();
        Option filesOptions = Option.builder().option("f").longOpt("filename").hasArgs()
                .valueSeparator(',').desc("Liste de fichiers séparé par des virgules à traiter (incompatible avec -d)").build();
        options.addOption(filesOptions);

        Option directory = Option.builder().option("d").longOpt("directory").hasArg().desc("Répertoire à trairer (incompatible avec -f)").build();
        options.addOption(directory);

        Option outDir = Option.builder().option("o").longOpt("outDir").hasArg().required().desc("Répertoire de sortie des fichiers txt").build();
        options.addOption(outDir);

        Option AnalysisOutDir =
                Option.builder().option("ad").longOpt("analysisOutDir").hasArg().desc("Répertoire de sortie des fichiers csv d'analyse").build();
        options.addOption(AnalysisOutDir);

        Option UnifiedFileNameOption =
                Option.builder().option("uf").longOpt("unifiedFileName").hasArg().desc("Nom du fichier txt unifié (par défaut unified.txt)").build();
        options.addOption(UnifiedFileNameOption);

        // cette option permet de convertir tous les fichiers en un seul fichier
        Option unifiedOption = Option.builder().option("u").longOpt("unified").desc("Unifie les fichiers traité dans un gros fichier txt").build();
        options.addOption(unifiedOption);

        // cette option permet de reconvertir les fichiers même s'ils ont déjà été convertis
        Option rebuildOption = Option.builder().option("r").longOpt("rebuild").desc("Ecras les fichiers txt déjà présent dans le dossier de sortie").build();
        options.addOption(rebuildOption);

        Option skipAnalysisOption = Option.builder().option("s").longOpt("skipAnalysis").desc("Passe l'analyse, convertis seulement les fichiers en txt").build();
        options.addOption(skipAnalysisOption);

        Option verboseOption = Option.builder().option("v").longOpt("verbose").desc("Niveau de log debug").build();
        options.addOption(verboseOption);

        Option extraVerbose = Option.builder().option("vv").longOpt("extraVerbose").desc("Niveau de log Trace").build();
        options.addOption(extraVerbose);

        Option motVidePath = Option.builder().option("m").longOpt("motVidePath").hasArg().desc("Fichier txt des mots vides").build();
        options.addOption(motVidePath);

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
                logger.debug("Le répertoire spécifié n'existe pas, on tente de le créer");
                file.mkdirs();
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

            if(file.endsWith(".txt")) {
                logger.info("Le fichier " + file + " est déjà un fichier txt");
                convertedFiles.add(new File(path + file));
                continue;
            }

            IConverter converter = ConversionFactory.getConverter(path, file, "",
                    outDirFile.getAbsolutePath() + File.separator);
            String filenameWithoutExtension = file.substring(0, file.lastIndexOf("."));
            File convertedFilePath = new File(outDirFile.getAbsolutePath() + File.separator
                    + filenameWithoutExtension + ".txt");
            if (!cmd.hasOption("r") && convertedFilePath.exists()) {
                logger.info("Le fichier " + file + " a déjà été converti");
                convertedFiles.add(convertedFilePath);
                continue;
            }
            if(converter == null) {
                logger.error("Le fichier " + file + " n'est pas un fichier supporté");
                continue;
            }
            try {
                convertedFiles.add(converter.convert());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

            logger.trace("Liste des fichiers :");
            for (File f : convertedFiles) {
                logger.trace(getRelativePath(f.getAbsolutePath()));
            }

            if (cmd.hasOption("s")) {
                logger.info("Analyse des fichiers ignorée");
                System.exit(0);
            }
            String analysisOutDir =
                    cmd.hasOption("ad") ? cmd.getOptionValue("ad") : outDirFile.getAbsolutePath();
            if (!analysisOutDir.endsWith(File.separator)) {
                analysisOutDir += File.separator;
            }

            // create dir if it doesn't exist
            File analysisDir = new File(analysisOutDir);
            if (!analysisDir.exists()) {
                logger.debug("Le répertoire d'analyse spécifié ("+ getRelativePath(analysisDir.getAbsolutePath()) +") n'existe pas, il sera créé");
                analysisDir.mkdirs();
            }
            if (!analysisDir.isDirectory()) {
                logger.error("Le chemin de sortie d'analyse spécifié n'est pas un répertoire !");
                System.exit(-1);
            }

            File motVideFile = cmd.hasOption("m") ? new File(cmd.getOptionValue("m")) : null;
            if (motVideFile != null && !motVideFile.exists()) {
                logger.warn("Le fichier de mots vides spécifié n'existe pas ! (" + getRelativePath(motVideFile.getAbsolutePath()) + ")");
            }

            Stemming stemming = new Stemming();

            if (unified) {
                File unifiedFile = null;
                String UnifiedFileName = cmd.hasOption("uf") ? cmd.getOptionValue("uf") : "unified";
                UnifiedFileName = UnifiedFileName.replaceAll(".txt", "");
                try {
                    unifiedFile = unifyFiles(convertedFiles, outDirFile.getAbsolutePath(),
                            UnifiedFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (unifiedFile != null) {
                    logger.info("Fichier unifié créé : " + getRelativePath(unifiedFile.getAbsolutePath()));
                }

                logger.info("Analyse du fichier unifié...");

                Analyse analyseur = new Analyse(unifiedFile.getAbsolutePath(), null, stemming);
                File csvFile = null;
                try {
                    csvFile = new File(analysisOutDir + UnifiedFileName + ".csv");
                    analyseur.ecrireCSV(csvFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                logger.info("Analyse terminée");
                logger.info("Fichier CSV créé : " + getRelativePath(csvFile.getAbsolutePath()));
            } else {
                int nbFile = convertedFiles.size();
                int compteur = 0;
                logger.info("Analyse de " + nbFile + " fichier(s)...");
                for (File f : convertedFiles) {

                    Analyse analyseur = new Analyse(f.getAbsolutePath(), null, stemming);

                    String filenameWithoutExt =
                            f.getName().substring(0, f.getName().lastIndexOf("."));

                    try {
                        analyseur.ecrireCSV(analysisOutDir + filenameWithoutExt + ".csv");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    logger.info("Analyse de " + f.getName() + " terminée (" + compteur + "/"
                            + nbFile + ")");
                    logger.info(
                            "Fichier CSV créé : " + getRelativePath(analysisOutDir + filenameWithoutExt + ".csv"));
                    compteur++;
                }

                logger.info("Analyse terminée");
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
                logger.debug("Fichier trouvé : " + fileEntry.getName());
                files.add(fileEntry.getName());
            }
        }
        return files.toArray(new String[files.size()]);
    }

    public static String getRelativePath(String path) {
        // get cli directory
        Path currentRelativePath = Paths.get("").toAbsolutePath();
        Path param = Paths.get(path).toAbsolutePath();
        Path relativePath = currentRelativePath.relativize(param);
        return relativePath.toString();
    }
}
