package com.moxiang.common.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.open.aweme.TikTokConstants;
import com.bytedance.sdk.open.aweme.TikTokOpenApiFactory;
import com.bytedance.sdk.open.aweme.TikTokOpenConfig;
import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.base.TikTokImageObject;
import com.bytedance.sdk.open.aweme.base.TikTokMediaContent;
import com.bytedance.sdk.open.aweme.base.TikTokVideoObject;
import com.bytedance.sdk.open.aweme.share.Share;
import com.moxiang.common.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2019/11/16.
 */
public class ShareManager {
    public static final String TAG = "ShareManager";
    public static final String WECHATPACKAGENAME = "com.tencent.mm";
    public static final String WEIBOPACKAGENAME = "com.sina.weibo";
    public static final String QQPACKAGENAME = "com.tencent.mobileqq";
    public static final String DOUYINPACKAGENAME = "com.ss.android.ugc.aweme";
    public static final int ERROR_WECHAT_NOT_INSTALLED = 1;
    public static final int ERROR_QZONE_NOT_INSTALLED = 2;
    private static ShareManager ourInstance;
    private Dialog dialog = null;
    private OnBeforeShareListener beforeShareListener;

    public Dialog getDialog() {
        return dialog;
    }

    private List<ShareGridItem> dataList = new ArrayList<>();
    private int[] icons = {R.drawable.ic_share_douyin, R.drawable.ic_share_wechat, R.drawable.ic_share_weibo, R.drawable.ic_share_qq};

    private WeakReference<Activity> activityWeakReference;

    public static ShareManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new ShareManager();
        }
        return ourInstance;
    }

    private ShareManager() {
    }

    public void initShareManager(Context context) {
//        UMConfigure.init(context, "5dba632a0cafb2509f0012e6", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.init(context,UMConfigure.DEVICE_TYPE_PHONE, null);
//        if(BuildConfig.DEBUG){
//            UMConfigure.setLogEnabled(true);
//        }
        PlatformConfig.setWeixin("wxa8e3e8e612d92eb1", "7d8180668ad9c046c8acfd7e67904871");
        PlatformConfig.setSinaWeibo("1345677814", "7d8180668ad9c046c8acfd7e67904871", "http://sns.whalecloud.com");
//        //抖音分享
        String clientkey = "awvcii9svhgkgixl";
        TikTokOpenApiFactory.init(new TikTokOpenConfig(clientkey));
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    public void shareTo(Activity activity, ShareEntity shareEntity, IShare actionListener) {
        activityWeakReference = new WeakReference<>(activity);
        Log.w(TAG, shareEntity.toString());
        share2Text(shareEntity, activity, actionListener);
    }

    private void share2Text(ShareEntity shareEntity, Activity activity, UMShareListener actionListener) {
        UMImage thumb = new UMImage(activity, R.drawable.vk_icon);
        thumb.setThumb(thumb);
        new ShareAction(activity)
                .setPlatform(shareEntity.getPlatform())//传入平台
                .withText(shareEntity.getTitle())//分享内容
                .withMedia(thumb)
                .setCallback(actionListener)//回调监听器
                .share();
    }

    private void shareVideo(ShareEntity shareEntity, Activity activity, UMShareListener actionListener) {
        UMImage thumb = new UMImage(activity, R.drawable.vk_icon);
        thumb.setThumb(thumb);
        UMVideo video = new UMVideo(shareEntity.getUrl());
        video.setTitle(shareEntity.getTitle());//视频的标题
        video.setThumb(thumb);//视频的缩略图
        video.setDescription(shareEntity.getDescription());//视频的描述
        new ShareAction(activity)
                .setPlatform(shareEntity.getPlatform())//传入平台
                .withText(shareEntity.getTitle())//分享内容
                .withMedia(video)
                .setCallback(actionListener)//回调监听器
                .share();
    }


    /**
     * 调用系统分享功能
     * s
     *
     * @param shareEntity
     * @param context
     */
    public void share2More(final ShareEntity shareEntity, final Activity context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, shareEntity.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, shareEntity.getDescription() + "\n" + shareEntity.getUrl());
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.app_name)));
    }


    /**
     * 弹出分享平台选择界面,友盟的UI.
     *
     * @param activity
     * @param shareEntity
     * @param listener    , SHARE_MEDIA.QQ,
     *                    , SHARE_MEDIA.WEIXIN_CIRCLE
     */
    public void showSharePlatform(Activity activity, ShareEntity shareEntity, UMShareListener listener) {
        new ShareAction(activity).withText(shareEntity.getTitle()).setDisplayList(SHARE_MEDIA.SINA,
                SHARE_MEDIA.WEIXIN)
                .setCallback(listener).open();
    }

    public void showMoSharePlatformGoHome(final Activity activity, final LinearLayout shareRelativeLayout, final ShareEntity shareEntity, final IShare iShare, final DismissListener dismissListener) {
        if (activity == null) {
            return;
        }
        if (dataList.size() == 0) {
            String[] sharePlatform = activity.getResources().getStringArray(R.array.share_platform);
            for (int i = 0; i < icons.length; i++) {
                dataList.add(new ShareGridItem(sharePlatform[i], icons[i]));
            }
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.grid_share_view_gohome, null);
        GridView gridView = view.findViewById(R.id.gridViewShareChooser);
        TextView dialogGoHome = view.findViewById(R.id.share_dialog_go_home);

        ShareGridAdapter shareGridAdapter = new ShareGridAdapter(activity, dataList);
        gridView.setAdapter(shareGridAdapter);
        dialog = new Dialog(activity, R.style.moDialogStyle);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        handleShareChannel(activity, shareEntity, ShareChooser.DouYin, iShare);
                        break;
                    case 1:
                        handleShareChannel(activity, shareEntity, ShareChooser.Wechat, iShare);
                        break;
                    case 2:
                        handleShareChannel(activity, shareEntity, ShareChooser.SinaWeibo, iShare);
                        break;
                    case 3:
                        handleShareChannel(activity, shareEntity, ShareChooser.QQ, iShare);
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                }
                if (dismissListener != null) {
                    dismissListener.dismiss(dialog);
                } else {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });

        dialogGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
                iShare.goHome();
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.MATCH_PARENT);
        shareRelativeLayout.addView(view, layoutParams);
