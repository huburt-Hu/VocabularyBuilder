package com.example.app.vocabularybuilder.util;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.SAXParserFactory;

/**
 * Created by hxb on 2016/5/1.
 * 解析XML的工具类
 */
public class ParseXML {
    /**
     * 使用SAX解析XML的方法
     */
    public static void parse(DefaultHandler handler, InputStream inputStream) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(reader));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
