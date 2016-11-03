package com.community.yuequ.modle;

import com.community.yuequ.imple.HomeData;

/**
 * Created by Administrator on 2016/11/3.
 */

public class HomeOnline implements HomeData {
    private int itemType;
    public RProgram mProgram;


    public HomeOnline(int itemType) {

        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }


}
