package com.uwu.Conversion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import com.uwu.Conversion.HTMLParser.HTMLElement;

public class HTMLConverter implements IConverter {
    public String fileName;
    public static Pattern bodyRegex = Pattern.compile("<body");
    public static Pattern baliseRegex = Pattern.compile("<[^>]*>");
    public static Pattern htmlCommentRegex = Pattern.compile("<!--[^-]+-->");

    public HTMLConverter(String f) {
        // TODO Auto-generated constructor stub
        this.fileName = f;
    }

    @Override
    public File convert() {
        // read file
        Boolean isBody = false;
        try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {
            StringBuilder sb = new StringBuilder();

            int linenb = 0;
            String line;
            do {
                line = br.readLine();
                System.out.println(linenb);
                linenb++;

                if (!isBody) {
                    if (bodyRegex.matcher(line).find()) {
                        isBody = true;
                    } else {
                        continue;
                    }
                }
                if (line == null)
                    break;
                sb.append(line);
                sb.append(System.lineSeparator());
            } while (line != null);
            String everything = sb.toString();
            
            System.out.println(everything);
            HTMLElement bodyElement = new HTMLElement(everything.replaceAll("<body>", "").replaceAll("</body>", "").replace("</html>", ""));

            BufferedWriter writer =
                    new BufferedWriter(new FileWriter(this.fileName.split("\\.")[0] + ".txt"));
            writer.write(everything);

            writer.close();
            return new File(this.fileName.split("\\.")[0] + ".txt");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
