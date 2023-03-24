package com.uwu.Conversion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfConverter implements IConverter {

    private String filePath;

    public PdfConverter(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public File convert() {
        try {
            PDDocument document = PDDocument.load(new File(filePath));
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.writeText(document, writer);

            writer.close();
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File("output.txt");
    }

}
