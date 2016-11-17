package com.community.yuequ.modle;

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
}
