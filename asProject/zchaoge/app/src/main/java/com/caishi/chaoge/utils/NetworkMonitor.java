package com.caishi.chaoge.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;
import android.telephony.TelephonyManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by victorxie on 10/20/15.
 */
public class NetworkMonitor {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE_WIFI, NONE_MOBILE, WIFI_NONE, WIFI_MOBILE, MOBILE_NONE, MOBILE_WIFI})
    public @interface NetworkState {
    }

    // 从无网到wifi
    public static final int NONE_WIFI = 100;
    // 从无网到移动网
    public static final int NONE_MOBILE = 101;
    // 从wifi到无网
    public static final int WIFI_NONE = 102;
    public static final int WIFI_MOBILE = 103;
    public static final int MOBILE_NONE = 104;
    public static final int MOBILE_WIFI = 105;

    public interface Listener {

        void onNetworkChanged(@NetworkState int state);
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({NONE, WIFI, MNG2, MNG3, MNG4})
    public @interface NetworkType {
    }

    public static final String NONE = "NONE";
    public static final String WIFI = "WIFI";
    public static final String MNG2 = "MNG2";
    public static final String MNG3 = "MNG3";
    public static final String MNG4 = "MNG4";

    static class Engine {

        final Context context;
        BroadcastReceiver receiver;
        String networkType = NONE;
        boolean prompted = false;
        ArrayList<WeakReference<Listener>> listeners = new ArrayList<>();

        Engine(Context context) {

            this.context = context.getApplicationContext();
            updateStatus(context);
            registerReceiver();
        }

        void release() {

            listeners.clear();
            unregisterReceiver();
        }

        void registerReceiver() {

            if (receiver == null) {
                receiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {

                        updateStatus(context);
                    }
                };
                context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }
        }

        void unregisterReceiver() {

            if (receiver != null) {
                context.unregisterReceiver(receiver);
                receiver = null;
            }
        }

        void updateListener(Listener listener, boolean remove) {

            for (int i = 0; i < listeners.size(); i++) {
                Listener listener1 = listeners.get(i).get();
                if (listener1 == null) {
                    listeners.remove(i);
                    i--;
                    continue;
                }
                if (listener == listener1) {
                    if (remove) {
                        listeners.remove(i);
                    }
                    return;
                }
            }
            listeners.add(new WeakReference<>(listener));
        }

        void updateStatus(Context context) {

            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            String type;
            if (networkInfo != null && networkInfo.isAvailable()) {
                if ("WIFI".equals(networkInfo.getTypeName())) {
                    type = WIFI;
                } else {

                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    int type1 = telephonyManager.getNetworkType();
                    switch (type1) {
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                            type = MNG2;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            type = MNG4;
                            break;
                        default:
                            type = MNG3;
                            break;
                    }
                }
                prompted = true;
            } else {
                type = NONE;
                prompted = false;
            }
            LogUtil.d(getClass().getName(), "Network Info=" + networkInfo);
            if (!type.equals(networkType)) {
                int state;
                switch (networkType) {
                    case NONE:
                        state = (type.equals(WIFI)) ? NONE_WIFI : NONE_MOBILE;
                        break;
                    case WIFI:
                        state = (type.equals(NONE)) ? WIFI_NONE : WIFI_MOBILE;
                        break;
                    default:
                        state = (type.equals(NONE)) ? MOBILE_NONE : MOBILE_WIFI;
                        break;
                }
                networkType = type;
//                HttpParam.SOCKET_TIMEOUT = evaluateLatency();
                for (int i = 0; i < listeners.size(); i++) {
                    Listener listener = listeners.get(i).get();
                    if (listener != null) {
                        listener.onNetworkChanged(state);
                    } else {
                        listeners.remove(i);
                        i--;
                    }
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static Engine sEngine = null;

    public static void init(Context context) {

        if (sEngine == null) {
            sEngine = new Engine(context);
        }
    }

    public static void release() {

        if (sEngine != null) {
            sEngine.release();
            sEngine = null;
        }
    }

    public static void registerListener(Listener listener) {

        if (sEngine != null) {
            sEngine.updateListener(listener, false);
        }
    }

    public static void unregisterListener(Listener listener) {

        if (sEngine != null) {
            sEngine.updateListener(listener, true);
        }
    }

    public static boolean isAvailable() {

        return ((sEngine != null) && (!sEngine.networkType.equals(NONE)));
    }

    public static boolean isWifiEnabled() {

        return ((sEngine != null) && (sEngine.networkType.equals(WIFI)));
    }

    public static boolean isMobileEnabled() {

        return ((sEngine != null) && (sEngine.networkType.equals(MNG2) ||
                sEngine.networkType.equals(MNG3) || sEngine.networkType.equals(MNG4)));
    }

    public static String getNetworkType() {

        return ((sEngine != null) ? sEngine.networkType : NONE);
    }

//    public static void prompt(int rscId) {
//
//        if (sEngine != null && !sEngine.prompted) {
//            ToastUtils.showToast(sEngine.context, rscId, Toast.LENGTH_SHORT);
//            sEngine.prompted = true;
//        }
//    }
}
