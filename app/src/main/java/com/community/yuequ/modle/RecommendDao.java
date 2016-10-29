package com.community.yuequ.modle;

import java.util.List;

/**
 * Created by Administrator on 2016/5/11.
 */
public class RecommendDao {
    /**
     * {
     "errorCode" : 200,
     "errorMessage" : "查询成功",
     "result" : {
     "advert" : [ {
     "title" : "电饭锅电饭锅",
     "link_type" : "2",
     "link_url" : "http://zhengzhou.auto.qq.com/?pgv_ref=aio2015&ptlang=2052",
     "img_path" : "http://171.8.238.102:8081/yqfile/advertImg/20160512111917958.png"
     } ],
     "program" : [ {
     "column_id" : 12,
     "column_name" : "影视",
     "type" : "1",
     "plist" : [ {
     "id" : 12,
     "name" : "《星光大道》关世鹏反串夺冠",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517225214447.jpg",
     "remark" : "0205期百名消防员登上星光舞台",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 20,
     "name" : "《星光大道》夫妻组合雌雄难辨",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517224833167.jpg",
     "remark" : "星光大道0430夫妻组合雌雄难辨 ",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 21,
     "name" : "《星光大道》夫妻组合雌雄难辨",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517224833167.jpg",
     "remark" : "星光大道0430夫妻组合雌雄难辨 ",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 22,
     "name" : "《星光大道超级版》草原王子云飞",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517225002996.jpg",
     "remark" : "江映蓉化身百老汇女王。",
     "type" : null,
     "picList" : [ ]
     } ]
     }, {
     "column_id" : 22,
     "column_name" : "搞笑",
     "type" : "1",
     "plist" : [ {
     "id" : 61,
     "name" : "《星光大道》夫妻组合雌雄难辨",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517224833167.jpg",
     "remark" : "星光大道0430夫妻组合雌雄难辨 ",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 62,
     "name" : "《星光大道超级版》草原王子云飞",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517225002996.jpg",
     "remark" : "江映蓉化身百老汇女王。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 63,
     "name" : "《星光大道》型男赤身秀肌肉",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517225113873.jpg",
     "remark" : "猛男画家踢馆大跳热舞。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 64,
     "name" : "《星光大道》关世鹏反串夺冠",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517225214447.jpg",
     "remark" : "0205期百名消防员登上星光舞台",
     "type" : null,
     "picList" : [ ]
     } ]
     }, {
     "column_id" : 5,
     "column_name" : "娱乐",
     "type" : "1",
     "plist" : [ {
     "id" : 9,
     "name" : "《星光大道》夫妻组合雌雄难辨",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517224833167.jpg",
     "remark" : "星光大道0430夫妻组合雌雄难辨 ",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 10,
     "name" : "《星光大道超级版》草原王子云飞",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517225002996.jpg",
     "remark" : "江映蓉化身百老汇女王。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 11,
     "name" : "《星光大道》型男赤身秀肌肉",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517225113873.jpg",
     "remark" : "猛男画家踢馆大跳热舞。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 14,
     "name" : "《星光大道》朱军小尼争一哥",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517225440077.jpg",
     "remark" : "0326期选手阿达深情演唱BigBang新歌。",
     "type" : null,
     "picList" : [ ]
     } ]
     }, {
     "column_id" : 17,
     "column_name" : "原创",
     "type" : "1",
     "plist" : [ {
     "id" : 101,
     "name" : "《星光大道》夫妻组合雌雄难辨",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517224833167.jpg",
     "remark" : "星光大道0430夫妻组合雌雄难辨 ",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 102,
     "name" : "《星光大道超级版》草原王子云飞",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517225002996.jpg",
     "remark" : "江映蓉化身百老汇女王。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 103,
     "name" : "《星光大道》型男赤身秀肌肉",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517225113873.jpg",
     "remark" : "猛男画家踢馆大跳热舞。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 104,
     "name" : "《星光大道》关世鹏反串夺冠",
     "img_path" : "http://171.8.238.102:8081/yqfile/videoImg/20160517225214447.jpg",
     "remark" : "0205期百名消防员登上星光舞台",
     "type" : null,
     "picList" : [ ]
     } ]
     }, {
     "column_id" : 40,
     "column_name" : "星座",
     "type" : "2",
     "plist" : [ {
     "id" : 19,
     "name" : "山东东营连环凶杀案嫌犯被抓",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517232237186.jpg",
     "remark" : "sdfdsfds",
     "type" : null,
     "picList" : [ "" ]
     }, {
     "id" : 281,
     "name" : "山东东营连环凶杀案嫌犯被抓",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517232237186.jpg",
     "remark" : "sdfdsfds",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 282,
     "name" : "人工助孕母亲生下630克女婴",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517233125277.jpg",
     "remark" : "",
     "type" : null,
     "picList" : [ "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pspic0912/20160517/5486625/155730.jpg" ]
     }, {
     "id" : 283,
     "name" : "浙江00后少女怀二胎遭滑胎",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517233706290.jpg",
     "remark" : "二胎时代来临，各大医院也迎来了又一波的“婴儿潮”。近日，120急救中心接到了一个来自天荒坪的求救电话，一位怀孕6个月的二胎准妈妈忽然肚子疼。",
     "type" : null,
     "picList" : [ ]
     } ]
     }, {
     "column_id" : 35,
     "column_name" : "美女",
     "type" : "2",
     "plist" : [ {
     "id" : 256,
     "name" : "被朝鲜扣押俄快艇顺利返俄",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235157803.jpg",
     "remark" : "日前被朝鲜扣押的俄罗斯“埃尔芬”号运动快艇于17日顺利返回俄远东地区的符拉迪沃斯托克港口。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 257,
     "name" : "工行停止个人账户综合理财业务",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235333605.jpg",
     "remark" : "据经济之声《天下财经》报道，中国工商银行官方网站昨晚突然发布消息，从今年6月1号起，不再受理新的个人账户综合理财业务，对于已签订协议的客户，将不再受理展期业务，业务停办满一年后存量客户协议将全部终止。消息一出，立刻在朋友圈刷屏了。今后，普通个人客户还能在工行买理财吗？对于这个大家最关心的问题，经济之声记者昨晚向工行方面以及多位业内权威专家进行求证。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 258,
     "name" : "村支书为索贿断村民大院水电",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235420405.jpg",
     "remark" : "原北京昌平区东小口镇中滩村党支部书记、村委会主任张亮，通过给他人承租村里的大院断水断电、挖沟、拆房等方式施压，并以此索要50万元，收钱后则立即恢复正常经营。此外，他还利用组织村党员旅游之机，收受旅行社2万元回扣。一审因非国家工作人员受贿罪被判处有期徒刑五年并处罚款5万元后，张亮提起上诉。昨天上午，此案二审在北京市二中院开庭，法院以同样的罪名当庭改判三年。据悉，二审认定原受贿金额未变。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 259,
     "name" : "在德失踪中国女学生确认遇害",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235521212.jpg",
     "remark" : "德国警方16日确认，日前在德国失踪的中国女留学生已经遇害。中国驻德国大使馆敦促德国警方尽快查明案情，同时采取有效措施保护中国公民安全。",
     "type" : null,
     "picList" : [ "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pgicon/20160517/3146997/102539983666.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pspic0912/20160517/5486563/102540.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pgicon/20160517/3146997/102539983666.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pspic0912/20160517/5486563/102540.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pspic0912/20160517/5486608/140955.jpg" ]
     } ]
     }, {
     "column_id" : 7,
     "column_name" : "段子",
     "type" : "2",
     "plist" : [ {
     "id" : 289,
     "name" : "在德失踪中国女学生确认遇害",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235521212.jpg",
     "remark" : "德国警方16日确认，日前在德国失踪的中国女留学生已经遇害。中国驻德国大使馆敦促德国警方尽快查明案情，同时采取有效措施保护中国公民安全。",
     "type" : null,
     "picList" : [ "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pgicon/20160517/3146997/102539983666.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pspic0912/20160517/5486563/102540.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pgicon/20160517/3146997/102539983666.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pspic0912/20160517/5486563/102540.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pspic0912/20160517/5486608/140955.jpg" ]
     }, {
     "id" : 346,
     "name" : "被朝鲜扣押俄快艇顺利返俄",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235157803.jpg",
     "remark" : "日前被朝鲜扣押的俄罗斯“埃尔芬”号运动快艇于17日顺利返回俄远东地区的符拉迪沃斯托克港口。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 348,
     "name" : "村支书为索贿断村民大院水电",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235420405.jpg",
     "remark" : "原北京昌平区东小口镇中滩村党支部书记、村委会主任张亮，通过给他人承租村里的大院断水断电、挖沟、拆房等方式施压，并以此索要50万元，收钱后则立即恢复正常经营。此外，他还利用组织村党员旅游之机，收受旅行社2万元回扣。一审因非国家工作人员受贿罪被判处有期徒刑五年并处罚款5万元后，张亮提起上诉。昨天上午，此案二审在北京市二中院开庭，法院以同样的罪名当庭改判三年。据悉，二审认定原受贿金额未变。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 349,
     "name" : "在德失踪中国女学生确认遇害",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235521212.jpg",
     "remark" : "德国警方16日确认，日前在德国失踪的中国女留学生已经遇害。中国驻德国大使馆敦促德国警方尽快查明案情，同时采取有效措施保护中国公民安全。",
     "type" : null,
     "picList" : [ "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pgicon/20160517/3146997/102539983666.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pspic0912/20160517/5486563/102540.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pgicon/20160517/3146997/102539983666.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pspic0912/20160517/5486563/102540.jpg", "http://epg30.wonhot.mobi:8086/epg30/selfadaimg.do?path=/pspic0912/20160517/5486608/140955.jpg" ]
     } ]
     }, {
     "column_id" : 30,
     "column_name" : "趣图",
     "type" : "2",
     "plist" : [ {
     "id" : 287,
     "name" : "工行停止个人账户综合理财业务",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235333605.jpg",
     "remark" : "据经济之声《天下财经》报道，中国工商银行官方网站昨晚突然发布消息，从今年6月1号起，不再受理新的个人账户综合理财业务，对于已签订协议的客户，将不再受理展期业务，业务停办满一年后存量客户协议将全部终止。消息一出，立刻在朋友圈刷屏了。今后，普通个人客户还能在工行买理财吗？对于这个大家最关心的问题，经济之声记者昨晚向工行方面以及多位业内权威专家进行求证。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 337,
     "name" : "工行停止个人账户综合理财业务",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235333605.jpg",
     "remark" : "据经济之声《天下财经》报道，中国工商银行官方网站昨晚突然发布消息，从今年6月1号起，不再受理新的个人账户综合理财业务，对于已签订协议的客户，将不再受理展期业务，业务停办满一年后存量客户协议将全部终止。消息一出，立刻在朋友圈刷屏了。今后，普通个人客户还能在工行买理财吗？对于这个大家最关心的问题，经济之声记者昨晚向工行方面以及多位业内权威专家进行求证。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 347,
     "name" : "工行停止个人账户综合理财业务",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235333605.jpg",
     "remark" : "据经济之声《天下财经》报道，中国工商银行官方网站昨晚突然发布消息，从今年6月1号起，不再受理新的个人账户综合理财业务，对于已签订协议的客户，将不再受理展期业务，业务停办满一年后存量客户协议将全部终止。消息一出，立刻在朋友圈刷屏了。今后，普通个人客户还能在工行买理财吗？对于这个大家最关心的问题，经济之声记者昨晚向工行方面以及多位业内权威专家进行求证。",
     "type" : null,
     "picList" : [ ]
     }, {
     "id" : 127,
     "name" : "工行停止个人账户综合理财业务",
     "img_path" : "http://171.8.238.102:8081/yqfile/imgText/20160517235333605.jpg",
     "remark" : "据经济之声《天下财经》报道，中国工商银行官方网站昨晚突然发布消息，从今年6月1号起，不再受理新的个人账户综合理财业务，对于已签订协议的客户，将不再受理展期业务，业务停办满一年后存量客户协议将全部终止。消息一出，立刻在朋友圈刷屏了。今后，普通个人客户还能在工行买理财吗？对于这个大家最关心的问题，经济之声记者昨晚向工行方面以及多位业内权威专家进行求证。",
     "type" : null,
     "picList" : [ "" ]
     } ]
     } ]
     }
     }
     */
    public int errorCode;
    public String errorMessage;
    public Recommend result;

    public static class Recommend{
        public List<Advert> advert;
        public List<RGroup> program;



    }
}
