package com.community.yuequ.modle;

import java.util.List;

/**
 * 推荐页的一个栏目
 */
public class RGroup {
    public int column_id;
    public String column_name;
    public String type;// 1：视频
    public List<RProgram> plist;

}
