package com.example.app.vocabularybuilder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hxb on 2016/5/3.
 * 新建数据库的帮助类
 */
public class WordsSQLiteOpenHelper extends SQLiteOpenHelper {
    /**
     * 建表语句
     */
    private final String CREATE_WORDS = "create table Words(" +
            "id Integer primary key autoincrement," +
            "isChinese text," +
            "key text," +
            "fy text," +
            "psE text," +
            "pronE text," +
            "psA text," +
            "pronA text," +
            "posAcceptation text," +
            "sent text)";

    public WordsSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
