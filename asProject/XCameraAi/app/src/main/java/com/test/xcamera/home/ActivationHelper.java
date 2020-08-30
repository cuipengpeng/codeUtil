package com.test.xcamera.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.framwork.base.BaseResponse;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.ActivationCode;
import com.test.xcamera.bean.AppVersion;
import com.test.xcamera.home.activation.ActivatingViewInterface;
import com.test.xcamera.home.activation.ActivationFailActivity;
import com.test.xcamera.home.activation.ActivationSucessfulActivity;
import com.test.xcamera.home.activation.IllegalActivationActivity;
import com.test.xcamera.home.activation.StartActivationActivty;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoGetActivateStatuCallback;
import com.test.xcamera.mointerface.MoGetDidInfoCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.upgrade.BaseVersion;
import com.test.xcamera.upgrade.VersionInfo;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.view.DeleteDialog;
import com.test.xcamera.view.basedialog.dialog.DialogAppUpData;

/**
 * Author: mz
 * Time:  2019/10/15
 * 激活设备helper类
 */
public class ActivationHelper {
    private static String TAG = "ActivationHelper";

    public static String PKG_TYPE = "PkgType";
    public static String CLOUD_APP_VERSION_SPKEY = "cloud_app_version";
    public Context mcontext;
    private String flag = "";// 判断从那个页面跳转过来
    private DeleteDialog deleteDialog;
    private Handler handler;
    private DialogAppUpData appUpDataDialog;


    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public ActivationHelper(Context mcontext) {
        this.mcontext = mcontext;
    }

    ActivatingViewInterface mViewInterface;

    public ActivationHelper(Context mcontext, String flag, ActivatingViewInterface viewInterface) {
        this.mcontext = mcontext;
        this.flag = flag;
        this.mViewInterface = viewInterface;
    }

    /**
     * 获取设计是否解禁 的状态   status    0  未解禁   1 解禁中    2  解禁完
     */
    public void getActivateStatue() {
        ConnectionManager.getInstance().getActivateStatue(new MoGetActivateStatuCallback() {
            @Override
            public void onSuccess(final int status, final String activeID) {
                LoggerUtils.i(TAG, "获取固件状态  " + "status=" + status + "  activeID=" + activeID);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        StatueLogic(status, activeID);
                    }
                });

            }

