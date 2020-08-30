package com.test.xcamera.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.xcamera.R;

import java.lang.reflect.Field;

/**
 * Author: mz
 * Time:  2019/9/26
 */
public class ToastCompat {
    private static Field sField_TN;
    private static Field sField_TN_Handler;
    private Toast mToast;

    static {
        try {
            sField_TN = Toast.class.getDeclaredField("mTN");
            sField_TN.setAccessible(true);
            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
            sField_TN_Handler.setAccessible(true);
        } catch (Exception e) {
        }
    }

    private View toastContentView;
    private ObjectAnimator anim;

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWarpper(preHandler));
        } catch (Exception e) {
        }
    }

    public void showToast(Context context, CharSequence cs, int length) {
        if (mToast == null) {
            mToast = Toast.makeText(context, cs, length);
        } else {
            mToast.setText(cs);
        }
        hook(mToast);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public void showToast(Context context, CharSequence cs, int length, int gravity_top, int x_offent, int y_offent) {
        if (mToast == null) {
            mToast = Toast.makeText(context, cs, length);
        } else {
            mToast.setText(cs);
        }
        hook(mToast);
        mToast.setGravity(gravity_top, x_offent, y_offent);
        mToast.show();
    }

    public void showToast(Context context, CharSequence cs, int duration, float rotation) {
        if (mToast == null) {
            mToast = Toast.makeText(context, cs, duration);
        }
        if (toastContentView == null)
            toastContentView = View.inflate(context, R.layout.toast_view, null);

        RelativeLayout toastLayout = toastContentView.findViewById(R.id.toastLayout);
        TextView toastContentTextView = this.toastContentView.findViewById(R.id.toastContent);
        toastContentTextView.setText(cs);
        mToast.setView(toastContentView);
        hook(mToast);

        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();

        if (anim == null)
            anim = ObjectAnimator.ofFloat(toastLayout, "rotation", 0f, rotation);

        anim.setDuration(5);
        anim.start();
    }

    public void showToast90(Context context, CharSequence cs, int length) {
        if (mToast == null) {
            mToast = Toast.makeText(context, cs, length);
        }
        if (toastContentView == null)
            toastContentView = View.inflate(context, R.layout.toast_view, null);

        RelativeLayout toastLayout = toastContentView.findViewById(R.id.toastLayout);
        TextView toastContentTextView = this.toastContentView.findViewById(R.id.toastContent);
        toastContentTextView.setText(cs);
        mToast.setView(toastContentView);
        hook(mToast);
        mToast.show();

        if (anim == null)
            anim = ObjectAnimator.ofFloat(toastLayout, "rotation", 0f, 90f);

        anim.setDuration(5);
        anim.start();
    }

    public void showToast180(Context context, CharSequence cs, int length) {
        if (mToast == null) {
            mToast = Toast.makeText(context, cs, length);
        }
        if (toastContentView == null)
            toastContentView = View.inflate(context, R.layout.toast_view, null);

        RelativeLayout toastLayout = toastContentView.findViewById(R.id.toastLayout);
        TextView toastContentTextView = this.toastContentView.findViewById(R.id.toastContent);
        toastContentTextView.setText(cs);
        mToast.setView(toastContentView);
        hook(mToast);
        mToast.show();

        if (anim == null)
            anim = ObjectAnimator.ofFloat(toastLayout, "rotation", 0f, -180f);

        anim.setDuration(5);
        anim.start();
    }

    public static class SafelyHandlerWarpper extends Handler {
        private Handler impl;

        public SafelyHandlerWarpper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
            }
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);//需要委托给原Handler执行
        }
    }
}
