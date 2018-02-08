package com.anlia.pageturn.inter;

import android.graphics.Bitmap;

/**
 * Created by jin on 2018/2/8.
 * 主要是数据的接口
 */

public interface FlipPagerDateInterface {

    public Bitmap currentPage();
    public Bitmap nextPage();

    public Bitmap prePage();

    public boolean hasNextPage();

    public boolean hasPrePage();

    public void defaultPage(String path);

    public boolean addPage(String pagePath);
}
