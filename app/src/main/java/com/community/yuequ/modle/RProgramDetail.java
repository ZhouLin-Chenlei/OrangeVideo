package com.community.yuequ.modle;

import com.community.yuequ.imple.PlayData;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by xmyb on 2016/5/19.
 */
public class RProgramDetail implements Serializable,PlayData {

    public int id;
    public String name;
    public String img_path;
    public String video_path;
    public String contents;
    public String type;
    public String link_url;
    /**1：上传，2：外链地址，3：手工填写视频URL*/
    public String show_type;
    //（0：否，1：是）
    public String is_cost;
    public String remark;

    private  List<String> js;

    private List<String> css;

    public List<String> getJs() {
        if(js==null){
            return Collections.EMPTY_LIST;
        }
//        js.clear();
//        js.add("http://news-at.zhihu.com/css/news_qa.auto.css?v=4b3e3");
        return js;
    }

    public List<String> getCss() {
        if(css==null){
            return Collections.EMPTY_LIST;
        }
        return css;
    }

    public String getBody() {
        return contents;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getVideoUrl() {
        return video_path;
    }

    @Override
    public String getVideType() {
        return type;
    }
}
