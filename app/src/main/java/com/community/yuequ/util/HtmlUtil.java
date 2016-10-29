package com.community.yuequ.util;


import com.community.yuequ.modle.RProgramDetail;

import java.util.List;

public class HtmlUtil
{

    //css样式,隐藏header
    private static final String HIDE_HEADER_STYLE = "<style>div.headline{display:none;}</style>";

    //css style tag,需要格式化
    private static final String NEEDED_FORMAT_CSS_TAG = "<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\"/>";

    // js script tag,需要格式化
    private static final String NEEDED_FORMAT_JS_TAG = "<script src=\"%s\"></script>";

    public static final String MIME_TYPE = "text/html; charset=utf-8";

    // final float ratio = collect.imgheight * 1f / collect.imgwidth;
   //  h = (int) (ratio * screenWidth);
    public final static String htmlTag = "<html><head><style type=\"text/css\">body {font-size:16px;} img{width:100% !important;height:auto !important;}</style></head>"
           +"<body><script type='text/javascript'>window.onload = function(){ var $img = document.getElementsByTagName('img');"
            +"for(var p in  $img){$img[p].style.width = '100%';$img[p].style.height ='auto'}}</script>";

    public final static String htmlTag2="</body></html>";


    public static final String ENCODING = "utf-8";

    private HtmlUtil()
    {

    }

    /**
     * 根据css链接生成Link标签
     *
     * @param url String
     * @return String
     */
    public static String createCssTag(String url)
    {

        return String.format(NEEDED_FORMAT_CSS_TAG, url);
    }

    /**
     * 根据多个css链接生成Link标签
     *
     * @param urls List<String>
     * @return String
     */
    public static String createCssTag(List<String> urls)
    {

        final StringBuilder sb = new StringBuilder();
        for (String url : urls)
        {
            sb.append(createCssTag(url));
        }
        return sb.toString();
    }

    /**
     * 根据js链接生成Script标签
     *
     * @param url String
     * @return String
     */
    public static String createJsTag(String url)
    {

        return String.format(NEEDED_FORMAT_JS_TAG, url);
    }

    /**
     * 根据多个js链接生成Script标签
     *
     * @param urls List<String>
     * @return String
     */
    public static String createJsTag(List<String> urls)
    {

        final StringBuilder sb = new StringBuilder();
        for (String url : urls)
        {
            sb.append(createJsTag(url));
        }
        return sb.toString();
    }

    /**
     * 根据样式标签,html字符串,js标签
     * 生成完整的HTML文档
     *
     * @param html string
     * @param css  string
     * @param js   string
     * @return string
     */
    private static String createHtmlData(String html, String css, String js)
    {

        return css.concat(HIDE_HEADER_STYLE).concat(html).concat(js);
    }


