package com.uwu.Conversion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfConverter implements IConverter {

    private String fileName;
    private String fileNameWithoutExtension;
    private String filePath;
    private String outputFolder;
    private String fileURL;
    private String outputURL;

    public PdfConverter(String filePath, String fileName, String outputfolder) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.outputFolder = outputfolder;

        this.fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));

        this.fileURL = this.filePath + this.fileName;
        this.outputURL = this.outputFolder + this.fileNameWithoutExtension + ".txt";
    }

    @Override
    public File convert() {
        try {
            PDDocument document = PDDocument.load(new File(fileURL));
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputURL));

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.writeText(document, writer);

            writer.close();
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(this.outputURL);
    }

}
