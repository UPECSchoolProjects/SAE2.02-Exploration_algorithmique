package com.uwu.Conversion;

public class ConversionFactory {

    public static IConverter getConverter(String f, String path, String classText) {
        switch (f.substring(f.lastIndexOf('.') + 1)) {
            case "pdf":
                return new PdfConverter(f);
            case "html":
                return new HTMLConverter(f, path, classText);
            default:
                throw new UnsupportedOperationException("Unsupported file type");
        }
    }
}
