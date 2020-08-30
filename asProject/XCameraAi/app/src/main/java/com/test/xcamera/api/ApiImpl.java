package com.test.xcamera.api;


import com.framwork.base.BaseResponse;
import com.test.xcamera.bean.ActivationCode;
import com.test.xcamera.bean.AppVersion;
import com.test.xcamera.bean.ClickLikeAndShare;
import com.test.xcamera.bean.CommunityBean;
import com.test.xcamera.bean.FeedList;
import com.test.xcamera.bean.SplashScreenBean;
import com.test.xcamera.bean.SqueezeBean;
import com.test.xcamera.bean.UploadWorksBean;
import com.test.xcamera.bean.User;
import com.framwork.http.HttpFactory;
import com.test.xcamera.bean.UserInfo;
import com.test.xcamera.phonealbum.bean.MusicResult;
import com.test.xcamera.phonealbum.bean.MusicTypeResult;

/**
 * creat by mz  2019.9.24
 */
public class ApiImpl {
    private volatile static ApiImpl api;
    private ApiService apiService;

    private ApiImpl() {
        apiService = HttpFactory.CreatHttp();
    }

    public static ApiImpl getInstance() {
        if (api == null) {
            api = new ApiImpl();
        }
        return api;
    }

    public void setApiNull() {
        api = null;
    }

    /**
     * 获取验证码
     *
     * @param phone
     */
    public void VerificationCode(String phone, final CallBack<BaseResponse> callBack) {
        HttpFactory.init(apiService.VerificationCode(phone), callBack);
    }

    /**
     * 请求开屏广告
     */
    public void getSplashScreeen(final CallBack<SplashScreenBean> callBack) {
        HttpFactory.init(apiService.getSplashScreenData(), callBack);
    }

    /**
     * 登陆
     *
     * @param
     * @param
     */
    public void login(String phone, String code, CallBack<User> callBack) {
        HttpFactory.init(apiService.login(phone, code), callBack);
    }

    /**
     * 登陆
     *
     * @param
     * @param
     */
    public void unLogin(CallBack<User> callBack) {
        HttpFactory.init(apiService.unLogin(), callBack);

    }

    /**
     * 用户信息编辑
     *
     * @param callBack
     */
    public void getUserInfo(CallBack<UserInfo> callBack) {
        HttpFactory.init(apiService.getUserInfo(), callBack);
    }


    /**
     * 用户信息编辑
     *
     * @param avatarFileId
     * @param nickname
     * @param description
     * @param callBack
     */
    public void userProfile(String avatarFileId, String nickname, String description, CallBack<User> callBack) {
        HttpFactory.init(apiService.userProfile(avatarFileId, nickname, description), callBack);
    }

    public void getActivationCode(String did, String nonce, CallBack<ActivationCode> callBack) {
//          nonce="QWJjZEVmZ2hJamtsTW5vcA";
        HttpFactory.init(apiService.ActivationCode(did, nonce), callBack);
    }

    public void PushActivationCode(String activationId, CallBack<BaseResponse> callBack) {
        HttpFactory.init(apiService.PushActivationCode(activationId), callBack);
    }

    /**
     * 上报App版本号
     *
     * @param appversion
     * @param callBack
     */
    public void PushAppVersion(String appversion, int platform, CallBack<AppVersion> callBack) {
        HttpFactory.init(apiService.PushAppVersion(appversion, platform), callBack);
    }

    /**
     * 上报固件版本号
     *
     * @param appversion
     * @param callBack
     */
    public void PushHardWareVersion(String appversion, String sn, CallBack<AppVersion> callBack) {
        HttpFactory.init(apiService.PushHardWareVersion(appversion, sn), callBack);
    }

    /**
     * 上传我的作品
     *
     * @param videoFileId
     * @param description
     * @param duration
     * @param coverFileId
     * @param templateId
     * @param bgmId
     * @param videoSize
     * @param callBack
     */
    public void UploadWorks(long videoFileId, String description, long duration, long coverFileId, long templateId, long bgmId, long videoSize, CallBack<UploadWorksBean> callBack) {
        HttpFactory.init(apiService.UploadWorks(videoFileId, description, duration, coverFileId, templateId, bgmId, videoSize), callBack);
    }

    /**
     * token 续期
     *
     * @param callBack
     */
    public void ExtendToken(CallBack<User> callBack) {
        HttpFactory.init(apiService.ExtendToken(), callBack);
    }


    /**
     * token 续期
     *
     * @param callBack
     */
    public void community(CallBack<CommunityBean> callBack) {
        HttpFactory.init(apiService.community(), callBack);
    }


    /**
     * 获取feed流列表信息
     *
     * @param pn
     * @param ps
     * @param callBack
     */
    public void getFeedList(int pn, int ps, CallBack<FeedList> callBack) {
        HttpFactory.init(apiService.getFeedList(pn, ps), callBack);
    }

    /**
     * 获取我的作品列表
     *
     * @param pn
     * @param ps
     * @param callBack
     */
    public void getMyOPusList(int pn, int ps, CallBack<FeedList> callBack) {
        HttpFactory.init(apiService.getMyOPusList(pn, ps), callBack);
    }

    /**
     * 获取Feed流列表的喜欢和分享数量
     *
     * @param opusId
     * @param callBack
     */
    public void getLikeAndShareNum(String opusId, CallBack<FeedList> callBack) {
        HttpFactory.init(apiService.getLikeAndShareNum(opusId), callBack);
    }

    /**
     * 获取Feed流列表的喜欢和分享数量
     *
     * @param opusId
     * @param callBack
     */
    public void getFeedSqueeze(String opusId, CallBack<SqueezeBean> callBack) {
        HttpFactory.init(apiService.getFeedSqueeze(opusId), callBack);
    }

    /**
     * like
     *
     * @param opusId
     * @param callBack
     */
    public void clickLike(long opusId, CallBack<ClickLikeAndShare> callBack) {
        HttpFactory.init(apiService.clickLike(opusId), callBack);
    }

    /**
     * cancel like
     *
     * @param opusId
     * @param callBack
     */
    public void cancelLike(long opusId, CallBack<ClickLikeAndShare> callBack) {
        HttpFactory.init(apiService.cancelLike(opusId), callBack);
    }

    /**
     * 分享
     *
     * @param opusId
     * @param callBack
     */
    public void clickShare(long opusId, CallBack<ClickLikeAndShare> callBack) {
        HttpFactory.init(apiService.clickShare(opusId), callBack);
    }

    /**
     * 获取音乐列表信息
     *
     * @param pn       当前页
     * @param ps       每页条数
     * @param callBack
     */

    public void getMusicResult(int pn, int ps, int typeId, CallBack<MusicResult> callBack) {
        HttpFactory.init(apiService.getMusicResult(pn, ps, typeId), callBack);
    }

    /**
     * 获取音乐列表信息
     *
     * @param pn       当前页
     * @param ps       每页条数
     * @param callBack
     */

    public void getSearchMusicResult(int pn, int ps, String keyWord, CallBack<MusicResult> callBack) {
        HttpFactory.init(apiService.getSearchMusicResult(pn, ps, keyWord), callBack);
    }

    /**
     * 获取分类
     *
     * @param pn
     * @param ps
     * @param callBack
     */
    public void getMusicTypeResult(int pn, int ps, CallBack<MusicTypeResult> callBack) {
        HttpFactory.init(apiService.getMusicTypeResult(pn, ps), callBack);
    }
}
