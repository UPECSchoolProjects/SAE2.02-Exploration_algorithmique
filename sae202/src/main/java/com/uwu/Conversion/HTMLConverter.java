package com.uwu.Conversion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import com.uwu.Conversion.HTMLParser.DOMSelecter;
import com.uwu.Conversion.HTMLParser.HTMLElement;

public class HTMLConverter implements IConverter {
    private static final Logger logger = LogManager.getLogger(HTMLConverter.class);

    public static final Pattern bodyRegex = Pattern.compile("<body");
    public static final Pattern baliseRegex = Pattern
            .compile("<(/?\\w+)((\\s+\\w+(\\s*=\\s*(\".*?\"|'.*?'|[\\^'\">\\s]+))?)+\\s*|\\s*)/?>");
    public static final Pattern htmlCommentRegex = Pattern.compile("<!--[^-]+-->");

    public static final ArrayList<String> balisesAutofermantes = new ArrayList<String>(
            Arrays.asList("area", "base", "br", "col", "command", "embed", "hr", "img", "input",
                    "keygen", "link", "meta", "param", "source", "track", "wbr"));

    // Liste des balises non auto-fermantes
    public static final ArrayList<String> balisesNonAutofermantes = new ArrayList<String>(
            Arrays.asList("a", "abbr", "address", "article", "aside", "audio", "b", "bdi", "bdo",
                    "blockquote", "body", "button", "canvas", "caption", "cite", "code", "data",
                    "datalist", "dd", "del", "details", "dfn", "dialog", "div", "dl", "dt", "em",
                    "fieldset", "figcaption", "figure", "footer", "form", "h1", "h2", "h3", "h4",
                    "h5", "h6", "head", "header", "hgroup", "html", "i", "iframe", "ins", "kbd",
                    "label", "legend", "li", "main", "map", "mark", "menu", "menuitem", "meter",
                    "nav", "noscript", "object", "ol", "optgroup", "option", "output", "p",
                    "picture", "pre", "progress", "q", "rp", "rt", "ruby", "s", "samp", "script",
                    "section", "select", "small", "span", "strong", "style", "sub", "summary",
                    "sup", "svg", "table", "tbody", "td", "template", "textarea", "tfoot", "th",
                    "thead", "time", "title", "tr", "u", "ul", "var", "video", "center"));

    public static final Pattern TagNameRegex = Pattern.compile("</?([^> /]+)", Pattern.MULTILINE);

    public String fileName;
    public String inputPath;
    public String filenameWithoutExtension;
    public String classText;
    public String outputPath;
    private String outputPathURL;
    private String fileURL;

