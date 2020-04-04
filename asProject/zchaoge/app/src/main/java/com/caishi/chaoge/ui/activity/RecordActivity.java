package com.caishi.chaoge.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.bean.ScenarioBean;
import com.caishi.chaoge.bean.VoiceTranslate1Bean;
import com.caishi.chaoge.http.Account;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.http.UserAgent;
import com.caishi.chaoge.manager.MediaPlayerManager;
import com.caishi.chaoge.ui.dialog.CountDownDialog;
import com.caishi.chaoge.ui.widget.MyCountDownTimer;
import com.caishi.chaoge.ui.widget.dialog.DialogUtil;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.utils.AudioCodec;
import com.caishi.chaoge.utils.BitmapUtils;
import com.caishi.chaoge.utils.FileUtils;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.LrcUtil;
import com.caishi.chaoge.utils.StringUtil;
import com.caishi.chaoge.utils.Utils;
import com.dyhdyh.support.countdowntimer.CountDownTimerSupport;
import com.dyhdyh.support.countdowntimer.OnCountDownTimerListener;
import com.dyhdyh.support.countdowntimer.TimerState;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.body.ProgressResponseCallBack;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.model.HttpHeaders;
import com.zlw.main.recorderlib.RecordManager;
import com.zlw.main.recorderlib.recorder.RecordConfig;
import com.zlw.main.recorderlib.recorder.RecordHelper;
import com.zlw.main.recorderlib.recorder.listener.RecordResultListener;
import com.zlw.main.recorderlib.recorder.listener.RecordTemporaryPlayListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.caishi.chaoge.ui.activity.ScenarioActivity.KEY_OF_SCENERIO_TYPE;
import static com.caishi.chaoge.ui.activity.ScenarioActivity.KEY_OF_SHOW_NO_SCENERIO;
import static com.caishi.chaoge.utils.ConstantUtils.AUDIO_MP3_BASEPATH;
import static com.caishi.chaoge.utils.ConstantUtils.AUDIO_WAV_BASEPATH;
import static com.caishi.chaoge.utils.Utils.long2Time;
import static com.zhouyou.http.model.HttpHeaders.HEAD_KEY_CONTENT_TYPE;

public class RecordActivity extends BaseActivity {

    private LottieAnimationView lav_record_record;
    private TextView tv_record_info, tv_record_title, tv_record_text, tv_record_time;
    private ImageView img_record_deleteText, img_record_record, img_record_audition, img_record_script, img_record_delete, img_record_album;
    private EditText et_record_edit;
    private LinearLayout ll_record_text, ll_record_import;
    private CountDownDialog countDownDialog;
    private MediaPlayerManager mediaPlayerManager;
    private boolean isPlay = false;
    private int duration = 60000;
    private CountDownTimerSupport mTimer;
    private RecordManager recordManager;
    public ArrayList<LrcBean> responseLrcBeanList = new ArrayList<>();
    private final String SPLITE_SYSBOLE = "#";
    private final int MAX_TEXT_PER_LINE = 9;
    private boolean isScriptBack = false;//是否是选择剧本返回
    private File recordFile = null;
    private String scriptId = "";
    private ArrayList<LrcBean> lrcList;


    @Override
    public void initBundle(Bundle bundle) {
        super.initBundle(bundle);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_record;
    }

    @Override
    public void initView(View view) {
        ImmersionBar.with(this).titleBar(R.id.record_view)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
        lav_record_record = $(R.id.lav_record_record);
        et_record_edit = $(R.id.et_record_edit);
        ll_record_text = $(R.id.ll_record_text);
        tv_record_info = $(R.id.tv_record_info);
        tv_record_title = $(R.id.tv_record_title);
        img_record_deleteText = $(R.id.img_record_deleteText);
        tv_record_text = $(R.id.tv_record_text);
        tv_record_time = $(R.id.tv_record_time);
        img_record_record = $(R.id.img_record_record);
        img_record_audition = $(R.id.img_record_audition);
        img_record_script = $(R.id.img_record_script);
        img_record_delete = $(R.id.img_record_delete);
        ll_record_import = $(R.id.ll_record_import);
        img_record_album = $(R.id.img_record_album);
        setBaseTitle("文字朗读", 0, "下一步");
        getPhoto();
    }

