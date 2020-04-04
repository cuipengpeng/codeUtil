package com.caishi.chaoge.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.EventBusBean.IssueBean;
import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.bean.ModuleMaterialBean;
import com.caishi.chaoge.bean.ScenarioBean;
import com.caishi.chaoge.bean.TemplateBean;
import com.caishi.chaoge.bean.VoiceTranslate1Bean;
import com.caishi.chaoge.bean.VoiceTranslateBean;
import com.caishi.chaoge.http.Account;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.http.UserAgent;
import com.caishi.chaoge.listener.OnUploadFileListener;
import com.caishi.chaoge.manager.UploadManager;
import com.caishi.chaoge.manager.VideoEditManager;
import com.caishi.chaoge.request.GetTemplateRequest;
import com.caishi.chaoge.request.RecognitionRequest;
import com.caishi.chaoge.ui.dialog.CountDownDialog;
import com.caishi.chaoge.ui.widget.CustomCircleProgressBar;
import com.caishi.chaoge.ui.widget.LrcView;
import com.caishi.chaoge.ui.widget.MyCountDownTimer;
import com.caishi.chaoge.ui.widget.MyScrollView;
import com.caishi.chaoge.ui.widget.dialog.DialogUtil;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.ui.widget.dialog.SYDialog;
import com.caishi.chaoge.utils.AEModuleUtils;
import com.caishi.chaoge.utils.BitmapUtils;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.FileUtils;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.KeyboardLayout;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.StringUtil;
import com.caishi.chaoge.utils.SystemUtils;
import com.caishi.chaoge.utils.Utils;
import com.dyhdyh.support.countdowntimer.CountDownTimerSupport;
import com.dyhdyh.support.countdowntimer.OnCountDownTimerListener;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.othershe.library.NiceImageView;
import com.rd.xpk.editor.modal.M;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.body.ProgressResponseCallBack;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.model.HttpHeaders;
import com.zlw.main.recorderlib.RecordManager;
import com.zlw.main.recorderlib.recorder.RecordConfig;
import com.zlw.main.recorderlib.recorder.listener.RecordResultListener;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.model.TResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.caishi.chaoge.ui.activity.ScenarioActivity.KEY_OF_SCENERIO_TYPE;
import static com.caishi.chaoge.ui.activity.ScenarioActivity.KEY_OF_SHOW_NO_SCENERIO;
import static com.caishi.chaoge.utils.ConstantUtils.AUDIO_WAV_BASEPATH;
import static com.caishi.chaoge.utils.ConstantUtils.DOWNLOAD_BG_PATH;
import static com.caishi.chaoge.utils.ConstantUtils.DOWNLOAD_FONT_PATH;
import static com.caishi.chaoge.utils.ConstantUtils.DOWNLOAD_MP3_PATH;
import static com.caishi.chaoge.utils.ConstantUtils.SINGLE_TEXT_DURATION;
import static com.zhouyou.http.model.HttpHeaders.HEAD_KEY_CONTENT_TYPE;
import static com.zlw.main.recorderlib.recorder.RecordHelper.RecordState.PAUSE;
import static com.zlw.main.recorderlib.recorder.RecordHelper.RecordState.RECORDING;
import static com.zlw.main.recorderlib.recorder.RecordHelper.RecordState.STOP;

public class TemplateEditActivity extends BaseActivity {


    private ImageView img_templateEdit_cover;
    private ImageView img_templateEdit_thumb;
    private ImageView img_templateEdit_record;
    private ImageView img_templateEdit_script;
    private TextView tv_templateEdit_title;
    private TextView tv_templateEdit_hint;
    private String photoPath;
    private int editFlag;
    private LrcView lrc_templateEdit_view;
    private TextView tv_templateEdit_time;
    private CustomCircleProgressBar cpb_progress;
    private SYDialog downloadProgressDialog;
    private TemplateBean template;
    private VideoEditManager videoEditManager;
    private NiceVideoPlayer video_templateEdit_play;
    private ModuleMaterialBean moduleMaterialBean;
    private RecordManager recordManager;
    private CountDownDialog countDownDialog;
    private CountDownTimerSupport countDownTimerSupport;//计时器
    private TakePhotoImpl takePhoto;
    private String recordFilePath;//录音文件地址
    private String originalPath;//原创选择的背景图片或者视频
    private String scriptId = "";//剧本id
    private String musicId = "";//音乐id
    private String modelId = "";//模板id
    private String backGroundId = "";//背景id
    private String snapshot = "";//封面图
    private boolean isStop = false;
    private boolean isRecord = false;
    private boolean isDelete = false;
    private boolean isVoice2Text = false;

