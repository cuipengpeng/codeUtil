package com.test.xcamera.view.basedialog.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.test.xcamera.R;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/4/14
 * e-mail zhouxuecheng1991@163.com
 */

public class DialogVersionUpdataTips implements View.OnClickListener {
    private Activity activity;
    private Dialog mDialog;
    private View view;
    private Button checkDetailBt;
    private TextView noDetail;
    private CheckDetailListener checkDetailListener;
    private TextView update_dialog_info;
    private TextView versionCode;

    public DialogVersionUpdataTips(Activity activity) {
        this.activity = activity;
        initDialogView();
        initView();
    }

    public void showDialog(CheckDetailListener checkDetailListener) {
        this.checkDetailListener = checkDetailListener;
        if (mDialog != null && !mDialog.isShowing() && !activity.isDestroyed() && !activity.isFinishing()) {
            mDialog.show();
        }
    }


    public TextView getUpdate_dialog_info() {
        return update_dialog_info;
    }

    public TextView getVersionCode() {
        return versionCode;
    }

    private void initView() {
        checkDetailBt = view.findViewById(R.id.update_dialog_confirm);
        checkDetailBt.setOnClickListener(this);

        noDetail = view.findViewById(R.id.temporaryNoUpdata);
        noDetail.setOnClickListener(this);
        update_dialog_info = view.findViewById(R.id.update_dialog_info);

        versionCode = view.findViewById(R.id.update_dialog_title);
    }

    /**
     * 点击dialog外部是否影藏dialog
     *
     * @param isHide
     */
    public void setDialogTouchOutside(boolean isHide) {
        mDialog.setCanceledOnTouchOutside(isHide);
        mDialog.setCancelable(isHide);
        noDetail.setVisibility(isHide ? View.VISIBLE : View.GONE);
    }

    private void initDialogView() {
        view = View.inflate(activity, R.layout.dialog_version_updata_tips_layout, null);
        mDialog = new Dialog(activity);
        mDialog.setContentView(view);

        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        mDialog.getWindow().setGravity(Gravity.TOP);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置横向铺满全屏
        mDialog.getWindow().setLayout(d.getWidth(), WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.temporaryNoUpdata) {
            if (checkDetailListener != null) {
                checkDetailListener.isCheckDetail(false);
            }
            if (mDialog != null) {
                mDialog.dismiss();
            }
        } else if (vid == R.id.update_dialog_confirm) {
            if (checkDetailListener != null) {
                checkDetailListener.isCheckDetail(true);
            }
            if (mDialog != null) {
                mDialog.dismiss();
            }
        }
    }

    public boolean isShow() {
        if (mDialog != null)
            return mDialog.isShowing();
        return false;
    }

    public interface CheckDetailListener {
        public void isCheckDetail(boolean isCheckDetail);
    }

    public void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void destroyDialog() {
        checkDetailListener = null;
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog.cancel();
            mDialog = null;
            activity = null;
        }
    }
}