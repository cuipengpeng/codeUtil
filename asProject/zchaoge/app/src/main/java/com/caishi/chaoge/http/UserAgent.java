package com.caishi.chaoge.http;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.caishi.chaoge.utils.CipherUtils;
import com.caishi.chaoge.utils.NetworkMonitor;
import com.caishi.chaoge.utils.SystemUtils;
import com.caishi.chaoge.utils.Utils;

import java.util.Locale;

/**
 * Created by victorxie on 4/7/16.
 */
public class UserAgent {

    public static String httpAgent = "";
    public static String operator = "";

    public static void init(Context context) {
        httpAgent = System.getProperty("http.agent");
        Account.sDeviceId = Utils.getDeviceId();
        operator = "0" + SystemUtils.getOperatorId(context);
    }

    public static String formatAgent(String path) {

        return String.format(Locale.ROOT,
                "%s %s (agent:s;channel:%s;credential:%s;deviceId:%s;osTypeId:01;detailInfo:%s;simTypeId:%s;netTypeId:%s;deviceTypeId:%s;osVersion:%s;token:%s)",
                httpAgent,
                ("ChaoGe/" + Product.sVersionName),
                Product.sAppChannel, // channel
                Account.sUserToken, // credential
                Account.sDeviceId, // deviceId
                Build.BRAND + " " + Build.MODEL,
                operator,
                NetworkMonitor.getNetworkType(),
                "01",
                Build.VERSION.RELEASE,
                createToken(path));
    }

    public static String createToken(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        String userId = Account.sUserId;
        if (Account.sUserId != null && Account.sUserId.length() > 8) {
            userId = String.format(Locale.ROOT, "%c*%c#%c!%c$%c", userId.charAt(0),
                    userId.charAt(1), userId.charAt(2), userId.charAt(4), userId.charAt(7));
        }
        int quest = path.indexOf('?');
        if (quest != -1) {
            path = path.substring(0, quest);
        }
        String token = path + "-" + (System.currentTimeMillis() + "0") + "-" +
                Product.sVersionName + "-";
        String sequence = CipherUtils.hashWithMD5(token + userId);

        return CipherUtils.encryptWithRSA((token + sequence).getBytes(),
                KEY.getBytes());
    }

    public static final String KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCE6JQcsUrPE/LxL17M0J/qtHkoXRr1DHiUY7ETO6d5GiJNcK6N3R22v9NiImMAQsbl8KmLSNj2dHVf682y+s4xXklthNtRjGCKuyD14V+xGIE5h6oBKipcm0JkPQXcpYeyON3OZP+72dvHT702fRLwNABvOkA1DEs4/Ay6zoiFOwIDAQAB";


}