    @Override
    public void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        photoPath = bundle.getString("photoPath");
        modelId = bundle.getString("modelId");
        originalPath = bundle.getString("originalPath");
        editFlag = bundle.getInt("editFlag");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_template_edit;
    }

    @Override
    public void initView(View view) {
        ImmersionBar.with(this)
                .keyboardEnable(true)
                .transparentStatusBar().init();
        LinearLayout ll_templateEdit_layout = $(R.id.ll_templateEdit_layout);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ll_templateEdit_layout.getLayoutParams());
        lp.setMargins(0, ImmersionBar.getStatusBarHeight(this), 0, 0);
        ll_templateEdit_layout.setLayoutParams(lp);
        setBaseTitle(Color.TRANSPARENT, "下一步", true);
        video_templateEdit_play = $(R.id.video_templateEdit_play);
        tv_templateEdit_title = $(R.id.tv_templateEdit_title);
        tv_templateEdit_hint = $(R.id.tv_templateEdit_hint);
        img_templateEdit_cover = $(R.id.img_templateEdit_cover);
        img_templateEdit_script = $(R.id.img_templateEdit_script);
        img_templateEdit_thumb = $(R.id.img_templateEdit_thumb);
        img_templateEdit_record = $(R.id.img_templateEdit_record);
        lrc_templateEdit_view = $(R.id.lrc_templateEdit_view);
        tv_templateEdit_time = $(R.id.tv_templateEdit_time);
        countDownDialog = CountDownDialog.newInstance();
        initRecord();
    }

    private void initRecord() {
        recordManager = RecordManager.getInstance();
        recordManager.changeFormat(RecordConfig.RecordFormat.WAV);
        recordManager.changeRecordDir(Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH);
    }


    @Override
    public void doBusiness() {
        moduleMaterialBean = new ModuleMaterialBean();
        videoEditManager = VideoEditManager.newInstance();
        tv_templateEdit_title.setText(editFlag == 0 ? "剧本" : "原创");
        if (editFlag == 0)//模板   下载资源
            templateVideo();
        else {//原创
            originalVideo();
        }
    }

    private void originalVideo() {
        video_templateEdit_play.releasePlayer();
        snapshot = originalPath;
        if (!originalPath.contains(".mp4")) {//设置背景
            img_templateEdit_thumb.setVisibility(View.GONE);
            video_templateEdit_play.setVisibility(View.GONE);
            img_templateEdit_cover.setVisibility(View.VISIBLE);
            GlideUtil.loadImg(originalPath, img_templateEdit_cover);
            moduleMaterialBean.duration = 60;
            moduleMaterialBean.bgPath = originalPath;
            tv_templateEdit_time.setText("60S");
        } else {
            img_templateEdit_thumb.setVisibility(View.VISIBLE);
            video_templateEdit_play.setVisibility(View.VISIBLE);
            img_templateEdit_cover.setVisibility(View.VISIBLE);
            GlideUtil.loadImg(Uri.fromFile(new File(originalPath)), img_templateEdit_cover);
            video_templateEdit_play.setLooping(false);
            video_templateEdit_play.setOnPlayCompletionListener(new NiceVideoPlayer.OnPlayCompletionListener() {
                @Override
                public void onPlayCompletion(int playCount) {
                    img_templateEdit_thumb.setVisibility(View.VISIBLE);
                }
            });
            video_templateEdit_play.setOnHintSurface(new NiceVideoPlayer.OnHintSurface() {
                @Override
                public void onInfo() {
                    img_templateEdit_cover.setVisibility(View.GONE);
                }

            });
            moduleMaterialBean.duration = FileUtils.getDuration(originalPath);
            video_templateEdit_play.setOnVideoPrepared(new NiceVideoPlayer.OnVideoPrepared() {
                @Override
                public void onPrepared() {
                    img_templateEdit_thumb.setVisibility(View.GONE);
                }
            });
            video_templateEdit_play.setUp(originalPath, true, null);
            moduleMaterialBean.bgPath = originalPath;
        }
        moduleMaterialBean.aeModulePath = AEModuleUtils.getAEPath(0);
        moduleMaterialBean.specialFlag = 0;
        tv_templateEdit_time.setText((int) moduleMaterialBean.duration + "S");
        countDownTimerSupport = new CountDownTimerSupport((int) moduleMaterialBean.duration * 1000, 50);
    }


    public void templateVideo() {
        downloadProgressDialog = new SYDialog.Builder(mContext)
                .setDialogView(R.layout.dialog_progress)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .setCancelableOutSide(false)
                .setBuildChildListener(new IDialog.OnBuildListener() {
                    @Override
                    public void onBuildChildView(final IDialog dialog, View view, int layoutRes, FragmentManager fragmentManager) {
                        cpb_progress = view.findViewById(R.id.cpb_progress);
                        cpb_progress.setMaxProgress(100);
                        TextView tv_progress_hint = view.findViewById(R.id.tv_progress_hint);
                        tv_progress_hint.setText("正在下载资源");
                    }
                }).show();


        GetTemplateRequest.newInstance(mContext).getModelById(modelId, new BaseRequestInterface<TemplateBean>() {
            @Override
            public void success(int state, String msg, TemplateBean templateBean) {
                template = templateBean;
                modelId = templateBean.modelId;
                musicId = templateBean.musicId;
                backGroundId = templateBean.backGroundId;
                scriptId = templateBean.scriptId;
                snapshot = templateBean.modelCover;
                moduleMaterialBean.specialFlag = templateBean.fontSpecial;
                if (templateBean.fontSpecial < 3) {
                    moduleMaterialBean.aeModulePath = AEModuleUtils.getAEPath(templateBean.fontSpecial);
                }
                String[] split = templateBean.specialPosAndroid.split(",");
                moduleMaterialBean.leftLocation = Float.parseFloat(split[0]);
                moduleMaterialBean.topLocation = Float.parseFloat(split[1]);
                List<LrcBean> lrcBeanList = new ArrayList<>();
                List<String> colorList = new ArrayList<>();
                if (null != templateBean.scriptTimeInfoList && templateBean.scriptTimeInfoList.size() > 0) {
                    int size = templateBean.scriptTimeInfoList.size() >= templateBean.contentList.size() ?
                            templateBean.contentList.size() : templateBean.scriptTimeInfoList.size();
                    for (int i = 0; i < size; i++) {
                        colorList.add(templateBean.scriptTimeInfoList.get(i).fontColorValue);
                        LrcBean lrcBean = new LrcBean();
                        lrcBean.setStart(templateBean.scriptTimeInfoList.get(i).startTime);
                        lrcBean.setEnd(templateBean.scriptTimeInfoList.get(i).endTime);
                        lrcBean.setLrc(templateBean.contentList.get(i));
                        lrcBeanList.add(lrcBean);
                    }
                } else {
                    for (int i = 0; i < templateBean.contentList.size(); i++) {
                        colorList.add(templateBean.fontColorValue);
                        LrcBean lrcBean = new LrcBean();
                        lrcBean.setLrc(templateBean.contentList.get(i));
                        if (i == 0) {
                            lrcBean.setStart(0);
                            lrcBean.setEnd((templateBean.contentList.get(i).length() * SINGLE_TEXT_DURATION));
                        } else {
                            lrcBean.setStart(lrcBeanList.get(i - 1).getEnd());
                            lrcBean.setEnd((templateBean.contentList.get(i).length() * SINGLE_TEXT_DURATION + lrcBeanList.get(i - 1).getEnd()));
                        }
                        lrcBeanList.add(lrcBean);
                    }
                }
                moduleMaterialBean.lrcList = lrcBeanList;
                moduleMaterialBean.colorList = colorList;
                lrc_templateEdit_view.setLrc(lrcBeanList);//设置内容
                if (!Utils.isEmpty(template.backGroundImage)) {//设置背景
                    img_templateEdit_thumb.setVisibility(View.GONE);
                    img_templateEdit_cover.setVisibility(View.VISIBLE);
                    GlideUtil.loadImg(template.backGroundImage, img_templateEdit_cover);
                    moduleMaterialBean.duration = 60;
                    moduleMaterialBean.bgPath = template.backGroundImage;
                } else if (!Utils.isEmpty(template.backGroundVideo)) {
                    img_templateEdit_thumb.setVisibility(View.VISIBLE);
                    video_templateEdit_play.setVisibility(View.VISIBLE);
                    img_templateEdit_cover.setVisibility(View.VISIBLE);
                    video_templateEdit_play.setOnHintSurface(new NiceVideoPlayer.OnHintSurface() {
                        @Override
                        public void onInfo() {
                            img_templateEdit_cover.setVisibility(View.GONE);
                        }

                    });
                    video_templateEdit_play.setLooping(false);
                    video_templateEdit_play.setOnPlayCompletionListener(new NiceVideoPlayer.OnPlayCompletionListener() {
                        @Override
                        public void onPlayCompletion(int playCount) {
                            video_templateEdit_play.pause();
                            img_templateEdit_thumb.setVisibility(View.VISIBLE);
                        }
                    });
                    video_templateEdit_play.setOnVideoPrepared(new NiceVideoPlayer.OnVideoPrepared() {
                        @Override
                        public void onPrepared() {
                            img_templateEdit_thumb.setVisibility(View.GONE);
                        }
                    });
                    GlideUtil.loadImg(template.modelCover, img_templateEdit_cover);
                    video_templateEdit_play.setUp(template.backGroundVideo, true, null);
                    moduleMaterialBean.bgPath = template.backGroundVideo;
                    setDuration(null);
                }
                tv_templateEdit_time.setText((int) moduleMaterialBean.duration + "S");
                countDownTimerSupport = new CountDownTimerSupport((int) moduleMaterialBean.duration * 1000, 50);

                String backGroundPath = FileUtils.fileIsExists(DOWNLOAD_BG_PATH, moduleMaterialBean.bgPath);
                if (Utils.isEmpty(backGroundPath)) {//文件不存在下载
                    downloadBackGround(moduleMaterialBean.bgPath);
                } else if (backGroundPath.equals("1")) {//文件下载地址为空 跳过 下载音乐
                    cpb_progress.setProgress(30);
                    moduleMaterialBean.bgPath = null;
                    setDuration(null);
                    String mp3Path = FileUtils.fileIsExists(DOWNLOAD_MP3_PATH, template.musicUrl);//判断音频是否存在
                    if (Utils.isEmpty(mp3Path)) {//不存在  下载
                        downloadMP3(template.musicUrl);
                    } else if (mp3Path.equals("1")) {//下载地址为空 跳过  去下载字体
                        cpb_progress.setProgress(60);
                        moduleMaterialBean.bgMusicPath = null;
                        String fontPath = FileUtils.fileIsExists(DOWNLOAD_FONT_PATH, template.fontUrl);//判断字体是否存在
                        if (Utils.isEmpty(fontPath)) {//不存在 下载
                            downloadFont(template.fontUrl);
                        } else if (fontPath.equals("1")) {//下载地址为空 跳过
                            cpb_progress.setProgress(100);
                            moduleMaterialBean.fontFilePath = null;
                            downloadProgressDialog.dismiss();
                        } else {//文件存在  不需要下载  跳过
                            cpb_progress.setProgress(100);
                            moduleMaterialBean.fontFilePath = fontPath;
                            downloadProgressDialog.dismiss();
                        }
                    } else {//文件存在 跳过 下载字体
                        cpb_progress.setProgress(60);
                        moduleMaterialBean.bgMusicPath = mp3Path;
                        String fontPath = FileUtils.fileIsExists(DOWNLOAD_FONT_PATH, template.fontUrl);//判断字体是否存在
                        if (Utils.isEmpty(fontPath)) {//不存在 下载
                            downloadFont(template.fontUrl);
                        } else if (fontPath.equals("1")) {//下载地址为空 跳过
                            cpb_progress.setProgress(100);
                            moduleMaterialBean.fontFilePath = null;
                            downloadProgressDialog.dismiss();
                        } else {//文件存在  不需要下载  跳过
                            cpb_progress.setProgress(100);
                            moduleMaterialBean.fontFilePath = fontPath;
                            downloadProgressDialog.dismiss();
                        }

                    }
                } else {//文件存在  跳过
                    cpb_progress.setProgress(30);
                    moduleMaterialBean.bgPath = backGroundPath;
                    setDuration(backGroundPath);
                    String mp3Path = FileUtils.fileIsExists(DOWNLOAD_MP3_PATH, template.musicUrl);//判断音频是否存在
                    if (Utils.isEmpty(mp3Path)) {//不存在  下载
                        downloadMP3(template.musicUrl);
                    } else if (mp3Path.equals("1")) {//下载地址为空 跳过  去下载字体
                        cpb_progress.setProgress(60);
                        moduleMaterialBean.bgMusicPath = null;

                        String fontPath = FileUtils.fileIsExists(DOWNLOAD_FONT_PATH, template.fontUrl);//判断字体是否存在
                        if (Utils.isEmpty(fontPath)) {//不存在 下载
                            downloadFont(template.fontUrl);
                        } else if (fontPath.equals("1")) {//下载地址为空 跳过
                            cpb_progress.setProgress(100);
                            moduleMaterialBean.fontFilePath = null;
                            downloadProgressDialog.dismiss();
                        } else {//文件存在  不需要下载  跳过
                            cpb_progress.setProgress(100);
                            moduleMaterialBean.fontFilePath = fontPath;
                            downloadProgressDialog.dismiss();
                        }
                    } else {//文件存在 跳过 下载字体

                        cpb_progress.setProgress(60);
                        moduleMaterialBean.bgMusicPath = mp3Path;
                        String fontPath = FileUtils.fileIsExists(DOWNLOAD_FONT_PATH, template.fontUrl);//判断字体是否存在
                        if (Utils.isEmpty(fontPath)) {//不存在 下载
                            downloadFont(template.fontUrl);
                        } else if (fontPath.equals("1")) {//下载地址为空 跳过
                            cpb_progress.setProgress(100);
                            moduleMaterialBean.fontFilePath = null;
                            downloadProgressDialog.dismiss();
                        } else {//文件存在  不需要下载  跳过
                            cpb_progress.setProgress(100);
                            moduleMaterialBean.fontFilePath = fontPath;
                            downloadProgressDialog.dismiss();
                        }

                    }
                }
            }

            @Override
            public void error(int state, String msg) {
                downloadProgressDialog.dismiss();
                if (state == 20017)
                    finish();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setListener() {
//        img_templateEdit_thumb.setOnClickListener(this);
//        rl_templateEdit_video.setOnClickListener(this);
        img_templateEdit_record.setOnClickListener(this);
        img_templateEdit_script.setOnClickListener(this);
        recordManager.setRecordResultListener(new RecordResultListener() {
            @Override
            public void onResult(File result) {
                if (isDelete)
                    return;
                recordFilePath = result.getAbsolutePath();
                moduleMaterialBean.personMusicPath = recordFilePath;
                if (editFlag == 1 && !originalPath.contains(".mp4"))
                    moduleMaterialBean.duration = FileUtils.getDuration(recordFilePath);
                if (null == moduleMaterialBean.lrcList)
                    voice2Text();
                else {
                    if (isStop) {
                        startActivity(false);
                    }
                }
            }
        });
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.img_templateEdit_thumb:
                if (video_templateEdit_play.isPaused()) {
                    video_templateEdit_play.restart();
                    img_templateEdit_thumb.setVisibility(View.GONE);
                } else {
                    if (video_templateEdit_play.isPlaying()) {
                        video_templateEdit_play.pause();
                        img_templateEdit_thumb.setVisibility(View.VISIBLE);
                    } else {
                        video_templateEdit_play.start(false);
                        img_templateEdit_thumb.setVisibility(View.GONE);
                    }

                }
                break;
            case R.id.img_templateEdit_script:
                if (!isRecord) {//录音文件为空是剧本
                    Intent intent = new Intent(mContext, ScenarioActivity.class);
                    intent.putExtra(KEY_OF_SCENERIO_TYPE, modelId);
                    intent.putExtra(KEY_OF_SHOW_NO_SCENERIO, true);
                    startActivityForResult(intent, 10001);

                } else {//删除
                    delete();
                }

                break;

            case R.id.img_templateEdit_record:
                XXPermissions.with(mContext)
                        //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                        //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                        .permission(Permission.RECORD_AUDIO, Permission.WRITE_EXTERNAL_STORAGE)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                if (isAll) {
                                    switch (recordManager.getState()) {
                                        case IDLE://开始录制
                                            countDownDialog.showCountDownDialog(mContext);
                                            new MyCountDownTimer(2000, 1000) {
                                                @Override
                                                public void onFinish() {
                                                    countDownDialog.dismissDialog();
                                                    startRecord();
                                                }
                                            }.start();
                                            break;
                                        case PAUSE://继续录制
                                            countDownDialog.showCountDownDialog(mContext);
                                            new MyCountDownTimer(2000, 1000) {
                                                @Override
                                                public void onFinish() {
                                                    countDownDialog.dismissDialog();
                                                    resumeRecord();
                                                }
                                            }.start();
                                            break;
                                        case RECORDING://暂停录制
                                            pauseRecord();
                                            break;

                                    }
                                } else {
                                    showToast("部分权限未正常授予");
                                    XXPermissions.gotoPermissionSettings(mContext);
                                }
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {
                                showToast("被永久拒绝授权，请手动授予权限");
                                XXPermissions.gotoPermissionSettings(mContext);
                            }
                        });

                break;


        }

    }

    @Override
    public void onSaveClick(View v) {
        super.onSaveClick(v);
        isDelete = false;
        VideoEditManager.clearVideoEditManager();
//        if (recordManager == null)
//            initRecord();
        if (recordManager.getState() == PAUSE || recordManager.getState() == RECORDING) {
            isStop = true;
            stopRecord();
        } else {
            startActivity(false);
        }
    }

    private void stopRecord() {
        recordManager.stop();
        video_templateEdit_play.pause();
        countDownTimerSupport.stop();
        img_templateEdit_record.setImageResource(R.drawable.ic_record);

    }


    private void startRecord() {
        isRecord = true;
        isDelete = false;
        img_templateEdit_script.setImageResource(R.drawable.im_delete);
        img_templateEdit_thumb.setVisibility(View.GONE);
        video_templateEdit_play.release();
        video_templateEdit_play.setUp(moduleMaterialBean.bgPath, true, null);
        video_templateEdit_play.start(false);
        recordManager.start();
        isStop = false;
        img_templateEdit_record.setImageResource(R.drawable.ic_record_stop);
        countDownTimerSupport.setOnCountDownTimerListener(new OnCountDownTimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                //间隔回调
                lrc_templateEdit_view.setCurrentMillis((int) (moduleMaterialBean.duration * 1000 - millisUntilFinished));
                String second = (int) ((moduleMaterialBean.duration * 1000 - millisUntilFinished) / 1000)
                        + "/" + (int) moduleMaterialBean.duration + "S";
                tv_templateEdit_time.setText(second);
            }

            @Override
            public void onFinish() {
                String second = (int) moduleMaterialBean.duration
                        + "/" + (int) moduleMaterialBean.duration + "S";
                tv_templateEdit_time.setText(second);
                //计时器停止
                stopRecord();
            }
        });
        countDownTimerSupport.start();
    }

    private void resumeRecord() {
        video_templateEdit_play.restart();
        img_templateEdit_thumb.setVisibility(View.GONE);
        recordManager.resume();
        countDownTimerSupport.resume();
        img_templateEdit_record.setImageResource(R.drawable.ic_record_stop);
    }

    private void pauseRecord() {
        video_templateEdit_play.pause();
        img_templateEdit_thumb.setVisibility(View.VISIBLE);
        recordManager.pause();
        countDownTimerSupport.pause();
        img_templateEdit_record.setImageResource(R.drawable.ic_record);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (video_templateEdit_play.isPlaying()) {
            video_templateEdit_play.pause();
        }
        if (null != recordManager)
            if (recordManager.getState() == RECORDING) {
                pauseRecord();
            }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (recordManager != null) {
            isDelete = true;
            recordManager.stop();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (recordManager != null)
//            recordManager = null;
    }

    private void downloadBackGround(String backGroundPath) {
        String name = backGroundPath.substring(backGroundPath.lastIndexOf("/") + 1);
        EasyHttp.downLoad(backGroundPath)
                .savePath(DOWNLOAD_BG_PATH)
                .saveName(name)//不设置默认名字是时间戳生成的
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = (int) (bytesRead * 30 / contentLength);
                        cpb_progress.setProgress(progress);
                        LogUtil.i("背景进度====" + progress);
                    }

                    @Override
                    public void onStart() {
                        //开始下载
                    }

                    @Override
                    public void onComplete(String path) {
                        moduleMaterialBean.bgPath = path;//下载成功  本地地址保存
                        setDuration(path);
                        String mp3Path = FileUtils.fileIsExists(DOWNLOAD_MP3_PATH, template.musicUrl);//判断音频是否出存在
                        if (Utils.isEmpty(mp3Path)) {//不存在  下载
                            downloadMP3(template.musicUrl);
                        } else if (mp3Path.equals("1")) {//下载地址为空 跳过 下载字体
                            cpb_progress.setProgress(60);
                            moduleMaterialBean.bgMusicPath = null;
                            downloadFont(template.fontUrl);
                        } else {//文件存在 跳过 下载字体
                            cpb_progress.setProgress(60);
                            moduleMaterialBean.bgMusicPath = mp3Path;

                            String fontPath = FileUtils.fileIsExists(DOWNLOAD_FONT_PATH, template.fontUrl);//判断字体是否存在
                            if (Utils.isEmpty(fontPath)) {//不存在 下载
                                downloadFont(template.fontUrl);
                            } else if (fontPath.equals("1")) {//下载地址为空 跳过
                                cpb_progress.setProgress(100);
                                moduleMaterialBean.fontFilePath = null;
                                downloadProgressDialog.dismiss();
                            } else {//文件存在  不需要下载  跳过
                                cpb_progress.setProgress(100);
                                moduleMaterialBean.fontFilePath = fontPath;
                                downloadProgressDialog.dismiss();
                            }

                        }
                    }

                    @Override
                    public void onError(ApiException e) {
                        //下载失败
                        downloadProgressDialog.dismiss();
                    }
                });
    }

    private void downloadMP3(String mp3Path) {
        String name = mp3Path.substring(mp3Path.lastIndexOf("/") + 1);
        EasyHttp.downLoad(mp3Path)
                .savePath(DOWNLOAD_MP3_PATH)
                .saveName(name)//不设置默认名字是时间戳生成的
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = 30 + (int) (bytesRead * 30 / contentLength);
                        cpb_progress.setProgress(progress);
                        LogUtil.i("mp3进度====" + progress);
                    }

                    @Override
                    public void onStart() {
                        //开始下载
                    }

                    @Override
                    public void onComplete(String path) {
                        moduleMaterialBean.bgMusicPath = path;//背景音乐下载成功保存
                        String fontPath = FileUtils.fileIsExists(DOWNLOAD_FONT_PATH, template.fontUrl);//判断字体是否存在
                        if (Utils.isEmpty(fontPath)) {//不存在 下载
                            downloadFont(template.fontUrl);
                        } else if (fontPath.equals("1")) {//下载地址为空 跳过
                            cpb_progress.setProgress(100);
                            moduleMaterialBean.fontFilePath = null;
                            downloadProgressDialog.dismiss();
                        } else {//文件存在  不需要下载  跳过
                            cpb_progress.setProgress(100);
                            moduleMaterialBean.fontFilePath = fontPath;
                            downloadProgressDialog.dismiss();
                        }


                    }

                    @Override
                    public void onError(ApiException e) {
                        //下载失败
                        downloadProgressDialog.dismiss();
                    }
                });
    }

    private void downloadFont(String fontPath) {
        String name = fontPath.substring(fontPath.lastIndexOf("/") + 1);
        EasyHttp.downLoad(fontPath)
                .savePath(DOWNLOAD_FONT_PATH)
                .saveName(name)//不设置默认名字是时间戳生成的
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = 70 + (int) (bytesRead * 30 / contentLength);
                        cpb_progress.setProgress(progress);
                        LogUtil.i("font进度====" + progress);
                    }

                    @Override
                    public void onStart() {
                        //开始下载
                    }

                    @Override
                    public void onComplete(String path) {
                        moduleMaterialBean.fontFilePath = path;//下载成功保存
                        downloadProgressDialog.dismiss();//关闭弹窗
                    }

                    @Override
                    public void onError(ApiException e) {
                        //下载失败
                        downloadProgressDialog.dismiss();
                    }
                });
    }


    private void voice2Text() {
        isVoice2Text = true;
        countDownDialog.showBeingDiscernDialog(mContext);
        //阿里离线语音识别
//        UploadManager uploadManager = new UploadManager(mContext);
//        final String firstImgPath = uploadManager.getObjKey(UploadManager.PNG);
//        uploadManager.upload(firstImgPath, new UploadManager.Body(recordFilePath), new OnUploadFileListener() {
//            @Override
//            public void success() {
//                LogUtil.i("上传———成功——文件路径---" + firstImgPath);
//                RecognitionRequest.newInstance(mContext).getRecognition(firstImgPath, new BaseRequestInterface<ArrayList<VoiceTranslateBean>>() {
//                    @Override
//                    public void success(int state, String msg, ArrayList<VoiceTranslateBean> voiceTranslateBeans) {
//                        if (null != voiceTranslateBeans && voiceTranslateBeans.size() > 0) {
//                            ArrayList<LrcBean> lrcList = new ArrayList<>();
//                            int size = voiceTranslateBeans.size();
//                            for (int i = 0; i < size; i++) {
//                                VoiceTranslateBean data = voiceTranslateBeans.get(i);
//                                String text = data.text;
//                                text = text.replace("？", "|");
//                                text = text.replace("，", "|");
//                                text = text.replace("。", "|");
//                                text = text.replace("！", "|");
//                                text = text.replace("\n", "|");
//                                String replace = text.replace("|", "");
//                                long time = data.endTime - data.beginTime;
//                                long everyTime = time / replace.length();//算每个字符的的时长
//                                String[] splitText = text.split("\\|");//根据|分割字符串
//                                LrcBean lrcBean;
//                                if (text.contains("|")) {//包含|的
//                                    for (int j = 0; j < splitText.length; j++) {//数据整理
//                                        lrcBean = new LrcBean();
//                                        lrcBean.setLrc(splitText[j]);
//                                        if (j == 0) {//重新设置开始结束时间
//                                            lrcBean.setStart(data.beginTime);
//                                            lrcBean.setEnd(data.beginTime + splitText[j].length() * everyTime);
//                                        } else {
//                                            lrcBean.setStart(lrcList.get(lrcList.size() - 1).getEnd());
//                                            lrcBean.setEnd(lrcList.get(lrcList.size() - 1).getEnd() + splitText[j].length() * everyTime);
//                                        }
//                                        lrcList.add(lrcBean);
//                                    }
//                                } else {//不包含|的
//                                    lrcBean = new LrcBean();
//                                    lrcBean.setLrc(text);
//                                    lrcBean.setStart(lrcList.get(i).getStart());
//                                    lrcBean.setEnd(lrcList.get(i).getEnd());
//                                    lrcList.add(lrcBean);
//                                }
//                            }
//                            ArrayList<LrcBean> newLrcList = new ArrayList<>();
//
//                            for (int i = 0; i < lrcList.size(); i++) {//遍历list
//                                String text = lrcList.get(i).getLrc();
//                                if (text.length() <= 12)
//                                    newLrcList.add(lrcList.get(i));
//                                else {//所有字符大于12的重新分割 生成新的list
//                                    long end = lrcList.get(i).getEnd();
//                                    long start = lrcList.get(i).getStart();
//                                    long time = end - start;
//                                    long everyTime = time / text.length();
//                                    while (text.length() > 12) {
//                                        String substring = text.substring(0, 12);
//                                        LrcBean lrcBean1 = new LrcBean();
//                                        lrcBean1.setLrc(substring);
//                                        lrcBean1.setStart(start);
//                                        lrcBean1.setEnd(start + substring.length() * everyTime);
//                                        newLrcList.add(lrcBean1);
//                                        text = text.substring(12, text.length());
//                                        start = start + substring.length() * everyTime;
//                                    }
//                                    LrcBean lrcBean = new LrcBean();
//                                    lrcBean.setLrc(text);
//                                    lrcBean.setStart(newLrcList.get(newLrcList.size() - 1).getEnd());
//                                    lrcBean.setEnd(newLrcList.get(newLrcList.size() - 1).getEnd() + text.length() * everyTime);
//                                    newLrcList.add(lrcBean);
//                                }
//                            }
//
//
//                            moduleMaterialBean.lrcList = newLrcList;
//
//                            Bundle bundle = new Bundle();
//                            bundle.putInt("editFlag", editFlag);
//                            bundle.putString("modelId", modelId);
//                            bundle.putString("backGroundId", backGroundId);
//                            bundle.putString("musicId", musicId);
//                            bundle.putString("scriptId", scriptId);
//                            bundle.putSerializable("ModuleMaterialBean", moduleMaterialBean);
//                            startActivity(VideoEditActivity.class, bundle);
//                            LogUtil.d(newLrcList.toString());
//                        } else {
//                            showHint();
//                        }
//                        countDownDialog.dismissDialog();
//                    }
//
//                    @Override
//                    public void error(int state, String msg) {
//                        showHint();
//                    }
//                });
//
//            }
//
//            @Override
//            public void error(String msg) {
//                LogUtil.i("上传———失败——原因---" + msg);
//                showHint();
//            }
//
//            @Override
//            public void progress(int progress, long currentSize, long totalSize) {
//                LogUtil.i("上传中—录音文件——进度——" + progress);
//            }
//        });

        //科大离线语音识别
        File file = new File(recordFilePath);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("User-Agent", UserAgent.formatAgent(null));
        httpHeaders.put("userId", Account.sUserId);
        httpHeaders.put("Source", "ChaoGe");
        httpHeaders.put(HEAD_KEY_CONTENT_TYPE, "multipart/from-data");
        EasyHttp.post(HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.RECOGNITION)
                .headers(httpHeaders)
                .params("multipartFile", file, new ProgressResponseCallBack() {
                    @Override
                    public void onResponseProgress(long bytesWritten, long contentLength, boolean done) {
                        LogUtil.d("Progress=====" + (bytesWritten));
                        LogUtil.d("Progress=====" + (contentLength));
                    }
                })   // 可以添加文件上传
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        showHint();
                        LogUtil.d("上传失败=====" + e.getMessage());
                        LogUtil.d("上传失败=====" + e.getDisplayMessage());
                        LogUtil.d("上传失败=====" + e.toString());
                    }

                    @Override
                    public void onSuccess(String s) {
                        LogUtil.d("上传成功=====" + s);
                        if (!Utils.isEmpty(s)) {
                            Gson gson = new Gson();
                            VoiceTranslate1Bean voiceTranslateBean = gson.fromJson(s, VoiceTranslate1Bean.class);
                            if (null != voiceTranslateBean && voiceTranslateBean.code == 10000) {
                                if (null != voiceTranslateBean.data) {
                                    ArrayList<LrcBean> lrcList = new ArrayList<>();
                                    List<VoiceTranslate1Bean.Data> dataList = voiceTranslateBean.data;
                                    int size = dataList.size();
                                    for (int i = 0; i < size; i++) {
                                        VoiceTranslate1Bean.Data data = dataList.get(i);
                                        String text = data.text;
                                        text = text.replace("？", "|");
                                        text = text.replace("，", "|");
                                        text = text.replace("。", "|");
                                        text = text.replace("！", "|");
                                        text = text.replace("\n", "|");
                                        String replace = text.replace("|", "");
                                        long time = data.endTime - data.beginTime;
                                        long everyTime = time / replace.length();//算每个字符的的时长
                                        String[] splitText = text.split("\\|");//根据|分割字符串
                                        LrcBean lrcBean;
                                        if (text.contains("|")) {//包含|的
                                            for (int j = 0; j < splitText.length; j++) {//数据整理
                                                lrcBean = new LrcBean();
                                                lrcBean.setLrc(splitText[j]);
                                                if (j == 0) {//重新设置开始结束时间
                                                    lrcBean.setStart(data.beginTime);
                                                    lrcBean.setEnd(data.beginTime + splitText[j].length() * everyTime);
                                                } else {
                                                    lrcBean.setStart(lrcList.get(lrcList.size() - 1).getEnd());
                                                    lrcBean.setEnd(lrcList.get(lrcList.size() - 1).getEnd() + splitText[j].length() * everyTime);
                                                }
                                                lrcList.add(lrcBean);
                                            }
                                        } else {//不包含|的
                                            lrcBean = new LrcBean();
                                            lrcBean.setLrc(text);
                                            lrcBean.setStart(data.beginTime);
                                            lrcBean.setEnd(data.endTime);
                                            lrcList.add(lrcBean);
                                        }
                                    }
                                    ArrayList<LrcBean> newLrcList = new ArrayList<>();

                                    for (int i = 0; i < lrcList.size(); i++) {//遍历list
                                        String text = lrcList.get(i).getLrc();
                                        if (text.length() <= 12)
                                            newLrcList.add(lrcList.get(i));
                                        else {//所有字符大于12的重新分割 生成新的list
                                            long end = lrcList.get(i).getEnd();
                                            long start = lrcList.get(i).getStart();
                                            long time = end - start;
                                            long everyTime = time / text.length();
                                            while (text.length() > 12) {
                                                String substring = text.substring(0, 12);
                                                LrcBean lrcBean1 = new LrcBean();
                                                lrcBean1.setLrc(substring);
                                                lrcBean1.setStart(start);
                                                lrcBean1.setEnd(start + substring.length() * everyTime);
                                                newLrcList.add(lrcBean1);
                                                text = text.substring(12, text.length());
                                                start = start + substring.length() * everyTime;
                                            }
                                            LrcBean lrcBean = new LrcBean();
                                            lrcBean.setLrc(text);
                                            lrcBean.setStart(newLrcList.get(newLrcList.size() - 1).getEnd());
                                            lrcBean.setEnd(newLrcList.get(newLrcList.size() - 1).getEnd() + text.length() * everyTime);
                                            newLrcList.add(lrcBean);
                                        }
                                    }
                                    if (newLrcList.size() > 0)
                                        newLrcList.get(0).setStart(0);
                                    moduleMaterialBean.lrcList = newLrcList;
                                    List<String> colorList = new ArrayList<>();
                                    if (moduleMaterialBean.colorList == null || moduleMaterialBean.colorList.size() < 1) {
                                        for (int i = 0; i < moduleMaterialBean.lrcList.size(); i++) {
                                            colorList.add("#000000");
                                        }
                                    } else {
                                        if (moduleMaterialBean.lrcList.size() != moduleMaterialBean.colorList.size()) {
                                            for (int i = 0; i < moduleMaterialBean.lrcList.size(); i++) {
                                                colorList.add(moduleMaterialBean.colorList.get(0));
                                            }
                                        }
                                    }
                                    moduleMaterialBean.colorList = colorList;
                                    startActivity(true);
                                } else {
                                    showHint();
                                }
                                countDownDialog.dismissDialog();
                            } else {
                                showHint();
                            }

                        } else {
                            showHint();
                        }


                    }
                });


    }


    private void startActivity(boolean isVoice2Text) {
        Utils.umengStatistics(mContext, "lz0007");//配音页面下一步
        img_templateEdit_record.setImageResource(R.drawable.ic_unrecord);
        img_templateEdit_record.setEnabled(false);
        Bundle bundle = new Bundle();
        bundle.putInt("editFlag", editFlag);
        bundle.putBoolean("isVoice2Text", isVoice2Text);
        bundle.putString("modelId", modelId);
        bundle.putString("backGroundId", backGroundId);
        bundle.putString("musicId", musicId);
        bundle.putString("scriptId", scriptId);
        bundle.putString("snapshot", snapshot);
        bundle.putSerializable("ModuleMaterialBean", moduleMaterialBean);
        startActivity(VideoEditActivity.class, bundle);
    }

    private void showHint() {
        FileUtils.deleteFile(recordFilePath);
        countDownDialog.dismissDialog();
        DialogUtil.createDefaultDialog(mContext, "提示", "AI识别太忙啦\n₍₍ (̨̡ ‾᷄ᗣ‾᷅ )̧̢ ₎₎\n需要过一会儿再尝试",
                "确定", new IDialog.OnClickListener() {
                    @Override
                    public void onClick(IDialog dialog) {
//                        voice2Text();
                        moduleMaterialBean.lrcList = null;
                        moduleMaterialBean.personMusicPath = null;
                        dialog.dismiss();
                    }
                });
    }


    private void delete() {
        video_templateEdit_play.pause();
        img_templateEdit_record.setEnabled(true);
        img_templateEdit_record.setImageResource(R.drawable.ic_record);
        isDelete = true;
        isRecord = false;
        countDownTimerSupport.reset();
        lrc_templateEdit_view.init();
        recordManager.stop();
        if (null != recordFilePath) {
            FileUtils.deleteFile(recordFilePath);
            recordFilePath = null;
        }
        if (isVoice2Text)
            moduleMaterialBean.lrcList = null;
        img_templateEdit_script.setImageResource(R.drawable.im_script);
        if (editFlag == 0) {
            if (null != moduleMaterialBean.bgPath) {
                moduleMaterialBean.duration = FileUtils.getDuration(moduleMaterialBean.bgPath);
            }
        } else {
            if (!originalPath.contains(".mp4")) {//设置背景
                moduleMaterialBean.duration = 60;
            } else {
                moduleMaterialBean.duration = FileUtils.getDuration(originalPath);
            }
        }
        tv_templateEdit_time.setText((int) moduleMaterialBean.duration + "S");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            if (resultCode == RESULT_OK) {
                ScenarioBean scenarioBean = (ScenarioBean) data.getSerializableExtra(ScenarioActivity.KEY_OF_SCENERIO_RESULT_DATA);
                if (null != scenarioBean) {
                    ArrayList<String> content = scenarioBean.content;
                    ArrayList<LrcBean> lrcBeanList = new ArrayList<>();
                    ArrayList<String> colorList = new ArrayList<>();
                    if (null != content && content.size() > 0) {
                        for (int i = 0; i < content.size(); i++) {
                            if (null != moduleMaterialBean.colorList && moduleMaterialBean.colorList.size() > 0) {
                                colorList.add(moduleMaterialBean.colorList.get(0));
                            } else {
                                colorList.add("#000000");
                            }
                            LrcBean lrcBean = new LrcBean();
                            lrcBean.setLrc(content.get(i));
                            if (i == 0) {
                                lrcBean.setStart(0);
                                lrcBean.setEnd((content.get(i).length() * SINGLE_TEXT_DURATION));
                            } else {
                                lrcBean.setStart(lrcBeanList.get(i - 1).getEnd());
                                lrcBean.setEnd((content.get(i).length() * SINGLE_TEXT_DURATION + lrcBeanList.get(i - 1).getEnd()));
                            }
                            lrcBeanList.add(lrcBean);
                        }
                        tv_templateEdit_hint.setText("");
                        moduleMaterialBean.lrcList = lrcBeanList;
                        moduleMaterialBean.colorList = colorList;
                        scriptId = scenarioBean.scriptId;
                        lrc_templateEdit_view.setLrc(lrcBeanList);
                    }
                } else {
                    scriptId = "";
                    moduleMaterialBean.lrcList = null;
                    lrc_templateEdit_view.setLrc(null);
                    tv_templateEdit_hint.setText("单击录制开始配音");
                }
            }

        }


    }

    private void setDuration(String SDPath) {
        if (null != template) {
            float backGroundDuration = template.backGroundDuration;
            float listLastDuration;
            float duration;
            if (null == template.scriptTimeInfoList) {
                duration = backGroundDuration;
            } else {
                listLastDuration = (float) template.scriptTimeInfoList.get(template.scriptTimeInfoList.size() - 1).endTime / 1000;
                if (!Utils.isEmpty(SDPath)) {
                    float SDPathDuration = FileUtils.getDuration(SDPath);
                    duration = ((duration = (backGroundDuration > listLastDuration) ?
                            backGroundDuration : listLastDuration) > SDPathDuration ? duration : SDPathDuration);
                } else {
                    duration = backGroundDuration > listLastDuration ? backGroundDuration : listLastDuration;
                }
            }
            moduleMaterialBean.duration = duration;
        }

    }
}