    public HTMLConverter(String inputPath, String fileName, String outputPath, String classText) {
        this.fileName = fileName;
        this.inputPath = inputPath == null ? "" : inputPath;
        this.outputPath = outputPath == null ? "" : outputPath;
        this.classText = classText;
        
        this.filenameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
        this.outputPathURL = this.outputPath + this.filenameWithoutExtension + "-parsed.txt";
        this.fileURL = this.inputPath + this.fileName;

        // check if the file exists
        File file = new File(this.fileURL);
        if (!file.exists()) {
            //throw new IllegalArgumentException("File " + this.fileURL + " does not exist");
            // create a new file
            // create dirs
            File dir = new File(this.inputPath);
            dir.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public File convert() {
        // read file
        String content = getText();
        if (content == null)
            return null;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPathURL));
            writer.write(content);

            writer.close();

            return new File(this.outputPathURL);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String readFile() {
        /*
         * Cette fonction lit le fichier HTML ligne par ligne et le convertit en String
         */
        // Ouvre le fichier HTML en utilisant un BufferedReader pour lire ligne par
        // ligne
        try (final BufferedReader br = new BufferedReader(new FileReader(this.fileURL))) {
            final StringBuilder sb = new StringBuilder();

            // ligne nb est utilisé pour le debug, pour mesurer l'avancement
            int linenb = 0;
            String line;

            // Lit le fichier HTML ligne par ligne
            do {
                line = br.readLine(); // on recupere la prochaine ligne
                logger.trace(linenb); // debug
                linenb++;

                if (line == null) // si la ligne est null, on a atteint la fin du fichier
                    break;

                sb.append(line); // on ajoute la ligne au StringBuilder
                // à la base je voulais ignorer les retours à la ligne.
                // cepandant, il semble que les retours à la ligne soient importants pour la
                // suite du traitement du texte (les mots sont séparés par des retours à la
                // ligne)
                sb.append(" ");
                // on continue tant que la ligne n'est pas null (fin du fichier)
            } while (line != null);
            String everything = sb.toString();
            // on supprime les commentaires HTML, ils ne sont pas utiles pour le traitement
            everything = htmlCommentRegex.matcher(everything).replaceAll("");
            return everything;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getText() {
        /*
         * Cette fonction recupere toutes les balise de l'HTML, et en extrait le texte ELle est
         * spécifique à WikiSource
         */
        final ArrayList<HTMLElement> elements = Parser();
        final DOMSelecter selecter = new DOMSelecter(elements);
        final HTMLElement el = selecter.selectFirst("class", this.classText);
        if (el == null)
            return "";
        final StringBuilder sb = new StringBuilder();
        logger.debug("---------------");
        for (HTMLElement child : el.children) {
            logger.debug(child.tag);
            if (child.tag.equals("p")) {
                sb.append(child.getInnerText());
            }
        }

        return sb.toString().replaceAll("&#160;;", "").replaceAll("&#160;", "")
                .replaceAll("\\s+", " ").replaceAll("\t", "");
    }

    public ArrayList<HTMLElement> Parser() {
        /*
         * Cette fonction permet de parser le fichier HTML et de retourner une liste d'HTMLElement
         * (classe définie dans le package com.uwu.Conversion.HTMLParser) ELle utilise un stack pour
         * gérer les balises ouvertes et fermées Elle lis les caractères un par un et les ajoute
         * dans le outerHTML de toutes les balises qui n'ont pas encore été fermée Une fois qu'une
         * balise est fermée, celà veut dire que le contenu de la balise est terminé, on peut donc
         * l'ajouter à la liste de balise et la sortir du stack De plus on ajoute la balise fermante
         * à la liste des enfants de la balise juste au dessus dans la hierachie
         * 
         * Cette fonction est la forme la plus simple que pourrait prendre un parser HTML, il y a
         * beaucoup de choses à améliorer Néanmoins, il est plus que suffisant pour notre projet.
         * (il ne garde pas dans tout les cas la mise en forme du texte)
         * 
         * J'aurais pu utiliser un parser HTML existant, mais j'avais envie de me lancer un défi
         * personnel et de faire un parser HTML basique en Java, c'était assez amusant à faire
         * (Maxime LOTTO)
         */

        char[] content = readFile().toCharArray();
        int error = 0;

        // le stack qui va contenir les balises ouvertes et leur ordre d'ouverture
        final Stack<HTMLElement> balises = new Stack<HTMLElement>();
        // la liste qui va contenir toutes les balises fermée une fois que l'on a tout
        // leur contenu
        final ArrayList<HTMLElement> elements = new ArrayList<HTMLElement>();
        int i = 0;
        while (i < content.length) {
            char c = content[i];
            

            // si on tombe sur une balise on la traite
            if (!(c == '<')) {
                // on ajoute le caractère au outerHTML de la balise parent
                if (!balises.isEmpty())
                    balises.peek().addOuterHTML(String.valueOf(c));
            } else {
                String balise = "";
                // on récupère tout le contenu de la balise jusqu'au ">"
                while (content[i] != '>') {
                    balise += content[i];
                    i++;
                }

                balise += content[i];

                // on ajoute la balise au outerHTML de la balise parent
                // étant donné que le premier while n'est plus pris en compte

                // on récupère le nom de la balise sans le /, le < ou le >
                final String tagName = getTagName(balise);
                final boolean closing = balise.startsWith("</"); // si la balise commence par </
                                                                 // alors
                // c'est une balise
                // fermante

                // si la balise est autofermante on ne s'en occupe pas, elle ne sert à rien pour
                // la SAE, on ne veut que le texte
                // donc on l'ignore est on reprend la boucle
                // bien-sûr dans le cas d'un vrai parser on devrait la traiter
                if (balisesAutofermantes.contains(tagName)) {
                    i++;
                    continue;
                }

                logger.debug("Balise : " + tagName);

                // si la balise est une balise non autofermante, on la traite
                if (balisesNonAutofermantes.contains(tagName)) {
                    // si jamais la balise est fermante, on regarde si la deniere balise ouverte est
                    // la même que celle qu'on vient de fermer
                    if (closing) {
                        // balise.peek() permet de récupérer la dernière balise ouverte sans la
                        // sortir
                        // du stack
                        if (balises.peek().tag.equals(tagName)) {
                            // Si la balise est la même que la dernière balise ouverte, on la sort
                            // du stack
                            // et on l'ajoute à la liste des balises fermées
                            // on ajoute aussi la balise fermante à la liste des enfants de la
                            // balise juste
                            // au dessus dans la hierachie
                            final HTMLElement element = balises.pop();
                            element.addOuterHTML(balise);
                            elements.add(element);

                            if (!balises.isEmpty()) {
                                balises.peek().addChild(element);
                                balises.peek().addOuterHTML(element.getOuterHTML());
                            }
                        } else {
                            // si la balise fermante n'est pas la même que la dernière balise
                            // ouverte, cela
                            // veut dire qu'il y a une erreur dans le code HTML
                            // on affiche un message d'erreur et on incrémente le nombre d'erreur
                            // il y'a de forte chance que le reste du parsing se déroule mal mais ça
                            // peut
                            // fonctionner quand même
                            error++;
                            logger.warn("Balise non fermée : " + balise);
                        }
                    } else {
                        // Si c'est une balise ouvrante, on l'ajoute au stack
                        final HTMLElement element = new HTMLElement(tagName, false, balise);
                        balises.push(element);
                    }
                } else {
                    // cela veut dire que c'est une balise html qui n'existe pas dans la
                    // spécification XHTML
                    logger.warn("Balise non reconnue : " + balise);
                }
            }
            i++;
        }
        logger.info("Nombre d'erreurs : " + error);

        return elements;
    }

    public void debugWriteAllHTML(final ArrayList<HTMLElement> elements) {
        final StringBuilder content = new StringBuilder();
        for (final HTMLElement element : elements) {
            content.append(element.getOuterHTML());
            content.append("\n\n");
        }

        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPathURL));
            writer.write(content.toString());

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTagName(final String balise) {
        final Matcher tagNameMatcher = TagNameRegex.matcher(balise);
        if (tagNameMatcher.find()) {
            return tagNameMatcher.group(1);
        } else {
            return "";
        }
    }
}
