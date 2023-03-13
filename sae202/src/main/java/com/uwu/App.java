package com.uwu;

import java.io.File;
import com.uwu.Conversion.ConversionFactory;
import com.uwu.Conversion.IConverter;

public class App {
    public static void main(String[] args) throws Exception {
        IConverter cf = ConversionFactory.getConverter("htmltest.html");

        cf.convert();
    }
}
