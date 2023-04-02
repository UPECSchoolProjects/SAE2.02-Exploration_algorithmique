package com.uwu.Conversion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.uwu.FileObj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PdfConverter implements IConverter {

    private static final Logger logger = LogManager.getLogger(PdfConverter.class);

    private FileObj file;

    private String outputFolder;
    private String outputURL;

    public PdfConverter(FileObj file, String outputfolder) {
        this.file = file;
        this.outputFolder = outputfolder;

        this.outputURL = this.outputFolder + this.file.getNameWithAnotherExt("txt");
    }

    @Override
    public File convert() {
        try {
            logger.info("Convertion de " + this.file.getName() + " en " + this.file.getNameWithAnotherExt("txt"));
            logger.debug("Output : " + this.outputURL);
            PDDocument document = PDDocument.load(new File(this.file.getFullPath()));
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
