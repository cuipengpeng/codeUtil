package com.test.xcamera.cameraclip;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.api.http.HttpRequest;
import com.test.xcamera.bean.UploadBean;
import com.test.xcamera.bean.UploadWorksBean;
import com.test.xcamera.home.HomeActivity;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.statistic.StatisticShare;
import com.test.xcamera.upload.UploadFileRequestBody;
import com.test.xcamera.upload.UploadListener;
import com.test.xcamera.upload.UploadUtil;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DisplayUtils;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.widget.ActivityContainer;
import com.test.xcamera.widget.DouYinController;
import com.moxiang.common.share.IShare;
import com.moxiang.common.share.ShareManager;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smz on 2019/11/5.
 */

public class CompleteVideoActivity extends MOBaseActivity {
    @BindView(R.id.left_iv_title)
    ImageView leftIvTitle;
    @BindView(R.id.iv_completeVideoActivity_fullScreen)
    ImageView fullScreenImageView;
    @BindView(R.id.rl_completeVideoActivity_shareContainer)
    LinearLayout shareContainerLinearLayout;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.ijk_ijkPlayer)
    IjkVideoView ijkIjkPlayer;
    @BindView(R.id.vc_videoControler)
    DouYinController vcVideoControler;
    @BindView(R.id.cover_img)
    ImageView coverImg;
    @BindView(R.id.tv_chat)
    TextView tvChat;
    @BindView(R.id.tv_firend)
    TextView tvFirend;
    @BindView(R.id.tv_qq)
    TextView tvQq;
    @BindView(R.id.tv_weibo)
    TextView tvWeibo;
    @BindView(R.id.lin1)
    LinearLayout lin1;
    @BindView(R.id.tv_to_contribute)
    TextView tvToContribute;
    @BindView(R.id.tv_to_home)
    TextView tvToHome;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.ed_title)
    EditText edTitle;
    @BindView(R.id.tv_edit_contribute)
    TextView tvEditContribute;
    @BindView(R.id.tv_edit_home)
    TextView tvEditHome;
    @BindView(R.id.rl_edit)
    RelativeLayout rlEdit;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.rl_progress)
    RelativeLayout rlProgress;
    private String path;
    private long videoDuration;

    public static final String KEY_OF_VIDEO_PATH = "videoPathKey";
    public static final String KEY_OF_VIDEO_DURATION = "videoDurationKey";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.left_iv_title, R.id.tv_chat,R.id.iv_completeVideoActivity_fullScreen,
            R.id.tv_firend, R.id.tv_qq, R.id.tv_weibo, R.id.tv_to_contribute, R.id.tv_to_home, R.id.tv_edit_contribute, R.id.tv_edit_home})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_iv_title:
                this.finish();
                break;
            case R.id.iv_completeVideoActivity_fullScreen:
                CompleteVideoLandScapeActivity.open(this, path, ijkIjkPlayer.getCurrentPosition());
                break;
            case R.id.tv_chat:
//                share(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.tv_firend:
//                share(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.tv_qq:
//                share(SHARE_MEDIA.QQ);
                break;
            case R.id.tv_weibo:
//                share(SHARE_MEDIA.SINA);
                break;
            case R.id.tv_to_contribute:
//                Toast.makeText(this, "正在完善中", Toast.LENGTH_SHORT).show();
                rlShare.setVisibility(View.GONE);
                rlEdit.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_to_home:
                goHomeActivity();
                break;
            case R.id.tv_edit_contribute:  //投稿
                //todo
                uploadFile();
                break;
            case R.id.tv_edit_home:  //回首页
                goHomeActivity();
                break;
        }
    }

    public void goHomeActivity() {
        if (ijkIjkPlayer != null) {
            ijkIjkPlayer.release();
        }
        ActivityContainer.getInstance().finishAllActivity();
        finish();
        startAct(HomeActivity.class);
    }

    @Override
    public int initView() {
        return R.layout.activity_complete_video;
    }

    @Override
    public void initData() {
        initTitle();
        path = getIntent().getStringExtra(KEY_OF_VIDEO_PATH);
        videoDuration = getIntent().getLongExtra(KEY_OF_VIDEO_DURATION, 0);
        CameraToastUtil.show(getResourceToString(R.string.complete_sucess), getApplicationContext(), Gravity.TOP, 0, DisplayUtils.dpInt2px(getApplicationContext(), 150));
        initIjkPlayer();
        share();
    }


    private void initTitle() {
        leftIvTitle.setImageResource(R.mipmap.icon_video_edit_back);
        leftIvTitle.setVisibility(View.GONE);
        tvMiddleTitle.setText(getResourceToString(R.string.share_and_save));
        path = getIntent().getStringExtra(KEY_OF_VIDEO_PATH);
        videoDuration = getIntent().getLongExtra(KEY_OF_VIDEO_DURATION, 0);
        initIjkPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ijkIjkPlayer.isPlaying() && vcVideoControler.getIvPlay().getVisibility() == View.GONE)
            restartVideo();
    }

    public void restartVideo() {
        ijkIjkPlayer.resume();
        if (vcVideoControler != null) {
            vcVideoControler.setSelect(false);
        }

    }

    public void initIjkPlayer() {
        PlayerConfig config = new PlayerConfig.Builder().setLooping().build();
        ijkIjkPlayer.setPlayerConfig(config);
        vcVideoControler.setMediaPlayer(ijkIjkPlayer);

        startPlay(path);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (ijkIjkPlayer.isPlaying() && vcVideoControler.getIvPlay().getVisibility() == View.GONE) {
            pauseVideo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ijkIjkPlayer != null) {
            ijkIjkPlayer.release();
        }
        VideoEditManger.releaseNvsStreamingContext();
    }

    @Override
    public void onBackPressed() {
        goHomeActivity();
    }

    public void pauseVideo() {
        ijkIjkPlayer.pause();
        if (vcVideoControler != null) {
            vcVideoControler.getIvPlay().setVisibility(View.GONE);
        }
    }

    private void startPlay(String video) {
        vcVideoControler.setSelect(false);
        ijkIjkPlayer.setUrl(video);
        ijkIjkPlayer.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
        ijkIjkPlayer.start();
        ijkIjkPlayer.getDuration();
    }

