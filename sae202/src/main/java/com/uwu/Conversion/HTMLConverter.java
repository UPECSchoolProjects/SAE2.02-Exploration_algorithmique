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

public class HTMLConverter implements IConverter {
    String fileName;

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
            String line = br.readLine();

            while (line != null) {
                if (line.contains("<body>"))
                    isBody = true;

                if (!isBody)
                    continue;

                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();

            BufferedWriter writer =
                    new BufferedWriter(new FileWriter(this.fileName.split(".")[0] + ".txt"));
            writer.write(everything);

            writer.close();
            return new File(this.fileName.split(".")[0] + ".txt");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
