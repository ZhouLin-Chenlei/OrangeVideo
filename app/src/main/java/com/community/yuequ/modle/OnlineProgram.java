package com.community.yuequ.modle;

import com.community.yuequ.imple.PlayData;

/**
 * Created by Administrator on 2016/11/9.
 */

public class OnlineProgram implements PlayData {
    public int id;
    public String name;
    public String remark;
    public String begin_time;
    public String end_time;
    public String order_date;
    public String img_path;
    public String contents;

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getVideoUrl() {
        return contents;
    }

    @Override
    public String getVideType() {
        return "3";
    }
}
