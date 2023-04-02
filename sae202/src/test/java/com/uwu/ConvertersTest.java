package com.uwu;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.uwu.Conversion.HTMLConverter;
import com.uwu.Conversion.PdfConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Unit test for simple App.
 */
public class ConvertersTest {
    private static final Logger logger = LogManager.getLogger(ConvertersTest.class);

    private static String textMatcher = "Histoire de Neuilly par l'Abbé Bellanger";

    /**
     * Rigorous Test :-)
     */
    @Test
    public void testHTMLConverter() {
        File file = new File("TestsInputs/test.html");
        HTMLConverter html = new HTMLConverter(new FileObj(file.getAbsolutePath()), null, null, "root");
        String text = html.getText();

        logger.debug("HTML TEXT : " + text);

        assertTrue(text.equals(textMatcher));
    }

    @Test
    public void testPDFConverter() {
        File inputFile = new File("TestsInputs/test.pdf");
        PdfConverter pdf = new PdfConverter(new FileObj(inputFile.getAbsolutePath()), "TestsOutput/");

        File file = pdf.convert();

        // read file
        String text = "";
        try {
            text = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.debug("PDF TEXT : " + text);

        assertTrue(text.trim().equals(textMatcher));
    }
}
