package com.anlia.pageturn;

import android.graphics.Bitmap;
import android.os.Environment;

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

    public DateCenter() {
       strings = new ArrayList<>();
       strings.add(Path + "/Camera/IMG_20180208_115143.jpg");
       strings.add(Path + "/Camera/IMG_20180206_095106.jpg");
       strings.add(Path + "/Camera/IMG_20180206_095106.jpg");
    }


    @Override
    public Bitmap currentPage() {
        return BitmapUtils.adjustFromFile2Bitmap(strings.get(currentPage));
    }

    @Override
    public Bitmap nextPage() {
        return BitmapUtils.adjustFromFile2Bitmap(strings.get(currentPage + 1));
    }

    @Override
    public Bitmap prePage() {
        return BitmapUtils.adjustFromFile2Bitmap(strings.get(currentPage - 1));
    }

    @Override
    public boolean hasNextPage() {
        return strings.size() > currentPage;
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
        return false;
    }
}
