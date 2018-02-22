package com.anlia.pageturn;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.LruCache;

import com.anlia.pageturn.inter.FlipPagerDateInterface;
import com.anlia.pageturn.utils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by jin on 2018/2/8.
 * 数据中心
 * 用来存储当前的翻页效果的总的数据的
 */

public class DateCenter implements FlipPagerDateInterface {
    /**
     * 默认的图片下载的地址
     */
    public static String DEFAULT_DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    ArrayList<String> strings;
    private int currentPage = 0;
    String Path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    private LruCache<String, Bitmap> objectObjectLruCache;


    public DateCenter() {
       strings = new ArrayList<>();
       objectObjectLruCache = new LruCache<String, Bitmap>(5);
       strings.add(Path + "/Camera/IMG_20180208_115143.jpg");
       strings.add(Path + "/Camera/IMG_20180206_095106.jpg");
       strings.add(Path + "/Camera/IMG_20180206_093101.jpg");
    }


    @Override
    public Bitmap currentPage() {
        Bitmap bitmap = objectObjectLruCache.get(strings.get(currentPage));
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = BitmapUtils.adjustFromFile2Bitmap(strings.get(currentPage)).copy(Bitmap.Config.ARGB_8888, true);
        objectObjectLruCache.put(strings.get(currentPage), bitmap);
        return bitmap;
    }

    @Override
    public Bitmap nextPage() {
        if (currentPage + 1 < strings.size()){
            Bitmap bitmap = objectObjectLruCache.get(strings.get(currentPage+1));
            if (bitmap != null)
                return bitmap;
            bitmap = BitmapUtils.adjustFromFile2Bitmap(strings.get(currentPage + 1)).copy(Bitmap.Config.ARGB_8888, true);
            objectObjectLruCache.put(strings.get(currentPage+1), bitmap);

            return bitmap;

        }
        else
            return null;
    }

    @Override
    public Bitmap prePage() {
        if (currentPage > 0) {

            Bitmap bitmap = objectObjectLruCache.get(strings.get(currentPage-1));
            if (bitmap != null)
                return bitmap;
            bitmap = BitmapUtils.adjustFromFile2Bitmap(strings.get(currentPage - 1)).copy(Bitmap.Config.ARGB_8888, true);
            objectObjectLruCache.put(strings.get(currentPage-1), bitmap);

            return bitmap;
        }
        else
            return null;

    }

    @Override
    public boolean hasNextPage() {
        return strings.size() > currentPage+1;
    }

    @Override
    public boolean hasPrePage() {
        return currentPage > 0;
    }

    @Override
    public void defaultPage(String path) {

    }

    @Override
    public boolean addPage(String pagePath) {
        return strings.add(pagePath);
    }

    @Override
    public int pulsing() {
        return currentPage++;
    }

    @Override
    public int minusing() {
        return currentPage--;
    }
}
