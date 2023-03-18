package com.uwu.Conversion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import com.uwu.Conversion.HTMLParser.HTMLElement;

public class HTMLConverter {
    public static Pattern bodyRegex = Pattern.compile("<body");
    public static Pattern baliseRegex = Pattern.compile("<(/?\\w+)((\\s+\\w+(\\s*=\\s*(\".*?\"|'.*?'|[\\^'\">\\s]+))?)+\\s*|\\s*)/?>");
    public static Pattern htmlCommentRegex = Pattern.compile("<!--[^-]+-->");

    ArrayList<String> balisesAutofermantes = new ArrayList<String>(Arrays.asList(
        "area", "base", "br", "col", "command", "embed", "hr", "img", "input",
        "keygen", "link", "meta", "param", "source", "track", "wbr"
    ));

    // Liste des balises non auto-fermantes
    ArrayList<String> balisesNonAutofermantes = new ArrayList<String>(Arrays.asList(
        "a", "abbr", "address", "article", "aside", "audio", "b", "bdi", "bdo",
        "blockquote", "body", "button", "canvas", "caption", "cite", "code", "data",
        "datalist", "dd", "del", "details", "dfn", "dialog", "div", "dl", "dt",
        "em", "fieldset", "figcaption", "figure", "footer", "form", "h1", "h2",
        "h3", "h4", "h5", "h6", "head", "header", "hgroup", "html", "i", "iframe",
        "ins", "kbd", "label", "legend", "li", "main", "map", "mark", "menu",
        "menuitem", "meter", "nav", "noscript", "object", "ol", "optgroup",
        "option", "output", "p", "picture", "pre", "progress", "q", "rp", "rt",
        "ruby", "s", "samp", "script", "section", "select", "small", "span",
        "strong", "style", "sub", "summary", "sup", "svg", "table", "tbody",
        "td", "template", "textarea", "tfoot", "th", "thead", "time", "title",
        "tr", "u", "ul", "var", "video", "center"
    ));

    public String fileName;

    public HTMLConverter(String f) {
        this.fileName = f;
    }

    @Override
    public File convert() {
        // read file
        String content = readFile();
        if (content == null)
            return null;
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName.split("\\.")[0] + ".txt"));
        writer.write(content);

        writer.close();
        return new File(this.fileName.split("\\.")[0] + ".txt");
    }

    public String readFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {
            StringBuilder sb = new StringBuilder();

            int linenb = 0;
            String line;
            do {
                line = br.readLine();
                System.out.println(linenb);
                linenb++;

                if (line == null)
                    break;
                sb.append(line);
            } while (line != null);
            String everything = sb.toString();
            everything = htmlCommentRegex.matcher(everything).replaceAll("");
            return everything;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void Parser() {
        String content = readFile();
        int error = 0;

        Matcher contentMatcher = baliseRegex.matcher(content);
        Stack<String> balises = new Stack<String>();

        while (contentMatcher.find()) {
            String balise = contentMatcher.group(1);
            String classes = contentMatcher.group(2);
            String baliseName = balise.replace("/", "");
            boolean closing = balise.startsWith("/");

            System.out.println(balise + " " + classes + "\n");

            if(balisesAutofermantes.contains(baliseName)) {
                continue;
            }

            if(balisesNonAutofermantes.contains(baliseName)) {
                if(closing) {
                    if(balises.peek().equals(baliseName)) {
                        balises.pop();
                    } else {
                        error++;
                        System.out.println("Balise non fermée : " + balise);
                    }
                } else {
                    balises.push(baliseName);
                }
            } else {
                System.out.println("Balise non reconnue : " + balise);
            }
        }

        System.out.println("Nombre d'erreurs : " + error);
    }
}
