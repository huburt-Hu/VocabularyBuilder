package com.example.app.vocabularybuilder.example;

/**
 * Created by hxb on 2016/5/7.
 */
public class WordInfo {
    public String word;
    public String interpret;
    public int wrong;
    public int right;
    public int grasp;
    public WordInfo(String word, String interpret, int wrong, int right,
                    int grasp) {
        super();
        this.word = word;
        this.interpret = interpret;
        this.wrong = wrong;
        this.right = right;
        this.grasp = grasp;
    }
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public String getInterpret() {
        return interpret;
    }
    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }
    public int getWrong() {
        return wrong;
    }
    public void setWrong(int wrong) {
        this.wrong = wrong;
    }
    public int getRight() {
        return right;
    }
    public void setRight(int right) {
        this.right = right;
    }
    public int getGrasp() {
        return grasp;
    }
    public void setGrasp(int grasp) {
        this.grasp = grasp;
    }
}
