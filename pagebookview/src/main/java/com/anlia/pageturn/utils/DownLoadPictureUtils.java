package com.anlia.pageturn.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jin on 2018/2/8.'
 * 下载图片
 */

public class DownLoadPictureUtils {
    /**
     * @param imgUrl
     *   远程图片文件的URL
     *
     *   下载远程图片
     */
    private void loadRmoteImage(String imgUrl) throws IOException {
        URL fileURL = null;
        Bitmap bitmap = null;
        try {
            fileURL = new URL(imgUrl);
        } catch (MalformedURLException err) {
            err.printStackTrace();
        }

            HttpURLConnection conn = (HttpURLConnection) fileURL
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            int length = (int) conn.getContentLength();
            if (length != -1) {
                byte[] imgData = new byte[length];
                byte[] buffer = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(buffer)) > 0) {
                    System.arraycopy(buffer, 0, imgData, destPos, readLen);
                    destPos += readLen;
                }
                bitmap = BitmapFactory.decodeByteArray(imgData, 0,
                        imgData.length);
            }
    }
}
