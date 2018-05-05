package com.test.bank.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.test.bank.base.BaseApplication;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/5<br>
*/
public class DeviceUtil {

    /**
     * 获取APP版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取设备IMEI号
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String IMEI = tm.getDeviceId();
        if (TextUtils.isEmpty(IMEI)) {//设备号为空，则实用ANDROID_ID
            IMEI = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return IMEI;
    }

    /**
     * 获取本机mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        // 在wifi未开启状态下，仍然可以获取MAC地址，但是IP地址必须在已连接状态下否则为0
        String macAddress = null, ip = null;
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        LogUtils.printLog("MAC地址：" + macAddress);
        return macAddress;
    }


    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";

    protected static UUID uuid;

    public static String getDevicecId() {
        if (uuid == null) {
            synchronized (DeviceUtil.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = BaseApplication.getContext().getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);

                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        uuid = UUID.fromString(id);

                    } else {

                        final String androidId = Settings.Secure.getString(BaseApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                        // Use the Android ID unless it's broken, in which case fallback on deviceId,
                        // unless it's not available, then fallback on a random number which we store
                        // to a prefs file
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            } else {
                                final String deviceId = ((TelephonyManager) BaseApplication.getContext().getSystemService(TELEPHONY_SERVICE)).getDeviceId();
                                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                        // Write the value out to the prefs file
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
                    }

                }
            }
        }
//        Log.e("zzzzzz", "getDevicecId: " + uuid.toString());
        return uuid.toString();
    }

    /**
     * 判断用户是否有系统锁屏密码
     *
     * @return
     */
    public static boolean isPhoneHasLock() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            KeyguardManager keyguardManager = (KeyguardManager) BaseApplication.applicationContext.getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.isKeyguardSecure();
        } else {
            return isSecured();
        }
    }

    private static boolean isSecured() {
        boolean isSecured = false;
        String classPath = "com.android.internal.widget.LockPatternUtils";
        try {
            Class<?> lockPatternClass = Class.forName(classPath);
            Object lockPatternObject = lockPatternClass.getConstructor(Context.class).newInstance(BaseApplication.applicationContext);

            Method method = lockPatternClass.getMethod("isSecure");
            isSecured = (boolean) method.invoke(lockPatternObject);

//            Method method = lockPatternClass.getMethod("getActivePasswordQuality");
//            Integer mode = (Integer) method.invoke(lockPatternObject);
//            if (mode == DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED) {
//               isSecured = false;
//            } else {
//                isSecured = true;
//            }

        } catch (Exception e) {
            isSecured = false;
        }
        return isSecured;
    }
}
