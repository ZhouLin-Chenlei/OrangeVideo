package com.community.yuequ.modle;

import android.net.Uri;
import android.provider.BaseColumns;

import com.community.yuequ.imple.PlayData;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 推荐页栏目的子元素，节目
 */
public class RProgram implements Serializable,PlayData{

    public int id;
    public String name;
    public String img_path;

//    @SerializedName(value="remark", alternate={"desc_content"})
    public String remark;//正在播出的节目名字

    /**1：上传，2：外链地址，3：手工填写视频URL*/
    public String show_type;
    public String link_url;
    public String type;//1视频，2图文，3直播
    public List<String> picList;//图片节目的话就有图片集合
    public String isCollection;//true,false   是否收藏
    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getVideoUrl() {
        return link_url;
    }

    @Override
    public String getVideType() {
        return type;
    }



    public static class Columns implements BaseColumns {

        public static final Uri CONTENT_URI =
                Uri.parse("content://com.orange.vedio/history");


        public static final String PID = "pid";
        public static final String NAME = "name";
        public static final String IMG_PATH = "img_path";
        public static final String REMARK = "remark";
        public static final String SHOW_TYPE = "show_type";
        public static final String LINK_URL = "link_url";
        public static final String TYPE = "type";
        public static final String COLLECT = "collect";
        public static final String CREATETIME = "createtime";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = CREATETIME + " DESC";


        public static final String[] HISTORY_QUERY_COLUMNS = {
                _ID, PID, NAME, IMG_PATH, REMARK,
                SHOW_TYPE, LINK_URL, TYPE, COLLECT, CREATETIME };

        /**
         * These save calls to cursor.getColumnIndexOrThrow()
         * THEY MUST BE KEPT IN SYNC WITH ABOVE QUERY COLUMNS
         */
        public static final int HISTORY_ID_INDEX = 0;
        public static final int HISTORY_PID_INDEX = 1;
        public static final int HISTORY_NAME_INDEX = 2;
        public static final int HISTORY_IMG_PATH_INDEX = 3;
        public static final int HISTORY_REMARK_INDEX = 4;
        public static final int HISTORY_SHOW_TYPE_INDEX = 5;
        public static final int HISTORY_LINK_URL_INDEX = 6;
        public static final int HISTORY_TYPE_INDEX = 7;
        public static final int HISTORY_COLLECT_INDEX = 8;
        public static final int HISTORY_CREATETIME_INDEX = 9;
    }

}
