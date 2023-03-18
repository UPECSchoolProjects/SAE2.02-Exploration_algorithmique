package com.uwu.Conversion.HTMLParser;

import java.util.ArrayList;

public class DOMSelecter {
    ArrayList<HTMLElement> root;

    public DOMSelecter(ArrayList<HTMLElement> root) {
        this.root = root;
    }

    public HTMLElement selectFirst(String key, String value) {
        // select on attributes
        for (HTMLElement element : root) {
            if (element.attributes.containsKey(key) && element.attributes.get(key).equals(value)) {
                return element;
            }
        }
        return null;
    }
}
