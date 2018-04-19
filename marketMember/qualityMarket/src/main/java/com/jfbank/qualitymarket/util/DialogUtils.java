package com.jfbank.qualitymarket.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.flyco.animation.ZoomEnter.ZoomInEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.listener.DialogListener;

import java.util.ArrayList;

/**
 * 功能：对话框工具类<br>
 * 作者：赵海<br>
 * 时间： 2017/4/21 0021<br>.
 * 版本：1.2.0
 */

public class DialogUtils {
    /**
     * @param context         上下文
     * @param title           标题
     * @param msg             提示信息
     * @param cancelBtn       取消
     * @param sureBtn         确认
     * @param onClickListener 点击事件
     * @return
     */
    public static MaterialDialog showTwoBtnDialog(Context context, String title, String msg, String cancelBtn, String sureBtn, final DialogListener.DialogClickLisenter onClickListener) {
        return showTwoBtnDialog(context, true, title, msg, cancelBtn, sureBtn, onClickListener);
    }

    /**
     * @param context         上下文
     * @param title           标题
     * @param msg             提示信息
     * @param cancelBtn       取消
     * @param sureBtn         确认
     * @param onClickListener 点击事件
     * @return
     */
    public static MaterialDialog showTwoBtnDialog(Context context, boolean cancelAble, String title, String msg, String cancelBtn, String sureBtn, final DialogListener.DialogClickLisenter onClickListener) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.setCancelable(cancelAble);
        ZoomInEnter mBasIn = new ZoomInEnter();
        ZoomOutExit mBasOut = new ZoomOutExit();
        boolean isTitle = !TextUtils.isEmpty(title);
        dialog.content(
                msg)//
                .title(isTitle ? title : "")
                .isTitleShow(isTitle)
                .btnText((TextUtils.isEmpty(cancelBtn) ? "取消" : cancelBtn), (TextUtils.isEmpty(sureBtn) ? "确定" : sureBtn))//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        onClickListener.onDialogClick(DialogListener.DialogClickLisenter.CLICK_CANCEL);
                    }
                },
                new OnBtnClickL() {//right btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        onClickListener.onDialogClick(DialogListener.DialogClickLisenter.CLICK_SURE);
                    }
                });
        return dialog;
    }

    /**
     * 显示底部对话框
     *
     * @param mContext
     * @param title
     * @param stringItems
     * @return
     */
    public static ActionSheetDialog showSheetDialog(Context mContext, String title, String[] stringItems, final DialogListener.DialogItemLisenter dialogItemLisenter) {
        final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
        boolean isTitleShow = !TextUtils.isEmpty(title);
        dialog.isTitleShow(isTitleShow);
        if (isTitleShow)
            dialog.title(title);
        dialog.titleTextSize_SP(14.5f)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                if (dialogItemLisenter != null)
                    dialogItemLisenter.onDialogClick(position);
            }
        });
        return dialog;
    }

    /**
     * 显示单个对话确认按钮
     *
     * @param context         上下文
     * @param cancelAble      是否可取消
     * @param title           是否显示标题
     * @param msg             内容提示
     * @param sureBtn         按钮名称
     * @param onClickListener 监听
     * @return
     */
    public static MaterialDialog showOneBtnDialog(Context context, boolean cancelAble, String title, String msg, String sureBtn, final DialogListener.DialogClickLisenter onClickListener) {
        final MaterialDialog oneDialog = new MaterialDialog(context);
        oneDialog.setCancelable(cancelAble);
        oneDialog.setCanceledOnTouchOutside(cancelAble);
        ZoomInEnter mBasIn = new ZoomInEnter();
        ZoomOutExit mBasOut = new ZoomOutExit();
        boolean isTitle = !TextUtils.isEmpty(title);
        oneDialog.content(
                msg)//
                .btnNum(1)
                .title(isTitle ? title : "")
                .isTitleShow(isTitle)
                .btnText((TextUtils.isEmpty(sureBtn) ? "确定" : sureBtn))//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        oneDialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        oneDialog.dismiss();
                        onClickListener.onDialogClick(DialogListener.DialogClickLisenter.CLICK_SURE);
                    }
                });
        return oneDialog;
    }

    /**
     * 列表单选对话框
     *
     * @param mContext
     * @param title              标题
     * @param mMenuItems         列表
     * @param dialogItemLisenter 监听器
     */

    public static void showListDialog(Context mContext, String title, ArrayList<DialogMenuItem> mMenuItems, final DialogListener.DialogItemLisenter dialogItemLisenter) {
        final NormalListDialog dialog = new NormalListDialog(mContext, mMenuItems);
        ZoomInEnter mBasIn = new ZoomInEnter();
        ZoomOutExit mBasOut = new ZoomOutExit();
        dialog.title(title)//
                .titleTextSize_SP(16)//
                .titleBgColor(mContext.getResources().getColor(R.color.c_F7F7F7))//
                .titleTextColor(mContext.getResources().getColor(R.color.themeRed))
                .itemPressColor(mContext.getResources().getColor(R.color.c_F7F7F7))//
                .itemTextColor(mContext.getResources().getColor(R.color.c_666666))//
                .itemTextSize(14)//
                .cornerRadius(4)//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .dividerColor(mContext.getResources().getColor(R.color.c_F7F7F7))
                .widthScale(0.8f)//
                .heightScale(0.7f);
//        Window dialogWindow = dialog.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        if (lp != null) {
//            lp.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//            lp.gravity = Gravity.CENTER;
//        }
//        dialogWindow.setAttributes(lp);
        dialog.show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogItemLisenter.onDialogClick(position);
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示对话框
     *
     * @param context         上下文
     * @param title           标题
     * @param msg             提示信息
     * @param onClickListener 点击事件
     * @return
     */
    public static MaterialDialog showTwoBtnDialog(Context context, String title, String msg, final DialogListener.DialogClickLisenter onClickListener) {
        return showTwoBtnDialog(context, title, msg, null, null, onClickListener);
    }
}
