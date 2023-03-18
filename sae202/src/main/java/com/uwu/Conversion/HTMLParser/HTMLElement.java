package com.uwu.Conversion.HTMLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLElement {
    public static Pattern AttributesRegex = Pattern.compile("([^=]+=[\"'][^\"|\']*[\"'])");
    public String tag;
    public boolean isSelfClosing;
    private String innerHTML;
    public HashMap<String, String> attributes;
    private String outerHTML;
    public ArrayList<HTMLElement> children;

    /*
     * HTML is <tag>innerHTML</tag>
     */
    public HTMLElement(String tag, boolean isSelfClosing, String balise) {
        this.tag = tag;
        this.isSelfClosing = isSelfClosing;
        this.innerHTML = "";
        this.attributes = new HashMap<String, String>();
        this.outerHTML = balise;
        this.children = new ArrayList<HTMLElement>();

        this.parseAttributes();
    }

    public String toString() {
        return outerHTML;
    }

    public void addOuterHTML (String text) {
        this.outerHTML += text;
        calculateInnerHTML();
    }

    public void addChild(HTMLElement child) {
        this.children.add(child);
    }

    public void calculateInnerHTML() {
        if (this.isSelfClosing) {
            return;
        }

        String endTag = "</" + this.tag + ">";
        int endTagIndex = this.outerHTML.indexOf(endTag);

        if(endTagIndex == -1) {
            endTagIndex = this.outerHTML.length();
        }

        int endStartTagIndex = this.outerHTML.indexOf('>') + 1;

        if(endStartTagIndex == -1) {
            this.innerHTML = "";
            return;
        }

        this.innerHTML = this.outerHTML.substring(this.outerHTML.indexOf('>') + 1, endTagIndex);
    }

    public String getInnerHTML() {
        return this.innerHTML;
    }

    public String getInnerText() {
        return this.outerHTML.replaceAll("<[^>]*>", "");
    }

    public String getOuterHTML() {
        return this.outerHTML;
    }


    public void parseAttributes() {
        String withoutTag =
                this.outerHTML.substring(this.tag.length() + 1, this.outerHTML.indexOf('>')).trim();
        //System.out.println("Without tag: " + withoutTag);
        Matcher attributesMatcher = AttributesRegex.matcher(withoutTag);
        while (attributesMatcher.find()) {
            String attribute = attributesMatcher.group(1);
            //System.out.println("Attribute Matcher: " + attribute);
            String[] attributeSplit = attribute.split("=");
            this.attributes.put(attributeSplit[0], attributeSplit[1].replaceAll("\"", ""));

            //System.out.println("Attribute: " + attributeSplit[1]);
        }
    }

}