    public static String createHtmlTag(String content)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(htmlTag);
        builder.append(content);
        builder.append(htmlTag2);
        return builder.toString();
//        return String.format(htmlTag2, content);
    }

    /**
     * 生成完整的HTML文档
     *
     * @return String
     */
    public static String createHtmlData(RProgramDetail detail)
    {

//        final String css = HtmlUtil.createCssTag(detail.getCss());
//        final String js = HtmlUtil.createJsTag(detail.getJs());
//        return createHtmlData(detail.getBody(), css, js)/*htmlText*/;
        return createHtmlTag(detail.getBody());
    }
    static String htmlText = "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://news-at.zhihu.com/css/news_qa.auto.css?v=4b3e3\"/><style>div.headline{display:none;}</style><div class=\"main-wrap content-wrap\">\n" +
            "<div class=\"headline\">\n" +
            "\n" +
            "<div class=\"img-place-holder\"></div>\n" +
            "\n" +
            "\n" +
            "\n" +
            "<div class=\"headline-background\">\n" +
            "<a class=\"headline-background-link\" href=\"http://tech.sina.com.cn/d/s/2016-07-04/doc-ifxtsatm1332177.shtml\">\n" +
            "<div class=\"heading\">相关新闻</div>\n" +
            "<div class=\"heading-content\">朱诺探测器今日进入木星轨道：已在太空中飞行 5 年</div>\n" +
            "<i class=\"icon-arrow-right\"></i>\n" +
            "</a>\n" +
            "</div>\n" +
            "\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"content-inner\">\n" +
            "\n" +
            "\n" +
            "\n" +
            "<div class=\"question\">\n" +
            "<h2 class=\"question-title\">朱诺号木星探测器：进击吧，屠龙勇士！</h2>\n" +
            "<div class=\"answer\">\n" +
            "\n" +
            "<div class=\"meta\">\n" +
            "<img class=\"avatar\" src=\"http://pic4.zhimg.com/22d970137c9739e644ac79c75dca965f_is.jpg\">\n" +
            "<span class=\"author\">刘博洋，</span><span class=\"bio\">天体物理学博士生</span>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"content\">\n" +
            "<p><strong>出品：科普中国<br />制作：中国科学院国家天文台 刘博洋<br />监制：中国科学院计算机网络信息中心</strong></p>\n" +
            "<p>北京时间 2016 年 7 月 5 日 11 时 18 分&mdash;&mdash;美国东部时间 7 月 4 日、亦即美国&ldquo;独立日&rdquo;当天 23 时 18 分&mdash;&mdash;朱诺号（Juno）木星探测器将打开其装配的 Leros 1b 主推进器，持续推进 35 分钟，完成木星轨道接入（JOI）机动。这是朱诺号升空 1796 天以来，最紧要攸关的一刻。</p>\n" +
            "<p>这是一个一次性的机遇：如果成功，朱诺号将被木星的引力捕获，进入俘获轨道，经过一系列的变轨，最终于今年 11 月进入开展预定探测任务的科学轨道；如果失败，它将永远失去进入木星轨道的机会，只能在绕日轨道寂寞终老。</p>\n" +
            "<p><img class=\"content-image\" src=\"http://pic1.zhimg.com/2bf0e781c4696b6ccf05872c76582104_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（JOI 之后，从木星北极俯冲而下的朱诺号，艺术家想象图。Image Credit: NASA/JPL-Caltech）</p>\n" +
            "<p>这些年来我们已经见闻了太多太阳系探测任务的壮举&mdash;&mdash;1997 年发射的土星探测器卡西尼号，抵达土星已经 12 年有余，至今仍在发回土星家族的高清大图；2003 年发射的机遇号火星车，原定任务时长 3 个月，而它至今仍然火星表面巡弋；2006 年发射的新视野号探测器，在去年飞掠冥王星之后，已经踏上了前往柯伊伯带更多小天体的漫长征程。</p>\n" +
            "<p>我们似乎已经习惯于看到探测器们无畏的挑战极限，一次又一次的超期服役，为我们带来更多惊喜。然而在朱诺号面前，这样的讨论可能显得有些残酷。</p>\n" +
            "<p>JOI 的到来，既标志着朱诺号即将进入激动人心的正式科学探测阶段，也标志着它剩余 595 天寿命倒数计时的开始。朱诺号，从一开始就注定是一个早逝的悲剧英雄。</p>\n" +
            "<p>因为它面前的，是伟岸雄浑的木星。</p>\n" +
            "<p><img class=\"content-image\" src=\"http://pic4.zhimg.com/cff9abcbd7ee7335f5a177ed9098711f_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（新视野号拍摄的木星）</p>\n" +
            "<p>以罗马神话中众神之王朱庇特(Jupiter)命名的木星，不是一颗平庸的行星。它的质量比太阳系中所有其他行星之和更大。它甚至不屑于做行星之王：如果不是在几十亿年前太阳系形成这场气体争夺战中憾负，它甚至可能成为太阳之外，这个系统中第二颗能够点燃氢聚变的恒星。</p>\n" +
            "<p>即使错失成为恒星的机会，木星也足以让人恐惧：它外核的液态金属氢电流赋予了它强大的磁场（磁矩为地球的 2 万倍），仿佛在太阳风的洪流中撑起了一把巨伞。这把叫做&ldquo;木星磁层&rdquo;的彗状巨伞其形之巨，足以装下数个太阳，其长尾甚至远及 4 个天文单位（6 亿公里）外的土星轨道&mdash;&mdash;这使得木星磁层成为太阳系中，除太阳风在星际介质中淘出的气泡&ldquo;日球层&rdquo;之外，最大的连续结构。如果我们能在地球上目视木星磁层，我们会发现它比日月盘面还要大 5 倍以上。</p>\n" +
            "<p><img class=\"content-image\" src=\"http://pic1.zhimg.com/2518f4ed51dfb938902fa6670fde4080_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（木星磁层结构及视尺度示意图，Neil Tyson et al., 'One Universe', Joseph Henry Press）</p>\n" +
            "<p>不，木星磁层并不是太阳风洪流中一湾宁静的避风港。</p>\n" +
            "<p>木卫一（Io），这颗距离木星最近的伽利略卫星，在木星巨大的潮汐力蹂躏下，宛如一颗被揉烂的橘子：它持续的火山活动每年向木星周围注入数千万吨二氧化硫，这些气体随即便被电离成为硫离子、氧离子、电子组成的等离子体，并在木星磁场的禁锢下随木星一同转动，形成温度可达百万度、拱卫木星的等离子体环，向外延展达数十个木星半径（200 余万公里）。</p>\n" +
            "<p><img class=\"content-image\" src=\"http://pic1.zhimg.com/7f7a5358b3edfa3d66b383b39aaab2f0_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（上左：卡西尼号拍摄的木星与木卫一；上右：新视野号拍摄的正在喷发的木卫一；下左：木卫一产生等离子体环及对木星磁场影响示意图；下右：木卫一产生的等离子体环中硫离子分布观测结果。）</p>\n" +
            "<p>在距离木星较近处，等离子体波与等离子体环中电子的相互作用将电子加速到接近光速，产生强烈的同步辐射&mdash;&mdash;这里被叫做&ldquo;辐射带&rdquo;。2002 年根据卡西尼号飞掠观测结果发表的一篇《自然》文章表示，辐射带中的电子温度最高可达千亿度（20MeV）。不知好歹的人类探测器一旦接触辐射带中的高能粒子，就会造成电子器件的物理伤害，直至完全失灵。</p>\n" +
            "<p>然而 6 月 24 日，朱诺号已经突入木星磁层顶激波面，犹如独骑挑战恶龙的勇士，越来越迫近木星，和它周围烈焰熊熊的城堡。</p>\n" +
            "<p><img class=\"content-image\" src=\"http://pic1.zhimg.com/5191127c546a9677f8552f03829cd634_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（朱诺号进入木星磁层前后，环境电子密度骤降。但随着朱诺号接近木星，示踪电子密度的等离子体振荡频率还将提升 3 个数量级。）</p>\n" +
            "<p>朱诺号当然有备而来。</p>\n" +
            "<p>它的心脏，是 RAD750 抗辐射处理器。它能承受 100 万倍于足以致人死地的辐射剂量，在木星周围的狂暴环境中，预计 15 年才会出现一次&ldquo;蓝屏&rdquo;。</p>\n" +
            "<p>它的铠甲，是 1 厘米厚的金属钛壳，总重 180 千克。绝大部分科学仪器都在钛壳的保护下，使其遭受的辐射强度减弱 800 倍，至电子器件可承受水平以下。</p>\n" +
            "<p>它的姿态保持，没有采用成熟的反应轮方案，而是采用略显复古的自旋稳定方案，以节省反应轮所占用的发射质量，为更厚实的钛壳、更多的科学仪器和更充沛的推进剂让出空间。</p>\n" +
            "<p><img class=\"content-image\" src=\"http://pic4.zhimg.com/d0468a55e435941424a111ef9fbb5577_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（朱诺号的钛壳）</p>\n" +
            "<p>朱诺号的利刃，也将直刺&ldquo;恶龙&rdquo;身上唯一一块&ldquo;鳞片&rdquo;掉落露出的软肋。</p>\n" +
            "<p>天文学家使用美国新墨西哥州的甚大射电望远镜阵（VLA）对木星周围的同步辐射进行了观测。他们发现，半径约 7 万公里的木星云顶之上，到环形辐射带之间尚有数千公里的罅隙，如果能从木星两极俯冲而下，准确的穿过这个缝隙，就可以既规避了强辐射对电子器件的伤害，又实现几乎紧贴木星表面飞过，从而获取最高分辨率的木星探测结果。</p>\n" +
            "<p><img class=\"content-image\" src=\"http://pic3.zhimg.com/b616c9be23cf44fbe0f875a385fdb8e6_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（VLA 2、3、6cm 三个波段合成的伪彩色木星图像，可见环绕木星的辐射带，及木星与辐射带之间的狭缝。）</p>\n" +
            "<p>这就是木星脱落的那块&ldquo;龙鳞&rdquo;，朱诺号必须射中。</p>\n" +
            "<p>与朱诺号数亿公里的旅途相比，这是数十万分之一的针孔；而且朱诺号在木星的强大引力势井中，将成为人类史上速度最快的航天器，速度超过 70 km/s。不用说，要在高速下准确穿越这个针孔，需要极精准的计算、极准确的机动。</p>\n" +
            "<p>朱诺号发射重量为 3625 千克，其中燃料与氧化剂总重量为 2032 千克，占总重一半还多。但这些燃料只能为探测器带来总计 2000m/s 的速度改变，用一点少一点，需要特别精打细算。</p>\n" +
            "<p><img class=\"content-image\" src=\"http://pic2.zhimg.com/36447e63f86dc8dce5760f101d882a39_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（左：Atlas V 551 火箭；右：朱诺号，可见球形的燃料仓和底部的主发动机。）</p>\n" +
            "<p>让朱诺号飞向木星的主要动能来源当然不是这区区一点自带的推进剂。2011 年 8 月 2 日，携带朱诺号升空的，是 Atlas V 551 型火箭。作为曾经把新视野号探测器送往冥王星的同款火箭，它给朱诺号注入 31.1 km&sup2;/s&sup2;的发射能量，将其送入约 2 年周期的绕日轨道。这条轨道的远日点在火星轨道之外：在那里，朱诺号实施了两次深空机动，使探测器重新瞄准地球飞了回来。这两次深空机动消耗了朱诺号 730 m/s 的速度改变预算。</p>\n" +
            "<p><img class=\"content-image\" src=\"http://pic1.zhimg.com/5175a2f6dbb622618025d718720c4180_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（朱诺号返回地球进行引力弹射时对地球和月球拍摄的系列画面）</p>\n" +
            "<p>2013 年 10 月 9 日，朱诺号与地球相距仅 559 公里擦身而过，在地球的引力弹射作用下，朱诺号获得了 7.3 km/s 的速度增量，这才具备了飞往木星的能力。在飞经地球前后，朱诺号抓紧开机测试了它所携带的科学仪器，为前进木星做好准备。</p>\n" +
            "<p>之后朱诺号小憩了 791 天，直到 2016 年 1 月 5 日，迫近木星前半年，复苏了。为了实施 7 月 5 日的木星轨道接入，朱诺号需要提前 2 个月开始注入燃料、加热管线，提前 7 天打开用于防范流星体的引擎保护罩，并将电池充满以备不测。</p>\n" +
            "<p><img class=\"content-image\" src=\"http://pic2.zhimg.com/e91e914dae735c573b71bbdea29a1899_b.png\" alt=\"\" /></p>\n" +
            "<p>（6 月 28 日，朱诺号在 620 万公里处拍摄的木星和伽利略卫星）</p>\n" +
            "<p>然后就是逆龙鳞、穿针眼的木星轨道接入（JOI）了&mdash;&mdash;这将消耗朱诺号 542 m/s 的速度改变预算。朱诺号计划从木星北极高速俯冲而下，在木星表面（1 bar 水准面）上空 4200 公里处掠过，如果飞低了，搞不好直接撞上木星，卒；如果飞高了，就需要动用已经所剩无几的燃料进行调整，后续的科学任务恐将受到影响。</p>\n" +
            "<p>在 JOI 点火前半小时，朱诺号调整姿态，将主引擎对准预定的点火方向。为此朱诺号不得不把对地通讯的主力高增益天线移开对地方向。在 JOI 期间，朱诺号只能用低增益天线发送少量事先约定的&ldquo;暗号&rdquo;，告知地球它的任务进展状态；美国戈尔德斯顿和澳大利亚堪培拉的两架深空通讯天线同时密切注视着它。此时，木星和地球之间的因有限光速而导致的通讯延迟是 48.3 分钟，也就是说无论 JOI 成功与否，我们能做的，只是等待。</p>\n" +
            "<p>JOI 成功之后，朱诺号将进入周期 53.5 天的木星俘获轨道。如此绕行两周之后，2016 年 10 月 19 日，朱诺号将实施其最后一次主要变轨，用 350 m/s 的速度改变预算，来实现进入周期约为 14 天的科学轨道。</p>\n" +
            "<p><img class=\"content-image\" src=\"http://pic4.zhimg.com/b8923ef6b4a3ccfde129403a65d9b763_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（上：木星轨道接入、俘获轨道、科学轨道等阶段；下：科学轨道的进动）</p>\n" +
            "<p>在那里，朱诺号将连戳木星的&ldquo;无麟软肋&rdquo;36 次：它计划用前 3 圈实现轨道的精细调节，然后用 8 圈实现对木星表面每隔 48&deg;经度的 8 个窄条的观测；再用 8 圈将观测区域间隔缩小到 24&deg;；再用 16 圈将其缩小到 12&deg;。在每一圈科学轨道的约两周时间之内，朱诺号只有最接近木星的 6-8 个小时才能有效的开展科学观测；但这 32 圈科学轨道编织的&ldquo;鸟笼&rdquo;，足以把整个木星表面纳入囊中。最后一圈，朱诺号将查遗补漏，听候地球指令选择感兴趣的区域实施最后的观测。</p>\n" +
            "<p><img class=\"content-image\" src=\"https://pic1.zhimg.com/4d0a83ceb35efd7e2d5d3d2555c37aa8_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（科学轨道中，近木点前后实际开展科学观测的轨道区间示意图。绿色和蓝色分别表示前 16 圈和后 16 圈。Image: NASA/JPL/Caltech）</p>\n" +
            "<p>而木星这只巨龙又岂能善罢甘休。由于木星的高速自转，它的形状严重偏离正球体，而是呈一扁球形。这种不对称的质量分布将持续的作用于朱诺号的轨道，迫使其轨道平面不断进动、轨道周期不断缩短、近木点高度不断增加&mdash;&mdash;是的，木星在一步步把朱诺号推向辐射带的烈焰。到第 36 轨，朱诺号近木点高度已经从 4147 公里增加到 7950 公里，每轨所受辐射剂量也随之增加了数倍：前 16 条科学轨道受到的总辐射剂量仅为后 16 条的 1/4。</p>\n" +
            "<p><img class=\"content-image\" src=\"https://pic2.zhimg.com/47d16a86425785e33e9c2c09c87b5555_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（蓝线表示朱诺号承受的总辐射剂量随绕木轨道数的增加而增加的预期）</p>\n" +
            "<p>尽管有钛壳护体，也许不是所有的科学载荷都能捱得过辐射带的炙烤。在最坏情况下，木星红外极光测绘仪（JIRAM）可能在第 8 轨之后就报销，微波辐射计（MWR）也可能只能活到第 11 轨。朱诺号携带的唯一一台可见光相机 JunoCam 的画质也将越来越糙，直到被坏点和噪声填满。</p>\n" +
            "<p>甚至朱诺号强健的心脏也终将不敌木星辐射带的伟力。如果完成预定观测任务之后任由朱诺号在木星周围飞行，它终有一天会心衰而亡，成为飘荡在木星周围一具安静的尸体。这是 NASA 行星保护守则不能允许的：朱诺号携带有剧毒的燃料，一旦失控并偶然坠毁在木星的卫星上，将会污染它们可能孕育生命的冰下海洋。因此朱诺号唯一的结局只能是功成身死，受控烧毁在木星浓密的大气层中。</p>\n" +
            "<p>那将是 2018 年 2 月 20 日，飞船时间 11:39。</p>\n" +
            "<p><img class=\"content-image\" src=\"https://pic2.zhimg.com/ef9e4433f15693d8eedfdb3f803b8055_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（电影《欧罗巴报告》剧照。木卫二欧罗巴拥有冰下海洋，甚至可能孕育生命。）</p>\n" +
            "<p>屠龙勇士的悲壮只是它自己的，巨龙本身甚至不会注意到它的存在。唯幸人类将从它的殉难中收获良多：</p>\n" +
            "<p>朱诺号将使用微波辐射计（MWR）研究木星云顶之下的隐藏结构。此前提及的 VLA 对木星观测采用 2、3、6 厘米三个波段，能探测到木星云顶之下几十公里的结构；而 MWR 使用最长 50 cm 的波长进行观测，将能穿透 550 km 的云层，看到木星大气深处的情状。MWR 还能测量木星大气中水和氨气的丰度，为木星演化理论提供依据。</p>\n" +
            "<p>朱诺号将在飞越木星表面时，持续接收地球发来的信号并立即回复，让地球能通过记录往来信号的开普勒频移，推测朱诺号受到木星引力拉扯导致的速度异常，从而计算出木星内部的质量分布。由于美国深空网只有第 25 号天线能够实施此项操作所需的 Ka 波段通讯，该项操作又必须实时测量，一旦观测日当天受天气影响无法观测，就会造成无法弥补的数据损失。</p>\n" +
            "<p><img class=\"content-image\" src=\"https://pic2.zhimg.com/18d016255bd96357d41dc08d9576d5f1_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（朱诺号利用开普勒频移测量木星重力场示意图。）</p>\n" +
            "<p>朱诺号的太阳能电池板末端安装的磁力计形如一把巨大的尖刀，它将对木星外部和内部磁场进行高精度的测量。由于朱诺号覆盖木星全球的轨道设计，朱诺号磁力计也将有机会测量木星三维磁场分布，从而获取除地球以外最精细的行星磁场观测结果。</p>\n" +
            "<p><img class=\"content-image\" src=\"https://pic3.zhimg.com/32dc842e1977c8226ba4dd8b09804fda_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（朱诺号的磁力计）</p>\n" +
            "<p>朱诺号在遭受辐射带高能粒子杀伤的同时，将会准确的记录下高能粒子的种类和能谱分布；在飞越两极的同时，朱诺号还将仔细研究木星的极光过程。</p>\n" +
            "<p><img class=\"content-image\" src=\"https://pic3.zhimg.com/41a58e028cd25d2ed447a646b3b507fe_b.png\" alt=\"\" /></p>\n" +
            "<p>（哈勃望远镜拍摄的木星极光紫外影像。Io spot 等木星极光中的若干亮斑是木星磁场受到木星卫星直接影响的证据。）</p>\n" +
            "<p>为达成朱诺号的科学目标，本身并不需要可见光观测仪器。但长途跋涉飞往木星而不拍点好看的图回来，简直是一种犯罪。因此朱诺号还是携带了一款科普用途的小相机 JunoCam。它能够以 RGB 三色记录全彩色影像，还将顺便观测甲烷的 890 纳米吸收谱带。不过，还请稍安勿躁，JOI 的时候所有科学仪器都将保持静默，JunoCam 也不会记录图像。JOI 之后 JunoCam 第一次记录影像，将会是 2016 年 8 月 27 日。到 2016 年 11 月之后，JunoCam 就将在每个近木点前后常态化拍摄并发回影像，这些影像将自动发布在美国西南研究所（SWRI）的<a class=\" wrap external\" href=\"https://link.zhihu.com/?target=https%3A//www.missionjuno.swri.edu/junocam/processing\" target=\"_blank\" rel=\"nofollow noreferrer\">朱诺号任务页面</a>上。</p>\n" +
            "<p>届时，公众还将可以通过线上投票，为 JunoCam 选定未来的拍摄目标&mdash;&mdash;实际上现在你已经可以开始参与 JunoCam 观测目标选择的线上讨论：<a class=\" wrap external\" href=\"https://link.zhihu.com/?target=https%3A//www.missionjuno.swri.edu/junocam/discussion\" target=\"_blank\" rel=\"nofollow noreferrer\">JunoCam Discussion</a></p>\n" +
            "<p><img class=\"content-image\" src=\"https://pic4.zhimg.com/363d0ad9ef01745b42dda0a6091ec83b_b.jpg\" alt=\"\" /></p>\n" +
            "<p>（JunoCam 拍摄目标线上讨论页面截图）</p>\n" +
            "<p>以上大约就是你需要了解的几乎全部关于朱诺号的背景知识了。如果说还有什么提醒的话，恐怕就是：北京时间 2016 年 7 月 5 日上午 10:30，<a class=\" wrap external\" href=\"https://link.zhihu.com/?target=http%3A//www.nasa.gov/multimedia/nasatv/index.html%23public\" target=\"_blank\" rel=\"nofollow noreferrer\">朱诺号 JOI 直播</a>开始，不要错过！</p>\n" +
            "<hr />\n" +
            "<p>&ldquo;科普中国&rdquo;是中国科协携同社会各方利用信息化手段开展科学传播的科学权威品牌。</p>\n" +
            "<p>本文由科普中国移动端出品，转载请注明出处。</p>\n" +
            "\n" +
            "<div class=\"view-more\"><a href=\"http://zhuanlan.zhihu.com/p/21479158\">查看知乎讨论</a></div>\n" +
            "\n" +
            "</div>\n" +
            "</div>\n" +
            "</div>\n" +
            "\n" +
            "\n" +
            "</div>\n" +
            "</div>";
}
