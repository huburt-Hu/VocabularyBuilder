package com.example.app.vocabularybuilder.model;

/**
 * Created by hxb on 2016/5/7.
 * 生词本中单词的对应类
 */
public class Vocabulary {
    /**
     * 生词的掌握程度，等级1：生词
     */
    public static final int MASTERY_LEVEL_1 = 1;
    /**
     * 生词的掌握程度，等级2：熟悉
     */
    public static final int MASTERY_LEVEL_2 = 2;
    /**
     * 生词的掌握程度，等级3：基本掌握
     */
    public static final int MASTERY_LEVEL_3 = 3;
    /**
     * 生词的掌握程度，等级4：完全掌握
     */
    public static final int MASTERY_LEVEL_4 = 4;
    /**
     * 生词的key，对应Words类的key属性
     */
    private String wordsKey;
    /**
     * 生词的基本释义，对应Words类的posAcceptation属性
     */
    private String translation;
    /**
     * 生词的掌握程度,可选四个等级，初始等级1，例：Vocabulary.MASTERY_LEVEL_1
     */
    private int masteryLevel;
    /**
     * 练习中答对的次数，练习答对两次提升掌握等级
     */
    private int right;
    /**
     * 练习中答错的次数，答错一次降低掌握等级
     */
    private int wrong;

    public Vocabulary() {
        wordsKey = "";
        translation = "";
        masteryLevel = MASTERY_LEVEL_1;
        right = 0;
        wrong = 0;
    }

    public Vocabulary(String wordsKey, String translation) {
        this.wordsKey = wordsKey;
        this.translation = translation;
        masteryLevel = MASTERY_LEVEL_1;
        right = 0;
        wrong = 0;
    }

    public String getWordsKey() {
        return wordsKey;
    }

    public void setWordsKey(String wordsKey) {
        this.wordsKey = wordsKey;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public int getMasteryLevel() {
        return masteryLevel;
    }

    public int getRight() {
        return right;
    }

    public int getWrong() {
        return wrong;
    }

    public void setMasteryLevel(int masteryLevel) {
        this.masteryLevel = masteryLevel;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public void setWrong(int wrong) {
        this.wrong = wrong;
    }
}
