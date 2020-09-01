package com.hospital.checkup.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetworkUtil {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    public static void gotoNetworkSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void pingTest(String ip) {
        DataOutputStream outputStream = null;
        BufferedReader reader = null;
        Process process;
        String command = "ping -s 1016 -c 3 " + ip;
        try {
            process = Runtime.getRuntime().exec("sh");
            outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.write(command.getBytes());
            outputStream.writeBytes("\n");
            outputStream.flush();
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("---")) {
                    line = reader.readLine();
                    if (line == null) {
                        return;
                    }
                    String recevie = line.substring(line.indexOf("transmitted,") + 13, line.indexOf("received")).trim();
                    if (Integer.valueOf(recevie) == 0) {
                        return;
                    }
                    line = reader.readLine();
                    if (line == null) {
                        return;
                    }
                    String timeList = line.substring(line.indexOf("=") + 2, line.indexOf("ms") - 1).trim();
                    String[] temp = timeList.split("/");
                    if (temp.length < 1) {
                        return;
                    }
                    float time = Float.valueOf(temp[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
