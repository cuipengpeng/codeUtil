package com.caishi.chaoge.http;


import com.caishi.chaoge.BuildConfig;

import retrofit2.http.GET;

/**
 * 接口路径
 */

public class RequestURL {


    public static final String VERSION = "v1";
    public static final String VERSION2 = "v2";
    /**
     * 服务器
     */
    public static String BASE_URL = "http://api.stage.chaogevideo.com/" + VERSION;
    public static String BASE_URL_SHARE = "";
    public static String BASE_URL_V2 = "http://api.stage.chaogevideo.com/" + VERSION2;

    public static final String BASE_URL_TEST = "http://api.stage.chaogevideo.com/" + VERSION;
    public static final String BASE_URL_SHARE_TEST = "http://api.stage.chaogevideo.com/";
    public static final String BASE_URL_V2_TEST = "http://api.stage.chaogevideo.com/" + VERSION2;
//    public static final String BASE_URL_TEST = "http://192.168.1.145:8081/" + VERSION;
//    public static final String BASE_URL_SHARE_TEST = "http://192.168.1.145:8081/";
//    public static final String BASE_URL_V2_TEST = "http://192.168.1.145:8081/" + VERSION2;

    public static final String BASE_URL_ONLINE = "http://api.chaogevideo.com/" + VERSION;
    public static final String BASE_URL_SHARE_ONLINE = "http://api.chaogevideo.com/";
    public static final String BASE_URL_V2_ONLINE = "http://api.chaogevideo.com/" + VERSION2;

    /**
     * 分享视频
     */
    public static String SHARE_VIDEO;
    /**
     * 分享个人主页
     */
    public static String SHARE_HOME_PAGE;

    public static void init() {
        if (BuildConfig.DEBUG) {
            BASE_URL = BASE_URL_TEST;
            BASE_URL_SHARE = BASE_URL_SHARE_TEST;
            BASE_URL_V2 = BASE_URL_V2_TEST;
        } else {
            BASE_URL = BASE_URL_ONLINE;
            BASE_URL_SHARE = BASE_URL_SHARE_ONLINE;
            BASE_URL_V2 = BASE_URL_V2_ONLINE;
        }
        SHARE_VIDEO = BASE_URL_SHARE + "static/share/indexVideo.html";
        SHARE_HOME_PAGE = BASE_URL_SHARE + "static/share/Individual.html";
    }

    /**
     * 视频图片的base_url
     */
    public static String VIDEO_BASE_URL = "http://video.chaogevideo.com/";

    /**
     * 首页视频
     */
    public static final String HOME_DATA = "moment/recommend";
    /**
     * 剧本
     */
    public static final String GET_SCRIPT = "script/getScript";

    /**
     * 剧本
     */
    public static final String GET_MODEL_SCRIPT = "model/getModelScriptList";

    /**
     * 获取上传阿里OSS的token
     */
    public static final String GET_TOKEN = "auth/token";

    /**
     * 发布
     */
    public static final String PUBLISH = "moment/publish";
    /**
     * 获取验证码
     */
    public static final String GET_VALIDATE_AUTHCODE = "auth/getvalidateAuthCode";
    /**
     * 登录
     */
    public static final String LOGIN = "user/login";
    /**
     * 自动登录
     */
    public static final String AUTO_LOGIN = "user/login/credential";
    /**
     * 虚拟登录
     */
    public static final String VIRTUAL_LOGIN = "user/login/device";
    /**
     * 绑定手机号
     */
    public static final String BOUND_MOBILD = "user/mobile/bind";

    /**
     * 获取用户信息
     */
    public static final String USER_INFO = "user/info/id";

    /**
     * 获取登录用户信息
     */
    public static final String MY_INFO = "user/info";

    /**
     * 更新用户信息
     */
    public static final String USER_INFO_UPDATE = "user/info/update";

    /**
     * 个人动态
     */
    public static final String PERSONAL = "moment/personal";

    /**
     * 小助手
     */
    public static final String SMALL_ASSISTANT = "moment/assistant";
    /**
     * 个人收藏动态
     */
    public static final String LIKES = "moment/likes";


    /**
     * 获取模板列表
     */
    public static final String GET_TEMPLET_LIST = "model/getModelList";

    /**
     * 获取音乐
     */
    public static final String GET_MUSIC = "music/getMusic";
    /**
     * 搜索音乐
     */
    public static final String SEARCH_MUSIC = "music/find";

