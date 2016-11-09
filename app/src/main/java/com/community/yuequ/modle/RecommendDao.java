package com.community.yuequ.modle;

import java.util.List;

/**
 * Created by Administrator on 2016/5/11.
 */
public class RecommendDao {

    public int errorCode;
    public String errorMessage;
    public Recommend result;

    public static class Recommend{
        public List<Advert> advert;
        public List<RGroup> program;



    }
}
