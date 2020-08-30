package com.test.xcamera.view.basedialog.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.utils.DensityUtils;

import java.lang.ref.WeakReference;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/4/16
 * e-mail zhouxuecheng1991@163.com
 */

public class CommonDownloadDialog implements DialogInterface.OnDismissListener {
    private WeakReference<MOBaseActivity> activity;
    private CloseDialogListener closeDialogListener;
    private View view;
    private Dialog mDialog;
    private ImageView downLoadIcon;
    private TextView progressText;
    private ImageView closeDownloadDialog;
    private Animation mAnimation;
    private DialogInterface.OnDismissListener mOnDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }

    public CommonDownloadDialog(Context activity) {
        this.activity = new WeakReference<>((MOBaseActivity) activity);
        initDialog();
        initView();
    }

    private void initView() {
        downLoadIcon = view.findViewById(R.id.downLoadIcon);
        progressText = view.findViewById(R.id.progressText);
        closeDownloadDialog = view.findViewById(R.id.closeDownloadDialog);
        closeDownloadDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeDialogListener != null) {
                    closeDialogListener.cancelDialog();
                }
            }
        });
    }

    public Dialog getmDialog() {
        return mDialog;
    }

    public void setDialogCanceledOnTouchOutside(boolean isTouchOutside) {
        if (mDialog == null) {
            return;
        }
        mDialog.setCanceledOnTouchOutside(false);
        //设置点击返回键不消失
        mDialog.setCancelable(isTouchOutside);
    }

    private void initDialog() {
        view = View.inflate(activity.get(), R.layout.dialog_common_download_layout, null);
        mDialog = new Dialog(activity.get());
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnDismissListener(this);
        WindowManager m = activity.get().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        mDialog.getWindow().setAttributes(params);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mBackKeyListener != null) {
                        mBackKeyListener.onBack(mDialog);
                        return true;
                    }
                }
                return false;
            }
        });
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void setProgress(String content) {
        progressText.setText(content);
    }

    public void setViewWidth(int width) {
        if (activity == null || progressText == null) {
            return;
        }
        int w = DensityUtils.dp2px(activity.get(), width);
        ViewGroup.LayoutParams layoutParams = progressText.getLayoutParams();
        layoutParams.width = w;
        progressText.setLayoutParams(layoutParams);
    }

    public void setCancelable(boolean isCance) {
        this.mDialog.setCancelable(isCance);
    }

    public void showDialog(boolean isVisibleCloseImageView) {
        showDialog(isVisibleCloseImageView, true);
    }

    public void showDialog(boolean isVisibleCloseImageView, boolean showText) {
        if (mDialog == null || activity == null || activity.get().isFinishing() || activity.get().isDestroyed()) {
            return;
        }
        if (isVisibleCloseImageView) {
            closeDownloadDialog.setVisibility(View.VISIBLE);
        } else {
            closeDownloadDialog.setVisibility(View.GONE);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        if (showText) {
            String format = String.format(activity.get().getResources().getString(R.string.download_progress), 0) + "%";
            setProgress(format);
        }
        startAnimation();
    }

    private void startAnimation() {
        if (downLoadIcon == null) {
            return;
        }
        mAnimation = AnimationUtils.loadAnimation(activity.get(), R.anim.download_animation);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        mAnimation.setInterpolator(interpolator);
        downLoadIcon.startAnimation(mAnimation);
    }

    public void setCloseDialogListener(CloseDialogListener closeDialogListener) {
        this.closeDialogListener = closeDialogListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mAnimation != null) {
            mAnimation.cancel();
            mAnimation = null;
        }
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
        if (closeDialogListener != null) {
            closeDialogListener.closeDialog();
        }
    }

    public void dismissDialog() {
        if (mAnimation != null) {
            mAnimation.cancel();
            mAnimation = null;
        }
        if(downLoadIcon!=null){
            downLoadIcon.clearAnimation();
        }
        if (mDialog != null && mDialog.isShowing() && activity.get() != null && !activity.get().isDestroyed()) {
            progressText.setText("");
            mDialog.dismiss();
        }
    }

    public void destroy() {
        if(mDialog!=null){
            mDialog.setOnDismissListener(null);
            mDialog.setOnCancelListener(null);
            mDialog.setOnDismissListener(null);
        }
        if (closeDownloadDialog != null) {
            closeDownloadDialog.setOnClickListener(null);
        }
        if (mAnimation != null) {
            mAnimation.cancel();
            mAnimation = null;
        }
        if(downLoadIcon!=null){
            downLoadIcon.clearAnimation();
        }
        mAnimation = null;
        activity = null;
        mDialog = null;
        closeDialogListener = null;
        closeDownloadDialog = null;
        mOnDismissListener = null;
    }

    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

    public interface CloseDialogListener {
        void closeDialog();

        void cancelDialog();
    }

    public BackKeyListener mBackKeyListener;

    public void setBackKeyListener(BackKeyListener backKeyListener1) {
        this.mBackKeyListener = backKeyListener1;
    }

    public interface BackKeyListener {
        void onBack(Dialog mDialog);
    }
}
