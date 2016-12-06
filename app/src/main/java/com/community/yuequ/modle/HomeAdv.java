package com.community.yuequ.modle;

import com.community.yuequ.imple.HomeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/25.
 */

public class HomeAdv implements HomeData {

    private int itemType;
    public final List<Advert> mAdvertList = new ArrayList<>();


    public HomeAdv(int itemType) {
        mAdvertList.clear();
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }




}
