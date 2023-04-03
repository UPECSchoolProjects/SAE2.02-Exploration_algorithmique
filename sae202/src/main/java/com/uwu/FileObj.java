package com.uwu;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FileObj a comme but de simplifier l'utilisation des fichiers
 * Elle permet de récupérer le nom, le chemin, l'extension et le nom du fichier sans l'extension
 * Le fichier est vérifié à la création de l'objet
 * 
 * Le ligne de code avec le substring pour trouver le fichiersans extension (ainsi que d'autres fonction du style) était utilisée un peu partout dans le code
 * J'ai donc décidé de la mettre dans une classe à part
 * 
 * La classe ne calcule les valeurs que si elles sont demandées
 */
public class FileObj {
    private static final Logger logger = LogManager.getLogger(App.class);

    private File file;
    private String fullPath;
    private String name;
    private String path;
    private String extension;
    private String filenameWithoutExtension;

    /**
     *  Constructeur de la classe
     *  ? On pourrait peut-être faire un constructeur qui prend en paramètre un File
     * @param path Chemin absolu du fichier
     */
    public FileObj(String path) {
        this.fullPath = path;
        this.file = new File(path);

        // check file exists
        if (!this.file.exists()) {
            logger.error("Le fichier " + path + " n'existe pas");
            System.exit(-1);
        }
    
        this.name = null;
        this.path = null;
        this.extension = null;
        this.filenameWithoutExtension = null;
    }

    /**
     * 
     * @return Renvoie le chemin absolu du fichier
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * Exemple : si le fichier est "C:\test\test.txt", la fonction renvoie "test.txt"
     * @return Renvoie le nom du fichier
     */
    public String getName() {
        if (name == null) {
            name = this.file.getName();
        }
        return name;
    }

    /**
     * 
     * @return Renvoie le chemin absolu du fichier
     */
    public String getPath() {
        if (path == null) {
            path = this.file.getParent();
        }
        return path;
    }

    /**
     * 
     * Exemple: si le fichier est "test.txt", la fonction renvoie "txt"
     * @return l'extension du fichier
     */
    public String getExtension() {
        if (extension == null) {
            extension = getName().substring(getName().lastIndexOf('.') + 1);
        }
        return extension;
    }


    /**
     * 
     * Exemple: si le fichier est "test.txt", la fonction renvoie "test"
     * @return nom du fichier sans l'extension
     */
    public String getFilenameWithoutExtension() {
        if (filenameWithoutExtension == null) {
            filenameWithoutExtension = getName().substring(0, getName().lastIndexOf('.'));
        }
        return filenameWithoutExtension;
    }

    /**
     * Renvoie le nom du fichier avec une autre extension
     * 
     * Exemple: si le fichier est "test.txt" et que l'on donne "html" en paramètre, la fonction renvoie "test.html"
     * @param ext nouvelle extension
     * @return nom du fichier avec l'extension donnée
     */
    public String getNameWithAnotherExt(String ext) {
        return getFilenameWithoutExtension() + "." + ext;
    }

    /**
     * Getter de l'objet File représentant le fichier
     * @return
     */
    public File getFile() {
        return file;
    }
}
