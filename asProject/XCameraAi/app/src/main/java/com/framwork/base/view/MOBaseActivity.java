package com.framwork.base.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.cameraclip.CompleteVideoLandScapeActivity;
import com.test.xcamera.home.HomeActivity;
import com.test.xcamera.ota.UploadHardWareActivity;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.DataManager;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.view.DeleteDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by smz on 2019/7/2.
 */

public abstract class MOBaseActivity extends FragmentActivity implements AccessoryManager.ConnectStateListener {
    private static final String TAG = "MOBaseActivity";

    public Context mContext;
    protected DeleteDialog deleteDialog;
    private Unbinder mUnBinder;
    public Gson gson = new Gson();

    public boolean isTitleStatus() {
        return titleStatus;
    }

    /**
     * 设置是否显示状态栏
     *
     * @param titleStatus
     */
    public void setTitleStatus(boolean titleStatus) {
        this.titleStatus = titleStatus;
    }

    private boolean titleStatus;

    protected void goneTiele() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitleStatus(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (this instanceof HomeActivity) {

            } else if (this instanceof UploadHardWareActivity) {
                this.getWindow().setStatusBarColor(Color.parseColor("#FF9D48"));
            }
            if (this instanceof CompleteVideoLandScapeActivity) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                this.getWindow().setStatusBarColor(Color.parseColor("#050505"));
            }
        }

        super.onCreate(savedInstanceState);
        init();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        init();
    }

    private void init() {
        mContext = this;
        setContentView(initView());
        if (!titleStatus) {
            noStatusBar();
        }
        //绑定初始化ButterKnife
        ButterKnife.bind(this);

        mUnBinder = ButterKnife.bind(this);
        if (deleteDialog == null) {
            deleteDialog = new DeleteDialog(this);
        }

//        initReceiver();
        initClick();
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        AccessoryManager.getInstance().setConnectStateListener(this, "MOBaseActivity");
    }

    public abstract int initView();

    public void initClick() {

    }

    public abstract void initData();

    public void show(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public void startAct(Class clazz) {
        Intent intent = new Intent(mContext, clazz);
        mContext.startActivity(intent);
    }


    public String getResourceToString(int id) {
        return getResources().getString(id);
    }

    /**
     * 沉浸式   背景是图片的时候
     */
    protected void setStatusBarImag() {
        //这里做了两件事情，1.使状态栏透明并使contentView填充到状态栏 2.预留出状态栏的位置，防止界面上的控件离顶部靠的太近。这样就可以实现开头说的第二种情况的沉浸式状态栏了
        StatusBarUtil.setTransparent(this);
    }

    /**
     * @param mColor
     * @param statusBarAlpha 透明度   0-255之间
     */
    protected void setStatusBarColor(int mColor, int statusBarAlpha) {
        StatusBarUtil.setColor(this, mColor, statusBarAlpha);
    }


    /**
     * 状态栏 是否显示
     */
    public void noStatusBar() {
        if (isTitleStatus()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示状态栏
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnBinder != null)
            mUnBinder.unbind();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void connectedUSB() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Constants.isConnect = true;
//                ToastUtil.showToast(MOBaseActivity.this, "usb connected");
            }
        });
    }

    @Override
    public void disconnectedUSB() {
        ConnectionManager.getInstance().setAlbumCount(0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Constants.isConnect = false;
//                ToastUtil.showToast(MOBaseActivity.this, "usb disconnected");
            }
        });
        DataManager.setConnected(false);
    }
}
