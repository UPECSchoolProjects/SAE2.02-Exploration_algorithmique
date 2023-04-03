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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.uwu.Stemming.Stemming;


public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        //CLI cli = new CLI(args);
        //cli.executeCLI();

        Stemming stemming = new Stemming();
        String word = "vieille";
        String stem = stemming.stemm(word);
        System.out.println("stemming de " + word + " : " + stem);
    }


    /**
     * prends une liste de fichier et les unifie dans un seul fichier .txt
     * 
     * @param files    liste de fichier à unifier
     * @param outDir   répertoire de sortie du fichier unifié
     * @param filename nom du fichier unifié
     * @return le fichier unifié (objet FileObj)
     * @throws IOException si le fichier n'a pas pu être créé
     */
    public static FileObj unifyFiles(ArrayList<FileObj> files, String outDir, String filename)
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
        for (FileObj f : files) {
            InputStream ips = new FileInputStream(f.getFile());
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
            }
            br.close();
        }

        bw.close();
        return new FileObj(file.getAbsolutePath());
    }

    /**
     * Liste les fichiers d'un répertoire
     * 
     * @param folder répertoire à lister
     * @return String[] contenant les noms des fichiers
     */
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

    /**
     * Récupère le chemin relatif d'un fichier par rapport au répertoire courant
     * 
     * @param path chemin du fichier
     * @return chemin relatif
     */
    public static String getRelativePath(String path) {
        // get cli directory
        Path currentRelativePath = Paths.get("").toAbsolutePath();
        Path param = Paths.get(path).toAbsolutePath();
        Path relativePath = currentRelativePath.relativize(param);
        return relativePath.toString();
    }
}