    /**
     * 关注
     */
    public static final String FOLLOW = "user/follow";
    /**
     * 我关注的人
     */
    public static final String MY_FOLLOW = "user/follows";
    /**
     * 我的粉丝
     */
    public static final String MY_FANS = "user/fans";

    /**
     * 删除视频
     */
    public static final String DELETE = "moment/del";
    /**
     * 点赞  转发
     */
    public static final String LIKE = "moment/like";
    /**
     * 2转发 3播放
     */
    public static final String FORWARD = "moment/forward";
    /**
     * 关注用户动态
     */
    public static final String GET_ATTENTION_LIST = "moment/follows";
    /**
     * 评论列表
     */
    public static final String LIST = "comment/list";
    /**
     * 发布评论
     */
    public static final String COMMENT_POST = "comment/post";

    /**
     * 评论点赞
     */
    public static final String LIKEORDISLIKE = "comment/likeOrDislike/set";
    /**
     * 第三方登录
     */
    public static final String OTHER_LOGIN = "user/partner/login";
    /**
     * 获取字体下载路径
     */
    public static final String FONT_GET = "font/get";
    /**
     * app版本升级
     */
    public static final String APP_VERSION = "version";

    /**
     * 第三方绑定
     */
    public static final String THIRD_PARTER_BIND = "user/partner/bind";
    /**
     * 启动广告页面
     */
    public static final String APP_START_AD = "startPage/get";
    /**
     * 搜索剧本
     */
    public static final String SEARCH_CONTENT = "script/find";

    /**
     * 获取视频背景图列表
     */
    public static final String GET_BACK_GROUND_IMAGE_LIST = "background/getBackGround";

    /**
     * 提交用户反馈信息
     */
    public static final String POST_USER_FEEDBACK = "basic/feedBack";
    /**
     * 获取标签
     */
    public static final String GET_LABEL = "basic/getLabel";

    /**
     * 获取模板
     */
    public static final String GET_MODEL = "model/getModelById";
    /**
     * 反馈
     */
    public static final String FEEDBACK = "basic/elasticFeedBack";

    /**
     * 提交反馈
     */
    public static final String SUBMIT_FEEDBACK = "basic/submitElasticFeed";
    /**
     * 获取分类信息
     */
    public static final String GET_CLASS_LIST = "basic/getClassList";
    /**
     * 获取首页基础数据
     */
    public static final String BASIC_DATA = "basic/basicData";
    /**
     * 获取字体
     */
    public static final String FONT_LIST = "basic/getFontList";
    /**
     * 获取颜色
     */
    public static final String COLOR_LIST = "basic/getColorList";
    /**
     * 随机剧本
     */
    public static final String RANDOM = "script/random";
    /**
     * 语音识别
     */
    public static final String RECOGNITION = "basic/voiceTranslate";
    /**
     * 获取模板推荐列表
     */
    public static final String RECOMMEND = "model/recommend";
    /**
     * 获取模板列表
     */
    public static final String GET_MODEL_LIST = "model/getModelList";

    /**
     * 获取模板列表
     */
    public static final String GET_MODEL_BY_ID = "model/getModelById";

    /**
     * 语音识别（阿里）
     */
    public static final String GET_RECOGNITION = "basic/getRecognition";
    /**
     * 粉丝消息列表
     */
    public static final String GET_NEW_FANS = "user/newFans";
    /**
     * 赞消息列表
     */
    public static final String GET_NEW_LIKES = "user/newLikes";
    /**
     * 评论消息列表
     */
    public static final String GET_NEW_MOMENT_COMMENTS = "user/newMomentComments";
    /**
     * 消息数
     */
    public static final String GET_MESSAGE_NUM = "user/checkNewMessage";
    /**
     * 根据动态Id查询动态
     */
    public static final String FIND_MOMENT = "findMoment4App";
    /**
     * 删除粉丝
     */
    public static final String DEL_NEW_FANS = "user/delNewFans";
    /**
     * 删除点赞 POST /v1/user/delNewLike
     */
    public static final String DE_NEW_LIKE = "user/delNewLike";
    /**
     * 删除评论  POST /v1/user/delNewMomentComment
     */
    public static final String DEL_NEW_MOMENT_COMMENTS = "user/delNewMomentComment";

}
