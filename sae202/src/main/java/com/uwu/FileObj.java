package com.uwu;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileObj {
    private static final Logger logger = LogManager.getLogger(App.class);

    private File file;
    private String fullPath;
    private String name;
    private String path;
    private String extension;
    private String filenameWithoutExtension;

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

    public String getFullPath() {
        return fullPath;
    }

    public String getName() {
        if (name == null) {
            name = this.file.getName();
        }
        return name;
    }

    public String getPath() {
        if (path == null) {
            path = this.file.getParent();
        }
        return path;
    }

    public String getExtension() {
        if (extension == null) {
            extension = getName().substring(getName().lastIndexOf('.') + 1);
        }
        return extension;
    }

    public String getFilenameWithoutExtension() {
        if (filenameWithoutExtension == null) {
            filenameWithoutExtension = getName().substring(0, getName().lastIndexOf('.'));
        }
        return filenameWithoutExtension;
    }

    public String getNameWithAnotherExt(String ext) {
        return getFilenameWithoutExtension() + "." + ext;
    }

    public File getFile() {
        return file;
    }
}
