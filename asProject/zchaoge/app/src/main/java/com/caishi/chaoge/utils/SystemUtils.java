package com.caishi.chaoge.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.Locale;

/**
 * Created by victorxie on 4/7/16.
 */
public abstract class SystemUtils {

    private SystemUtils() {

    }


    @SuppressLint("HardwareIds")
    public static int getOperatorId(Context context) {

        // 0: NONE; 1: CMCC; 2: CMUC; 3: CMTC
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();

        if (IMSI == null) {
            return 0;
        }
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            return 1;
        } else if (IMSI.startsWith("46001")) {
            return 2;
        } else if (IMSI.startsWith("46003")) {
            return 3;
        } else {
            return 0;
        }
    }

    public static String getNetworkType(Context context) {

        final String[] NETWORK_TYPES = {
                "UNKNOWN",
                "GPRS",
                "EDGE",
                "UMTS",
                "CDMA",
                "EVOD0",
                "EVODA",
                "1xRTT",
                "HSDPA",
                "HSUPA",
                "HSPA",
                "IDEN",
                "EVODB",
                "LTE",
                "EHRPD",
                "HSPAP",
                "GSM",
                "TDSCDMA",
                "IWLAN",
        };
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int type = telephonyManager.getNetworkType();
        if (type < 0 || type >= NETWORK_TYPES.length) {
            type = 0;
        }
        return NETWORK_TYPES[type];
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isAvailable();
    }


    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
