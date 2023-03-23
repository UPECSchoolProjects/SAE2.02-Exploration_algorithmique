package com.uwu;

import com.uwu.Conversion.HTMLConverter;

public class App {
    public static void main(String[] args) throws Exception {

         HTMLConverter html = new HTMLConverter("HTMLTEST/html6.html", "HTMLParsed/");
         
        html.convert();
         
    }
}
