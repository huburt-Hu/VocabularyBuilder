package com.example.app.vocabularybuilder.example;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;


/**
 * Created by hxb on 2016/5/7.
 */
public class WordListParser {
    public DataBaseHelper dbHelper=null;
    public Context context=null;
    public String tableName=null;
    public WordListParser(){

    }


    public WordListParser(Context context, String tableName) {
        this.context=context;
        this.tableName=tableName;
        dbHelper=new DataBaseHelper(context, tableName);
    }




    public void parse(String lineStr){
        int countWord=0;
        int countInterpret=0;
        int count=0;
        String strInterpret="";
        String str="";
        char[] charArray=null;
        Pattern patternWord=Pattern.compile("[a-zA-Z]+[ ]+");
        //"%>[^<%%>]*<%"
        Pattern patternInterpret=Pattern.compile("%E>[^<S%%E>]+<S%");
        Matcher matcherWord=patternWord.matcher(lineStr);
        Matcher matcherInterpret=null;
        ArrayList<String> wordList=new ArrayList<String>();
        ArrayList<String> interpretList=new ArrayList<String>();

        while(matcherWord.find()){
            str=matcherWord.group();
            charArray=str.toCharArray();
            if(charArray.length>0 && (charArray[0]>='A'&& charArray[0]<='Z' )){
                charArray[0]+=('a'-'A');
                str=new String(charArray,0,charArray.length);     //首字母去掉大写
            }
            wordList.add(str.trim());
        }
        if(wordList.size()<=0)
            return;
        matcherWord.reset(lineStr);
        if(matcherWord.find()){
            strInterpret=matcherWord.replaceAll("<S%%E>");
        }
        strInterpret+="<S%%E>";


        matcherInterpret=patternInterpret.matcher(strInterpret);
        while(matcherInterpret.find()){
            str=matcherInterpret.group();
            interpretList.add(new String(str.toCharArray(),3,str.length()-6));
        }
        countWord=wordList.size();
        countInterpret=interpretList.size();
        count=countWord>countInterpret?countInterpret:countWord;
        for(int i=0;i<count;i++){
            dbHelper.insertWordInfoToDataBase(wordList.get(i), interpretList.get(i), true);
        }
    }

//    public boolean isOfAnWord(int index,char[] str){
//        int i=index;
//        for( ;i<str.length;i++  ){
//            if(isAlpha(str[i])==false)
//                break;
//        }
//        if(i==index)
//            return false;
//        if(i>=str.length)
//            return true;
//        if(str[i]=='.')
//            return false;
//        return true;
//
//    }
//
//
//    public boolean isAlpha(char ch){
//        if((ch>='A'&&ch<='Z') ||(ch>='a'&&ch<='z')){
//            return true;
//        }
//        else
//            return false;
//    }
//
//
//    public boolean isChinese(char ch){
//        if(ch>129)
//            return true;
//        else
//            return false;
//    }
}
