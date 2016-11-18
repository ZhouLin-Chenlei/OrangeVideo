package com.community.yuequ;

/**
 * 常量
 */
public class Contants {

    public static final int HTTP_OK = 200;
    /**1：上传，2：外链地址，3：手工填写视频URL*/
    public static final String SHOWTYPE_UPLOAD = "1";
    public static final String SHOWTYPE_LINK = "2";
    public static final String SHOWTYPE_EDIT = "3";


    public static final String APPKEY = "18fdf500cd85e";
    public static final String APPSECRET = "db7dc0fdcf972fac2078f31023b0dada";
    public static final String COUNTRY_CODE = "86";


    public static final String WX_APP_ID = "wx3ad75346593f9ef6";

    // 新浪微博相关
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public static final String WeiBo_APP_KEY = "2409447434";

    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";


    public static final String ACTION_LOGIN = "com.oli.receivers.login";
    public static final String ACTION_LOGOUT = "com.oli.receivers.logout";
    public static final String ACTION_EDIT_USERINFO = "com.oli.receivers.edit_userinfo";
    public static final String ACTION_EDIT_PASSOWRD = "com.oli.receivers.edit_password";

    //201：游客，202：包月会员，203：按次订购会员
    public static final int HTTP_NO = 201;
    public static final int HTTP_VIP = 202;
    public static final int HTTP_ONECE = 203;
    public static final int HTTP_NO_PERMISSION = 500;


    protected static final String PROTOCOL_HTTP = "http://";
    protected static String PROTOCOL = PROTOCOL_HTTP;

//    public static String DOMAIN = "192.168.5.127:8080/oli";
    public static String DOMAIN = "223.223.200.156:8081";
    //http://223.223.200.156:8081/
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
    //直播栏目列表
    public static String URL_LIVELIST = PROTOCOL + DOMAIN +"/column/liveList.shtml";
    //直播节目列表
    public static String URL_LIVEPROGRAMLIST = PROTOCOL + DOMAIN +"/program/liveProgramList.shtml";
    //直播节目单列表
    public static String URL_LIVEPROGRAMORDERLIST = PROTOCOL + DOMAIN +"/program /liveProgramOrderList.shtml";
    //注册
    public static String URL_REGISTER = PROTOCOL + DOMAIN +"/user/register.shtml";
    //登录
    public static String URL_LOGIN = PROTOCOL + DOMAIN +"/user/login.shtml";
    //收藏节目列表
    public static String URL_USERCOLLECTIONSLIST = PROTOCOL + DOMAIN +"/user/userCollectionsList.shtml";
    //重置密码
    public static String URL_RESETPASSWORD = PROTOCOL + DOMAIN +"/user/resetPassword.shtml";
    //上传头像
    public static String URL_UPDATEHEAD = PROTOCOL + DOMAIN +"/user/updateHead.shtml";
    //修改用户信息
    public static String URL_UPDATEUSERINFO = PROTOCOL + DOMAIN +"/user/updateUser.shtml";
    //修改密码
    public static String URL_UPDATEPASSWORD = PROTOCOL + DOMAIN +"/user/updatePassword.shtml";
    //节目收藏
    public static String URL_COLLECTPROGRAM = PROTOCOL + DOMAIN +"/user/collectProgram.shtml";
    //取消收藏
    public static String URL_DELCOLLECTPROGRAM = PROTOCOL + DOMAIN +"/user/delCollectProgram.shtml";
    //反馈
    public static String URL_ADDFEEDBACK = PROTOCOL + DOMAIN +"/user/addFeedBack.shtml";


}
