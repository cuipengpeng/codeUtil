package com.caishi.chaoge.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.MineDataBean;
import com.caishi.chaoge.ui.dialog.HomeDialog;
import com.caishi.chaoge.ui.widget.dialog.DialogUtil;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.utils.ConstantUtils;
import com.caishi.chaoge.utils.SPUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseUILocalDataActivity {
    @BindView(R.id.tv_setting_cache)
    TextView tv_setting_cache;

    private TextView tv_baseTitle_title;
    private MineDataBean result;
    private final int DELETE_CACHE_FILES = 101;
    private final int CALCULATE_CACHE_FILES_SIZE = 102;
    private long allFileSize = 0l;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELETE_CACHE_FILES:
                    tv_setting_cache.setText("0MB");
                    break;
                case CALCULATE_CACHE_FILES_SIZE:
                    int fileSize = (int) (((long) msg.obj) / 1024 / 1024);
                    tv_setting_cache.setText(fileSize + "MB");
                    break;
            }
        }
    };


    @Override
    protected String getPageTitle() {
        return "设置";
    }

    @Override
    public int getSubLayoutId() {
        return R.layout.activity_seting;
    }

    @Override
    public void initPageData() {
        result = SPUtils.readThirdAccountBind(this, SPUtils.readCurrentLoginUserInfo(this).userId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                long fileSize = getAllFilesSize(new File(Environment.getExternalStorageDirectory().getPath() + "/" + ConstantUtils.FILE_BASE_PATH));
                Message msg = handler.obtainMessage();
                msg.what = CALCULATE_CACHE_FILES_SIZE;
                msg.obj = fileSize;
                handler.sendMessage(msg);
            }
        }).start();
    }

    @OnClick({R.id.btn_setting_quit, R.id.rl_setting_userInfo,
            R.id.rl_setting_invite, R.id.rl_setting_safety, R.id.rl_setting_protocol,
            R.id.rl_setting_cache, R.id.rl_setting_opinion, R.id.rl_setting_about})
    public void onClickView(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_setting_quit://退出登录
                showHintDialog(2);
                break;
            case R.id.rl_setting_userInfo://个人信息
                Bundle bundle = new Bundle();
                if (result != null) {
                    bundle.putSerializable("MineDataBean", result);
                }
                intent.setClass(this, UserInfoActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.rl_setting_invite://邀请好友
                HomeDialog.newInstance().showShareDialog(mContext, 2,null, null, null, null,
                        null, null, null, null, -1);
                break;
            case R.id.rl_setting_safety://账号安全
                intent.setClass(this, AccountAndSecurityActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_setting_protocol://用户协议
                intent.setClass(this, WebviewActivity.class);
                intent.putExtra(WebviewActivity.KEY_OF_URL, LoginActivity.USER_PROTOCAL_HTML);
                startActivity(intent);
                break;
            case R.id.rl_setting_cache://清除缓存
                showHintDialog(1);
                break;
            case R.id.rl_setting_opinion://意见反馈
                intent.setClass(this, SuggestionFeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_setting_about://关于
                intent.setClass(this, AboutActivity.class);
                startActivity(intent);
                break;
        }

    }


    public void showHintDialog(final int flag) {
        String hint = ((flag == 1) ? "确定清除缓存？" : "确定退出登录？");
        DialogUtil.createDefaultDialog(this, "提示", hint,
                "确定", new IDialog.OnClickListener() {
                    @Override
                    public void onClick(IDialog dialog) {
                        if (flag == 2) {
                            SPUtils.clearLoginInfo(SettingActivity.this);
                            MobclickAgent.onProfileSignOff();//退出用户统计
                            MainActivity.open(SettingActivity.this, MainActivity.SHOW_HOME_FRAGMENT);
                            finish();
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    deleteAllFiles(new File(Environment.getExternalStorageDirectory().getPath() + "/" + ConstantUtils.FILE_BASE_PATH));
                                    Message msg = handler.obtainMessage();
                                    msg.what = DELETE_CACHE_FILES;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        }

                        dialog.dismiss();
                    }
                },
                "取消", new IDialog.OnClickListener() {
                    @Override
                    public void onClick(IDialog dialog) {
                        dialog.dismiss();
                    }
                });


    }

    public static void deleteAllFiles(File f) {
        if (f.exists()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File file : files)
                    if (file.isDirectory()) {
                        deleteAllFiles(file);
                        file.delete(); //删除目录下的所有文件后，该目录变成了空目录，可直接删除
                    } else if (file.isFile()) {
                        file.delete();
                    }
            }
            f.delete(); //删除最外层的目录
        }
    }

    private long getAllFilesSize(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        getAllFilesSize(files[i]);
                    } else {
                        allFileSize += files[i].length();
                    }
                }
            }
        }
        return allFileSize;
    }
}
