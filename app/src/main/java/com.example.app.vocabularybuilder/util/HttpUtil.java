package com.example.app.vocabularybuilder.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hxb on 2016/5/1.
 * Http访问工具类
 */
public class HttpUtil {
    /**
     * 在新线程中发送网络请求
     *
     * @param address  网络地址
     * @param listener HttpCallBackListener接口的实现类;
     *                 onFinish方法为访问成功后的回调方法;
     *                 onError为访问不成功时的回调方法
     */
    public static void sentHttpRequest(final String address, final HttpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    if (listener != null) {
                        listener.onFinish(inputStream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError();
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
