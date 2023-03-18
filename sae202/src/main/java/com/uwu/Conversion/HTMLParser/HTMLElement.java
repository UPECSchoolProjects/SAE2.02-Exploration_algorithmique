package com.uwu.Conversion.HTMLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLElement {
    public static Pattern AttributesRegex = Pattern.compile("([^=]+=[\"'][^\"|\']*[\"'])");
    public static Pattern TagNameRegex = Pattern.compile("<([\\S]+)", Pattern.MULTILINE);

    public ArrayList<HTMLElement> children;
    public String tag;
    public String innerHTML;
    public String outerHTML;
    public HashMap<String, String> attributes;

    /*
     * HTML is <tag>innerHTML</tag>
     */
    public HTMLElement(String HTML) {
        this.outerHTML = HTML.trim();

        this.children = new ArrayList<HTMLElement>();
        this.attributes = new HashMap<String, String>();

        Matcher tagNameMatcher = TagNameRegex.matcher(this.outerHTML);
        if (tagNameMatcher.find()) {
            this.tag = tagNameMatcher.group(1);
        } else {
            this.tag = "";
        }

        this.innerHTML = HTML.substring(HTML.indexOf('>') + 1, HTML.lastIndexOf('<'));
        this.parseAttributes();

        System.out.println("Tag: " + this.tag);
        System.out.println("OuterHTML: " + this.outerHTML);
        System.out.println("InnerHTML: " + this.innerHTML);
        System.out.println("Attributes: " + this.attributes);

        // Childrens are all direct child
        // parse child
        
    }

    public void parseAttributes() {
        String withoutTag =
                this.outerHTML.substring(this.tag.length() + 1, this.outerHTML.indexOf('>')).trim();
        System.out.println("Without tag: " + withoutTag);
        Matcher attributesMatcher = AttributesRegex.matcher(withoutTag);
        while (attributesMatcher.find()) {
            String attribute = attributesMatcher.group(1);
            System.out.println("Attribute Matcher: " + attribute);
            String[] attributeSplit = attribute.split("=");
            this.attributes.put(attributeSplit[0], attributeSplit[1].replaceAll("\"", ""));

            System.out.println("Attribute: " + attributeSplit[1]);
        }
    }



}