    @Override
    public void doBusiness() {
        if (null != tv_baseTitle_save) {
            tv_baseTitle_save.setVisibility(View.GONE);
        }
        tv_record_text.setText("最长录制60秒");
//        tv_record_text.setText(Joiner.on("\n").join(textInfoList));
        mediaPlayerManager = new MediaPlayerManager(mContext);
        countDownDialog = CountDownDialog.newInstance();
        recordManager = RecordManager.getInstance();
        recordManager.changeRecordDir(Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH);
        recordManager.changeFormat(RecordConfig.RecordFormat.WAV);
        recordManager.clearFile();
        //总时长 间隔时间
        mTimer = new CountDownTimerSupport(duration, 50);
        mTimer.setOnCountDownTimerListener(new OnCountDownTimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                //间隔回调
                tv_record_time.setText(long2Time(duration - millisUntilFinished));
            }

            @Override
            public void onFinish() {
                //计时器停止
                lav_record_record.setVisibility(View.GONE);
                img_record_record.setImageResource(R.drawable.ic_unrecord);
                img_record_record.setEnabled(false);
                img_record_audition.setVisibility(View.VISIBLE);
                img_record_delete.setVisibility(View.VISIBLE);
                img_record_script.setVisibility(View.GONE);
                ll_record_import.setVisibility(View.GONE);
                tv_record_time.setText(long2Time(duration));
                pauseRecording();
            }
        });


    }

    @Override
    public void setListener() {
        lav_record_record.setOnClickListener(this);
        img_record_deleteText.setOnClickListener(this);
        ll_record_import.setOnClickListener(this);
        img_record_script.setOnClickListener(this);
        img_record_record.setOnClickListener(this);
        img_record_audition.setOnClickListener(this);
        img_record_delete.setOnClickListener(this);
        mediaPlayerManager.setOnPlayMusicListener(new MediaPlayerManager.OnPlayMusicListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
            }

            @Override
            public void onCompletion(MediaPlayer mp) {
                img_record_audition.setImageResource(R.drawable.ic_audition);
            }

            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });
    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.img_record_deleteText://删除剧本
                ll_record_text.setVisibility(View.GONE);
                et_record_edit.setVisibility(View.VISIBLE);
                tv_baseTitle_save.setVisibility(View.GONE);
                isScriptBack = false;
                break;
            case R.id.img_record_script://剧本
                Intent intent = new Intent(mContext, ScenarioActivity.class);
                intent.putExtra(KEY_OF_SCENERIO_TYPE, "");
                intent.putExtra(KEY_OF_SHOW_NO_SCENERIO, false);
                startActivityForResult(intent, 2005);
                break;
            case R.id.img_record_audition://试听
                if (isPlay) {
                    if (mediaPlayerManager.isPlay()) {
                        mediaPlayerManager.pauseByHand();
                        img_record_audition.setImageResource(R.drawable.ic_audition);
                    } else {
                        img_record_audition.setImageResource(R.drawable.ic_suspend1);
                        mediaPlayerManager.continuePlay();
                    }
                    return;
                }
                recordManager.makeTemporaryPlay();
                recordManager.setRecordTemporaryPlayListener(new RecordTemporaryPlayListener() {
                    @Override
                    public void onResult(File result) {
                        if (null != result) {
                            img_record_audition.setImageResource(R.drawable.ic_suspend1);
                            mediaPlayerManager.changeUrl(result.getAbsolutePath(), false);
                            isPlay = true;
                        }
                    }
                });


                break;

            case R.id.ll_record_import://相册
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofVideo())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .imageSpanCount(4)// 每行显示个数 int
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewVideo(false)// 是否可预览视频 true or false
                        .isCamera(false)// 是否显示拍照按钮 true or false
                        .videoMaxSecond(60)// 显示多少秒以内的视频or音频也可适用 int
                        .videoMinSecond(5)// 显示多少秒以内的视频or音频也可适用 int
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

                break;
            case R.id.img_record_delete://删除
                DialogUtil.createDefaultDialog(mContext, "提示", "确定删除此段录音？",
                        "确定", new IDialog.OnClickListener() {
                            @Override
                            public void onClick(IDialog dialog) {
                                if (mediaPlayerManager.isPlay()) {
                                    mediaPlayerManager.pause();
                                    mediaPlayerManager.release();
                                }
                                tv_baseTitle_save.setVisibility(isScriptBack ? View.VISIBLE : View.GONE);

                                img_record_script.setVisibility(View.VISIBLE);
                                ll_record_import.setVisibility(View.VISIBLE);
                                img_record_audition.setVisibility(View.GONE);
                                img_record_delete.setVisibility(View.GONE);
                                recordManager.clearFile();
                                img_record_record.setImageResource(R.drawable.ic_recording);
                                img_record_record.setEnabled(true);
                                tv_record_time.setText(long2Time(0));
                                recordFile = null;
                                mTimer.stop();
                                mTimer.reset();
                                mediaPlayerManager.release();
                                dialog.dismiss();
                            }
                        },
                        "取消", new IDialog.OnClickListener() {
                            @Override
                            public void onClick(IDialog dialog) {
                                dialog.dismiss();
                            }
                        });


                break;
            case R.id.img_record_record:
            case R.id.lav_record_record:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    XXPermissions.with(this)
                            .permission(Permission.RECORD_AUDIO, Permission.WRITE_EXTERNAL_STORAGE)
                            .request(new OnPermission() {
                                @Override
                                public void hasPermission(List<String> granted, boolean isAll) {
                                    if (isAll) {
                                        startOrStop();
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
                } else {
                    startOrStop();
                }
                break;
        }
    }

    @Override
    public void onSaveClick(View v) {
        super.onSaveClick(v);
        if (mediaPlayerManager.isPlay()) {
            mediaPlayerManager.pauseByHand();
        }
        if (mTimer.getTimerState() == TimerState.START) {
            mTimer.pause();
        }

        if ((recordManager.getState() == RecordHelper.RecordState.RECORDING ||
                recordManager.getState() == RecordHelper.RecordState.PAUSE) && null == recordFile) {//停止录音
            stopRecording();
        } else if (isScriptBack) {
            Bundle bundle = new Bundle();
            if (null != recordFile)
                bundle.putString("personMusicPath", recordFile.getAbsolutePath());
            bundle.putString("scriptId", scriptId);
            bundle.putSerializable("lrcList", lrcList);
            startActivity(DeclaimActivity.class, bundle);
        }

    }

    private void getPhoto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> systemPhotoList = FileUtils.getSystemPhotoList(mContext);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != systemPhotoList) {
                            String photoPath = systemPhotoList.get(systemPhotoList.size() - 1);
                            Bitmap circleBitmapByShader =
                                    BitmapUtils.getCircleBitmapByShader(BitmapFactory.decodeFile(photoPath), 200, 200, 10);
                            GlideUtil.loadImg(circleBitmapByShader, img_record_album);
                        }
                    }
                });
            }
        }).start();


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2005:
                    isScriptBack = true;
                    tv_baseTitle_save.setVisibility(View.VISIBLE);
                    ScenarioBean scenarioData = (ScenarioBean) data.getSerializableExtra(ScenarioActivity.KEY_OF_SCENERIO_RESULT_DATA);
                    scriptId = scenarioData.scriptId;
                    String text = scenarioData.substance.replace("  ", "\n");
                    String titleText = scenarioData.title;
                    lrcList = LrcUtil.strLrc2LrcBean(scenarioData.content);
                    ll_record_text.setVisibility(View.VISIBLE);
                    et_record_edit.setVisibility(View.GONE);
                    tv_record_title.setText("《" + titleText + "》");
                    tv_record_info.setText(text);
                    break;
                case 2006:
                    tv_baseTitle_save.setVisibility(View.GONE);
                    img_record_script.setVisibility(View.VISIBLE);
                    ll_record_import.setVisibility(View.VISIBLE);
                    img_record_audition.setVisibility(View.GONE);
                    img_record_delete.setVisibility(View.GONE);
                    recordManager.clearFile();
                    img_record_record.setImageResource(R.drawable.ic_recording);
                    img_record_record.setEnabled(true);
                    tv_record_time.setText(long2Time(0));
                    recordFile = null;
                    mTimer.stop();
                    mTimer.reset();
                    mediaPlayerManager.release();
                    break;
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    String originalPath = selectList.get(0).getPath();

                    if (originalPath.contains(".mp4")) {
                        countDownDialog.showBeingDiscernDialog(mContext);
                        AudioCodec.getAudioFromVideo(originalPath, Environment.getExternalStorageDirectory().getAbsolutePath()
                                + AUDIO_MP3_BASEPATH, new AudioCodec.AudioDecodeListener() {
                            @Override
                            public void decodeOver(String audioSavePath) {
                                voice2Text(audioSavePath);
                            }

                            @Override
                            public void decodeFail() {
                                countDownDialog.dismissDialog();
                                showToast("失败！请重试");
                            }
                        });

                    } else
                        showToast("请选择视频文件");

                    break;
            }
        }
    }


    private void startOrStop() {
        if (recordManager.getState() == RecordHelper.RecordState.RECORDING) {//暂停
            lav_record_record.setVisibility(View.VISIBLE);
            img_record_record.setImageResource(R.drawable.ic_recording);
            img_record_audition.setVisibility(View.VISIBLE);
            img_record_delete.setVisibility(View.VISIBLE);
            pauseRecording();
            isPlay = false;
            tv_baseTitle_save.setVisibility(View.VISIBLE);
        } else {//开始
            if (mediaPlayerManager.isPlay()) {
                mediaPlayerManager.pauseByHand();
                img_record_audition.setImageResource(R.drawable.ic_audition);
            }
            img_record_record.setImageResource(R.drawable.ic_record_stop);
            img_record_script.setVisibility(View.GONE);
            ll_record_import.setVisibility(View.GONE);
            img_record_audition.setVisibility(View.VISIBLE);
            img_record_delete.setVisibility(View.VISIBLE);
            countDownDialog.showCountDownDialog(mContext);
            new MyCountDownTimer(2000, 1000) {
                @Override
                public void onFinish() {
                    countDownDialog.dismissDialog();
                    resumeRecording();
                }
            }.start();
        }
    }


    private void resumeRecording() {
        lav_record_record.setVisibility(View.GONE);
        if (recordManager.getState() == RecordHelper.RecordState.PAUSE) {
            recordManager.resume();
        } else {
            recordManager.start();
        }
        {
            if (mTimer.getTimerState() == TimerState.PAUSE) {
                mTimer.resume();
            } else {
                mTimer.start();
            }
        }
    }

    private void pauseRecording() {
        recordManager.pause();
        mTimer.pause();
    }

    private void stopRecording() {
        if (recordManager != null) {
            recordManager.stop();
            recordManager.setRecordResultListener(new RecordResultListener() {
                @Override
                public void onResult(File result) {
                    recordFile = result;
                    if (isScriptBack) {//如果是剧本返回就直接跳转  不进行语音识别
                        calculateTime(recordFile.getAbsolutePath(), lrcList);
                        Bundle bundle = new Bundle();
                        if (null != recordFile)
                            bundle.putString("personMusicPath", recordFile.getAbsolutePath());
                        bundle.putString("scriptId", scriptId);
                        bundle.putSerializable("lrcList", lrcList);
                        startActivity(DeclaimActivity.class, bundle);
                    } else {
                        countDownDialog.showBeingDiscernDialog(mContext);
                        voice2Text(result.getAbsolutePath());
                    }
                }
            });
        }
    }

    private void calculateTime(String personMusicPath, List<LrcBean> lrcList) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(personMusicPath);
            mediaPlayer.prepare();
            int duration = mediaPlayer.getDuration();
            int size = 0;
            for (int i = 0; i < lrcList.size(); i++) {
                for (int j = 0; j < lrcList.get(i).getLrc().length(); j++) {
                    size++;
                }
            }
            int time = duration / size;
            ArrayList<LrcBean> newLrcBean = new ArrayList<>();
            for (int i = 0; i < lrcList.size(); i++) {
                LrcBean lrcBean = new LrcBean();
                lrcBean.setLrc(lrcList.get(i).getLrc());
                if (i == 0) {
                    lrcBean.setStart(0);
                    lrcBean.setEnd(lrcList.get(i).getLrc().length() * time);
                } else {
                    lrcBean.setStart(newLrcBean.get(i - 1).getEnd());
                    lrcBean.setEnd((lrcList.get(i).getLrc().length() * time + newLrcBean.get(i - 1).getEnd()));
                }
                newLrcBean.add(lrcBean);
            }
            this.lrcList = newLrcBean;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void voice2Text(final String fileName) {
        File file = new File(fileName);
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
                        FileUtils.deleteFile(fileName);
                        showToast("识别失败，请重试");
                        countDownDialog.dismissDialog();
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
                            responseLrcBeanList.clear();
                            if (null != voiceTranslateBean && voiceTranslateBean.code == 10000) {
                                List<VoiceTranslate1Bean.Data> data = voiceTranslateBean.data;
                                if (null != data && data.size() > 0) {
                                    data.get(0).beginTime = 0; //按要求将服务器端返回的多条语句的第一条语句的开始时间置为 0
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < data.size(); i++) {
                                        String text = data.get(i).text;

                                        String[] strAray;
                                        int charTotalCount = 0;
                                        long charTime = 0;
                                        text = text.replace("？", SPLITE_SYSBOLE);
                                        text = text.replace("，", SPLITE_SYSBOLE);
                                        text = text.replace("。", SPLITE_SYSBOLE);
                                        text = text.replace("！", SPLITE_SYSBOLE);
                                        text = text.replace("\n", SPLITE_SYSBOLE);
                                        strAray = text.split(SPLITE_SYSBOLE);
                                        for (int j = 0; j < strAray.length; j++) {
                                            charTotalCount += strAray[j].length();
                                        }
                                        charTime = (data.get(i).endTime - data.get(i).beginTime) / charTotalCount;

                                        List<String> stringList = new ArrayList<>();
                                        stringList = Arrays.asList(strAray);
                                        List<String> tmpStringList = new ArrayList<>();
                                        tmpStringList.addAll(stringList);
                                        int removePosition = 0;
                                        for (int k = 0; k < stringList.size(); k++) {
                                            int nineCount = 0;
                                            String lineString = stringList.get(k);
                                            if (lineString.length() > MAX_TEXT_PER_LINE) {
                                                tmpStringList.remove(removePosition);
                                                nineCount = lineString.length() / MAX_TEXT_PER_LINE;
                                                String tmp = "";
                                                for (int p = 0; p < nineCount; p++) {
                                                    tmp = lineString.substring(p * MAX_TEXT_PER_LINE, (p + 1) * MAX_TEXT_PER_LINE);
                                                    tmpStringList.add((p + removePosition), tmp);
                                                }
                                                removePosition = removePosition + nineCount - 1;

                                                tmp = lineString.substring(nineCount * MAX_TEXT_PER_LINE, lineString.length());
                                                if (StringUtil.notEmpty(tmp)) {
                                                    tmpStringList.add(removePosition + 1, tmp);
//                                                    tmpStringList.add(removePosition + nineCount, tmp);
                                                    removePosition += 1;
                                                }
                                            }
                                            removePosition++;
                                        }

                                        int lrcCharCount = 0;
                                        for (int q = 0; q < tmpStringList.size(); q++) {
                                            LrcBean lrcBean = new LrcBean();
                                            lrcBean.setLrc(tmpStringList.get(q));
                                            stringBuilder.append(tmpStringList.get(q) + "\n");
                                            lrcBean.setStart(data.get(i).beginTime + lrcCharCount * charTime);
                                            lrcCharCount += tmpStringList.get(q).length();
                                            lrcBean.setEnd(data.get(i).beginTime + lrcCharCount * charTime);
                                            responseLrcBeanList.add(lrcBean);
                                        }
                                    }
                                    Bundle bundle = new Bundle();
                                    if (null != recordFile)
                                        bundle.putString("personMusicPath", recordFile.getAbsolutePath());
                                    bundle.putSerializable("ResponseLrcBeanList", responseLrcBeanList);
                                    startActivityForResult(EditTextActivity.class, bundle, 2006);
                                    countDownDialog.dismissDialog();
                                } else {
                                    showToast("没有文字内容，请重试");
                                    countDownDialog.dismissDialog();
                                }

                            } else {
                                showToast("识别失败，请重试");
                                countDownDialog.dismissDialog();
                            }

                        } else {
                            showToast("识别失败，请重试");
                            countDownDialog.dismissDialog();
                        }
                    }
                });


    }

}
