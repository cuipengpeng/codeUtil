package com.jfbank.qualitymarket.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.balysv.materialripple.MaterialRippleLayout;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.CategoryActivity;
import com.jfbank.qualitymarket.activity.WebViewActivity;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.fragment.PopDialogFragment;
import com.jfbank.qualitymarket.helper.CacheImageLoaderHelper;
import com.jfbank.qualitymarket.helper.DiskLruCacheHelper;
import com.jfbank.qualitymarket.listener.IMeterialClickLisenter;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.widget.BadgeView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.simple.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import qiu.niorgai.StatusBarCompat;

import static android.content.Context.MODE_PRIVATE;

/**
 * 功能：公共类方法<br>
 * 作者：RaykerTeam
 * 时间： 2016/11/16 0016<br>.
 * 版本：1.0.0
 */

public class CommonUtils {
    private static final String TAG = CommonUtils.class.getName();

    public static BadgeView addBageCountView(Context context, View parentView, int grayvity) {
        BadgeView bageNumView = new BadgeView(context);
        bageNumView.setBadgeCount(12);
        bageNumView.setVisibility(View.GONE);
        bageNumView.setBackground(8, context.getResources().getColor(R.color.themeRed));
        bageNumView.setBadgeGravity(grayvity);
        bageNumView.setBadgeMargin(0, 0, 15, 0);
        bageNumView.setTextColor(context.getResources().getColor(R.color.white));
        bageNumView.setTargetView(parentView);
        bageNumView.setTextSize(10);
        return bageNumView;
    }