//    @Override
//    public void onBackPressed() {
//        //禁用back键
//    }

    private void uploadMultiFiles() {
        Map<String, String> params = new HashMap<>();
        params.put("nikeName", "nkay");
        params.put("age", "16");
        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("file", new File("/storage/emulated/0/Mo_AiCamera/cf4334d3ec15708594797fc78ac9550f.mp4"));
        fileMap.put("file_001", new File("/storage/emulated/0/Mo_AiCamera/39cb30469bb35641386355c0ccd0aee8.mp4"));
        fileMap.put("file_002", new File("/storage/emulated/0/Mo_AiCamera/e33428bf74eac7f80eee4461110390ad.mp4"));

        HttpRequest.uploadFiles(HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.UPLOAD_VIDEO_FILE, params, fileMap, new UploadFileRequestBody.ProgressListener() {
            @Override
            public void onSuccess(String response) {
            }

            @Override
            public void onFailure(String response) {
            }

            @Override
            public void onUploadStart() {
                rlProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressUpdate(int progress) {
                progressBar.setProgress(progress);
                tvProgress.setText(progress + "%");
            }

            @Override
            public void onUploadFinish() {
                rlProgress.setVisibility(View.GONE);
            }
        });
    }

    public void uploadFile() {
        List<String> filePathList = new ArrayList<>();
        filePathList.add(path);
        filePathList.add(FileUtils.BitmapToFile(ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND)));

        UploadUtil.uploadFiles(filePathList, new UploadListener<UploadBean>() {
            @Override
            public void onProgress(int progress) {
                rlProgress.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
                tvProgress.setText(progress + "%");
            }

            @Override
            public void onFail(Throwable errorInfo) {
                rlProgress.setVisibility(View.GONE);
            }

            @Override
            public void onSucess(UploadBean bean) {

                long videoid = bean.getData().get(0).getFileId();
                String description = edTitle.getText().toString().trim();
                long duration = videoDuration;
                long coverFileId = bean.getData().get(1).getFileId();
                long templateId = 0;
                long bgmid = 0;

                LoggerUtils.d(" uploadFile ", "videoid==" + videoid + "coverFileId==" + coverFileId);
                uploadWorks(videoid, description, duration, coverFileId, templateId, bgmid, new File(path).length());

            }
        });
    }

    private void uploadWorks(long videoFileId, String description, long duration, long coverFileId, long templateId, long bgmId, long videoSize) {
        ApiImpl.getInstance().UploadWorks(videoFileId, description, duration, coverFileId, templateId, bgmId, videoSize, new CallBack<UploadWorksBean>() {
            @Override
            public void onSuccess(UploadWorksBean uploadWorksBean) {
                progressBar.setProgress(100);
                tvProgress.setText(100 + "%");
                rlProgress.setVisibility(View.GONE);

                if (!uploadWorksBean.isSucess()) {
                    CameraToastUtil.show(uploadWorksBean.getMessage(), CompleteVideoActivity.this);
                } else {
                    startAct(HomeActivity.class);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                rlProgress.setVisibility(View.GONE);
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    private void goFeed() {
        ActivityContainer.getInstance().finishAllActivity();
        finish();
        startAct(HomeActivity.class);
    }

    private void share() {
        ShareManager.ShareEntity shareEntity = new ShareManager.ShareEntity();
        shareEntity.setThumbUrl(path);
        shareEntity.setOnStatisticShare(new ShareManager.OnStatisticShare() {
            @Override
            public void onCallBack(ShareManager.ShareChooser shareChooser) {
                StatisticShare.getInstance().shareTo(shareChooser,StatisticShare.SHARE_FROM_KEY);
            }
        });
        ShareManager.getInstance().showMoSharePlatformGoHome(new WeakReference<>(this).get(), shareContainerLinearLayout, shareEntity, new IShare() {
            @Override
            public void goHome() {
                goFeed();
            }

            @Override
            public void goPublishPage() {

            }

            @Override
            public void onItemClick(ShareManager.ShareChooser shareChooser) {
                // ShareManager.getInstance().shareTo(CompleteVideoActivity.this, shareEntity, shareChooser, null);
            }

            @Override
            public void onStart(SHARE_MEDIA share_media) {
                goHome();
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {

            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        }, new ShareManager.DismissListener() {
            @Override
            public void dismiss(Dialog dialog) {

            }
        });
    }

    public static void open(Context context, String filePath, long videoDuration) {
        Intent intent = new Intent(context, CompleteVideoActivity.class);
        intent.putExtra(CompleteVideoActivity.KEY_OF_VIDEO_DURATION, videoDuration);
        intent.putExtra(CompleteVideoActivity.KEY_OF_VIDEO_PATH, filePath);
        context.startActivity(intent);
    }

}
