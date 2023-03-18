package com.uwu;

import com.uwu.Conversion.ConversionFactory;
import com.uwu.Conversion.HTMLConverter;
import com.uwu.Conversion.IConverter;

public class App {
    public static void main(String[] args) throws Exception {

        HTMLConverter html = new HTMLConverter("htmltest.html");
        html.Parser();

    }
}
