package com.test.xcamera;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;


import com.framwork.base.BaseResponse;
import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.bean.ActivationCode;
import com.test.xcamera.bean.AppVersion;
import com.test.xcamera.bean.User;
import com.test.xcamera.download.DownloadListener;
import com.test.xcamera.download.OkHttpDownloadUtils;
import com.test.xcamera.utils.CameraToastUtil;

import java.io.File;

/**
 * creat by mz  2019.9.24
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_code;
    private TextView tv_code1;
    private TextView tv_code2;
    private TextView tv_code3;
    private TextView tv_code4;
    private TextView tv_code5;
    private EditText ed_code;
    private SeekBar seekBar;
    private TextView tv_text;
    private TextView tv_code6;
    private TextView tv_code7;
    private TextView tv_hardtext;
    private SeekBar hard_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//         String phone="13716303282";
//         VerificationCode(phone);
//        new ToastCompat().showToast(this,"你好啊小伙子", Toast.LENGTH_LONG);

        tv_code = findViewById(R.id.tv_code);
        tv_code1 = findViewById(R.id.tv_code1);
        ed_code = findViewById(R.id.ed_code);
        tv_code2 = findViewById(R.id.tv_code2);
        tv_code3 = findViewById(R.id.tv_code3);
        tv_code4 = findViewById(R.id.tv_code4);
        tv_code5 = findViewById(R.id.tv_code5);

        tv_code6 = findViewById(R.id.tv_code6);
        tv_code7 = findViewById(R.id.tv_code7);
        tv_hardtext = findViewById(R.id.tv_hardtext);

        hard_progress = findViewById(R.id.hard_progress);

        tv_code.setOnClickListener(this);
        tv_code1.setOnClickListener(this);
        tv_code2.setOnClickListener(this);
        tv_code3.setOnClickListener(this);
        tv_code4.setOnClickListener(this);
        tv_code5.setOnClickListener(this);

        tv_code6.setOnClickListener(this);
        tv_code7.setOnClickListener(this);


        seekBar = findViewById(R.id.progress);
        seekBar.setMax(100);

        tv_text = findViewById(R.id.tv_text);

    }


    public void VerificationCode(String phone) {
        ApiImpl.getInstance().VerificationCode(phone, new CallBack<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                CameraToastUtil.show("获取验证码成功", MainActivity.this);
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                System.out.println("onFailure " + e.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted ");
            }
        });
    }


    public void login(String phone, String code) {
        ApiImpl.getInstance().login(phone, code, new CallBack<User>() {
            @Override
            public void onSuccess(User user) {

            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });

    }

    String activationId;

    public void getActivationCode(String did, String nonce) {
        ApiImpl.getInstance().getActivationCode(did, nonce, new CallBack<ActivationCode>() {
            @Override
            public void onSuccess(ActivationCode activationCode) {
                if (activationCode.isSucess()) {
                    CameraToastUtil.show("获取激活码成功", MainActivity.this);
                    activationId = activationCode.getData().getActivationId();
                }
            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }


    public void PushActivationCode(String activationId) {
        ApiImpl.getInstance().PushActivationCode(activationId, new CallBack<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                if (baseResponse.isSucess()) {
                    CameraToastUtil.show("通知云端激活码写入成功", MainActivity.this);
                }
            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });

    }

    String version = "";

    public void PushAppVersion(String AppVsersion) {
        ApiImpl.getInstance().PushAppVersion(AppVsersion, 1, new CallBack<AppVersion>() {
            @Override
            public void onSuccess(AppVersion appVersion) {
                if (appVersion.isSucess()) {
                    CameraToastUtil.show("App版本申报成功", MainActivity.this);
                    version = appVersion.getData().get(0).getVersion();
                    CameraToastUtil.show(appVersion.getData().get(0).getDescription(), MainActivity.this);
                }
            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    String myhardwareVersion;

    public void PushHardWareVersion(final String hardwareVersion, String sn) {
        ApiImpl.getInstance().PushHardWareVersion(hardwareVersion, sn, new CallBack<AppVersion>() {
            @Override
            public void onSuccess(AppVersion appVersion) {
                if (appVersion.isSucess()) {
                    CameraToastUtil.show("固件版本申报成功", MainActivity.this);
                    myhardwareVersion = appVersion.getData().get(0).getVersion();
                } else {
                    CameraToastUtil.show(appVersion.getMessage(), MainActivity.this);
                }
            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        String phone = "13716303282";
        switch (v.getId()) {
            case R.id.tv_code:
                VerificationCode(phone);
                break;
            case R.id.tv_code1:
                String code = ed_code.getText().toString().trim();
                login(phone, code);
                break;
            case R.id.tv_code2:
                String did = "dhT+SPmoS9k2H6LjVELJ6ExuosU6xa1w5Op36r+Scj8=";
                getActivationCode(did, "");
                break;
            case R.id.tv_code3:
                PushActivationCode(activationId);
                break;
            case R.id.tv_code4:
                PushAppVersion("1.0.0.0");
                break;
            case R.id.tv_code5:
                path = mRootPath + File.separator + System.currentTimeMillis() + version + ".apk";
                downloadApk(version, path);
                break;
            case R.id.tv_code6:
                PushHardWareVersion("1.0.0.0", "");
                break;
            case R.id.tv_code7:
                hard_path = mRootPath + File.separator + "com.meetvr.aicamera";
//                downloadHadrWareFile(myhardwareVersion,hard_path);
                break;
        }
    }

    public static String hard_path;
    public static String path;
    private static File mRootPath = Environment.getExternalStorageDirectory();

    public void downloadApk(String version, String path) {
        OkHttpDownloadUtils.instance().startDownload(version, "", path, true, new DownloadListener() {
            @Override
            public void onStart() {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_text.setText("开始下载");
                    }
                });

            }

            @Override
            public void onProgress(final int progress) {

                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_text.setText("已下载" + progress + "%");
                        seekBar.setProgress(progress);
                    }
                });
            }

            @Override
            public void onFinish(File path) {

                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_text.setText("下载完成");
                    }
                });
            }

            @Override
            public void onFail(String errorInfo) {

            }
        });
    }

    //    public void    downloadHadrWareFile(String version,String  path,String sn){
//        DownloadUtil.downHardWareFile(version, path, new DownloadListener() {
//            @Override
//            public void onStart() {
//                mhandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv_hardtext.setText("开始下载");
//                    }
//                });
//            }
//
//            @Override
//            public void onProgress(final int progress) {
//
//                mhandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv_hardtext.setText("已下载"+progress+"%");
//                        hard_progress.setProgress(progress);
//                    }
//                });
//            }
//
//            @Override
//            public void onFinish(File path) {
//
//                mhandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv_hardtext.setText("下载完成");
//                    }
//                });
//            }
//
//            @Override
//            public void onFail(String errorInfo) {
//            }
//        });
//    }
    private Handler mhandler = new Handler();

}
