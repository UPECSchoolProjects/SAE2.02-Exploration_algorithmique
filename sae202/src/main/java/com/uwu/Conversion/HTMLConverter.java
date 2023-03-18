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
    public static Pattern baliseRegex = Pattern
            .compile("<(/?\\w+)((\\s+\\w+(\\s*=\\s*(\".*?\"|'.*?'|[\\^'\">\\s]+))?)+\\s*|\\s*)/?>");
    public static Pattern htmlCommentRegex = Pattern.compile("<!--[^-]+-->");

    public static ArrayList<String> balisesAutofermantes = new ArrayList<String>(Arrays.asList(
            "area", "base", "br", "col", "command", "embed", "hr", "img", "input",
            "keygen", "link", "meta", "param", "source", "track", "wbr"));

    // Liste des balises non auto-fermantes
    public static ArrayList<String> balisesNonAutofermantes = new ArrayList<String>(Arrays.asList(
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
            "tr", "u", "ul", "var", "video", "center"));

    public static Pattern TagNameRegex = Pattern.compile("</?([^> ]+)", Pattern.MULTILINE);

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

    // public void Parser() {
    // String content = readFile();
    // int error = 0;

    // Matcher contentMatcher = baliseRegex.matcher(content);
    // Stack<String> balises = new Stack<String>();

    // while (contentMatcher.find()) {
    // String balise = contentMatcher.group(1);
    // String classes = contentMatcher.group(2);
    // String baliseName = balise.replace("/", "");
    // boolean closing = balise.startsWith("/");

    // System.out.println(balise + " " + classes + "\n");

    // if(balisesAutofermantes.contains(baliseName)) {
    // continue;
    // }

    // if(balisesNonAutofermantes.contains(baliseName)) {
    // if(closing) {
    // if(balises.peek().equals(baliseName)) {
    // balises.pop();
    // } else {
    // error++;
    // System.out.println("Balise non fermée : " + balise);
    // }
    // } else {
    // balises.push(baliseName);
    // }
    // } else {
    // System.out.println("Balise non reconnue : " + balise);
    // }
    // }

    // System.out.println("Nombre d'erreurs : " + error);
    // }

    // public void Parser() {
    //     char[] content = readFile().toCharArray();
    //     int error = 0;

    //     Stack<HTMLElement> balises = new Stack<HTMLElement>();
    //     ArrayList<HTMLElement> elements = new ArrayList<HTMLElement>();
    //     int i = 0;
    //     while (i < content.length) {
    //         char c = content[i];
    //         if (c == '<') {
    //             String balise = "";
    //             while (content[i] != '>') {
    //                 balise += content[i];
    //                 i++;
    //             }
    //             balise += content[i];
    //             balise = balise.substring(1, balise.length() - 1).trim();
    //             String baliseName;
    //             Matcher tagNameMatcher = TagNameRegex.matcher(balise);
    //             if (tagNameMatcher.find()) {
    //                 baliseName = tagNameMatcher.group(1);
    //             } else {
    //                 baliseName = "";
    //             }
    //             System.out.println("Balise Name : " + baliseName);
    //             boolean closing = balise.startsWith("/");

    //             System.out.println(balise + "\n");

    //             if (balisesAutofermantes.contains(baliseName)) {
    //                 continue;
    //             }

    //             if (balisesNonAutofermantes.contains(baliseName)) {
    //                 if (closing) {
    //                     if (balises.peek().tag.equals(baliseName)) {
    //                         elements.add(balises.pop());
    //                     } else {
    //                         error++;
    //                         System.out.println("Balise non fermée : " + balise);
    //                     }
    //                     for (HTMLElement element : balises) {
    //                         element.innerHTML += "<" + balise + ">";
    //                     }
    //                 } else {
    //                     for (HTMLElement element : balises) {
    //                         element.innerHTML += "<" + balise + ">";
    //                     }
    //                     HTMLElement element = new HTMLElement(baliseName, false, "<" + balise + ">");
    //                     balises.push(element);
    //                 }
    //             } else {
    //                 System.out.println("Balise non reconnue : " + balise);
    //             }
    //         } else {
    //             System.out.println(c);
    //             for (HTMLElement element : balises) {
    //                 element.innerHTML += c;
    //             }
    //         }
    //         i++;
    //     }
    //     System.out.println("Nombre d'erreurs : " + error);
    //     StringBuilder sb = new StringBuilder();
    //     for (HTMLElement element : elements) {
    //         sb.append(element.toString());
    //         sb.append("\n");
    //     }
    //     try {
    //         BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName.split("\\.")[0] + "-parsed.txt"));
    //         writer.write(sb.toString());

    //         writer.close();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    // }

    public void Parser() {
        char[] content = readFile().toCharArray();
        int error = 0;

        Stack<HTMLElement> balises = new Stack<HTMLElement>();
        ArrayList<HTMLElement> elements = new ArrayList<HTMLElement>();
        int i = 0;
        while (i < content.length) {
            char c = content[i];
            for (HTMLElement element : balises) {
                element.addOuterHTML(String.valueOf(c));
            }
            if (c == '<') {
                String balise = "";
                while (content[i] != '>') {
                    balise += content[i];
                    i++;
                }

                balise += content[i];

                for (HTMLElement element : balises) {
                    element.addOuterHTML(balise.substring(1, balise.length()));
                }

                String tagName = getTagName(balise);
                boolean closing = balise.startsWith("</");

                if (balisesAutofermantes.contains(tagName)) {
                    continue;
                }

                if (balisesNonAutofermantes.contains(tagName)) {
                    if (closing) {
                        if (balises.peek().tag.equals(tagName)) {
                            HTMLElement element = balises.pop();
                            elements.add(element);

                            if (!balises.isEmpty()) {
                                balises.peek().addChild(element);
                            }
                        } else {
                            error++;
                            System.out.println("Balise non fermée : " + balise);
                        }
                    } else {
                        HTMLElement element = new HTMLElement(tagName, false, balise);
                        balises.push(element);
                    }
                }
            }
            i++;
        }
        System.out.println("Nombre d'erreurs : " + error);
        StringBuilder sb = new StringBuilder();
        for (HTMLElement element : elements) {
            sb.append(element.toString());
            sb.append("\n");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName.split("\\.")[0] + "-parsed.txt"));
            writer.write(sb.toString());

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTagName(String balise) {
        Matcher tagNameMatcher = TagNameRegex.matcher(balise);
        if (tagNameMatcher.find()) {
            return tagNameMatcher.group(1);
        } else {
            return "";
        }
    }
}
