package com.uwu.Conversion;

import java.io.File;

public class ConversionFactory {

    public static IConverter getConverter(String f, String path) {
        switch (f.substring(f.lastIndexOf('.') + 1)) {
            case "pdf":
                return new PdfConverter(f);
            case "html":
                return new HTMLConverter(f, path);
            default:
                throw new UnsupportedOperationException("Unsupported file type");
        }
    }
}
