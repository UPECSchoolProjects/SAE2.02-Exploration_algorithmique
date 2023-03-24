package com.uwu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.uwu.Conversion.HTMLConverter;

/**
 * Unit test for simple App.
 */
public class ConvertersTest 
{
    private static String textMatcher = "Histoire de Neuilly par l'Abbé Bellanger";
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testHTMLConverter()
    {
        HTMLConverter html = new HTMLConverter("TestsInputs/", "test.html", null, "root");
        String text = html.getText();
        System.out.println(text);
        assertTrue(text.equals(textMatcher));
    }
}
