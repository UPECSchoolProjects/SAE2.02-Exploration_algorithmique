package com.uwu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.uwu.Conversion.ConversionFactory;
import com.uwu.Conversion.IConverter;
import com.uwu.Stemming.Stemming;

public class CLI {
    private static final Logger logger = LogManager.getLogger(CLI.class);

    public static boolean checkDir(File dirFile, String name, boolean create) {
        if (!dirFile.exists()) {
            if (create) {
                logger.info("Création du répertoire " + name + " (" + App.getRelativePath(dirFile.getAbsolutePath())
                        + ")");
                if (dirFile.mkdirs()) {
                    logger.info("Répertoire " + name + " créé avec succès");
                } else {
                    logger.error("Impossible de créer le répertoire " + name + " ("
                            + App.getRelativePath(dirFile.getAbsolutePath())
                            + ")");
                    return false;
                }
            } else {
                logger.error("Le répertoire de " + name + " spécifié n'existe pas ("
                        + App.getRelativePath(dirFile.getAbsolutePath()) + ")");
                return false;
            }
        }
        if (!dirFile.isDirectory()) {
            logger.error("Le chemin de " + name + " spécifié n'est pas un répertoire("
                    + App.getRelativePath(dirFile.getAbsolutePath()) + ")");
            return false;
        }

        return true;
    }

    private String[] args;
    private Options options;
    private CommandLine cmd;
    private File outDirFile;
    // défini individuellement si -f est utilisé sinon défini par -d
    private String inputFilePath;
    private ArrayList<FileObj> inputFiles; // liste des fichiers à traiter
    private boolean unified;
    private ArrayList<FileObj> convertedFiles;
    private String analysisOutDir;
    private File motVideFile;
    private Stemming stemming;
    private List<String> motsVide;
    private String keyText;
    private String classText;

    public CLI(String[] args) {
        this.args = args;
    }

    public void executeCLI() {
        this.options = constructOptions();
        this.cmd = commandParser();

        checkOutDir();
        getInputFiles();
        logger.debug("Les fichiers a traité ont été récupérés");

        logger.debug("inputFilePath : " + this.inputFilePath);

        this.keyText = cmd.hasOption(CLIOptions.KEY_TEXT.getShortOpt())
                ? cmd.getOptionValue(CLIOptions.KEY_TEXT.getShortOpt())
                : null;
        this.classText = cmd.hasOption(CLIOptions.CLASS_TEXT.getShortOpt())
                ? cmd.getOptionValue(CLIOptions.CLASS_TEXT.getShortOpt())
                : null;

        convertFiles();

        if (cmd.hasOption("s")) {
            logger.info("Analyse des fichiers ignorée");
            System.exit(0);
        }
        getAnalysisOutFolder();
        getMotsVide();

        if (this.motVideFile != null) {
            this.motsVide = Analyse.lire_mot_vides(this.motVideFile.getAbsolutePath());
        } else {
            this.motsVide = new ArrayList<String>();
        }

        this.stemming = new Stemming();

        this.unified = this.cmd.hasOption(CLIOptions.IS_UNIFIED.getShortOpt());

        if (unified) {
            FileObj unifiedFile = null;
            String UnifiedFileName = cmd.hasOption(CLIOptions.UNIFIED_FILENAME.getShortOpt())
                    ? cmd.getOptionValue(CLIOptions.UNIFIED_FILENAME.getShortOpt())
                    : "unified";
            UnifiedFileName = UnifiedFileName.replaceAll(".txt", "");
            try {
                unifiedFile = App.unifyFiles(convertedFiles, outDirFile.getAbsolutePath(),
                        UnifiedFileName);
            } catch (IOException e) {
                logger.error("Impossible de créer le fichier unifié : " + e.getMessage());
                System.exit(-1);
            }

            logger.info("Fichier unifié créé : " + App.getRelativePath(unifiedFile.getFullPath()));

            logger.info("Analyse du fichier unifié...");
            analyeurClI(unifiedFile);
            logger.info("Analyse terminée");
        } else {
            int nbFile = convertedFiles.size();
            int compteur = 0;
            logger.info("Analyse de " + nbFile + " fichier(s)...");
            for (FileObj f : convertedFiles) {

                analyeurClI(f);
                logger.info("Analyse de " + f.getName() + " terminée (" + compteur + "/"
                        + nbFile + ")");
                compteur++;
            }

            logger.info("Analyse terminée");
        }
    }

    /**
     * Renvoie un objet options avec toutes les options possibles
     * utile pour le CLI
     * 
     * @return Options possibles de la commande
     */
    public Options constructOptions() {

        Options options = new Options();

        for (CLIOptions option : CLIOptions.values()) {
            options.addOption(option.toOption());
        }

        return options;
    }

