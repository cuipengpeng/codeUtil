package com.test.xcamera.accrssory;

import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.MoErrorData;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoSyncCameraInfo;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoCurModeCallback;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.mointerface.MoRequestValueCallback;
import com.test.xcamera.mointerface.MoSyncCameraInfoCallback;
import com.test.xcamera.mointerface.MoTakeVideoCallback;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.StringUtil;
import com.moxiang.common.logging.Logcat;

/**
 * Created by smz on 2020/4/23.
 * <p>
 * 连接相机时 同步相机的状态
 * <p>
 * 长曝光在录制中时 需要在ConnectionManager.getInstance().setErrorI中调用syncLE
 * 否则流程无法走完
 */

public class UsbDispose {
    public static final boolean debug = true;

    public SyncStatus mSyncStatus;
    private int mFpvModeTry = 0;
    private boolean mProgressing = false;

    public void setSyncStatus(SyncStatus syncStatus) {
        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("UsbDispose setSyncStatus==>" + syncStatus).out();
        this.mSyncStatus = syncStatus;
    }

    public interface SyncStatus {
        /**
         * 同步成功
         */
        void onSyncSucc();

        /**
         * 同步失败
         */
        void onSyncFailed();

        /**
         * 开始同步
         */
        void onSyncStart();

        /**
         * 正在同步
         */
        default void onProgress() {
        }
    }

    public void dispose(SyncStatus syncStatus) {
        this.mSyncStatus = syncStatus;
        dispose();
    }

    /**
     * 执行同步流程
     */
    public void dispose() {
        mProgressing = false;

        if (mSyncStatus != null)
            mSyncStatus.onSyncStart();

        if (!debug)
            syncFpvMode();
        else
            ConnectionManager.getInstance().getCurMode(new MoCurModeCallback() {
                @Override
                public void success(int mode) {
                    //非抖音模式 升级模式 低功耗模式 则同步fpv
                    if (mode != 8 && mode != 11 && mode != 12)
                        syncFpvMode();
                    else if (mSyncStatus != null)
                        mSyncStatus.onSyncSucc();
                }

                @Override
                public void onFailed() {
                    if (mSyncStatus != null)
                        mSyncStatus.onSyncFailed();
                }
            });
    }

    public void setError() {
        ConnectionManager.getInstance().setErrorI(new MoErrorCallback() {
            @Override
            public void onError(MoErrorData data) {
                syncLE(data.event);
            }
        });
    }

    /**
     * 同步长曝光参数
     * 需要在ConnectionManager.getInstance().setErrorI中执行
     */
    public void syncLE(int event) {
        if (event == MoErrorCallback.PHOTO_FINISH)
            if (mProgressing) {
                mProgressing = false;
                syncTime();
                enableAppMode(0);
            }
    }

    private void syncFpvMode() {
        ConnectionManager.getInstance().syncCameraInfo(new MoSyncCameraInfoCallback() {
            @Override
            public void onSuccess(MoSyncCameraInfo info) {
                Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("syncCameraInfo  mode==>" + info.toString()).out();
                //其他模式  如果在录像中 则停录像 然后切换到录像模式
                if (info.mode == 9 || info.mode == 10 || info.mode == 5) {
                    if (info.progress == 1) {
                        ConnectionManager.getInstance().takeVideoStop(new MoTakeVideoCallback() {
                            @Override
                            public void onSuccess(MoImage image) {
                                syncTime();
                                ConnectionManager.getInstance().switchMode("video", new MoRequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        enableAppMode(0);
                                    }
                                });
                            }

                            @Override
                            public void onFailed() {

                            }
                        });
                    } else {
                        ConnectionManager.getInstance().switchMode("video", new MoRequestCallback() {
                            @Override
                            public void onSuccess() {
                                enableAppMode(0);
                            }

                            @Override
                            public void onFailed() {
                                //如果失败 可能是回放模式正在切换到轨迹延时模式 需要等1秒后重试
                                AiCameraApplication.mApplication.mHandler.postDelayed(() -> {
                                    ConnectionManager.getInstance().switchMode("video", new MoRequestCallback() {
                                        @Override
                                        public void onSuccess() {
                                            enableAppMode(0);
                                        }

                                        @Override
                                        public void onFailed() {
                                            AiCameraApplication.mApplication.mHandler.post(() -> {
                                                DlgUtils.toast(AiCameraApplication.getContext(), StringUtil.getStr(R.string.sync_param_err), 0);
                                            });
                                        }
                                    });
                                }, 1000);
                            }
                        });
                    }
                    //如果是录像模式 录像中 先停录像
                } else if (info.mode == 3 || info.mode == 4 || info.mode == 6) {
                    if (info.progress == 1) {
                        ConnectionManager.getInstance().takeVideoStop(new MoTakeVideoCallback() {
                            @Override
                            public void onSuccess(MoImage image) {
                                syncTime();
                                enableAppMode(0);
                            }

                            @Override
                            public void onFailed() {

                            }
                        });
                    } else {
                        enableAppMode(0);
                    }
                } else if (info.mode == 1 || info.mode == 2) {
                    if (info.progress == 1) {
                        mProgressing = true;
                        if (mSyncStatus != null)
                            mSyncStatus.onProgress();
                    } else {
                        enableAppMode(0);
                    }
                } else if (info.mode == 11) {

                }
            }

            @Override
            public void onFailed() {
                if (mSyncStatus != null)
                    mSyncStatus.onSyncFailed();
            }
        });
    }

    private void syncTime() {
        ConnectionManager.getInstance().syncTime(System.currentTimeMillis(), new MoRequestValueCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed(int errCode) {
            }
        });
    }

    //fpv模式统一处理
    private void enableAppMode(int mode) {
        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("enableAppMode==>" + mode + "  mSyncStatus==>" + mSyncStatus).out();
        ConnectionManager.getInstance().appFpvMode(mode, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                if (mSyncStatus != null)
                    mSyncStatus.onSyncSucc();
            }

            @Override
            public void onFailed() {
                AiCameraApplication.mApplication.mHandler.postDelayed(() -> {
                    if (mFpvModeTry++ < 2)
                        enableAppMode(mode);
                    else if (mSyncStatus != null)
                        mSyncStatus.onSyncFailed();
                }, 500);
            }
        });
    }
}
