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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.uwu.Stemming.Stemming;

public class Analyse {

    private static final Logger logger = LogManager.getLogger(Analyse.class);

    static Pattern ponctuationRegex = Pattern.compile("[!'.,:;?’><»«)(\"\\[\\]{}<>\\/…\\-']");
    static Pattern pronomSuffixRegex = Pattern.compile("-(?:je|tu|il|elle|nous|vous|ils|elles|moi)\\b");

    /**
     * Prends une map avec des mots et leur fréquence et retourne le mot le plus
     * fréquent
     * La map represente toute les déclinaisons d'une même racine. Cela permet
     * d'afficher le mot le plus representatif de la racine dans le nuage de mot
     * 
     * @param map map avec des mots et leur fréquence
     * @return
     */
    public static String getMostFrequentWordInHashMap(Map<String, Integer> map) {
        int max = 0;
        String mot = "";
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                mot = entry.getKey();
            }
        }
        return mot;
    }

    List<String> motsVides;
    private String filePath;
    private String motVidePath;
    private Stemming stemming;
    private boolean addTxt; // si il faut fichier txt avec le texte nettoyé (sans mots vides, sans
                            // ponctuation, etc.)
    private String addTxtPath; // chemin du fichier txt avec le texte nettoyé
    // les variables addTxt et addTxtPath sont soit tous les deux null, soit tous
    // les deux non null grâce à la fonction
    // setAddTxt qui est la seule à pouvoir les modifier (en dehors de la classe
    // elle-même)

    public Analyse(String filePath, List<String> motVide, Stemming stemming) {
        this.filePath = filePath;
        this.motVidePath = motVidePath == null ? "mot_vide.txt" : motVidePath;
        this.stemming = stemming;
        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("Le fichier " + filePath + " est introuvable");
            System.exit(-1);
        }

        this.motsVides = motVide == null ? new ArrayList<String>() : motVide;
        this.addTxt = false;
        this.addTxtPath = null;
    }

    // public Analyse(String filePath) {
    // this.filePath = filePath;

    // try {
    // this.lire_mot_vides();
    // } catch (IOException e) {
    // e.printStackTrace();
    // logger.error("Erreur lors de la lecture du fichier mot_vide.txt : " +
    // e.getMessage());
    // System.exit(-1);
    // }
    // }

    /**
     * prends un fichier texte et retourne une liste de mots
     * 
     * @param motVidePath chemin du fichier contenant les mots vides
     * @return List String liste des mots contenu dans le fichier passé en paramètre
     */
    public static List<String> lire_mot_vides(String motVidePath) {
        File file = new File(motVidePath);
        List<String> motsVides = new ArrayList<String>();
        if (!file.exists()) {
            logger.error("Le fichier " + file.getAbsolutePath() + " est introuvable");
            return motsVides;
        }

        try {
            InputStream ips = new FileInputStream(file);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String line;
            while ((line = br.readLine()) != null) {
                motsVides.add(line);
            }

            br.close();

            logger.info("Le fichier " + file.getAbsolutePath() + " a été lu avec succès ("
                    + motsVides.size() + " mots)");
        } catch (IOException e) {
            logger.error("Erreur lors de la lecture du fichier " + file.getAbsolutePath());
            logger.debug(e.getStackTrace());
        }

        return motsVides;
    }

    /**
     * Renvoie une Map avec les mots dominants dans chaque racine trouvée et leur
     * fréquence
     * 
     * Appel la méthode addTxt() si on a besoin d'un fichier txt avec le texte
     * nettoyé (sans mots vides, sans ponctuation, etc.).
     * Utie pour avoir la cosine similarity pour calculer la similarité entre deux
     * textes
     * 
     * @return Map String, AnalyseMot Map avec les mots dominants dans chaque racine
     *         trouvée et leur fréquence (voir classe AnalyseMot)
     * @throws IOException dans le cas où le fichier n'existe pas ou n'est pas
     *                     lisible
     */
    public Map<String, AnalyseMot> calculerFrequences() throws IOException {
        Map<String, Map<String, Integer>> compteurMotDominantDansRacine = new HashMap<String, Map<String, Integer>>();
        int nbMot = 0;
        Map<String, Integer> compteur = new HashMap<>();

        StringBuilder listmot = null;

        if (this.addTxt) {
            listmot = new StringBuilder();
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath), "UTF-8"))) { // Ouvre le fichier
            String ligne;
            while ((ligne = br.readLine()) != null) { // Lit le fichier ligne par ligne
                ligne = ponctuationRegex.matcher(ligne).replaceAll(" "); // enlever ponctuation

                String[] mots = ligne.split(" +"); // Sépare la ligne en mots
                for (String mot : mots) {

                    // enlever le pronom après un tiret ex : "ecrit-elle"
                    // ce regex est là pour éviter de supprimer les mots au-dessus etc
                    mot = pronomSuffixRegex.matcher(mot).replaceAll("");

                    logger.trace("mot: " + mot);
                    String motRacine = stemming.stemm(mot);
                    logger.trace('[' + mot + "] -> [" + motRacine + ']');

                    if (compteurMotDominantDansRacine.containsKey(motRacine)) {
                        Map<String, Integer> map = compteurMotDominantDansRacine.get(motRacine);
                        map.put(mot, map.getOrDefault(mot, 0) + 1);
                        compteurMotDominantDansRacine.put(motRacine, map);
                    } else {
                        Map<String, Integer> map = new HashMap<>();
                        map.put(mot, 1);
                        compteurMotDominantDansRacine.put(motRacine, map);
                    }
                    logger.trace("mot: " + motRacine + " - length: " + motRacine.length());
                    if ((!motsVides.contains(motRacine)) && motRacine.length() > 1) { // Vérifie que le mot n'est
                        // pas vide
                        nbMot++;
                        compteur.put(motRacine, compteur.getOrDefault(motRacine, 0) + 1); // Incrémente la fréquence du
                        // mot

                        if (listmot != null) {
                            listmot.append(mot).append(" ");
                        }
                    }
                }
            }
        }
        logger.trace("freq: " + compteur);
        logger.debug("nbMot: " + nbMot);
        Map<String, AnalyseMot> analyseMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : compteur.entrySet()) { // Parcourt la Map
            if (entry.getValue() > 1) { // Vérifie que le mot apparait plus d'une fois
                AnalyseMot analyseMot = new AnalyseMot(
                        getMostFrequentWordInHashMap(compteurMotDominantDansRacine.get(entry.getKey())),
                        entry.getValue() / ((double) nbMot), entry.getValue());
                analyseMap.put(entry.getKey(), analyseMot); // Ajoute le mot et sa fréquence
                logger.trace("mot: " + entry.getKey() + " - occurence: " + entry.getValue() + " - freq: "
                        + entry.getValue() / ((double) nbMot));
            }
        }

        if (this.addTxt) {
            logger.info("Ecriture du fichier cleaned txt");
            this.writeAddTxt(listmot.toString(), this.addTxtPath);
        }

        return analyseMap;
    }

    /**
     * Genre un fichier CSV avec les mots et leur fréquence
     * 
     * @param nomFichier chemin du fichier CSV
     * @throws IOException Erreur lors de l'écriture du fichier
     */
    public void ecrireCSV(String nomFichier) throws IOException {
        // si le fichier existe, on le supprime
        File file = new File(nomFichier);
        if (file.exists()) {
            file.delete();
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(nomFichier, true), StandardCharsets.UTF_8))) {
            writer.append("MOT");
            writer.append(";");
            writer.append("RACINE");
            writer.append(";");
            writer.append("Occurence");
            writer.append(";");
            writer.append("Frequence");
            writer.append("\n");

            Map<String, AnalyseMot> freq = calculerFrequences();
            for (Map.Entry<String, AnalyseMot> entry : freq.entrySet()) { // Parcourt la Map
                writer.append(entry.getValue().getMot());
                writer.append(";");
                writer.append(entry.getKey());
                writer.append(";");
                writer.append(String.valueOf(entry.getValue().getOccurence()));
                writer.append(";");
                writer.append(String.valueOf(entry.getValue().getFrequence()));
                writer.append("\n");
            }
        }
    }

    /**
     * Ecrit le fichier txt avec le texte nettoyé à partir d'un string
     * 
     * @param text     texte à écrire
     * @param filePath chemin du fichier
     * @throws IOException Erreur lors de l'écriture du fichier
     */
    public void writeAddTxt(String text, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath, true), StandardCharsets.UTF_8))) {
            writer.append(text);
        }
    }

    /**
     * Défini si on veut écrire le fichier txt avec le texte nettoyé
     * Cette fonctio garanti que les variables addTxt et addTxtPath sont bien
     * définies en même temps
     * 
     * @param addTxt      true si on veut écrire le fichier txt, false sinon
     * @param TxtfilePath chemin du fichier txt à écrire
     */
    public void setAddTxt(boolean addTxt, String TxtfilePath) {
        this.addTxt = addTxt;
        this.addTxtPath = TxtfilePath;
    }
}