    /**
     * Parse les arguments de la commande en fonction des options
     * 
     * @param options Options de la commande
     * @param args    args dans le main
     * @return CommandLine contenant les arguments parsé de la commande
     */
    public CommandLine commandParser() {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(this.options, this.args);

            if (!cmd.hasOption(CLIOptions.INPUTDIR.getShortOpt())
                    && !cmd.hasOption(CLIOptions.INPUTFILES.getShortOpt())) {
                logger.info("Vous devez spécifier au moins un fichier ou un répertoire");
                System.exit(-1);
            }
            if (cmd.hasOption(CLIOptions.INPUTDIR.getShortOpt())
                    && cmd.hasOption(CLIOptions.INPUTFILES.getShortOpt())) {
                logger.info("Vous ne pouvez pas spécifier un répertoire et un fichier");
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

        if (cmd.hasOption(CLIOptions.VERBOSE.getShortOpt())) {
            Logger logger = LogManager.getRootLogger();
            Configurator.setAllLevels(logger.getName(), Level.getLevel("DEBUG"));
            logger.info("Mode verbeux (DEBUG) activé");
        }

        if (cmd.hasOption(CLIOptions.EXTRA_VERBOSE.getShortOpt())) {
            Logger logger = LogManager.getRootLogger();
            Configurator.setAllLevels(logger.getName(), Level.getLevel("TRACE"));
            logger.info("Mode extra-verbeux (TRACE) activé");
        }

        return cmd;
    }

    public void checkOutDir() {
        File outDirFile = new File(this.cmd.getOptionValue(CLIOptions.OUTPUTDIR.getShortOpt()));

        if (!checkDir(outDirFile, "sortie", true)) {
            System.exit(-1);
        }

        this.outDirFile = outDirFile;
    }

    public void getInputFiles() {
        this.inputFiles = new ArrayList<FileObj>();
        String[] files;
        if (this.cmd.hasOption(CLIOptions.INPUTFILES.getShortOpt())) {
            files = cmd.getOptionValues(CLIOptions.INPUTFILES.getShortOpt());
            for (String file : files) {
                this.inputFiles.add(new FileObj(file));
            }
            this.inputFilePath = null;
        } else {
            // read all files in the directory
            File inputDirFile = new File(this.cmd.getOptionValue(CLIOptions.INPUTDIR.getShortOpt()));
            if (!checkDir(inputDirFile, "d'entrée", false)) {
                System.exit(-1);
            }
            files = App.listFilesForFolder(inputDirFile);
            this.inputFilePath = inputDirFile.getAbsolutePath() + File.separator;
            logger.debug("inputFilePath : " + this.inputFilePath);
            for (String file : files) {
                logger.debug("File : " + file);
                logger.debug("FUll path : " + this.inputFilePath + file);
                this.inputFiles.add(new FileObj(this.inputFilePath + file));
            }
        }
    }

    public void convertFiles() {
        this.convertedFiles = new ArrayList<FileObj>();
        for (FileObj file : this.inputFiles) {
            // get path and filename separately
            String path = this.inputFilePath;
            if (this.cmd.hasOption(CLIOptions.INPUTFILES.getShortOpt())) {
                path = file.getPath();
            }
            logger.debug("Path : " + path + " File : " + file.getName());

            if (file.getExtension().equalsIgnoreCase("txt")) {
                logger.info("Le fichier " + file.getName() + " est déjà un fichier txt");
                convertedFiles.add(file);
                continue;
            }

            logger.debug("Out dir : " + this.outDirFile.getAbsolutePath() + File.separator);

            IConverter converter = ConversionFactory.getConverter(file,
                    this.outDirFile.getAbsolutePath() + File.separator, this.keyText, this.classText);
            File convertedFilePath = new File(
                    this.outDirFile.getAbsolutePath() + File.separator + file.getNameWithAnotherExt("txt"));
            if (!this.cmd.hasOption(CLIOptions.REBUILD.getShortOpt()) && convertedFilePath.exists()) {
                logger.info("Le fichier " + file.getName() + " a déjà été converti");
                convertedFiles.add(new FileObj(convertedFilePath.getAbsolutePath()));
                continue;
            }
            if (converter == null) {
                logger.error("Le fichier " + file + " n'est pas un fichier supporté");
                continue;
            }
            try {
                convertedFiles.add(new FileObj(converter.convert().getAbsolutePath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logger.trace("Liste des fichiers :");
        for (FileObj f : convertedFiles) {
            logger.trace(App.getRelativePath(f.getFullPath()));
        }
    }

    public void getAnalysisOutFolder() {
        this.analysisOutDir = this.cmd.hasOption(CLIOptions.OUTPUT_ANALYSIS.getShortOpt())
                ? this.cmd.getOptionValue(CLIOptions.OUTPUT_ANALYSIS.getShortOpt())
                : this.outDirFile.getAbsolutePath();
        if (!this.analysisOutDir.endsWith(File.separator)) {
            this.analysisOutDir += File.separator;
        }

        // create dir if it doesn't exist
        File analysisDir = new File(this.analysisOutDir);
        if (!checkDir(analysisDir, "sortie d'analyse", true)) {
            System.exit(-1);
        }
    }

    public void getMotsVide() {
        this.motVideFile = this.cmd.hasOption(CLIOptions.MOTS_VIDE_PATH.getShortOpt())
                ? new File(this.cmd.getOptionValue(CLIOptions.MOTS_VIDE_PATH.getShortOpt()))
                : null;
        if (this.motVideFile != null && !this.motVideFile.exists()) {
            logger.warn("Le fichier de mots vides spécifié n'existe pas ! ("
                    + App.getRelativePath(this.motVideFile.getAbsolutePath()) + ")");
        }
    }

    public void analyeurClI(FileObj file) {
        Analyse analyseur = new Analyse(file.getFullPath(), this.motsVide, stemming);
        File csvFile = null;
        try {
            csvFile = new File(analysisOutDir + file.getNameWithAnotherExt("csv"));
            analyseur.ecrireCSV(csvFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Fichier CSV créé : " + App.getRelativePath(csvFile.getAbsolutePath()));
    }
}
