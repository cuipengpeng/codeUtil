package com.jf.jlfund.utils;

import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseApplication;

/**
 * Date:2016/7/1 0001 11:52
 */
public class ToastUtils {
    private ToastUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static Toast toast = null;

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(CharSequence message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (Looper.getMainLooper().isCurrentThread()) {
            showDuration(message, Toast.LENGTH_SHORT);
        } else {
            LogUtils.e("showShort on not ui thread.");
            Looper.prepare();
            showDuration(message, Toast.LENGTH_SHORT);
            Looper.loop();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(int message) {
        if (Looper.getMainLooper().isCurrentThread()) {
            showDuration(message, Toast.LENGTH_SHORT);
        } else {
            Looper.prepare();
            showDuration(message, Toast.LENGTH_SHORT);
            Looper.loop();
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(CharSequence message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        showDuration(message, Toast.LENGTH_LONG);
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(int message) {
        showDuration(message, Toast.LENGTH_LONG);
    }


    //给toast基础设置样式
    private static void setBaseStyle(CharSequence message) {
        //初始化布局控件
        View toastRootView = View.inflate(BaseApplication.getContext(), R.layout.toast_layout, null);
        //将view赋值给toast的mNextView,方便以后获取
        toast.setView(toastRootView);
        //获取textview
        TextView mTextView = (TextView) toastRootView.findViewById(R.id.tv_message);
        //设置文案
        mTextView.setText(message.toString());
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toast.setGravity(Gravity.TOP, 0, DensityUtil.getScreenHeight() / 2);
        toast.setView(toastRootView);
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    public static void showDuration(int message, int duration) {
        showDuration(BaseApplication.getContext().getResources().getText(message), duration);
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    public static void showDuration(CharSequence message, int duration) {
        if (toast != null) {
            //获取textview
            TextView mTextView = (TextView) toast.getView().findViewById(R.id.tv_message);
            mTextView.setText(TextUtils.isEmpty(message.toString()) ? "提示信息为空" : message);
            toast.setDuration(duration);
        } else {
            toast = new Toast(BaseApplication.getContext());
            setBaseStyle(message);
            toast.setDuration(duration);
        }
        toast.show();
    }

}
