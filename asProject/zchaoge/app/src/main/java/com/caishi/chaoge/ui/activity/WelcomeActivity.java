package com.caishi.chaoge.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.bean.LoginBean;
import com.caishi.chaoge.http.Account;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.http.UserAgent;
import com.caishi.chaoge.utils.FileUtils;
import com.caishi.chaoge.utils.PathUtils;
import com.caishi.chaoge.utils.SPUtils;
import com.google.gson.Gson;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.rd.vecore.RdVECore;
import com.rd.vecore.exception.InitializeException;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.caishi.chaoge.base.BaseApplication.APP_KEY;
import static com.caishi.chaoge.base.BaseApplication.APP_SECRET;
import static com.caishi.chaoge.utils.ConstantUtils.ISFIRSTLAUNCH;


/**
 * 欢迎启动页面
 */
public class WelcomeActivity extends BaseActivity{
    public static final String TAG = WelcomeActivity.class.getName();
    public static String strCustomPath;

    @Override
    public void initBundle(Bundle bundle) {
    }

    @Override
    public int bindLayout() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        return R.layout.activity_welcome;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        requestPermission();
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        requestPermission();

        if (SPUtils.isLogin(this)) {
            //自动登录
            autoLogin();
        } else {
            if (!TextUtils.isEmpty(Account.sDeviceId)) {
                //虚拟登录
                HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.VIRTUAL_LOGIN, new HashMap<String, String>(), new HttpRequest.HttpResponseCallBank() {
                    @Override
                    public void onSuccess(String response) {
                    }

                    @Override
                    public void onFailure(String t) {

                    }
                });
            }
        }
    }

    private void requestPermission() {
        XXPermissions.with(this)
                .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_PHONE_STATE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            UserAgent.init(mContext);
                            initRDSDK();
                            Account.sUserToken = SPUtils.readCurrentLoginUserInfo(mContext).credential;
                            Account.sUserId = SPUtils.readCurrentLoginUserInfo(mContext).userId;
                            startMainActivity();
                        } else {
                            showToast("部分权限未正常授予");
                            XXPermissions.gotoPermissionSettings(WelcomeActivity.this);
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        showToast("被永久拒绝授权，请手动授予权限");
                        XXPermissions.gotoPermissionSettings(WelcomeActivity.this);
                    }
                });

    }


    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void widgetClick(View v) {

    }


    /**
     * 启动主页面
     */
    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(WelcomeActivity.this, SelectMusicActivity.class);
//                startActivity(intent);

//                ScenarioActivity.open(WelcomeActivity.this, "1a8451626d52d109b50f8ab58e50c6dc", 101);
                ISFIRSTLAUNCH = true;
                MainActivity.open(WelcomeActivity.this, MainActivity.SHOW_HOME_FRAGMENT);
                finish();
            }
        }, 1500);

    }

    private void initRDSDK() {
        // 自定义根目录，如果为空则默认为/sdcard/Android/data/com.caishi.chaoge/files/chaoge
        strCustomPath = FileUtils.getExternalFilesDirEx(this, "chaoge").getAbsolutePath();
        // sdk初始化
        try {
            PathUtils.initialize(mContext, TextUtils.isEmpty(strCustomPath) ? null : new File(strCustomPath));
            RdVECore.initialize(this, strCustomPath, APP_KEY, APP_SECRET, true);
        } catch (InitializeException e) {
            e.printStackTrace();
        }
    }


    public void autoLogin() {
        HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.AUTO_LOGIN, new HashMap<String, String>(), new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                LoginBean loginBean = gson.fromJson(response, LoginBean.class);
                Account.sUserToken = loginBean.credential;
                Account.sUserId = loginBean.userId;
                SPUtils.writeCurrentLoginUserInfo(mContext, loginBean);
            }

            @Override
            public void onFailure(String t) {
                Account.sUserId = "";
                Account.sUserToken = "";
            }
        });
    }

}
