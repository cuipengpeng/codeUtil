package com.test.xcamera.phonealbum.usecase;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.editvideo.ToastUtil;
import com.editvideo.VideoCompileUtil;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;

import java.io.File;

/**
 * 视频合成
 */
public class VideoCompile {
    private CommonDownloadDialog downProgressDialog;
    private Context mContext;
    private NvsStreamingContext mStreamingContext;
    private Handler mHandler;
    private String compileVideoPath;

    public static VideoCompile getInstance(NvsStreamingContext streamingContext, Context context) {
        return new VideoCompile(streamingContext, context);
    }

    public VideoCompile(NvsStreamingContext streamingContext, Context context) {
        mContext = context;
        mStreamingContext = streamingContext;
    }

    public void compileVideo(NvsTimeline timeline, String compilePath, long startTime, long endTime) {
        compileVideoPath=compilePath;
        setCallBack();
        downProgressDialog = new CommonDownloadDialog(mContext);
        downProgressDialog.setCancelable(false);
        downProgressDialog.showDialog(false);
        downProgressDialog.setProgress(AiCameraApplication.getContext().getString(R.string.video_edit_complie_ing, "" + 0 + "%"));
        VideoCompileUtil.compileVideo(mStreamingContext, timeline, compilePath, startTime, endTime);

    }

    public void setCallBack() {
        mStreamingContext.setCompileCallback(new NvsStreamingContext.CompileCallback() {
            @Override
            public void onCompileProgress(NvsTimeline nvsTimeline, int i) {
                if (downProgressDialog != null) {
                    downProgressDialog.setProgress(AiCameraApplication.getContext().getString(R.string.video_edit_complie_ing, "" + i + "%"));
                }
                /*为防止卡在100%*/
                if (i == 100) {
                    if (mHandler == null) {
                        mHandler = new Handler();
                    }
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            compileDismissDialog();
                            if (mStreamingContext != null) {
                                mStreamingContext.stop();
                            }
                        }
                    }, 5000);
                }
                Log.i("meic_club", "meic_club:progress:" + i);
            }

            @Override
            public void onCompileFinished(NvsTimeline nvsTimeline) {
                compileDismissDialog();
            }

            @Override
            public void onCompileFailed(NvsTimeline nvsTimeline) {
                compileDismissDialog();
            }
        });
        mStreamingContext.setHardwareErrorCallback(new NvsStreamingContext.HardwareErrorCallback() {
            @Override
            public void onHardwareError(int i, String s) {
                compileDismissDialog();
                ToastUtil.showToast(AiCameraApplication.getContext(), s);
                Log.i("meic_club", "meic_club:error:" + s);

            }
        });
        mStreamingContext.setCompileCallback2(new NvsStreamingContext.CompileCallback2() {

            @Override
            public void onCompileCompleted(NvsTimeline nvsTimeline, boolean isCanceled) {
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                compileDismissDialog();
                if (isCanceled) {
                    File file = new File(compileVideoPath);
                    if (file.exists()) {
                        file.delete();
                    }
                    ToastUtil.showToast(AiCameraApplication.getContext(), R.string.video_edit_complie_canceled);
                    if(mOnCompileCallBack!=null){
                        mOnCompileCallBack.onCallBack(false);
                    }
                    return;
                }
                if(mOnCompileCallBack!=null){
                    mOnCompileCallBack.onCallBack(true);
                }
            }
        });
    }

    private void compileDismissDialog() {
        if (downProgressDialog != null) {
            downProgressDialog.dismissDialog();
        }
    }
    private OnCompileCallBack mOnCompileCallBack;

    public void setOnCompileCallBack(OnCompileCallBack mOnCompileCallBack) {
        this.mOnCompileCallBack = mOnCompileCallBack;
    }

    public interface OnCompileCallBack {
        void onCallBack(boolean isStatus);
    }
    public void destroy(){
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mContext=null;
    }
}
