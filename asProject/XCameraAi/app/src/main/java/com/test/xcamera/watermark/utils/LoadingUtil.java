package com.test.xcamera.watermark.utils;

import android.content.Context;
import android.os.Looper;

import com.test.xcamera.watermark.controller.MarkerHandler;
import com.test.xcamera.watermark.dialog.LoadingDialog;


public class LoadingUtil {
    private MarkerHandler handler;
    private LoadingDialog loading;

    public LoadingUtil(MarkerHandler handler) {
        this.handler = handler;
    }

    /**
     * 显示加载提示
     *
     * @param tips
     */
    public void showLoading(final String tips) {
        final Context context = ContextRef.getInstance().getContext();
        /*
         * ApplicationContext 无法调起Dialog
         * alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
         * 6.0以上需要手动开启权限
         */
        if (ContextRef.getInstance().isApplicationContext()) {
            return;
        }
        loading = new LoadingDialog(context);
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loading.setTips(tips);
            loading.show();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loading.setTips(tips);
                    loading.show();
                }
            });
        }
    }

    /**
     * @param tips
     * @param cancelable 是否可取消  false 不可以  true 可以
     */
    public void showLoading(final String tips, final boolean cancelable) {
        final Context context = ContextRef.getInstance().getContext();
        /*
         * ApplicationContext 无法调起Dialog
         * alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
         * 6.0以上需要手动开启权限
         */
        if (ContextRef.getInstance().isApplicationContext()) {
            return;
        }
        endLoading();
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loading = new LoadingDialog(context, tips, cancelable);
            loading.show();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loading = new LoadingDialog(context, tips, cancelable);
                    loading.show();
                }
            });
        }
    }

    /**
     * 显示加载提示
     *
     * @param title 标题
     * @param tips  提示
     */
    public void showLoading(String title, String tips) {
        final Context context = ContextRef.getInstance().getContext();
        /*
         * ApplicationContext 无法调起Dialog
         * alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
         * 6.0以上需要手动开启权限
         */
        if (ContextRef.getInstance().isApplicationContext()) {
            return;
        }
        endLoading();
        loading = new LoadingDialog(context, title, tips);
        loading.show();
    }

    public void setTips(String tips) {
        if (loading == null) {
            final Context context = ContextRef.get();
            loading = new LoadingDialog(context);
            loading.setTips(tips);
            loading.show();
        } else {
            loading.setTips(tips);
        }
    }

    /**
     * 是否响应back键
     *
     * @param cancelable true：响应，false：不响应
     */
    public void setLoadingCancelable(boolean cancelable) {
        if (null != loading) {
            loading.setCancelable(cancelable);
        }
    }

    /**
     * 隐藏loading
     */
    public void endLoading() {
        if (null != loading) {
            loading.dismiss();
            loading = null;
        }
    }

    public boolean isLoading() {
        return loading != null && loading.isShowing();
    }
}
