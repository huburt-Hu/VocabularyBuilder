package com.example.app.vocabularybuilder.util;

import android.util.Log;

import com.example.app.vocabularybuilder.model.Words;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by hxb on 2016/5/1.
 * 解析查词XML的对应类
 */
public class WordsHandler extends DefaultHandler {
    //记录当前节点
    private String nodeName;
    private Words words;
    //单词的词性与词义
    private StringBuilder posAcceptation;
    //例句
    private StringBuilder sent;

    /**
     * 获取解析后的words对象
     */
    public Words getWords() {
        return words;
    }

    //开始解析XML时调用
    @Override
    public void startDocument() throws SAXException {
        //初始化
        words = new Words();
        posAcceptation = new StringBuilder();
        sent = new StringBuilder();
    }

    //结束解析XML时调用
    @Override
    public void endDocument() throws SAXException {
        //将所有解析出来的内容赋予words
        words.setPosAcceptation(posAcceptation.toString().trim());
        words.setSent(sent.toString().trim());
    }

    //开始解析节点时调用
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        nodeName = localName;
    }

    //结束解析节点时调用
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //在读完整个节点后换行
        if ("acceptation".equals(localName)) {
            posAcceptation.append("\n");
        } else if ("orig".equals(localName)) {
            sent.append("\n");
        } else if ("trans".equals(localName)) {
            sent.append("\n");
            sent.append("\n");
        }
    }

    //获取节点中内容时调用
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String a = new String(ch, start, length);
        //去掉文本中原有的换行
        for (int i = start; i < start + length; i++) {
            if (ch[i] == '\n')
                return;
        }
        //将节点的内容存入Words对象对应的属性中
        if ("key".equals(nodeName)) {
            words.setKey(a.trim());
        } else if ("ps".equals(nodeName)) {
            if (words.getPsE().length() <= 0) {
                words.setPsE(a.trim());
            } else {
                words.setPsA(a.trim());
            }
        } else if ("pron".equals(nodeName)) {
            if (words.getPronE().length() <= 0) {
                words.setPronE(a.trim());
            } else {
                words.setPronA(a.trim());
            }
        } else if ("pos".equals(nodeName)) {
            posAcceptation.append(a);
        } else if ("acceptation".equals(nodeName)) {
            posAcceptation.append(a);
        } else if ("orig".equals(nodeName)) {
            sent.append(a);
        } else if ("trans".equals(nodeName)) {
            sent.append(a);
        } else if ("fy".equals(nodeName)) {
            words.setFy(a);
            words.setIsChinese(true);
        }
    }
}