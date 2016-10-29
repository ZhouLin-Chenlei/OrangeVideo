package com.community.yuequ;

/**
 * Created by Administrator on 2016/5/5.
 */
public class Contants {

    public static final int HTTP_OK = 200;
    /**1：上传，2：外链地址，3：手工填写视频URL*/
    public static final String SHOWTYPE_UPLOAD = "1";
    public static final String SHOWTYPE_LINK = "2";
    public static final String SHOWTYPE_EDIT = "3";


    //201：游客，202：包月会员，203：按次订购会员
    public static final int HTTP_NO = 201;
    public static final int HTTP_VIP = 202;
    public static final int HTTP_ONECE = 203;
    public static final int HTTP_NO_PERMISSION = 500;


    protected static final String PROTOCOL_HTTP = "http://";
    protected static String PROTOCOL = PROTOCOL_HTTP;
    public static String DOMAIN = "182.92.192.208:8088/yqi";
//    public static String DOMAIN = "171.8.238.171:8086/yqi";
    public static String PICDOMAIN = "http://182.92.192.208:8088/yqfile";

    public static final String PRODUCTNAME = "yuequ";
    //系统初始化接口
    public static String URL_INIT = PROTOCOL+DOMAIN+"/system/init.shtml";
    //推荐接口
    public static String URL_RECOMMEND = PROTOCOL + DOMAIN +"/column/recommendList.shtml";
    //视频栏目列表
    public static String URL_VIDEOLIST = PROTOCOL + DOMAIN +"/column/videoList.shtml";
    //图文栏目列表
    public static String URL_PICTURELIST = PROTOCOL + DOMAIN +"/column/pictureList.shtml";
    //节目列表（视频或图文）
    public static String URL_PROGRAMLIST = PROTOCOL + DOMAIN +"/program/programList.shtml";
    //节目详情
    public static String URL_PROGRAMDETAIL = PROTOCOL + DOMAIN +"/program/programDetail.shtml";
    //专题列表
    public static String URL_SPECIALSUBJECTLIST = PROTOCOL + DOMAIN +"/column/specialSubjectList.shtml";
    //专题节目列表
    public static String URL_SPECPROGRAMLIST = PROTOCOL + DOMAIN +"/program/specProgramList.shtml";
    //2.10	根据订购类型获取订购提示语
    public static String URL_ORDERTIPS = PROTOCOL + DOMAIN +"/order/orderTips.shtml";
    //2.11	视频播放鉴权
    public static String URL_PLAYACCESS = PROTOCOL + DOMAIN +"/program/playAccess.shtml";
    //2.13	更新用户信息（系统没有用户手机号，添加用户手机号）
    public static String URL_UPDATEUSER = PROTOCOL + DOMAIN +"/system/updateUser.shtml";
    //2.12	根据订购类型订购节目
    public static String URL_BUY = PROTOCOL + DOMAIN +"/order/buy.shtml";


}
