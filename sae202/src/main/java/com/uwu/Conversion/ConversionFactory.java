package com.uwu.Conversion;

public class ConversionFactory {

    public static IConverter getConverter(String path, String fileName, String classText, String outputPath) {
        switch (fileName.substring(fileName.lastIndexOf('.') + 1)) {
            case "pdf":
                return new PdfConverter(fileName);
            case "html":
                return new HTMLConverter(path, fileName, outputPath, classText);
            default:
                throw new UnsupportedOperationException("Unsupported file type");
        }
    }
}
