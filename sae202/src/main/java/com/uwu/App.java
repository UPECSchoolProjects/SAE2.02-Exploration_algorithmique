package com.uwu;

import java.io.File;
import java.io.*;
import com.uwu.Conversion.ConversionFactory;
import com.uwu.Conversion.IConverter;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        // IConverter cf = ConversionFactory.getConverter("test.pdf");

        // cf.convert();

        // File test = new File("test.txt");
        // System.out.println(test);

        analyse text=new analyse();
        text.phrase("test.txt");



        
    }
}
