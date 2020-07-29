package com.wisp.game.bet.utils;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;

public class XMLUtils {

    public static Document file2Document (String filePath) {
        Document doc = null;
        FileInputStream fis = null;
        try {
            SAXReader saxReader = new SAXReader ();
            saxReader.setEncoding ("UTF-8");
            File f = new File (filePath);
            if (f.exists()) {
                fis = new FileInputStream (f);
                doc = saxReader.read(fis);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return doc;
    }
}
