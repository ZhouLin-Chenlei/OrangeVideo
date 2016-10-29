package com.community.yuequ.modle;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by xmyb on 2016/5/19.
 */
public class RProgramDetail implements Serializable{

    /**
     *{
     "errorCode" : 200,
     "errorMessage" : "查询成功",
     "result" : {
     "id" : 20,
     "name" : "《星光大道》夫妻组合雌雄难辨",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517224833167.jpg",
     "video_path" : "http://171.8.238.102:8081/yqfilehttp://183.232.54.230:1935/vod/sycf/2016/04/04/yl0403yfxg01_zqbz.mp4/playlist.m3u8",
     "contents" : "包子店员王春燕斩获周冠军。",
     "type" : "1",
     "link_url" : "",
     "show_type" : "3",
     "is_cost" : "0",
     "remark" : "星光大道0430夫妻组合雌雄难辨 "
     }
     }
     */

    /**
     * {
     "errorCode" : 200,
     "errorMessage" : "查询成功",
     "result" : {
     "id" : 346,
     "name" : "被朝鲜扣押俄快艇顺利返俄",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235157803.jpg",
     "video_path" : "http://171.8.238.102:8081/yqfilenull",
     "contents" : "<p>&nbsp;</p>\r\n\r\n<p>据塔斯社报道，&ldquo;埃尔芬&rdquo;号运动快艇所属的&ldquo;七英尺&rdquo;游艇俱乐部新闻发言人亚娜&middot;科诺普利茨卡亚17日表示，这艘快艇已顺利抵达符拉迪沃斯托克港，船上所有5人健康状况良好。</p>\r\n\r\n<p><img alt=\"\" src=\"http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pspic0912/20160517/5486608/140955.jpg\" style=\"height:172px; width:230px\" /></p>\r\n\r\n<p>俄 外交部14日通报说，&ldquo;埃尔芬&rdquo;号运动快艇13日晚在从韩国釜山参加国际帆船比赛后返回符拉迪沃斯托克途中，被朝鲜海岸警卫队扣押。事发地点离岸80海里，在朝鲜专属经济区内，但属国际水域。</p>\r\n\r\n<p>事发后，俄方向朝鲜方面发出照会，要求解释快艇被扣的原因。俄罗斯驻朝鲜清津总领事博奇卡廖夫15日说，朝鲜方面当天释放了被扣船只，并表示船只被扣纯属误会。（来源：新华网）</p>\r\n\r\n<p>&nbsp;</p>\r\n",
     "type" : "2",
     "link_url" : "",
     "show_type" : "1",
     "is_cost" : "0",
     "remark" : "日前被朝鲜扣押的俄罗斯“埃尔芬”号运动快艇于17日顺利返回俄远东地区的符拉迪沃斯托克港口。"
     }
     }
     */
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
}
