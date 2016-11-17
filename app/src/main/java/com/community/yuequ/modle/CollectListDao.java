package com.community.yuequ.modle;

import java.util.List;

/**
 * Created by Administrator on 2016/11/17.
 */

public class CollectListDao {
    public int errorCode;
    public String errorMessage;
    public CollectListBean result;

    public static class CollectListBean{
        public int total_cnt;//总页数
        public int total_page;//总页数
        public List<Collect> list;

    }
}
