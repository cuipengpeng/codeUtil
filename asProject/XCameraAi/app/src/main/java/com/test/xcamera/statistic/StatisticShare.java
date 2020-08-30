package com.test.xcamera.statistic;

import android.util.Log;

import com.test.xcamera.application.AppContext;
import com.test.xcamera.base.StatisticHelper;
import com.moxiang.common.share.ShareManager;

/**
 * 分享
 */
public class StatisticShare {
    /*抖音*/
    public static final String Share_To_Douyin="Share_To_Douyin";
    /*微信*/
    public static final String Share_To_Wechat="Share_To_Wechat";
    /*微博*/
    public static final String Share_To_WeiBo="Share_To_WeiBo";
    /*qq*/
    public static final String Share_To_QQ="Share_To_QQ";
    /*一键成片*/
    public static final String One_key_Video="One_key_Video";
    /*首页feed*/
    public static final String Home_Feed="Home_Feed";
    /*视频编辑*/
    public static final String Video_Edit="Video_Edit";
    /*相册分享*/
    public static final String Album="Album";

    public static String SHARE_FROM_KEY=Home_Feed;
    private static StatisticShare instance;
    private StatisticShare(){}
    public static StatisticShare getInstance() {
        if(instance!=null){
            return instance;
        }
        instance=new StatisticShare();
        return instance;
    }
    public void setOnEvent(String action){
        StatisticHelper.onEvent(AppContext.getInstance(),action);
    }
    public void setOnEvent(String action,String value){
        Log.i("StatisticShare","Share_action:"+action+"  value:"+value);
        StatisticHelper.onEvent(AppContext.getInstance(),action,value);
    }
    public void shareTo(ShareManager.ShareChooser shareChooser,String key) {
        switch (shareChooser) {
            case Wechat:
                setOnEvent(Share_To_Wechat,key);
                break;
            case SinaWeibo:
                setOnEvent(Share_To_WeiBo,key);
                break;
            case QQ:
                setOnEvent(Share_To_QQ,key);
                break;
            case DouYin:
                setOnEvent(Share_To_Douyin,key);
                break;
        }

    }
}
