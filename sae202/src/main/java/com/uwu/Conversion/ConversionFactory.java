package com.uwu.Conversion;

import com.uwu.FileObj;

public class ConversionFactory {

    public static IConverter getConverter(FileObj file, String outputPath, String keyText, String classText) {
        switch (file.getExtension()) {
            case "pdf":
                return new PdfConverter(file, outputPath);
            case "html":
            case "htm":
                return new HTMLConverter(file, outputPath, keyText, classText);
            case "txt":
                return null;
            default:
                throw new UnsupportedOperationException("Unsupported file type");
        }
    }
}
