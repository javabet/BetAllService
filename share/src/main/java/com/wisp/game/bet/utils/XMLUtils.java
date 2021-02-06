package com.wisp.game.bet.utils;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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

    public static Document file2Document (InputStream inputStream) {
        Document doc = null;
        try {
            SAXReader saxReader = new SAXReader();
            doc = saxReader.read(inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return doc;
    }

    public static String getStringByElement(Element element, String key)
    {
        Attribute attribute = element.attribute(key);
        if( attribute == null )
        {
            return "";
        }

        return attribute.getValue();
    }


    public static int getIntByElement(Element element, String key)
    {
        Attribute attribute = element.attribute(key);
        if( attribute == null )
        {
            return 0;
        }
        String numStr = attribute.getValue();
        if( numStr == "" )
        {
            return 0;
        }

        return Integer.valueOf(numStr);
    }

    public static boolean getBooleanByElement(Element element,String key)
    {
        Attribute attribute = element.attribute(key);
        if( attribute == null )
        {
            return false;
        }
        String numStr = attribute.getValue();
        if( numStr == "" )
        {
            return false;
        }

        return Boolean.valueOf(numStr);
    }

    public static long getLongByElement(Element element,String key)
    {
        Attribute attribute = element.attribute(key);
        if( attribute == null )
        {
            return 0l;
        }
        String numStr = attribute.getValue();
        if( numStr == "" )
        {
            return 0l;
        }

        return Long.valueOf(numStr);
    }

    public static double getDoubleByElement(Element element,String key)
    {
        Attribute attribute = element.attribute(key);
        if( attribute == null )
        {
            return 0d;
        }
        String numStr = attribute.getValue();
        if( numStr == "" )
        {
            return 0d;
        }

        return Double.valueOf(numStr);
    }

    public static float getFloatleByElement(Element element,String key)
    {
        Attribute attribute = element.attribute(key);
        if( attribute == null )
        {
            return 0;
        }
        String numStr = attribute.getValue();
        if( numStr == "" )
        {
            return 0;
        }

        return Float.valueOf(numStr);
    }

}
