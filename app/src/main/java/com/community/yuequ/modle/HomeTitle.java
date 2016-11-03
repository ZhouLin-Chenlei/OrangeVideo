package com.community.yuequ.modle;

import com.community.yuequ.imple.HomeData;

/**
 * Created by Administrator on 2016/11/3.
 */

public class HomeTitle implements HomeData {
    public String titleName;
    public String type;
    public int id;
    private int itemType;



    public HomeTitle(int id,String titleName, String type,int itemType) {
        this.id = id;
        this.titleName = titleName;
        this.type = type;
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

}
