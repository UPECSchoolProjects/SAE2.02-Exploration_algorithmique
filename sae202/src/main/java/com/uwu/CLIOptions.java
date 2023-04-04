package com.uwu;

import org.apache.commons.cli.Option;

public enum CLIOptions {
    HELP("h", "help", NbArgs.NONE, "Affiche l'aide", false),
    INPUTFILES("f", "filename", NbArgs.MULTIPLE, "Liste de fichiers séparé par des virgules à traiter (incompatible avec -d)", false),
    INPUTDIR("d", "inputdir", NbArgs.ONE, "Dossier à traiter (incompatible avec -f)", false),
    OUTPUTDIR("txto", "txtOutDir", NbArgs.ONE, "Répertoire de sortie des fichiers txt", true),
    OUTPUT_ANALYSIS("ao", "analysisOutDir", NbArgs.ONE, "Répertoire de sortie des fichiers csv d'analyse", false),
    UNIFIED_FILENAME("uf", "unifiedFilename", NbArgs.ONE, "Nom du fichier unifié", false),
    IS_UNIFIED("u", "unified", NbArgs.NONE, "Unifie les fichiers traité dans un gros fichier txt puis analyse", false),
    REBUILD("r", "rebuild", NbArgs.NONE, "Ecrase les fichiers txt déjà présent dans le dossier de sortie", false),
    SKIP_ANALYSIS("s", "skipAnalysis", NbArgs.NONE, "Passe l'analyse, convertis seulement les fichiers en txt", false),
    MOTS_VIDE_PATH("m", "motsVidePath", NbArgs.ONE, "Chemin vers le fichier motsVide.txt", false),
    CLASS_TEXT("c", "classText", NbArgs.ONE, "Classe de texte à traiter (pour fichier HTML). par défaut \"text\"", false),
    KEY_TEXT("k", "keyText", NbArgs.ONE, "Attribut ou chercher la classe. par défaut \"class\"", false),
    VERBOSE("v", "verbose", NbArgs.NONE, "Affiche les logs de debug", false),
    EXTRA_VERBOSE("vv", "extraVerbose", NbArgs.NONE, "Affiche les logs de trace", false);

    enum NbArgs {
        NONE,
        ONE,
        MULTIPLE
    }
    
    private String shortOpt;
    private String longOpt;
    private NbArgs nbArgs;
    private String description;
    private boolean isRequired;

    private CLIOptions(String shortOpt, String longOpt, NbArgs nbArgs, String description, boolean isRequired) {
        this.shortOpt = shortOpt;
        this.longOpt = longOpt;
        this.nbArgs = nbArgs;
        this.description = description;
        this.isRequired = isRequired;
    }

    /**
     * Convertit l'element de l'enum en option pour la librairie Apache Commons CLI
     * @return org.apache.commons.cli.Option correspondant à l'element de l'enum
     */
    public Option toOption() {
        Option option = new Option(this.shortOpt, this.description);
        option.setLongOpt(this.longOpt);
        option.setRequired(this.isRequired);
        switch (this.nbArgs) {
            case NONE:
                option.setArgs(0);
                break;
            case ONE:
                option.setArgs(1);
                break;
            case MULTIPLE:
                option.setArgs(Option.UNLIMITED_VALUES);
                break;
        }
        return option;
    }

    /**
     * Le nom court de l'option est utilisé dans les fonctions de la librairie Apache Commons CLI
     * @return Renvoie le nom court de l'option
     */
    public String getShortOpt() {
        return this.shortOpt;
    }
}
