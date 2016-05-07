package com.example.app.vocabularybuilder.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by hxb on 2016/5/5.
 * 文件存储的工具类
 */
public class FileUtil {
    /**
     * SD卡的目录
     */
    private String SDPath;
    /**
     * 本app存储的目录
     */
    private String AppPath;
    /**
     * 本类的单例
     */
    private static FileUtil fileUtil;

    /**
     * 私有化的构造器
     */
    private FileUtil() {
        //如果手机已插入SD卡，且应用程序具有读写SD卡的功能，则返回true
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SDPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
            //清理测试时产生的文件
//            File f = new File(SDPath + "VocabularyBuilder");
//            deleteFile(f);
            File fileV = createSDDir(SDPath, "VocabularyBuilder");
            AppPath = fileV.getAbsolutePath() + "/";
        }
    }

    /**
     * 单例类FileUtil获取实例方法
     */
    public static FileUtil getInstance() {
        if (fileUtil == null) {
            synchronized (FileUtil.class) {
                if (fileUtil == null) {
                    fileUtil = new FileUtil();
                }
            }
        }
        return fileUtil;
    }

    /**
     * 创建目录
     *
     * @param path    文件夹的路径
     * @param dirName 文件夹名
     */
    public File createSDDir(String path, String dirName) {
        File dir = new File(path + dirName);
        if (dir.exists() && dir.isDirectory()) {
            return dir;
        }
        dir.mkdir();
        Log.d("测试", "创建目录成功");
        return dir;
    }

    /**
     * 创建SD文件
     *
     * @param path     文件的路径
     * @param fileName 文件名
     */
    public File createSDFile(String path, String fileName) {
        File file = new File(path + fileName);
        if (file.exists() && file.isFile()) {
            return file;
        }
        try {
            file.createNewFile();
            Log.d("测试", "创建文件成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 向SD卡中写入文件
     *
     * @param path        文件夹名
     * @param fileName    文件名
     * @param inputStream 输入流
     */
    public void writeToSD(String path, String fileName, InputStream inputStream) {
        OutputStream outputStream = null;
        try {
            File dir = createSDDir(AppPath, path);
            File file = createSDFile(dir.getAbsolutePath() + "/", fileName);
            outputStream = new FileOutputStream(file);
            int length;
            byte[] buffer = new byte[2 * 1024];
            while ((length = inputStream.read(buffer)) != -1) {
                //注意这里的length；
                //利用read返回的实际成功读取的字节数，将buffer写入文件，
                // 否则将会出现错误的字节，导致保存文件与源文件不一致
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            Log.d("测试", "写入成功");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("测试", "写入失败");
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件在SD卡上绝对路径，如无该文件返回""
     *
     * @param fileName 单词对应的文件夹名
     */
    public String getPathInSD(String fileName) {
        File file = new File(AppPath + fileName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return "";
    }

    /**
     * 递归删除文件夹
     *
     * @param file 文件夹或者文件名
     */
    private void deleteFile(File file) {
        if (file.exists()) {//判断文件是否存在
            if (file.isFile()) {//判断是否是文件
                file.delete();//删除文件
            } else if (file.isDirectory()) {//否则如果它是一个目录
                File[] files = file.listFiles();//声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) {//遍历目录下所有的文件
                    deleteFile(files[i]);//把每个文件用这个方法进行迭代
                }
                file.delete();//删除文件夹
            }
            Log.d("测试", "删除成功");
        }
    }
}



