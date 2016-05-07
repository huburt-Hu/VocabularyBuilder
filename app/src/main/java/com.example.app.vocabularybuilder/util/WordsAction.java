package com.example.app.vocabularybuilder.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.example.app.vocabularybuilder.db.WordsSQLiteOpenHelper;
import com.example.app.vocabularybuilder.model.Words;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by hxb on 2016/5/3.
 * 查词的工具类，内部有方法：保存words到数据库
 * 获取address地址
 * 向数据库中查找words
 * 保存发音mp3文件
 * 播放发音MP3
 */
public class WordsAction {
    /**
     * 本类的实例
     */
    private static WordsAction wordsAction;
    /**
     * Words的表名
     */
    private final String TABLE_WORDS = "Words";
    /**
     * 数据库工具，用于增、删、该、查
     */
    private SQLiteDatabase db;
    private MediaPlayer player = null;

    /**
     * 私有化的构造器
     */
    private WordsAction(Context context) {
        WordsSQLiteOpenHelper helper = new WordsSQLiteOpenHelper(context, TABLE_WORDS, null, 1);
        db = helper.getWritableDatabase();
    }

    /**
     * 单例类WordsAction获取实例方法
     *
     * @param context 上下文
     */
    public static WordsAction getInstance(Context context) {
        //双重效验锁，提高性能
        if (wordsAction == null) {
            synchronized (WordsAction.class) {
                if (wordsAction == null) {
                    wordsAction = new WordsAction(context);
                }
            }
        }
        return wordsAction;
    }

    /**
     * 向数据库中保存新的Words对象
     * 会先对word进行判断，为有效值时才会保存
     *
     * @param words 单词类的实例
     */
    public boolean saveWords(Words words) {
        //判断是否是有效对象，即有数据
        if (words.getSent().length() > 0) {
            ContentValues values = new ContentValues();
            values.put("isChinese", "" + words.getIsChinese());
            values.put("key", words.getKey());
            values.put("fy", words.getFy());
            values.put("psE", words.getPsE());
            values.put("pronE", words.getPronE());
            values.put("psA", words.getPsA());
            values.put("pronA", words.getPronA());
            values.put("posAcceptation", words.getPosAcceptation());
            values.put("sent", words.getSent());
            db.insert(TABLE_WORDS, null, values);
            values.clear();
            return true;
        }
        return false;
    }

    /**
     * 从数据库中查找查询的words
     *
     * @param key 查找的值
     * @return words 若返回words的key为空，则说明数据库中没有该词
     */
    public Words getWordsFromSQLite(String key) {
        Words words = new Words();
        Cursor cursor = db.query(TABLE_WORDS, null, "key=?", new String[]{key}, null, null, null);
        //数据库中有
        if (cursor.getCount() > 0) {
            Log.d("测试", "数据库中有");
            if (cursor.moveToFirst()) {
                do {
                    String isChinese = cursor.getString(cursor.getColumnIndex("isChinese"));
                    if ("true".equals(isChinese)) {
                        words.setIsChinese(true);
                    } else if ("false".equals(isChinese)) {
                        words.setIsChinese(false);
                    }
                    words.setKey(cursor.getString(cursor.getColumnIndex("key")));
                    words.setFy(cursor.getString(cursor.getColumnIndex("fy")));
                    words.setPsE(cursor.getString(cursor.getColumnIndex("psE")));
                    words.setPronE(cursor.getString(cursor.getColumnIndex("pronE")));
                    words.setPsA(cursor.getString(cursor.getColumnIndex("psA")));
                    words.setPronA(cursor.getString(cursor.getColumnIndex("pronA")));
                    words.setPosAcceptation(cursor.getString(cursor.getColumnIndex("posAcceptation")));
                    words.setSent(cursor.getString(cursor.getColumnIndex("sent")));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            Log.d("测试", "数据库中没有");
            cursor.close();
        }

        return words;
    }

    /**
     * 获取网络查找单词的对应地址
     *
     * @param key 要查询的单词
     * @return address 所查单词对应的http地址
     */
    public String getAddressForWords(final String key) {
        String address_p1 = "http://dict-co.iciba.com/api/dictionary.php?w=";
        String address_p2 = "";
        String address_p3 = "&key=E568F04171398072F7EC5D8B4A6CBDB4";
        if (isChinese(key)) {
            try {
                //此处非常重要！对中文的key进行重新编码，生成正确的网址
                address_p2 = "_" + URLEncoder.encode(key, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            address_p2 = key;
        }
        return address_p1 + address_p2 + address_p3;

    }

    /**
     * 判断是否是中文
     *
     * @param strName String类型的字符串
     */
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据Unicode编码完美的判断中文汉字和符号
     *
     * @param c char类型的字符串
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 保存words的发音MP3文件到SD卡
     * 先请求Http，成功后保存
     *
     * @param words words实例
     */
    public void saveWordsMP3(Words words) {
        String addressE = words.getPronE();
        String addressA = words.getPronA();
        if (addressE != "") {
            final String filePathE = words.getKey();
            HttpUtil.sentHttpRequest(addressE, new HttpCallBackListener() {
                @Override
                public void onFinish(InputStream inputStream) {
                    FileUtil.getInstance().writeToSD(filePathE, "E.mp3", inputStream);
                }

                @Override
                public void onError() {

                }
            });
        }
        if (addressA != "") {
            final String filePathA = words.getKey();
            HttpUtil.sentHttpRequest(addressA, new HttpCallBackListener() {
                @Override
                public void onFinish(InputStream inputStream) {
                    FileUtil.getInstance().writeToSD(filePathA, "A.mp3", inputStream);
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    /**
     * 播放words的发音
     *
     * @param wordsKey 单词的key
     * @param ps       E 代表英式发音
     *                 A 代表美式发音
     * @param context  上下文
     */
    public void playMP3(String wordsKey, String ps, Context context) {
        String fileName = wordsKey + "/" + ps + ".mp3";
        String adrs = FileUtil.getInstance().getPathInSD(fileName);
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
        if (adrs != "") {//有内容则播放
            player = MediaPlayer.create(context, Uri.parse(adrs));
            Log.d("测试", "播放");
            player.start();
        } else {//没有内容则重新去下载
            Words words = getWordsFromSQLite(wordsKey);
            saveWordsMP3(words);
        }
    }
}
