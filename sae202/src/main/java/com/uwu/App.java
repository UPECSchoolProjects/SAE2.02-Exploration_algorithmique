package com.uwu;

import com.uwu.Conversion.ConversionFactory;
import com.uwu.Conversion.IConverter;

public class App {
    public static void main(String[] args) throws Exception {

        // IConverter cf = ConversionFactory.getConverter("test.pdf");

        // cf.convert();

        // File test = new File("test.txt");
        // System.out.println(test);

        analyse text=new analyse();
        text.phrase("test.txt");



        
    }
}
