package com.caishi.chaoge.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.caishi.chaoge.http.Product;
import com.caishi.chaoge.http.RequestURL;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static com.caishi.chaoge.utils.ConstantUtils.FILE_BASE_PATH;

/**
 * 其他
 */

public class Utils {
    private static final int MIN_DELAY_TIME = 300;  // 两次点击间隔不能少于500ms

    public static boolean isEmpty(String src) {
        return null == src || src.trim().length() == 0 || src.equals("null");
    }


    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    public static boolean isFastClick(int time) {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= time) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    /**
     * umeng事件统计
     * @param mContext  上下文
     * @param flag  事件
     */
    public static void umengStatistics(Context mContext, String flag) {
        MobclickAgent.onEvent(mContext, flag, Product.sAppChannel);
    }

    /**
     * 判断url
     *
     * @param url
     * @return
     */
    public static String isUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            String trim = url.trim();
            return trim.startsWith("http") ? "" + trim : RequestURL.VIDEO_BASE_URL + trim;
        }
        return "";
    }

    public static String toRGB(String hex) {
        int color = Integer.parseInt(hex.replace("#", ""), 16);
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        LogUtil.i("color", "red=" + red + "--green=" + green + "--blue=" + blue);
        return red + "," + green + "," + blue;
    }


    /**
     * 毫秒转分秒
     *
     * @param time 毫秒
     * @return 分秒
     */
    public static String setTimeText(int time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(time);
    }

    /**
     * 将long处理成分秒毫秒
     *
     * @param textInput
     * @return
     */
    public static String long2Time(long textInput) {
        String minuteStr, secondStr, msecondStr;
        if (textInput <= 0) {
            return "00:00:00";
        }
        long mSecond = textInput % 1000;
        long second = textInput / 1000 % 60;
        long minute = textInput / 1000 / 60 % 60;

        if (minute < 10) {
            minuteStr = "0" + minute;
        } else {
            minuteStr = minute + "";
        }
        if (second < 10) {
            secondStr = "0" + second;
        } else {
            secondStr = second + "";
        }

        if (mSecond >= 100) {
            msecondStr = mSecond + "";
        } else if (mSecond >= 10 && mSecond < 100) {
            msecondStr = "0" + mSecond;
        } else {
            msecondStr = "00" + mSecond;
        }
        return minuteStr + ":" + secondStr + ":" + msecondStr.substring(0, 2);
    }


    public static String colorToBitmap(String color) {
        //TODO 需要判断文件是否存在
        File sd = Environment.getExternalStorageDirectory();
        String path = sd.getPath() + "/" + FILE_BASE_PATH + "paint/";
        File file = new File(path);
        file.mkdir();
        Bitmap bm = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(Color.parseColor(color));
        canvas.drawRect(0, 0, 720, 1280, p);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        String fileName = path + color + ".png";
        LogUtil.d("Chao", fileName);
        if (new File(fileName).exists()) {
            return fileName;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileName;
    }

    public static Bitmap RGB2Bitmap(String rbg) {
        Bitmap bm = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(Color.parseColor(rbg));
        canvas.drawRect(0, 0, 10, 10, p);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bm;
    }


    public static int dp2px(Context context, int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }


    /**
     * 获取年龄
     */
    public static int getAge(long birthday) {
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);
        cal.setTimeInMillis(birthday);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int year = cal.get(Calendar.YEAR);

        int age = yearNow - year;

        if (monthNow <= month) {
            if (monthNow == month) {
                // monthNow==monthBirth
                if (dayNow < day) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }
        if (age < 0) {
            age = 0;
        }
        return age;
    }


    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String toJson(Map<String, Object> map) {
        JSONObject jsonObj = new JSONObject();
        for (String key : map.keySet()) {
            try {
                jsonObj.put(key, map.get(key));
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return jsonObj.toString();
    }


    public static List<String> getAssetPicPath(Context context) {
        AssetManager am = context.getAssets();
        String[] path = null;
        try {
            path = am.list("pic");  // ""获取所有,填入目录获取该目录下所有资源
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> pciPaths = new ArrayList<>();
        for (int i = 0; i < path.length; i++) {
            if (path[i].endsWith(".jpg")) {  // 根据图片特征找出图片
                pciPaths.add(path[i]);
            }
        }
        return pciPaths;
    }

    /**
     * 关闭键盘
     *
     * @param activity Activity
     */
    public static void hideSoftInput(Activity activity) {
        if (activity.getCurrentFocus() != null)
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

//    public static LatLng GPSToBaiDu(LatLng latLng) {
//        // 将GPS设备采集的原始GPS坐标转换成百度坐标
//        CoordinateConverter converter = new CoordinateConverter();
//        converter.from(CoordinateConverter.CoordType.GPS);
//        // sourceLatLng待转换坐标
//        converter.coord(latLng);
//        return converter.convert();
//    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 针对系统文夹只需要扫描,不用插入内容提供者,不然会重复
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    public static void scanFile(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(intent);
    }

    /**
     * 校验手机格式
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobile(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    /**
     * 手机号用****号隐藏中间数字
     *
     * @param phone
     * @return
     */
    public static String settingphone(String phone) {
        String phone_s = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        return phone_s;
    }


    /**
     * 设备ID
     *
     * @return
     */
    public static String getDeviceId() {
        String serial = "serial";

        String m_szDevIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10;

        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            LogUtil.printLog(exception.getMessage());
        }finally {
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }
    }


}
