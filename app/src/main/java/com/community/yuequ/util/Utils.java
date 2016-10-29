package com.community.yuequ.util;

import com.community.yuequ.Contants;

/**
 * Created by apple on 16/5/26.
 */
public class Utils {

    public static boolean isPass(String is_cost,int erroCode){

        return "0".equals(is_cost)||erroCode == Contants.HTTP_VIP ||erroCode == Contants.HTTP_ONECE;

    }
    public static boolean isPass(int erroCode){

        return erroCode == Contants.HTTP_VIP ||erroCode == Contants.HTTP_ONECE;

    }
}
