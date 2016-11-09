package com.community.yuequ.modle;

import java.util.List;

/**
 * Created by Administrator on 2016/5/18.
 */
public class PicListDao {

    public int errorCode;
    public String errorMessage;
    public PicListBean result;

     public static class PicListBean{
        public int total_cnt;//总个数
        public int total_page;//总页数
        public List<RProgram> list;

     }
}
