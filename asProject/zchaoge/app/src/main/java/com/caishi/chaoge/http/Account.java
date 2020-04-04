package com.caishi.chaoge.http;

/**
 * Created by victorxie on 5/16/16.
 */
public abstract class Account {

    public static final String TOKEN_NONE = "123456";
    public static String sUserId = "123456";
    public static String sUserToken = TOKEN_NONE;
    public static String sDeviceId = "imeiDeviceId";


    public static boolean isTokenValid() {
        return sUserToken != null && !TOKEN_NONE.equals(sUserToken);
    }
}
