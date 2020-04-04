package com.caishi.chaoge.utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.caishi.chaoge.R;


public class ToastUtils {
    private static Toast toast;


    public ToastUtils(Context context, CharSequence text, int duration) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_toast, null);
        TextView textView = view.findViewById(R.id.tv_toast_info);
        textView.setText(text);
        toast = new Toast(context);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
    }

    /**
     * 显示短自定义居中Toast
     *
     * @param context 上下文
     * @param msg 提示语
     */
    public static void showCentreToast(Context context, String msg) {
        if (context == null || msg == null) {
            Log.i("post", "ToastUtils空指针");
            return;
        }
        cancel();
        makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private static ToastUtils makeText(Context context, CharSequence text, int duration) {
        return new ToastUtils(context, text, duration);
    }

    /**
     * 显示短Toast
     *
     * @param msg 提示语
     */
    public static void show(Context context, String msg) {
        if (context == null || msg == null) {
            Log.i("post", "ToastUtils空指针");
            return;
        }
        cancel();
        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * 默认短Toast
     *
     * @param context
     * @param msg
     * @return
     */
    public static Toast showDefault(Context context, String msg) {
        Toast toastTemp = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toastTemp.show();
        return toastTemp;
    }

    /**
     * 销毁toast
     */
    public static void cancel() {
        if (toast != null) {
            toast.cancel();
        }
    }

}
