package com.community.yuequ.modle;

import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */

public class OrOnlineListDao {
    public int errorCode;
    public String errorMessage;
    public OrOnlineListDao.OrOnlineListBean result;

    public static class OrOnlineListBean{
        public int total_cnt;//总页数
        public int total_page;//总页数
        public List<RProgram> list;

    }
}
