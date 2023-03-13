package com.uwu;

import java.io.File;
import com.uwu.Conversion.ConversionFactory;
import com.uwu.Conversion.IConverter;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        IConverter cf = ConversionFactory.getConverter(new File("test.pdf"));

        cf.convert();
    }
}
