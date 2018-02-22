package com.anlia.pageturn.inter;

/**
 * Created by jinmingkai on 2018/2/13.
 * 控件的回调接口
 */

public interface BookPageCallable {

    /**
     * 没有下一页的回调
     */
    public void noNextPage();

    /**
     * 没有前一页
     */
    public void noPrePage();


}
