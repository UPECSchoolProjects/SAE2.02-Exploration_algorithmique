package com.uwu.Conversion.HTMLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLElement {
    public String tag;
    public boolean isSelfClosing;
    public ArrayList<HTMLElement> children;
    public String innerHTML;
    public String outerHTML;
    public HashMap<String, String> attributes;

    /*
     * HTML is <tag>innerHTML</tag>
     */
    public HTMLElement(String tag, boolean isSelfClosing) {
        this.tag = tag;
        this.children = new ArrayList<HTMLElement>();
        this.attributes = new HashMap<String, String>();
    }



}
