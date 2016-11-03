package com.community.yuequ.modle;

import com.community.yuequ.imple.HomeData;

import java.util.ArrayList;

/**
 * 点播视频，一行有2个或者3个
 */

public class HomeItem implements HomeData {
    private int itemType;
    public final ArrayList<RProgram> mPrograms = new ArrayList<>();

    public HomeItem(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }


}
