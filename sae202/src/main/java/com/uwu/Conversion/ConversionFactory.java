package com.uwu.Conversion;

public class ConversionFactory {

    public static IConverter getConverter(String path, String fileName, String classText, String outputPath) {
        switch (fileName.substring(fileName.lastIndexOf('.') + 1)) {
            case "pdf":
                return new PdfConverter(path, fileName, outputPath);
            case "html":
                return new HTMLConverter(path, fileName, outputPath, classText);
            case "txt":
                return null;
            default:
                throw new UnsupportedOperationException("Unsupported file type");
        }
    }
}