    /**
     * get App versionCode
     *
     * @param context
     * @return
     */
    public static String getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode = "1";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     */
    public static String bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encode(bytes);
    }

    /**
     * 图片转化成base64字符串
     *
     * @param path 待处理的图片路径
     * @return base字符串
     */
    public static String Image2Base64String(String path) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(path);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            return "";
        }
        //对字节数组Base64编码
        //返回Base64编码过的字节数组字符串
        return android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
    }

    public static String bitmap2StrByBase64(String imgPath) {
        try {
            return bitmap2StrByBase64(BitmapFactory.decodeFile(imgPath));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }

    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager
     * @param mRecyclerView
     * @param n
     */
    public static void MoveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {


        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(n);
        }

    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager
     * @param n
     */
    public static void MoveToPosition(LinearLayoutManager manager, int n) {
        manager.scrollToPositionWithOffset(n, 0);
        manager.setStackFromEnd(true);
    }

    /**
     * 拨打联系客服电话
     *
     * @param context
     * @param url
     */
    public static void callContactService(final FragmentActivity context, final String url) {
        callPhone(context, "确定拨打客服电话吗？", url);
    }

    /**
     * 跳转到webView
     *
     * @param context          上下文
     * @param url              url地址
     * @param isCanBack        是否返回上一页url界面
     * @param isShowDeleteIcon 是否显示关闭按钮
     */
    public static void startWebViewActivity(Context context, String url, boolean isCanBack, boolean isShowDeleteIcon) {
        startWebViewActivity(context, url, isCanBack, isShowDeleteIcon, true);
    }

    /**
     * 跳转到webView
     *
     * @param context          上下文
     * @param url              url地址
     * @param isCanBack        是否返回上一页url界面
     * @param isShowDeleteIcon 是否显示关闭按钮
     */
    public static void startWebViewActivity(Context context, String url, boolean isCanBack, boolean isShowDeleteIcon, boolean isShowTitle) {
        if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
            Intent starter = new Intent(context, WebViewActivity.class);
            starter.putExtra(WebViewActivity.KEY_OF_HTML_URL, url);
            starter.putExtra(WebViewActivity.KEY_OF_WEB_VIEW_GO_BACK, isCanBack);
            starter.putExtra(WebViewActivity.KEY_OF_WEB_VIEW_SHOWDELETE_ICON, isShowDeleteIcon);
            starter.putExtra(WebViewActivity.KEY_OF_WEB_VIEW_SHOW_TITLE, isShowTitle);
            context.startActivity(starter);
        }
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) AppContext.mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    public static void loadCacheImage(Activity context, ImageView imageView, String url) {
        loadCacheImage(context, imageView, url, -1);
    }

    ;

    /**
     * 图片缓存。
     * @param context
     * @param imageView
     * @param url
     * @param defualt
     */
    public static void loadCacheImage(final Activity context, final ImageView imageView, final String url, final int defualt) {
        imageView.setTag(url);
        if (TextUtils.isEmpty(url)) {
            if (defualt != -1) {
                imageView.setImageResource(defualt);
            }
        } else {
            final RequestCreator requestCreator = Picasso.with(context).load(url);
            if (defualt != -1) {
                requestCreator.placeholder(defualt);
            }
            requestCreator.into(imageView);
        }
    }


    /**
     * 跳转显示商品详情
     *
     * @param context
     * @param proDuctId
     */
    public static void startGoodsDeteail(Context context, String proDuctId) {
        String loadUrl = HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.H5_PAGE_SUB_URL + HttpRequest.H5_PAGE_GOODS_DETAIL;
        // 商品详情
        // 判断是不是json字符串
        if (proDuctId.contains("{") && proDuctId.contains("}") && proDuctId.contains(":")) {
            // 从首页跳转过来
            JSONObject productJsonObject = JSON.parseObject(proDuctId);
            loadUrl = loadUrl + "?productNo=" + productJsonObject.getString("productNo") + "&isActivity="
                    + productJsonObject.getString("isActivity") + "&activityNo="
                    + productJsonObject.getString("activityNo");
        } else {
            // 从搜索页面跳转过来
            loadUrl = loadUrl + "?productNo=" + proDuctId + "&isActivity=0";
            Log.e(TAG, loadUrl);
        }
        CommonUtils.startWebViewActivity(context, loadUrl, false, false);
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param title
     * @param url
     */
    public static void callPhone(final FragmentActivity context, String title, final String url) {
        final PopDialogFragment dialog = PopDialogFragment.newDialog(true, true, null, title, "取消", "确定", null);
        dialog.setOnClickListen(new PopDialogFragment.OnClickListen() {

            @Override
            public void rightClick() {
                dialog.dismiss();
                if (isCanUseSim(context)) {
                    try {//异常处理
                        /**
                         * 拨打电话
                         */
                        //意图：想干什么事
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        //url:统一资源定位符
                        //uri:统一资源标示符（更广）
                        intent.setData(Uri.parse(url));
                        //开启系统拨号器
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "请先设置打电话权限", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "未安装SIM卡", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void leftClick() {
                dialog.dismiss();
            }
        });
        dialog.show(context.getSupportFragmentManager(), "TAG");
    }

    /**
     * 是否有SIM卡
     *
     * @param context
     * @return
     */
    public static boolean isCanUseSim(Context context) {
        try {
            TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return TelephonyManager.SIM_STATE_READY == mgr
                    .getSimState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置标题
     *
     * @param activity
     * @param rlTitle
     */
    public static void setTitle(Activity activity, View rlTitle) {
        setTitleColor(activity, rlTitle);
        setStatusColor(activity);
    }

    /**
     * 修改標題顏色
     *
     * @param activity
     * @param rlTitle
     */
    public static void setTitleColor(Activity activity, View rlTitle) {
        rlTitle.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
    }

    /**
     * 设置状态栏
     *
     * @param activity
     */
    public static void setStatusColor(Activity activity) {
        if (AndroidVersionUtil.hasM()) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarCompat.setStatusBarColor(activity, ContextCompat.getColor(activity, R.color.white));
        } else if (AndroidVersionUtil.hasKitKat()) {
            StatusBarCompat.setStatusBarColor(activity, ContextCompat.getColor(activity, R.color.c_666666));
        }
    }

    /**
     * 设置状态栏颜色，6.0以上透明
     *
     * @param activity
     */
    public static void setStatusTransColor(Activity activity) {
        if (AndroidVersionUtil.hasM()) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarCompat.translucentStatusBar(activity);
        } else if (AndroidVersionUtil.hasKitKat()) {
            StatusBarCompat.setStatusBarColor(activity, ContextCompat.getColor(activity, R.color.c_666666));
        }
    }

    public static void setStatusBarTitle(Activity activity, View rlTitle) {
        if (AndroidVersionUtil.hasM()) {
            LinearLayout.LayoutParams ivMsgParams = new LinearLayout.LayoutParams(-1, CommonUtils.dipToPx(activity, 50));
            ivMsgParams.topMargin = CommonUtils.getStatusBarHeight(activity);
            ivMsgParams.gravity = Gravity.RIGHT | Gravity.TOP;
            rlTitle.setLayoutParams(ivMsgParams);
        }
        rlTitle.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
    }

    public static SpannableString getTextStyle(Activity activity, String[] str, int[] style) {
        return getTextStyle(activity, str, style, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * 返回 SpannableString
     *
     * @param activity
     * @param str        内容
     * @param style      字体风格
     * @param appearance
     * @return
     */
    public static SpannableString getTextStyle(Activity activity, String[] str, int[] style, int appearance) {
        String strArr = "";
        int[] length = new int[str.length];
        for (int i = 0; i < str.length; i++) {
            strArr = strArr + str[i];
            length[i] = strArr.length();
        }
        SpannableString styledText = new SpannableString(strArr);
        for (int i = 0; i < str.length; i++) {
            styledText.setSpan(new TextAppearanceSpan(activity, style[i]), length[i] - str[i].length(), length[i],
                    appearance);
        }
        return styledText;
    }

    /**
     * 控件实现meterailDesign效果
     *
     * @param meterialView    控件
     * @param onClickListener 点击事件
     */
    public static MaterialRippleLayout makeMeterial(final View meterialView, final IMeterialClickLisenter onClickListener) {
        return makeMeterial(meterialView, false, onClickListener);
    }

    /**
     * 控件实现meterailDesign效果
     *
     * @param meterialView    控件
     * @param isAdapter       是否实现适配器
     * @param onClickListener 点击事件
     */
    public static MaterialRippleLayout makeMeterial(final View meterialView, boolean isAdapter, final IMeterialClickLisenter onClickListener) {
//         app:mrl_rippleOverlay="true"              // if true, ripple is drawn in foreground; false - background
//        app:mrl_rippleColor="#ff0000"             // color of ripple
//        app:mrl_rippleAlpha="0.1"                 // alpha of ripple
//        app:mrl_rippleDimension="10dp"            // radius of hover and starting ripple
//        app:mrl_rippleHover="true"                // if true, a hover effect is drawn when view is touched
//        app:mrl_rippleRoundedCorners="10dp"       // radius of corners of ripples. Note: it uses software rendering pipeline for API 17 and below
//        app:mrl_rippleInAdapter="true"            // if true, MaterialRippleLayout will optimize for use in AdapterViews
//        app:mrl_rippleDuration="350"              // duration of ripple animation
//        app:mrl_rippleFadeDuration="75"           // duration of fade out effect on ripple
//        app:mrl_rippleDelayClick="true"           // if true, delays calls to OnClickListeners until ripple effect ends
//        app:mrl_rippleBackground="#FFFFFF"        // background under ripple drawable; used with rippleOverlay="false"
//        app:mrl_ripplePersistent="true"           // if true, ripple background color persists after animation, until setRadius(0) is called
        MaterialRippleLayout materialRippleLayout = MaterialRippleLayout.on(meterialView)
                .rippleColor(meterialView.getResources().getColor(R.color.mrl_rippleColor))
                .rippleAlpha(0.1f)
                .rippleBackground(meterialView.getResources().getColor(R.color.transparent))
                .rippleDelayClick(true)
                .rippleDuration(400)
                .rippleDiameterDp(20)
                .rippleRoundedCorners(20)
                .rippleInAdapter(isAdapter)
                .rippleHover(true)
                .rippleDuration(75)
//                .ripplePersistent(false)
                .create();
        if (onClickListener != null) {
            materialRippleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onMetrialClick(meterialView);
                }
            });
        }
        return materialRippleLayout;
    }

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    /**
     * 状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        return getInternalDimensionSize(context.getResources(), STATUS_BAR_HEIGHT_RES_NAME);
    }

    /**
     * 获取系统Android自带的 dimen
     *
     * @param res
     * @param key
     * @return
     */
    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取渠道
     *
     * @param ctx
     * @return
     */
    public static int getAppChannelId(Context ctx) {
        switch (getAppMetaData(ctx, "UMENG_CHANNEL")) {
            case "anzi"://安智
                return 1002;
            case "yingyonghui"://应用汇
                return 1003;
            case "xiaomi"://小米
                return 1004;
            case "sougou"://搜狗
                return 1005;
            case "baidu"://百度
                return 1006;
            case "wandoujia"://豌豆荚
                return 1007;
            case "store91"://91市场
                return 1008;
            case "liantongwo"://联通沃
                return 1009;
            case "mumayi"://木蚂蚁
                return 1010;
            case "android"://Android市场
                return 1011;
            case "yingyongbao"://应拥宝
                return 1012;
            case "shougoustore"://搜狗手机助手
                return 1013;
            case "huawei"://华为市场
                return 1014;
            case "feifansoftware"://非凡软件
                return 1015;
            case "android3G"://3G安卓
                return 1016;
            case "c360"://360手机助手
                return 1017;
        }
        return 1001;//pinzhishangcheng万卡商城默认渠道
    }

    /**
     * //获取渠道号
     *
     * @param ctx
     * @return
     */
    public static String getAppMetaData(Context ctx) {
        return getAppMetaData(ctx, "UMENG_CHANNEL");
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    /**
     * list是否为空判断
     *
     * @param list
     * @return
     */
    public static boolean isEmptyList(List<?> list) {
        return list == null || list.size() == 0;
    }

    /**
     * dip转px
     */
    public static int dipToPx(Context context, float dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * px转dip
     */
    public static int pxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm;
    }

    /**
     * 安装apk
     *
     * @param context 上下文
     * @param file    文件安装包
     */
    public static void installApk(Context context, File file) {
        try {
            Intent intent_ins = new Intent(Intent.ACTION_VIEW);
            intent_ins.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent_ins.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.getApplicationContext().startActivity(intent_ins);
        } catch (Exception e) {
            Log.e("downLoad", "安装包异常");
        }
    }

    /**
     * 幻灯片等跳转判断
     *
     * @param mContext
     * @param appPage
     * @param appParams
     */
    public static void startIntent(Context mContext, String appPage, String appParams) {
        if (TextUtils.equals(ConstantsUtil.INTENT_CLASSIFY, appPage)) {//品类界面
            if (TextUtils.isEmpty(appParams) || !(appParams.length() > 0 && appParams.startsWith("classifyID="))) {
//                Toast.makeText(mContext, R.string.error_params, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(mContext, CategoryActivity.class);
            intent.putExtra("upCategoroyType", appParams.split("&")[0].replace("classifyID=", ""));
//            Log.e("upCategoroyType",appParams+":"+appParams.split("&")[0].replace("classifyID=", ""));
            mContext.startActivity(intent);
        } else if (TextUtils.equals(ConstantsUtil.INTENT_WEBONLY, appPage)) {
            if (TextUtils.isEmpty(appParams) || !(appParams.length() > 4 && appParams.startsWith("url=http"))) {
                return;
            }
            String url = appParams.substring(4);
            CommonUtils.startWebViewActivity(mContext, url, true, false);
        }
    }

    /**
     * 跳转到秒杀
     *
     * @param context
     */
    public static void startMsHtmlList(Context context, int activityBox, String productNo) {
        String url = HttpRequest.QUALITY_MARKET_WEB_URL + "activities/views/activities/activity20170223/secondkill20170223.html?activityBox=";
        if (activityBox > 2) {
            activityBox = 2;
        }
        url = url + activityBox + "&productNo=" + productNo;
        startWebViewActivity(context, url, false, false);
        TDUtils.onEvent(context, "100006", "限时秒杀");
        EventBus.getDefault().post(new Object(), EventBusConstants.EVENTT_UPDATE_MS_DATA);
    }

    public static Drawable getDrawable(@Nullable Context context, @Nullable int id) {
        Drawable draw = context.getResources().getDrawable(id);
        draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
        return draw;
    }

    /**
     * 半角转换为全角
     *
     * @param str
     * @return
     */

    public static String toDBC(String str) {

        char[] c = str.toCharArray();

        for (int i = 0; i < c.length; i++) {

            if (c[i] == 12288) {

                c[i] = (char) 32;

                continue;

            }

            if (c[i] > 65280 && c[i] < 65375)

                c[i] = (char) (c[i] - 65248);

        }

        return new String(c);

    }

    public static String getBase64Url(String url) {
        if (url.startsWith("http")) {
            return url;
        } else {
            try {
                return new String(android.util.Base64.decode(url, android.util.Base64.DEFAULT));
            } catch (Exception e) {
                return url;
            }
        }
    }

    /**
     * 是否是手机号
     *
     * @param mobileString 手机号
     * @return
     */
    public static boolean isMobilePhoneVerify(String mobileString) {
        if (mobileString == null || "".equals(mobileString.trim())) {
            return false;
        } else {
            String mobileTrim = mobileString.trim();
            Pattern patternMobile = Pattern.compile("[1][34578]\\d{9}"); // 1\\d{10}
            Matcher matcherMobile = patternMobile.matcher(mobileTrim);
            if (!matcherMobile.matches()) {
                return false;
            } else {
                return true;
            }
        }
    }

    /*
        * 毫秒转化时分秒
        */
    public static String[] formatTimeToSfm(long ms) {
        String[] formatTime = new String[3];
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;
        long day = ms / dd;
        long h = (ms - day * dd) / hh;
        long m = (ms - day * dd - h * hh) / mi;
        long s = (ms - day * dd - h * hh - m * mi) / ss;
        formatTime[0] = h < 10 ? ("0" + h) : h + "";
        formatTime[1] = m < 10 ? ("0" + m) : m + "";
        formatTime[2] = s < 10 ? ("0" + s) : s + "";
        return formatTime;
    }

    /**
     * 分享方法
     *
     * @param context             上下文
     * @param title               分享的标题
     * @param content             分享的内容
     * @param imagePathOrImageUrl 分享的图标
     * @param urlAfterClick       点击分享的item后响应的url地址
     * @param shareRecommendCode  直接传值false
     */
    public static void oneKeyShare(final Context context, final String title, final String content,
                                   final String imagePathOrImageUrl, final String urlAfterClick, boolean shareRecommendCode) {
        LogUtil.printLog("title=" + title + " ; content=" + content + " ; imagePathOrImageUrl = " + imagePathOrImageUrl
                + " ; urlAfterClick = " + urlAfterClick);

        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 显示推荐码
//		oks.setShareRecommendCode(shareRecommendCode);
        oks.setSilent(false);

        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {

            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {

                // 本地分享数据
                String localTitle = "分期购物上万卡商城";
                String localContent = "玖富万卡商城，京东及第三方品质保障，爆款直降，白条购物，想分就分！";
                String localUrl = HttpRequest.QUALITY_MARKET_WEB_URL + "views/share.html";
                SharedPreferences sharedPreferences = context
                        .getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, MODE_PRIVATE);
                String localImagePath = sharedPreferences.getString(ConstantsUtil.APP_ICON_LOCAL_STORE_KEY, null);
                if (StringUtil.notEmpty(title)) {
                    localTitle = title;
                }

                if (StringUtil.notEmpty(content)) {
                    localContent = content;
                }

                if (StringUtil.notEmpty(urlAfterClick)) {
                    localUrl = urlAfterClick;
                }

                paramsToShare.setTitle(localTitle);
                // 微信收藏和微信朋友圈该属性不显示
                paramsToShare.setText(localContent);
                if (imagePathOrImageUrl != null && !"".equals(imagePathOrImageUrl)) {
                    paramsToShare.setImageUrl(imagePathOrImageUrl);
                    // paramsToShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
                } else {
                    paramsToShare.setImagePath(localImagePath);
                }
                // url仅在微信（包括好友和朋友圈）中使用
                paramsToShare.setUrl(localUrl);
                paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                // titleUrl是标题的网络链接，site是分享此内容的网站名称，siteUrl是分享此内容的网站地址,仅在人人网和QQ空间使用
                paramsToShare.setTitleUrl(localUrl);
                paramsToShare.setSite(context.getString(R.string.app_name));
                paramsToShare.setSiteUrl(localUrl);
                // 发短信用到的参数
                paramsToShare.setAddress("");

//				paramsToShare.setTitle("分享标题--Title");
//				paramsToShare.setTitleUrl("http://mob.com");
//				paramsToShare.setText("分享测试文--Text");
//				paramsToShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
            }
        });
        // 启动分享GUI
        oks.show(context);
    }

    public static boolean isChineseByREG(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[\\u4E00-\\u9FA5·]{2,10}$");
        return pattern.matcher(str.trim()).find();
    }

    public static boolean isCorrectIdcard(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]{17}[0-9xX]{1}");
        return pattern.matcher(str.trim()).find();
    }
}
