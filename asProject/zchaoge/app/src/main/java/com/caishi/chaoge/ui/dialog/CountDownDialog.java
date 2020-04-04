package com.caishi.chaoge.ui.dialog;

import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.caishi.chaoge.R;
import com.caishi.chaoge.ui.activity.MainActivity;
import com.caishi.chaoge.ui.activity.SettingActivity;
import com.caishi.chaoge.ui.widget.dialog.DialogUtil;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.ui.widget.dialog.SYDialog;
import com.caishi.chaoge.utils.ConstantUtils;
import com.caishi.chaoge.utils.SPUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

public class CountDownDialog {

    private SYDialog loadDialog;
    private SYDialog beingDialog;
    private LottieAnimationView lav_countDown_anim;

    public static CountDownDialog newInstance() {
        return new CountDownDialog();
    }


    /**
     * 等待dialog
     */
    public void showCountDownDialog(final Context context) {
        loadDialog = new SYDialog.Builder(context)
                .setDialogView(R.layout.dialog_count_down)
                .setScreenWidthP(0.3f)
                .setScreenHeightP(0.3f)
                .setWindowBackgroundP(0.6f)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setCancelableOutSide(false)
//                .setAnimStyle(R.style.AnimUp)
                .setBuildChildListener(new IDialog.OnBuildListener() {

                    @Override
                    public void onBuildChildView(final IDialog dialog, View view, int layoutRes, FragmentManager fragmentManager) {
                        lav_countDown_anim = view.findViewById(R.id.lav_countDown_anim);
                        lav_countDown_anim.playAnimation();
                    }
                }).show();
    }

    /**
     * 正在识别dialog
     */
    public void showBeingDiscernDialog(final Context context) {
        beingDialog = new SYDialog.Builder(context)
                .setDialogView(R.layout.dialog_being_discern)
                .setScreenWidthP(0.6f)
                .setScreenHeightP(0.3f)
                .setWindowBackgroundP(0.6f)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .setCancelableOutSide(false)
//                .setAnimStyle(R.style.AnimUp)
                .setBuildChildListener(new IDialog.OnBuildListener() {

                    @Override
                    public void onBuildChildView(final IDialog dialog, View view, int layoutRes, FragmentManager fragmentManager) {
                        ImageView img_beingDiscern_gif = view.findViewById(R.id.img_beingDiscern_gif);
                        Glide.with(context).load(R.drawable.gif_being_discern).into(img_beingDiscern_gif);
                    }
                }).show();
    }

    /**
     * 关闭弹窗 注意dialog=null;防止内存泄漏
     */
    public void dismissDialog() {
        if (lav_countDown_anim != null)
            lav_countDown_anim.cancelAnimation();
        if (loadDialog != null) {
            loadDialog.dismiss();
            loadDialog = null;
        }
        if (beingDialog != null) {
            beingDialog.dismiss();
            beingDialog = null;
        }
    }


}
