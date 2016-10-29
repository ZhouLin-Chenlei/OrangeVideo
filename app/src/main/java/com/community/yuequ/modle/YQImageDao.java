package com.community.yuequ.modle;

import java.util.List;

/**
 * Created by Administrator on 2016/5/11.
 */
public class YQImageDao {

    /**
     * {
     "errorCode" : 200,
     "errorMessage" : "查询成功",
     "result" : [ {
     "id" : 7,
     "name" : "段子",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "",
     "program_cnt" : 55
     }, {
     "id" : 30,
     "name" : "趣图",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "",
     "program_cnt" : 45
     }, {
     "id" : 35,
     "name" : "美女",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "",
     "program_cnt" : 50
     }, {
     "id" : 40,
     "name" : "星座",
     "img_path" : "http://171.8.238.102:8081/yqfile",
     "content_desc" : "",
     "program_cnt" : 60
     } ]
     }
     */

    public int errorCode;
    public String errorMessage;
    public List<RTextImage> result;



}
