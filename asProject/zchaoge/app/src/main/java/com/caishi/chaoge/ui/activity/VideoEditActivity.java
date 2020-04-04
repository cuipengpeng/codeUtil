package com.caishi.chaoge.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.bean.EventBusBean.AudioVolumeBean;
import com.caishi.chaoge.bean.EventBusBean.EventMusicBean;
import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.bean.ModuleMaterialBean;
import com.caishi.chaoge.manager.VideoEditManager;
import com.caishi.chaoge.ui.dialog.FontDialog;
import com.caishi.chaoge.ui.widget.dialog.BaseDialog;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.ui.widget.dialog.SYDialog;
import com.caishi.chaoge.utils.ArrayUtils;
import com.caishi.chaoge.utils.FileUtils;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.Utils;
import com.gyf.barlibrary.ImmersionBar;
import com.rd.vecore.VirtualVideoView;

import java.util.List;

public class VideoEditActivity extends BaseActivity {


    private ProgressBar pb_videoEdit_progress;
    private VirtualVideoView vvv_videoEdit_video;
    private RelativeLayout rl_videoEdit_layout;
    private ImageView img_videoEdit_play;
    private int editFlag;//0 模板编辑  1原创编辑
    int[] ids = new int[]{R.id.img_videoEdit_text, R.id.img_videoEdit_special, R.id.img_videoEdit_music, R.id.img_videoEdit_volume};
    ImageView[] imgs;
    private ModuleMaterialBean moduleMaterialBean;
    private VideoEditManager videoEditManager;
    private LinearLayout ll_videoEdit_control;
    private SYDialog volumeDialog;

    private String modelId;
    private String backGroundId;
    private String musicId;
    private String scriptId;
    private String snapshot;
    private boolean isBack;
    private FontDialog fontDialog;
    private List<LrcBean> textInfoList;
    private List<String> colorList;
    private boolean isVoice2Text;