//        dialog.setContentView(view);
//        dialog.setCancelable(false);
//        Window window = dialog.getWindow();
//        if (window != null) {
//            window.setWindowAnimations(R.style.popup_enter_exit_anim);
//            window.getDecorView().setPadding(0, 0, 0, 0);
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            params.gravity = Gravity.BOTTOM;
//            params.dimAmount = 0f;
//            window.setAttributes(params);
//            dialog.show();
//        }
    }

    /**
     * 弹出投稿分享页面
     */
    public void showMoSharePlatform(final Activity activity, final ShareEntity shareEntity, final IShare iShare, final DismissListener dismissListener) {
        if (dataList.size() == 0) {
            String[] sharePlatform = activity.getResources().getStringArray(R.array.share_platform);
            for (int i = 0; i < icons.length; i++) {
                dataList.add(new ShareGridItem(sharePlatform[i], icons[i]));
            }
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.grid_share_view, null);
        GridView gridView = view.findViewById(R.id.gridViewShareChooser);
        TextView dialogCancel = view.findViewById(R.id.share_dialog_cancel);

        ShareGridAdapter shareGridAdapter = new ShareGridAdapter(activity, dataList);
        gridView.setAdapter(shareGridAdapter);
        dialog = new Dialog(activity, R.style.moDialogStyle);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        handleShareChannel(activity, shareEntity, ShareChooser.DouYin, iShare);
                        break;
                    case 1:
                        handleShareChannel(activity, shareEntity, ShareChooser.Wechat, iShare);
                        break;
                    case 2:
                        handleShareChannel(activity, shareEntity, ShareChooser.SinaWeibo, iShare);
                        break;
                    case 3:
                        handleShareChannel(activity, shareEntity, ShareChooser.QQ, iShare);
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                }
                if (dismissListener != null) {
                    dismissListener.dismiss(dialog);
                } else {
                    dialog.dismiss();
                }
            }
        });

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.setContentView(view);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.popup_enter_exit_anim);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
            dialog.show();
        }
    }

    private void handleShareChannel(Activity activity, ShareEntity shareEntity, ShareChooser shareChooser, IShare iShare) {
        if (shareEntity.isBackHandle) {
            iShare.onItemClick(shareChooser);
        } else {
            shareTo(activity, shareEntity, shareChooser, iShare);
        }
    }

    private void openWechat(Activity activity) {
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(WECHATPACKAGENAME);
        activity.startActivity(intent);
    }

    private void openWeibo(Activity activity) {
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(WEIBOPACKAGENAME);
        activity.startActivity(intent);
    }

    private void openQQ(Activity activity) {
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(QQPACKAGENAME);
        activity.startActivity(intent);
    }

    private void openApp(Activity activity, String packageName) {
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        activity.startActivity(intent);
    }


    private void shareToWechat(Activity activity, ShareEntity shareEntity, IShare iShare) {
        if (checkAppInstalled(activity, WECHATPACKAGENAME)) {
//            shareTo(activity, shareEntity, iShare);
            openApp(activity, WECHATPACKAGENAME);
        } else {
            Toast.makeText(activity, R.string.app_need_install, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareToWeibo(Activity activity, ShareEntity shareEntity, IShare iShare) {
        if (checkAppInstalled(activity, WEIBOPACKAGENAME)) {
//            shareTo(activity, shareEntity, iShare);
            openApp(activity, WEIBOPACKAGENAME);
        } else {
            Toast.makeText(activity, R.string.app_need_install, Toast.LENGTH_SHORT).show();
        }
    }


    public enum ShareChooser {
        Wechat, SinaWeibo, QQ, DouYin
    }

    public static class ShareEntity implements Serializable {
        private String title;
        private String description;
        private String url;
        private Bitmap thumb;
        private String thumbUrl;
        private SHARE_MEDIA platform;


        //是否需要返回调用处处理
        private boolean isBackHandle;

        private OnStatisticShare mOnStatisticShare;

        public OnStatisticShare getOnStatisticShare() {
            return mOnStatisticShare;
        }


        public void setOnStatisticShare(OnStatisticShare mOnStatisticShare) {
            this.mOnStatisticShare = mOnStatisticShare;
        }

        public boolean isBackHandle() {
            return isBackHandle;
        }

        public void setBackHandle(boolean backHandle) {
            isBackHandle = backHandle;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Bitmap getThumb() {
            return thumb;
        }

        public void setThumb(Bitmap thumb) {
            this.thumb = thumb;
        }

        public String getThumbUrl() {
            return thumbUrl;
        }

        public void setThumbUrl(String thumbUrl) {
            this.thumbUrl = thumbUrl;
        }

        public SHARE_MEDIA getPlatform() {
            return platform;
        }

        public void setPlatform(SHARE_MEDIA platform) {
            this.platform = platform;
        }

        @Override
        public String toString() {
            return "ShareEntity{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", url='" + url + '\'' +
                    ", thumb=" + thumb +
                    ", thumbUrl='" + thumbUrl + '\'' +
                    '}';
        }
    }

    /**
     * 判断某个应用是否安装
     *
     * @param activity
     * @param pkgName
     * @return
     */
    private boolean checkAppInstalled(Activity activity, String pkgName) {
        if (pkgName == null || pkgName.isEmpty()) {
            return false;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = activity.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }


    /**
     * share 功能示例代码
     *
     * @param shareType
     * @return
     */
    private boolean share(int shareType, TiktokOpenApi tiktokOpenApi, ArrayList<String> uris, String shareText) {
        Share.Request request = new Share.Request();
        switch (shareType) {
            case Share.IMAGE:
                TikTokImageObject imageObject = new TikTokImageObject();
                imageObject.mImagePaths = uris;
                TikTokMediaContent mediaContent = new TikTokMediaContent();
                mediaContent.mMediaObject = imageObject;
                request.mHashTag = shareText;
                request.mMediaContent = mediaContent;
                break;
            case Share.VIDEO:
                TikTokVideoObject videoObject = new TikTokVideoObject();
                videoObject.mVideoPaths = uris;
                request.mHashTag = shareText;
                TikTokMediaContent content = new TikTokMediaContent();
                content.mMediaObject = videoObject;
                request.mMediaContent = content;

                //可以在mState传入shareId
                request.mState = "ss";

//                 可以通过callerLocalEntry设置自己接收回调的类，不必非得用TikTokEntryActivity
                request.callerLocalEntry = "com.meetvr.aicamera.tiktokap.TikTokEntryActivity";
                //如果拥有默认话题权限，则可通过这个变量设置默认话题
//                request.mHashTag = "设置我的默认话题";

                // 0.0.1.1版本新增分享带入小程序功能，具体请看官网 对应抖音及tiktok版本6.7.0
//                TikTokMicroAppInfo mMicroInfo = new TikTokMicroAppInfo();
//                mMicroInfo.setAppTitle("小程序title");
//                mMicroInfo.setDescription("小程序描述");
//                mMicroInfo.setAppId("小程序id");
//                mMicroInfo.setAppUrl("小程序启动链接");
//                request.mMicroAppInfo = mMicroInfo;

                break;
        }

        return tiktokOpenApi.share(request);
    }

    private void shareToQQ(Activity activity, ShareEntity shareEntity, IShare iShare) {
        if (checkAppInstalled(activity, QQPACKAGENAME)) {
//            shareTo(activity, shareEntity, iShare);
            openApp(activity, QQPACKAGENAME);
        } else {
            Toast.makeText(activity, R.string.app_need_install, Toast.LENGTH_SHORT).show();
        }
    }


    public void shareTo(Activity activity, ShareEntity shareEntity, ShareChooser shareChooser, IShare iShare) {
        if(shareEntity.mOnStatisticShare!=null){
            shareEntity.mOnStatisticShare.onCallBack(shareChooser);
        }
        switch (shareChooser) {
            case Wechat:
                shareToWechat(activity, shareEntity, iShare);
                break;
            case SinaWeibo:
                shareToWeibo(activity, shareEntity, iShare);
                break;
            case QQ:
                shareToQQ(activity, shareEntity, iShare);
                break;
            case DouYin:
                shareToDouyin(activity, 0, shareEntity);
                break;
        }

    }

    public boolean shareToDouyin(Activity activity, int shareType, ShareEntity shareEntity) {
        if (!checkAppInstalled(activity, DOUYINPACKAGENAME)) {
            Toast.makeText(activity, R.string.app_need_install, Toast.LENGTH_SHORT).show();
            return false;
        }

        ArrayList<String> uris = new ArrayList<>();
        uris.add(shareEntity.getThumbUrl());
        TiktokOpenApi tiktokOpenApi = TikTokOpenApiFactory.create(activity, TikTokConstants.TARGET_APP.AWEME);
        Share.Request request = new Share.Request();
        switch (shareType) {
            // 单图目前只有抖音支持，海外版不支持
            case Share.IMAGE:
                TikTokImageObject imageObject = new TikTokImageObject();
                imageObject.mImagePaths = uris;
                TikTokMediaContent mediaContent = new TikTokMediaContent();
                mediaContent.mMediaObject = imageObject;
                request.mHashTag = "怎么拍dou橙";
                request.mMediaContent = mediaContent;
                break;
            case Share.VIDEO:
                TikTokVideoObject videoObject = new TikTokVideoObject();
                videoObject.mVideoPaths = uris;
                request.mHashTag = "怎么拍dou橙";
                TikTokMediaContent content = new TikTokMediaContent();
                content.mMediaObject = videoObject;
                request.mMediaContent = content;
                //可以在mState传入shareId
                request.mState = "ss";
                break;
        }
        return tiktokOpenApi.share(request);
    }

    public void dismissDialog() {
        try {
            if (null != dialog && dialog.isShowing()) {
                Window window = dialog.getWindow();
                if (window == null) {
                    return;
                }

                dialog.dismiss();
            }

        } catch (IllegalArgumentException e) {

        } finally {
            dialog = null;
        }
    }

    public void destroy() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        dataList.clear();
    }

    public interface DismissListener {
        void dismiss(Dialog dialog);
    }
    public interface OnStatisticShare {
        void onCallBack(ShareChooser shareChooser);
    }
    private OnStatisticShare mOnStatisticShare;

    public void setOnStatisticShare(OnStatisticShare mOnStatisticShare) {
        this.mOnStatisticShare = mOnStatisticShare;
    }

    public void setBeforeShareListener(OnBeforeShareListener beforeShareListener) {
        this.beforeShareListener = beforeShareListener;
    }

    public interface OnBeforeShareListener {
        void onBeforeShare(ShareEntity entity, Activity activity, ShareChooser shareChooser);
    }
}

