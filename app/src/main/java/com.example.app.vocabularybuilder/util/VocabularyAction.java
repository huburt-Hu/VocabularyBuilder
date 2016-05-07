package com.example.app.vocabularybuilder.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.app.vocabularybuilder.db.VocabularySQLiteHelper;
import com.example.app.vocabularybuilder.model.Vocabulary;
import com.example.app.vocabularybuilder.model.Words;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxb on 2016/5/7.
 * 生词本操作的工具类，包含所有对生词本操作的方法
 */
public class VocabularyAction {
    /**
     * Vocabulary的表名
     */
    private final String TABLE_VOCABULARY = "Vocabulary";
    /**
     * 本类的实例
     */
    private static VocabularyAction vocabularyAction;
    /**
     * 数据库工具，用于增、删、该、查
     */
    private SQLiteDatabase db;

    /**
     * 私有化的构造器
     */
    private VocabularyAction(Context context) {
        VocabularySQLiteHelper vocabularySQLiteHelper = new VocabularySQLiteHelper(context, TABLE_VOCABULARY, null, 1);
        db = vocabularySQLiteHelper.getWritableDatabase();
    }

    /**
     * 单例类VocabularyAction获取实例方法
     *
     * @param context 上下文
     */
    public static VocabularyAction getInstance(Context context) {
        if (vocabularyAction == null) {
            synchronized (VocabularyAction.class) {
                if (vocabularyAction == null) {
                    vocabularyAction = new VocabularyAction(context);
                }
            }
        }
        return vocabularyAction;
    }

    /**
     * 向生词本中添加单词
     *
     * @param words 查询到的单词
     */
    public void addToVocabulary(Words words) {
        Vocabulary vocabulary = new Vocabulary(words.getKey(), words.getPosAcceptation());
        ContentValues values = new ContentValues();
        values.put("wordsKey", vocabulary.getWordsKey());
        values.put("translation", vocabulary.getTranslation());
        values.put("masteryLevel", vocabulary.getMasteryLevel());
        values.put("right", vocabulary.getRight());
        values.put("wrong", vocabulary.getWrong());
        db.insert(TABLE_VOCABULARY, null, values);
        values.clear();
    }

    /**
     * 判断单词是否存在于生词本中
     *
     * @param wordsKey 查询到的单词的key
     */
    public boolean isExistInVocabulary(String wordsKey) {
        Cursor cursor = db.query(TABLE_VOCABULARY, null, "wordsKey = ?", new String[]{wordsKey}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    /**
     * 删除生词本中的指定单词
     *
     * @param wordsKey 指定单词的key
     */
    public void deleteFormVocabulary(String wordsKey) {
        if (isExistInVocabulary(wordsKey)) {
            db.delete(TABLE_VOCABULARY, "wordsKey = ?", new String[]{wordsKey});
        }
    }

    /**
     * 获取生词本中所有生词
     * @return vocabularyList Vocabulary对象的List
     */
    public List<Vocabulary> getVocabularyList() {
        List<Vocabulary> vocabularyList = new ArrayList<Vocabulary>();
        Cursor cursor = db.query(TABLE_VOCABULARY, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Vocabulary vocabulary = new Vocabulary();
                vocabulary.setWordsKey(cursor.getString(cursor.getColumnIndex("wordsKey")));
                vocabulary.setTranslation(cursor.getString(cursor.getColumnIndex("translation")));
                vocabulary.setMasteryLevel(cursor.getInt(cursor.getColumnIndex("masteryLevel")));
                vocabulary.setRight(cursor.getInt(cursor.getColumnIndex("right")));
                vocabulary.setWrong(cursor.getInt(cursor.getColumnIndex("wrong")));
                vocabularyList.add(vocabulary);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return vocabularyList;
    }

    /**
     * 练习中完成选择后调用此方法，根据回答情况，更新传入Vocabulary的掌握情况、对错次数属性，并保存到数据库。
     *
     * @param vocabulary 完成练习的vocabulary
     * @param isRight    回答是否正确
     */
    public void finishSelect(Vocabulary vocabulary, boolean isRight) {
        int masteryLevel = vocabulary.getMasteryLevel();
        int right = vocabulary.getRight();
//        int worng = vocabulary.getWrong();//留用于影响掌握等级的答错次数修改
        if (isRight) {
            right += 1;
            if (right >= 2) {
                //练习答对2次，提升掌握等级
                //if条件语句省略括号
                if (masteryLevel == Vocabulary.MASTERY_LEVEL_1)
                    masteryLevel = Vocabulary.MASTERY_LEVEL_2;
                else if (masteryLevel == Vocabulary.MASTERY_LEVEL_2)
                    masteryLevel = Vocabulary.MASTERY_LEVEL_3;
                else
                    masteryLevel = Vocabulary.MASTERY_LEVEL_4;
                right = 0;
            }
        } else {
            //答错直接降低掌握等级
            if (masteryLevel == Vocabulary.MASTERY_LEVEL_4)
                masteryLevel = Vocabulary.MASTERY_LEVEL_3;
            else if (masteryLevel == Vocabulary.MASTERY_LEVEL_3)
                masteryLevel = Vocabulary.MASTERY_LEVEL_2;
            else
                masteryLevel = Vocabulary.MASTERY_LEVEL_1;
        }
        ContentValues values = new ContentValues();
        values.put("masteryLevel", masteryLevel);
        values.put("right", right);
        db.update(TABLE_VOCABULARY, values, "wordsKey = ?", new String[]{vocabulary.getWordsKey()});
        values.clear();
    }
}
