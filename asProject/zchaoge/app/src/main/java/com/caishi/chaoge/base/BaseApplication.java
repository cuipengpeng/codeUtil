package com.caishi.chaoge.base;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.common.OSSLog;
import com.bytedance.sdk.account.bdopen.impl.BDOpenConfig;
import com.bytedance.sdk.account.open.aweme.impl.TTOpenApiFactory;
import com.caishi.chaoge.BuildConfig;
import com.caishi.chaoge.bean.LoginBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.Product;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.utils.CrashAppLog;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.NetworkMonitor;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.SystemUtils;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.IIOAdapter;
import com.meituan.android.walle.WalleChannelReader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.zhouyou.http.EasyHttp;
import com.zlw.main.recorderlib.RecordManager;


import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import me.jessyan.autosize.AutoSizeConfig;


public class BaseApplication extends MultiDexApplication {

    static {
        PlatformConfig.setWeixin("wx61f990fc99a401d8", "d6b2ce42e77b15dd91baedd8cb9d61f8");
        PlatformConfig.setSinaWeibo("1787010701", "37005ccf6973952cd46e34a14c1eea9f", "http://sns.whalecloud.com/sina2/callback");
        PlatformConfig.setQQZone("1107867325", "QIQxXVEWNv2j7nvZ");
    }

    public static LoginBean loginBean;
    /**
     * 已获取的AppKey
     */
    public static final String APP_KEY = "1ffcdcfb202e6d18";
    /**
     * 已获取的AppSecret
     */
    public static final String APP_SECRET = "3f786fc9584f54735ec6eb1d270cfc6cKysk0k7B6Uv0gTgsGX0b5oUMBqUmcEHfqm91y7mrZodGKZvVt5qmw4vmWMoan49VAMzQzL9tJvBRZiYI+i7K5+vOPfsNFLjM6MN/LI941IMLCcanmVLV+VL0pIXcyn3SX7EyE7Lve7pQjm7HxglDAAuqwsfeYUz51nDJ6dRHrAH6HhbbG5AB5Tqc260vA8NbJcb52tYJQNQzESmh4WcbhEkWK7/22eeZHuPeLiUoQMd5O/Gohk3lp0OsAHULWNB4SJJcF+gP2WF2RjGRQSapk6NTCMbOWNTratFNLbmJDHOXdWRV4B7rzx+bLC8cy5yLQQaYQzkMWqCFBXWQab4ouBnWChtKSExjjem9JUlZ2rxpmdM2d+2xYceKtZRss7uprnpRpQKflc987yiV8Q08tqb20pm9F9lLzJi1ZM0a6ChgWPlm0ksYkm3FmpPlKcb1tPCZCZjFfG/3SZfUgZ/KaEoqNJEGBfkh3O4pKd0Dm+I=";


    private static Context context;
    public static boolean isSupported = true;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        loginBean = SPUtils.readCurrentLoginUserInfo(this);
        AutoSizeConfig.getInstance().setCustomFragment(true);
        //初始化handler
        mHandler = new Handler();
        NetworkMonitor.init(this);
        //语句分词
        initHanLP();
        RequestURL.init();
        if (BuildConfig.DEBUG)
            CrashAppLog.getInstance().init(this);
//        initRDSDK();
        HttpRequest.initEnvironment();
        getBaseInfo();
        RecordManager.getInstance().init(this, BuildConfig.DEBUG);
        //抖音
        TTOpenApiFactory.init(new BDOpenConfig("aw9gosd07p110vru"));
        // 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
        // 参数一：当前上下文context；
        // 参数二：应用申请的Appkey（需替换）；
        // 参数三：渠道名称；
        // 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
        // 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）
        if (BuildConfig.DEBUG) {
            UMConfigure.init(this, "5c342178f1f556ebda0010f0"
                    , "ChaoGe", UMConfigure.DEVICE_TYPE_PHONE, "b05e1be133a1aba539069132963b9acf");
        } else {
            UMConfigure.init(this, "5be1795ab465f50a0500033b"
                    , "ChaoGe", UMConfigure.DEVICE_TYPE_PHONE, "");
        }
        //小米
        MiPushRegistar.register(this, "2882303761517897658", "5651789788658");
        //华为
        HuaWeiRegister.register(this);
        //OPPO通道，参数1为app key，参数2为app secret
        OppoRegister.register(this, "6beeb84e930e4317b46cda56a465b781", "de1fbfc180334a36adbc8cece965d881");
        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                LogUtil.i("注册成功：deviceToken：-------->  " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtil.e("注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });

        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setCatchUncaughtExceptions(true);
        if (BuildConfig.DEBUG)
            OSSLog.enableLog();
        MultiDex.install(this);
        EasyHttp.init(this);
        EasyHttp.getInstance().setConnectTimeout(30000).setRetryCount(5);
        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
                isSupported = true;
            }

            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
                isSupported = false;
            }
        });
    }

    private void initHanLP()
    {
        try
        {
            Os.setenv("HANLP_ROOT", "", true);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        final AssetManager assetManager = getAssets();
        HanLP.Config.IOAdapter = new IIOAdapter()
        {
            @Override
            public InputStream open(String path) throws IOException
            {
                return assetManager.open(path);
            }

            @Override
            public OutputStream create(String path) throws IOException
            {
                throw new IllegalAccessError("不支持写入" + path + "！请在编译前将需要的数据放入app/src/main/assets/data");
            }
        };
    }

    private void getBaseInfo() {
        String channel = WalleChannelReader.getChannel(this.getApplicationContext());
        if (TextUtils.isEmpty(channel)) {
            channel = "ChaoGe";
        }
        Product.sVersionName = SystemUtils.getVerName(context);
        Product.sAppChannel = channel;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MobclickAgent.onKillProcess(this);
    }

    /**
     * 在主线程中刷新UI的方法
     *
     * @param r
     */
    public static void runOnUIThread(Runnable r) {
        getMainHandler().post(r);
    }

    //qcl用来在主线程中刷新ui
    private static Handler mHandler;

    public static Handler getMainHandler() {
        return mHandler;
    }


//    @Override
//    public void onTrimMemory(int level) {
//        super.onTrimMemory(level);
//        if (level==TRIM_MEMORY_UI_HIDDEN){
//        }
//    }
}
