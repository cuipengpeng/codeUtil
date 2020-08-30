package com.test.xcamera.dymode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bytedance.sdk.open.aweme.share.Share;
import com.test.xcamera.R;
import com.test.xcamera.dymode.utils.FileUtils;
import com.test.xcamera.utils.Constants;
import com.moxiang.common.share.ShareManager;
import com.ss.android.ttve.common.TEDefine;
import com.ss.android.vesdk.VECommonCallback;
import com.ss.android.vesdk.VECommonCallbackInfo;
import com.ss.android.vesdk.VEEditor;
import com.ss.android.vesdk.VEResult;
import com.ss.android.vesdk.VEVideoEncodeSettings;

import static com.ss.android.vesdk.VEVideoEncodeSettings.COMPILE_TYPE.COMPILE_TYPE_MP4;
import static com.ss.android.vesdk.VEVideoEncodeSettings.USAGE_COMPILE;

public class PreviewActivity extends Activity {

    private SurfaceView mSurfaceView;
    private VEEditor mEditor;
    String[] videoPaths;
    String[] audioPaths;
    String[] transitions;

    String mBackgroundMusic;
    int mBackgroundMusicTrimIn;
    int mBackgroundMusicTrimOut;
    int mBackgroundMusicLoopType;

    private static final String TAG = "PreviewActivity";

    ProgressDialog mProgressDialog;

    String mVideoSavePath;
    private boolean mNeedPrepare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview);

        mVideoSavePath = Constants.myGalleryLocalPath + "/" + System.currentTimeMillis() + ".mp4";

        mSurfaceView = findViewById(R.id.preview_surface);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        initEditor();

        findViewById(R.id.compress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compress();
            }
        });
    }

    private void initEditor() {
        mEditor = new VEEditor(FileUtils.ROOT_DIR, mSurfaceView);
        String videoPath = getIntent().getStringExtra("video_path");
        String audioPath = getIntent().getStringExtra("audio_path");
        boolean isMusic = getIntent().getBooleanExtra("isMusic", false);
        if (isMusic) {
            mBackgroundMusic = audioPath;
        }
        mBackgroundMusicTrimIn = getIntent().getIntExtra("in", 0);
        mBackgroundMusicTrimOut = getIntent().getIntExtra("in", 15000);
        mBackgroundMusicLoopType = getIntent().getIntExtra("loop", 1);

        audioPaths = isMusic ? null : new String[]{audioPath};
        videoPaths = new String[]{videoPath};
        transitions = new String[]{TEDefine.TETransition.FADE};

        int ret = mEditor.init(videoPaths, null, audioPaths, VEEditor.VIDEO_RATIO.VIDEO_OUT_RATIO_ORIGINAL);
        if (ret != VEResult.TER_OK) {
            final int result = ret;
            Toast.makeText(this, "所有文件都不好使啊！！！" + result, Toast.LENGTH_SHORT).show();
            if (mEditor != null) {
                mEditor.destroy();
                mEditor = null;
            }
            return;
        }

        mEditor.setLoopPlay(true);
        mEditor.setScaleMode(VEEditor.SCALE_MODE.SCALE_MODE_CENTER_CROP);

        mEditor.prepare();

        int duration = mEditor.getDuration();

        mEditor.addAudioTrack(mBackgroundMusic, mBackgroundMusicTrimIn, mBackgroundMusicTrimOut, mBackgroundMusicLoopType == 1);
        mEditor.seek(0, VEEditor.SEEK_MODE.EDITOR_SEEK_FLAG_LastSeek);
        mEditor.play();
        mNeedPrepare = false;
        mEditor.setOnInfoListener(new VECommonCallback() {
            @Override
            public void onCallback(int what, int ext, float f, String s) {
                switch (what) {
                    case VECommonCallbackInfo.TE_INFO_COMPILE_DONE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                                Toast.makeText(PreviewActivity.this, "合成成功", Toast.LENGTH_SHORT).show();
                            }
                        });

//                        Intent intent = new Intent();
//                        intent.setClass(PreviewActivity.this, VideoPlayerActivity.class);
//                        intent.putExtra("video_path", mVideoSavePath);
//                        startActivity(intent);

                        ShareManager.ShareEntity entity = new ShareManager.ShareEntity();
                        entity.setThumbUrl(mVideoSavePath);
                        ShareManager.getInstance().shareToDouyin(PreviewActivity.this, Share.VIDEO, entity);

                        mNeedPrepare = true;
                }

            }
        });
        mEditor.setOnErrorListener(new VECommonCallback() {
            @Override
            public void onCallback(int ret, int ext, float f, String s) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNeedPrepare) {
            mEditor.prepare();
            mNeedPrepare = false;
            mEditor.play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEditor != null) {
            mEditor.destroy();
            mEditor = null;
        }
    }

    private void compress() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("保存中...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        VEVideoEncodeSettings setting = new VEVideoEncodeSettings.Builder(USAGE_COMPILE)
                .setCompileType(COMPILE_TYPE_MP4)
//                .setVideoRes(1920, 1080)
                .setVideoRes(1080, 1920)
                .setHwEnc(true)
                .setGopSize(30)
                .setVideoBitrate(VEVideoEncodeSettings.ENCODE_BITRATE_MODE.ENCODE_BITRATE_ABR, 4 * 1024 * 1024)
                .setFps(25)
                .build();
        mEditor.compile(mVideoSavePath, null, setting);
    }
}