    @Override
    public void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        editFlag = bundle.getInt("editFlag");
        modelId = bundle.getString("modelId");
        backGroundId = bundle.getString("backGroundId");
        musicId = bundle.getString("musicId");
        scriptId = bundle.getString("scriptId");
        snapshot = bundle.getString("snapshot");
        isVoice2Text = bundle.getBoolean("isVoice2Text");
        moduleMaterialBean = (ModuleMaterialBean) bundle.getSerializable("ModuleMaterialBean");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_video_edit;
    }

    @Override
    public void initView(View view) {
        ImmersionBar.with(this).titleBar(R.id.view_edit)
                .init();
        LinearLayout ll_videoEdit_layout = $(R.id.ll_videoEdit_layout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ll_videoEdit_layout.getLayoutParams());
        lp.setMargins(0, ImmersionBar.getStatusBarHeight(this), 0, 0);
//        ll_videoEdit_layout.setLayoutParams(lp);
        setBaseTitle(Color.TRANSPARENT, "下一步", true);
        imgs = new ImageView[ids.length];
        for (int i = 0; i < ids.length; i++) {
            imgs[i] = $(ids[i]);
            imgs[i].setOnClickListener(this);
        }
        pb_videoEdit_progress = $(R.id.pb_videoEdit_progress);
        ll_videoEdit_control = $(R.id.ll_videoEdit_control);
        vvv_videoEdit_video = $(R.id.vvv_videoEdit_video);
        img_videoEdit_play = $(R.id.img_videoEdit_play);
        rl_videoEdit_layout = $(R.id.rl_videoEdit_layout);
        textInfoList = moduleMaterialBean.lrcList;
        colorList = moduleMaterialBean.colorList;
        if (editFlag == 0) {//隐藏 字效 音乐
            imgs[0].setVisibility(View.GONE);
            imgs[1].setVisibility(View.GONE);
            imgs[2].setVisibility(View.GONE);
            if (isVoice2Text) {
                imgs[0].setVisibility(View.VISIBLE);
            }
        } else {
            if (null == moduleMaterialBean.personMusicPath && null == textInfoList) {
                imgs[0].setVisibility(View.GONE);
                imgs[1].setVisibility(View.GONE);
            }
        }
        List<LrcBean> lrcList = moduleMaterialBean.lrcList;
        if (editFlag == 1) {
            if (null != moduleMaterialBean.personMusicPath) {
                moduleMaterialBean.duration = FileUtils.getDuration(moduleMaterialBean.personMusicPath);
            } else if (null != lrcList && lrcList.size() > 0) {
                moduleMaterialBean.duration = lrcList.get(lrcList.size() - 1).getEnd() / 1000f;
            } else if (null != moduleMaterialBean.bgPath && moduleMaterialBean.bgPath.contains(".mp4")) {
                moduleMaterialBean.duration = FileUtils.getDuration(moduleMaterialBean.bgPath);
            }

        }

//        imgs[1].setVisibility(View.VISIBLE);
//        imgs[2].setVisibility(View.VISIBLE);
        //媒体宽高小于这个面积的使用强制软解
        vvv_videoEdit_video.setSWDecoderSize(3840 * 3840);
        //设置当OnPause后是否释放播放资源
        vvv_videoEdit_video.setReleasePlaybackOnPause(false);
        videoEditManager = VideoEditManager.newInstance();
        fontDialog = FontDialog.newInstance();
        fontDialog.init(mContext);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isBack) {
            videoEditManager.init(mContext, vvv_videoEdit_video, moduleMaterialBean, 1);
        }
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void setListener() {
        rl_videoEdit_layout.setOnClickListener(this);
        vvv_videoEdit_video.setOnPlaybackListener(new VirtualVideoView.VideoViewListener() {
            @Override
            public void onPlayerPrepared() {
                pb_videoEdit_progress.setMax((int) (videoEditManager.getDuration() * 1000));
                LogUtil.d(" vvv_videoEdit_video.getDuration();=====" + vvv_videoEdit_video.getDuration());
            }

            @Override
            public void onPlayerCompletion() {
                img_videoEdit_play.setVisibility(View.VISIBLE);
            }

            @Override
            public void onGetCurrentPosition(float v) {
                LogUtil.d("v===" + v);
                int progress = (int) (v * 1000);
                pb_videoEdit_progress.setProgress(progress);
            }

            @Override
            public boolean onPlayerError(int i, int i1, String s) {
                return super.onPlayerError(i, i1, s);
            }
        });
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.rl_videoEdit_layout:
                if (vvv_videoEdit_video.isPlaying()) {
                    vvv_videoEdit_video.pause();
                    img_videoEdit_play.setVisibility(View.VISIBLE);
                } else {
                    vvv_videoEdit_video.start();
                    img_videoEdit_play.setVisibility(View.GONE);
                }
                return;
            default:
                if (vvv_videoEdit_video.isPlaying()) {
                    vvv_videoEdit_video.pause();
                    img_videoEdit_play.setVisibility(View.VISIBLE);
                }
                switchDialog(ArrayUtils.getIndex(ids, v.getId()));
                break;

        }
    }

    @Override
    public void onSaveClick(View v) {
        super.onSaveClick(v);
        Utils.umengStatistics(mContext, "lz0008");//配音编辑页面下一步
        Bundle bundle = new Bundle();
        bundle.putString("firstSnapshot", snapshot);
        bundle.putString("musicId", musicId);
        bundle.putString("modelId", modelId);
        bundle.putString("scriptId", scriptId);
        bundle.putString("backGroundId", backGroundId);
        bundle.putInt("classFlag", 1);
        startActivity(IssueActivity.class, bundle);
    }

    private void switchDialog(int index) {
        isBack = true;
        ll_videoEdit_control.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.video_bottom_out));
        switch (index) {
            case 0:
                fontDialog.showFontDialog(textInfoList, colorList, moduleMaterialBean.fontFilePath, true, -1);
                fontDialog.setOnDialogKeyListener(new FontDialog.OnDialogKeyListener() {
                    @Override
                    public void onBack() {
                        translateYAnimationIn();
                    }

                    @Override
                    public void onAffirm(List<String> colorList, List<LrcBean> lrcList, String fontPath) {
                        translateYAnimationIn();
                        moduleMaterialBean.colorList = colorList;
                        moduleMaterialBean.lrcList = lrcList;
                        moduleMaterialBean.fontFilePath = fontPath;
                        videoEditManager.updateVirtualVideo();
                    }
                });
                break;

            case 1:
                Bundle bundle = new Bundle();
                bundle.putInt("specialFlag", moduleMaterialBean.specialFlag);
                bundle.putString("snapshot", snapshot);
                startActivityForResult(SpecialActivity.class, bundle, 10002);
                overridePendingTransition(R.anim.push_bottom_in, 0);
                break;

            case 2:
                SelectMusicActivity.open(this, 10003);
                break;
            case 3:
                volumeDialog = new SYDialog.Builder(mContext)
                        .setDialogView(R.layout.dialog_volume)
                        .setScreenWidthP(1f)
                        .setScreenHeightP(1f)
                        .setGravity(Gravity.BOTTOM)
                        .setCancelable(true)
                        .setCancelableOutSide(false)
                        .setAnimStyle(R.style.AnimUp)
                        .setBuildChildListener(new IDialog.OnBuildListener() {
                            @Override
                            public void onBuildChildView(final IDialog dialog, View view, int layoutRes, final FragmentManager fragmentManager) {
                                final SeekBar sb_volume_person = view.findViewById(R.id.sb_volume_person);
                                final SeekBar sb_volume_bg = view.findViewById(R.id.sb_volume_bg);
                                final SeekBar sb_volume_video = view.findViewById(R.id.sb_volume_video);
                                if (null == moduleMaterialBean.personMusicPath) {
                                    sb_volume_person.setEnabled(false);
                                }
                                if (null == moduleMaterialBean.bgMusicPath) {
                                    sb_volume_bg.setEnabled(false);
                                }
                                if (!moduleMaterialBean.bgPath.contains(".mp4")) {
                                    sb_volume_video.setEnabled(false);
                                }
                                final AudioVolumeBean volume = videoEditManager.getVolume();
                                sb_volume_person.setMax(300);
                                sb_volume_bg.setMax(100);
                                sb_volume_video.setMax(100);
                                sb_volume_person.setProgress(volume.personVolume);
                                sb_volume_bg.setProgress(volume.bgVolume);
                                sb_volume_video.setProgress(volume.videoBgVolume);
                                view.findViewById(R.id.tv_volume_affirm).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        volume.bgVolume = sb_volume_bg.getProgress();
                                        volume.personVolume = sb_volume_person.getProgress();
                                        volume.videoBgVolume = sb_volume_video.getProgress();
                                        videoEditManager.setVolume(volume);
                                        volumeDialog.dismiss();
                                        translateYAnimationIn();
                                    }
                                });
                                view.findViewById(R.id.view_volume_bg).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        volumeDialog.dismiss();
                                        translateYAnimationIn();
                                    }
                                });
                                volumeDialog.setOnBackListener(new BaseDialog.OnBackListener() {
                                    @Override
                                    public void onBack(boolean isBack) {
                                        if (isBack) {
                                            translateYAnimationIn();
                                        }
                                    }
                                });
                            }
                        }).show();

                break;

        }

    }


    private void translateYAnimationIn() {
        ll_videoEdit_control.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.video_bottom_in));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        translateYAnimationIn();
        if (resultCode == RESULT_OK) {
            if (requestCode == 10002) {
                videoEditManager.updateVirtualVideo(vvv_videoEdit_video);
            }
            if (requestCode == 10003) {
                EventMusicBean eventMusicBean = (EventMusicBean) data.getSerializableExtra(SelectMusicActivity.KEY_OF_MUSIC_RESULT);
                if (null != eventMusicBean) {
                    moduleMaterialBean.bgMusicPath = eventMusicBean.musicPath;
                    musicId = eventMusicBean.musicID;
                    videoEditManager.updateVirtualVideo();
                } else {
                    moduleMaterialBean.bgMusicPath = "";
                    musicId = "";
                }

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBack = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isBack = true;
        if (vvv_videoEdit_video.isPlaying()) {
            vvv_videoEdit_video.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != vvv_videoEdit_video) {
            vvv_videoEdit_video.cleanUp();
            vvv_videoEdit_video = null;
        }
    }

}
