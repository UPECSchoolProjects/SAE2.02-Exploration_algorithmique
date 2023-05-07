package com.uwu.Conversion;

import com.uwu.FileObj;

/**
 * Factory pour les classes de conversion. Elle permet le fonctionnement du pattern Factory
 */
public class ConversionFactory {

    /**
     * Renvoie un convertisseur en fonction de l'extension du fichier à convertir (pattern Factory)
     * Chaque convertisseur implémente l'interface IConverter et possède une méthode convert()
     * 
     * Les paramètres keyText et classText sont utilisés pour la conversion HTML. Ils permettent de cibler un élément à récupérer.
     * Mettre à null pour les autres types de fichiers
     * 
     * @param file fichier à convertir (objet FileObj)
     * @param outputPath répertoire de sortie du fichier converti
     * @param keyText Pour la conversion HTML, "id" ou "class" selon ce qu'on veut récupérer. Pour cibler un élément à récupérer
     * @param classText Pour la conversion HTML, le nom de la classe ou de l'id à récupérer 
     * @return Renvoie un convertisseur
     */
    public static IConverter getConverter(FileObj file, String outputPath, String keyText, String classText) {
        switch (file.getExtension()) {
            case "pdf":
                return new PdfConverter(file, outputPath);
            case "html":
            case "htm":
                return new HTMLConverter(file, outputPath, keyText, classText);
            case "txt":
                return null;
            default:
                throw new UnsupportedOperationException("Unsupported file type");
        }
    }
}
