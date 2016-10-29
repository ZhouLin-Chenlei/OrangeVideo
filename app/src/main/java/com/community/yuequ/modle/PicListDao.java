package com.community.yuequ.modle;

import java.util.List;

/**
 * Created by Administrator on 2016/5/18.
 */
public class PicListDao {
    /**
     * {
     "errorCode" : 200,
     "errorMessage" : "查询成功",
     "result" : {
     "total_cnt" : 5,
     "list" : [ {
     "id" : 126,
     "name" : "被朝鲜扣押俄快艇顺利返俄",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235157803.jpg",
     "remark" : "日前被朝鲜扣押的俄罗斯“埃尔芬”号运动快艇于17日顺利返回俄远东地区的符拉迪沃斯托克港口。",
     "type" : "2",
     "picList" : [ "" ]
     }, {
     "id" : 236,
     "name" : "被朝鲜扣押俄快艇顺利返俄",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235157803.jpg",
     "remark" : "日前被朝鲜扣押的俄罗斯“埃尔芬”号运动快艇于17日顺利返回俄远东地区的符拉迪沃斯托克港口。",
     "type" : "2",
     "picList" : [ ]
     }, {
     "id" : 286,
     "name" : "被朝鲜扣押俄快艇顺利返俄",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235157803.jpg",
     "remark" : "日前被朝鲜扣押的俄罗斯“埃尔芬”号运动快艇于17日顺利返回俄远东地区的符拉迪沃斯托克港口。",
     "type" : "2",
     "picList" : [ ]
     }, {
     "id" : 336,
     "name" : "被朝鲜扣押俄快艇顺利返俄",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235157803.jpg",
     "remark" : "日前被朝鲜扣押的俄罗斯“埃尔芬”号运动快艇于17日顺利返回俄远东地区的符拉迪沃斯托克港口。",
     "type" : "2",
     "picList" : [ ]
     }, {
     "id" : 346,
     "name" : "被朝鲜扣押俄快艇顺利返俄",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235157803.jpg",
     "remark" : "日前被朝鲜扣押的俄罗斯“埃尔芬”号运动快艇于17日顺利返回俄远东地区的符拉迪沃斯托克港口。",
     "type" : "2",
     "picList" : [ ]
     } ]
     }
     }
     */

    public int errorCode;
    public String errorMessage;
    public PicListBean result;

     public static class PicListBean{
        public int total_cnt;//总个数
        public int total_page;//总页数
        public List<RProgram> list;

     }
}
