package com.community.yuequ.modle;

import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class YQVideoOrPicGroupDao {
    /**
     * {
     "errorCode" : 200,
     "errorMessage" : "查询成功",
     "result" : [ {
     "id" : 31,
     "name" : "可爱萌宠",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "",
     "program_cnt" : null
     }, {
     "id" : 32,
     "name" : "邪恶漫画",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "",
     "program_cnt" : null
     }, {
     "id" : 33,
     "name" : "轻松幽默",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "",
     "program_cnt" : null
     }, {
     "id" : 34,
     "name" : "搞笑囧图",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "",
     "program_cnt" : null
     } ]
     }
     */


    /**
     * {
     "errorCode" : 200,
     "errorMessage" : "查询成功",
     "result" : [ {
     "id" : 13,
     "name" : "院线热映",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "从",
     "program_cnt" : null
     }, {
     "id" : 14,
     "name" : "经典台词",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "",
     "program_cnt" : null
     }, {
     "id" : 15,
     "name" : "热剧放送",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "",
     "program_cnt" : null
     }, {
     "id" : 16,
     "name" : "酷辣评影",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "",
     "program_cnt" : null
     } ]
     }
     */
    public int errorCode;
    public String errorMessage;
    public List<VideoOrPicGroup> result;

}
