package com.jfbank.qualitymarket.util;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.flyco.dialog.widget.MaterialDialog;
import com.jfbank.qualitymarket.listener.DialogListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.attr.fragment;

/**
 * 功能：权限控制<br>
 * 作者：RaykerTeam
 * 时间： 2016/11/3 0003<br>.
 * 版本：1.0.0
 */

public class PermissionUtils {
    /**
     * 无需开启权限API
     */
    public interface OnPermissionListener {
        void noNeedPermission();

        void denyPermisson(String[] permissions);
    }

    /**
     * 请求权限返回结果判断
     */
    public interface OnPermissionResultListener {
        void onResult(boolean iGranted);
    }

    private static final String TAG = "PermissionUtils";

    /**
     * 处理权限申请回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @param onPermissionListener 处理结果回调
     */
    public static void handlePermissionCallback(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, OnPermissionResultListener onPermissionListener) {
        boolean iGrantedPermission = true;
        for (int state : grantResults) {
            if (state == PackageManager.PERMISSION_GRANTED) {
            } else {
                iGrantedPermission = false;
                break;
            }
        }
        if (onPermissionListener != null) {
            onPermissionListener.onResult(iGrantedPermission);
        }
    }


    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        return hasPermission(context, Arrays.asList(permissions));
    }

    /**
     * <p>
     * 这个问题在项目中，一直存在，
     * 主要是第三方厂商各种改，返回的状态不正常；
     * 主要解决思路：
     * 在第三方成功获取权限时，
     * 再用系统原生的api去判断一下，是否真正获取了权限：
     * <p>
     * <p>
     * 系统层的权限判断
     *
     * @param context     上下文
     * @param permissions 申请的权限 Manifest.permission.READ_CONTACTS
     * @return 是否有权限 ：其中有一个获取不了就是失败了
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull List<String> permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        for (String permission : permissions) {
            return checkPermission(context, permission);
        }
        return true;
    }

    public static boolean hasPermission(@NonNull Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        return checkPermission(context, permission);
    }

    public static boolean requestPermission(Context activity, String[] permissionArray, OnPermissionListener onPermissionListener) {
        List<String> permissions = new ArrayList<>();
        for (int i = 0; i < permissionArray.length; i++) {
            if (!hasPermission(activity, permissionArray[i])) {
                permissions.add(permissionArray[i]);
            }
        }
        if (permissions.size() == 0) {
            if (onPermissionListener != null) {
                onPermissionListener.noNeedPermission();
            }
            return true;
        } else {
            if (onPermissionListener != null) {
                onPermissionListener.denyPermisson(permissions.toArray(new String[permissions.size()]));
            }
            return false;
        }
    }

    public static void startPermission(Activity activity, String[] permissionArray, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissionArray, requestCode);
    }

    /**
     * 开启权限对话框
     *
     * @param activity
     * @param msg
     */
    public static void showDialogPermison(final Context activity, String msg) {
        showDialogPermison(activity,true, msg, null);
    }

    public static void showDialogPermison(final Context activity,boolean isCanable, String msg, DialogListener.DialogClickLisenter dialogClickLisenter) {
        if (dialogClickLisenter == null) {
            dialogClickLisenter = new DialogListener.DialogClickLisenter() {
                @Override
                public void onDialogClick(int type) {
                    if (type == CLICK_SURE) {
                        startSysSettingsUI(activity);
                    }

                }
            };
        }
        DialogUtils.showTwoBtnDialog(activity,isCanable, "权限提示", msg, "取消", "开启", dialogClickLisenter);
    }

    /**
     * 跳转到系统界面
     * @param activity
     */
    public static void startSysSettingsUI(Context activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        } catch (Exception e) {
            ToastUtil.show("请到[设置]手动开启应用权限");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean checkPermission(Context context, String permission) {
        String op = AppOpsManagerCompat.permissionToOp(permission);
        if (TextUtils.isEmpty(op)) return true;
        int result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
        if (result != AppOpsManagerCompat.MODE_ALLOWED) {
            return false;
        }
        result = ContextCompat.checkSelfPermission(context, permission);
        if (result != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        //适配小米等机型
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int checkOp = appOpsManager.checkOp(AppOpsManager.permissionToOp(permission), android.os.Process.myUid(), context.getPackageName());
        if (checkOp != AppOpsManagerCompat.MODE_ALLOWED) {
            return false;
        }
        return true;
    }
}