            @Override
            public void onFailed() {
                LoggerUtils.i(TAG, "获取固件状态失败");
            }
        });

    }

    /**
     * 获取did 信息
     */
    public void getDidInfo() {
        ConnectionManager.getInstance().getDidInfo(new MoGetDidInfoCallback() {
            @Override
            public void onSuccess(String did, String nonce) {
                //请求获取激活码
                getActivationCode(did, nonce);

                LoggerUtils.i(TAG, "获取did信息  " + "did=" + did + "  nonce=" + nonce);
            }

            @Override
            public void onFailed() {
                LoggerUtils.i(TAG, "获取did信息失败");
            }
        });
    }

    /**
     * 获取激活码
     *
     * @param did
     * @param nonce
     */
    public void getActivationCode(String did, String nonce) {
        ApiImpl.getInstance().getActivationCode(did, nonce, new CallBack<ActivationCode>() {
            @Override
            public void onSuccess(ActivationCode activation) {
                if (activation.isSucess()) {
                    CameraToastUtil.show("获取激活码成功", mcontext);
                    ActivationCode.ActivationCodeDetail codeDetail = activation.getData();
                    String activationCode = codeDetail.getActivationCode();//激活码
                    String activationId = codeDetail.getActivationId();  //激活id

                    LoggerUtils.i(TAG, "获取后台激活码信息  " + "activationCode=" + activationCode + "  activationId=" + activationId);
                    // 请求写入激活码
                    activateDevice(activationCode, activationId);
                } else {
                    //非法设备 跳到激活失败的某个页面
                    CameraToastUtil.show(activation.getMessage(), AiCameraApplication.mApplication);
                    if (activation.getCode() == 20604) {   //说明此设备已经激活成功
                        startAct(ActivationSucessfulActivity.class);
                    } else if (activation.getCode() == 20605) {
                        startAct(IllegalActivationActivity.class);//
                    }
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

    /**
     * 请求写入设备激活码
     */
    public void activateDevice(String activationCode, final String activationId) {
        ConnectionManager.getInstance().activateDevice(activationCode, activationId, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                LoggerUtils.i(TAG, "请求设备写入激活码  ");
                //写入成功后 ，通知云端写入成功
                PushActivationCode(activationId);
                if ("ActivatingActivity".equals(flag)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mViewInterface.getProgressBar().setProgress(30);
                        }
                    });

                }
            }

            @Override
            public void onFailed() {
                LoggerUtils.i(TAG, "请求设备写入激活码失败");
            }
        });
    }

    /**
     * 通知云端激活码写入成功
     *
     * @param activationId
     */
    public void PushActivationCode(String activationId) {
        ApiImpl.getInstance().PushActivationCode(activationId, new CallBack<BaseResponse>() {
            @Override
            public void onSuccess(final BaseResponse baseResponse) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (baseResponse.isSucess()) {
                            CameraToastUtil.show(" 云端激活码写入成功", mcontext);
                            LoggerUtils.i(TAG, "通知 云端激活码写入成功  ");
                            if ("ActivatingActivity".equals(flag)) {
                                mViewInterface.getWebImage().setImageResource(R.mipmap.icon_checked);
                                mViewInterface.getProgressBar().setProgress(75);
                            }
                            //通知相机修改状态
                            modifyStatus(1);
                        } else {
                            CameraToastUtil.show(baseResponse.getMessage(), AiCameraApplication.mApplication);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    /**
     * 请求修改激活状态
     *
     * @param status
     */
    public void modifyStatus(int status) {
        ConnectionManager.getInstance().modifyStatus(status, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                LoggerUtils.i(TAG, "修改激活码状态成功  ");

//                if("ActivatingActivity".equals(flag)){

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        CameraToastUtil.show("设备解禁成功", mcontext);
                        mViewInterface.getIvHardImage().setImageResource(R.mipmap.icon_checked);
                        mViewInterface.getProgressBar().setProgress(90);
                        try {
                            Thread.sleep(1000);
                            mViewInterface.getProgressBar().setProgress(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

//                }
                //激活成功页面
                startAct(ActivationSucessfulActivity.class);
            }

            @Override
            public void onFailed() {
                LoggerUtils.i(TAG, "修改激活码状态失败");
                startAct(ActivationFailActivity.class);
            }
        });
    }

    /**
     * 状态逻辑处理
     *
     * @param status
     * @param activeID
     */
    public void StatueLogic(int status, String activeID) {

        switch (status) {
            case 0:
                if ("ActivatingActivity".equals(flag)) {
                    getDidInfo();
                } else {
                    showDialog(mcontext, status, activeID);
                }
                break;
            case 1:
                if ("ActivatingActivity".equals(flag)) {
                    PushActivationCode(activeID);  //直接上报给服务器
                } else {
                    showDialog(mcontext, status, activeID);
//                    startAct(status,activeID,StartActivationActivty.class);
                }
                break;
            case 2:
                //status 为2 时说明是解禁成功不需要做任何操作  nothing  todo
                break;
        }
    }

    public void startAct(int status, String activeId, Class clazz) {
        Intent intent = new Intent(mcontext, clazz);
        intent.putExtra("status", status);
        intent.putExtra("activeId", activeId);
        mcontext.startActivity(intent);
    }

    public void startAct(Class clazz) {
        Intent intent = new Intent(mcontext, clazz);
        mcontext.startActivity(intent);
    }

    public void showDialog(Context context, final int status, final String activeID) {
        if (deleteDialog == null) {
            deleteDialog = new DeleteDialog((Activity) context);
            deleteDialog.showTitleAndContent("设备解禁", "暂未解禁的设备不能用哦", View.VISIBLE, View.VISIBLE);
        }
        deleteDialog.showDialog(new DeleteDialog.SureOnClick() {
            @Override
            public void sure_button() {
                startAct(status, activeID, StartActivationActivty.class);
            }
        });
    }

    public void hideDlg() {
        if (deleteDialog != null) {
            deleteDialog.hideDlg();
        }
    }

    /**
     * check app version
     *
     * @param AppVsersion
     */
    public void checkAppVersion(String AppVsersion) {
        ApiImpl.getInstance().PushAppVersion(AppVsersion, 1, new CallBack<AppVersion>() {
            @Override
            public void onSuccess(AppVersion appVersion) {
                Activity activity = ((MOBaseActivity) mcontext);
                if (!FileUtils.existSDCard() || activity == null) {
                    CameraToastUtil.show(mcontext.getResources().getString(R.string.sdka_error), mcontext);
                    return;
                }
                if (appVersion.isSucess()) {
                    LoggerUtils.i(TAG, AppVsersion + "  onSuccess: check app version " + appVersion.toString());
                    if (appVersion.getData().size() > 0) {
                        AppVersion.AppVersionDetail detail = appVersion.getData().get(0);
                        int pkgType = detail.getPkgType();
                        SPUtils.put(mcontext, PKG_TYPE, pkgType);
                        if (appUpDataDialog != null) {
                            appUpDataDialog = null;
                        }
                        if (detail != null && pkgType != 1) {
                            String spSaveCloudAppVersion = (String) SPUtils.get(mcontext, CLOUD_APP_VERSION_SPKEY, "");

                            appUpDataDialog = new DialogAppUpData(activity, initData(BaseVersion.DEFAULT_STYLE, detail));

                            if (pkgType == 2) {
                                appUpDataDialog.settemporaryNoUpdataText(mcontext.getResources().getString(R.string.noUpdata));
                                appUpDataDialog.setDialogTouchOutside(true);
                                if (!spSaveCloudAppVersion.equals(detail.getVersion())) {
                                    showDialog();
                                }
                            } else {
                                appUpDataDialog.settemporaryNoUpdataText(mcontext.getResources().getString(R.string.out_app));
                                appUpDataDialog.setDialogTouchOutside(false);
                                showDialog();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable e) {
                LoggerUtils.i(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    private void showDialog() {
        boolean isShow = appUpDataDialog.isShow();
        if (!isShow) {
            appUpDataDialog.showDialog();
        }
    }

    private VersionInfo initData(int dialogStyle, AppVersion.AppVersionDetail appVersionDetail) {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setContent(appVersionDetail.getDescription());
        versionInfo.setTitle(mcontext.getResources().getString(R.string.version_refresh));
        versionInfo.setMustup(false);
        versionInfo.setVersion(appVersionDetail.getVersion());
        versionInfo.setFilepath(Constants.apk_down_filedetail);
        versionInfo.setViewStyle(dialogStyle);
        return versionInfo;
    }
}
