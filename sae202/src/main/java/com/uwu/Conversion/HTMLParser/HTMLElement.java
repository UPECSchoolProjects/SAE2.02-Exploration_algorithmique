package com.uwu.Conversion.HTMLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cette classe représente une balise HTML. Elle contient les attributs de la
 * balise, son
 * contenu, ses enfants, etc.
 * 
 * Elle est utilisée par le parser HTML pour créer un arbre DOM.
 * 
 * /!\ je crois qu'il y'a déjà une classe HTMLElement dans Java, mais je ne l'ai
 * pas utilisée
 */
public class HTMLElement {

    private static final Logger logger = LogManager.getLogger(HTMLElement.class);

    public static final Pattern AttributesRegex = Pattern.compile("([^=]+=[\"'][^\"|\']*[\"'])");
    public String tag; // nom de la balise (sans les < et >)
    public boolean isSelfClosing; // si la balise est auto fermante (inline)
    private String innerHTML; // contenu HTML de la balise
    public HashMap<String, String> attributes; // attributs de la balise
    private String outerHTML; // OuterHTML est <tag>innerHTML</tag>
    public ArrayList<HTMLElement> children; // enfants de la balise

    /**
     * Constructeur de la classe HTMLElement 
     * ELle prend en paramètre le nom de la balise, si elle est auto fermante et le contenu de la balise
     * 
     * Elle parse les attributs de la balise et les stocke dans un HashMap
     * 
     * @param tag Nom de la balise
     * @param isSelfClosing Si la balise est auto fermante
     * @param balise Contenu de la balise
     */
    public HTMLElement(String tag, boolean isSelfClosing, String balise) {
        this.tag = tag;
        this.isSelfClosing = isSelfClosing;
        this.innerHTML = null;
        this.attributes = new HashMap<String, String>();
        this.outerHTML = balise;
        this.children = new ArrayList<HTMLElement>();

        this.parseAttributes(); // parse les attributs de la balise avec tout plein de regex

        if (this.tag.equals("div")) {
            logger.debug(this.tag);
            for (String key : this.attributes.keySet()) {
                logger.debug(key + " : " + this.attributes.get(key));
            }
        }

    }

    @Override
    public String toString() {
        return outerHTML;
    }

    /**
     * Ajoute du texte à la fin de la balise
     * @param text
     */
    public void addOuterHTML(String text) {
        this.outerHTML += text;
        this.innerHTML = null;
    }

    /**
     * Ajoute un enfant à la balise
     * @param child Enfant à ajouter (HTMLElement)
     */
    public void addChild(HTMLElement child) {
        this.children.add(child);
    }

    /**
     * Calcule le contenu HTML de la balise (innerHTML) en enlevant les balises
     */
    public void calculateInnerHTML() {
        if (this.isSelfClosing) {
            return;
        }

        final String endTag = "</" + this.tag + ">";
        int endTagIndex = this.outerHTML.indexOf(endTag);

        if (endTagIndex == -1) {
            endTagIndex = this.outerHTML.length();
        }

        final int endStartTagIndex = this.outerHTML.indexOf('>') + 1;

        if (endStartTagIndex == -1) {
            this.innerHTML = "";
            return;
        }

        this.innerHTML = this.outerHTML.substring(this.outerHTML.indexOf('>') + 1, endTagIndex).trim();
    }

    /**
     * Retourne le contenu HTML de la balise (innerHTML)
     * Elle le calcule si il n'est pas déjà calculé (lazy loading)
     * @return
     */
    public String getInnerHTML() {
        if (this.innerHTML == null) {
            this.calculateInnerHTML();
        }
        return this.innerHTML;
    }

    /**
     * Retourne le texte contenu dans la balise sans tout l'html imbriqué
     * @return
     */
    public String getInnerText() {
        // cette fonction retourne le texte contenu dans la balise sans tout l'html
        // imbriqué
        return this.outerHTML.replaceAll("<[^>]*>", "").trim();
    }

    /**
     * Retourne le cotenu complet de la balise (outerHTML)
     * @return
     */
    public String getOuterHTML() {
        return this.outerHTML;
    }

    /**
     * Ajoute les attributs de la balise dans un HashMap
     * @param key Nom de l'attribut
     * @return
     */
    public void parseAttributes() {
        // cette fonction parse les attributs de la balise avec des regex

        // enleve le nom du tag par exemple <div id="b" class="a"> => id="b" class="a"
        final String withoutTag = this.outerHTML.substring(this.tag.length() + 1, this.outerHTML.indexOf('>')).trim();
        // System.out.println("Without tag: " + withoutTag);
        final Matcher attributesMatcher = AttributesRegex.matcher(withoutTag);
        while (attributesMatcher.find()) {
            final String attribute = attributesMatcher.group(1);
            // System.out.println("Attribute Matcher: " + attribute);
            final String[] attributeSplit = attribute.split("=");
            this.attributes.put(attributeSplit[0], attributeSplit[1].replaceAll("\"", ""));

            // System.out.println("Attribute: " + attributeSplit[1]);
        }
    }

}
