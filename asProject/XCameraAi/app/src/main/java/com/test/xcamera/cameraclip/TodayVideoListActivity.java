package com.test.xcamera.cameraclip;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.bean.MoErrorData;
import com.test.xcamera.bean.MoVideoByDate;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.cameraclip.adapter.TodayVideoListAdapter;
import com.test.xcamera.cameraclip.bean.TempleteScoreBean;
import com.test.xcamera.cameraclip.bean.VideoFile;
import com.test.xcamera.cameraclip.bean.VideoTag;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.mointerface.MoGetVideoByDateCallback;
import com.test.xcamera.personal.UNLoginDialog;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.DownloadSingleCameraFile;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;
import com.test.xcamera.widget.ActivityContainer;
import com.moxiang.common.logging.Logcat;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: mz
 * Time:  2019/10/28
 */
public class TodayVideoListActivity extends MOBaseActivity {
    @BindView(R.id.rv_view)
    RecyclerView recyclerView;
    @BindView(R.id.left_iv_title)
    ImageView leftIvTitle;
    @BindView(R.id.right_iv_title)
    ImageView rightIvTitle;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.tv_todayVideoListActivity_content)
    TextView contentTextView;
//    @BindView(R.id.rl_progress)
//    RelativeLayout progressRelativeLayout;
    @BindView(R.id.progress1)
    ProgressBar progressBar;
    @BindView(R.id.tv_todayVideoListActivity_loadingContent)
    TextView loadingContentTextView;
    @BindView(R.id.rl_todayVideoListActivity_emptyData)
    RelativeLayout emptyDataRelativeLayout;

    CommonDownloadDialog progressRelativeLayout;
    private DownloadVideoTempleteDataUtil downloadVideoTempleteDataUtil;
    private TodayVideoListAdapter gridAdapter;
    private boolean loading = false;
    private boolean pullUp = false;
    private int mPageCount = 0;
    private NetVideoTempleteHelper netVideoTempleteHelper;

    private Handler handler = new Handler();
    private boolean isCanClick = true;

    @OnClick({R.id.left_iv_title, R.id.rl_progress,R.id.right_iv_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_iv_title:
                onBackPressed();
                break;
            case R.id.right_iv_title:
                UNLoginDialog deleteDialog=new UNLoginDialog(this);
                deleteDialog.showGoneTitleDialog();
                deleteDialog.showGoneDialogCancel();
                deleteDialog.setDialogContent(15,getString(R.string.tipTodayVideo));
                deleteDialog.setButtonClickListener(new UNLoginDialog.ButtonClickListener() {
                    @Override
                    public void sureButton(Dialog mDialog) {

                    }

                    @Override
                    public void cancelButton(Dialog mDialog) {

                    }
                });
                break;
        }
    }

    @Override
    public int initView() {
        return R.layout.activity_camera_clip;
    }

    @Override
    public void initData() {
        boolean hasGuide =getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).getBoolean(NewerGuideActivity.KEY_OF_TODAY_WONDERFUL,false);
        if (!hasGuide) {
            NewerGuideActivity.open(this, NewerGuideActivity.NEWER_GUIDE_TODAY_WONDERFUL);
        }
        netVideoTempleteHelper = new NetVideoTempleteHelper();
        downloadVideoTempleteDataUtil = new DownloadVideoTempleteDataUtil();
        downloadVideoTempleteDataUtil.setOnFinishDownloadVideoCallback(new DownloadVideoTempleteDataUtil.OnFinishDownloadVideoCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {
                loadingContentTextView.setText(getResources().getString(R.string.loadingData) + " " + progress + "%");
            }

            @Override
            public void onFinish(VideoTemplete videoTemplete) {
                CameraClipEditActivity.open(TodayVideoListActivity.this, videoTemplete, netVideoTempleteHelper.mNetVideoTempleteList);
            }

            @Override
            public void onFail(String errorInfo) {

            }
        });

        progressRelativeLayout = new CommonDownloadDialog(this);
        progressRelativeLayout.setBackKeyListener(new CommonDownloadDialog.BackKeyListener() {
            @Override
            public void onBack(Dialog mDialog) {
                if (progressRelativeLayout.isShowing()) {
//            ConnectionManager.getInstance().stopPlay(null);
                    Toast.makeText(TodayVideoListActivity.this, getResources().getString(R.string.cannotCancelWhenDownload), Toast.LENGTH_SHORT).show();
                }
            }
        });
        leftIvTitle.setImageResource(R.mipmap.backicon);
        tvMiddleTitle.setText("");
        rightIvTitle.setVisibility(View.VISIBLE);
        rightIvTitle.setImageResource(R.mipmap.help);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_ff7700), PorterDuff.Mode.SRC_IN);
        ActivityContainer.getInstance().addActivity(this);

        emptyDataRelativeLayout.setVisibility(View.GONE);
        initRecycleView();
        if (AccessoryManager.getInstance().mIsRunning) {
            getVideoByDate(true, 0);//获取精彩数据
        }
    }

    /**
     * 获取今日精彩数据
     */
    public void getVideoByDate(final boolean refresh, int pageIndex) {
        loading = true;
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("request camera json data: timestamp="+System.currentTimeMillis()).out();
        if (null != progressRelativeLayout)
            progressRelativeLayout.showDialog(false, false);
        ConnectionManager.getInstance().getVideoByDate(pageIndex, new MoGetVideoByDateCallback() {
            @Override
            public void onSuccess(final ArrayList<MoVideoByDate> videoByDates) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hiddenLoadingView();

                        LoggerUtils.printLog("获取精彩推荐数据" + videoByDates + "");
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("response camera json data " + videoByDates+"      timestamp="+System.currentTimeMillis()).out();
                        if (!isDestroyed()) {
                            if (videoByDates != null) {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyDataRelativeLayout.setVisibility(View.GONE);
                                contentTextView.setVisibility(View.VISIBLE);
                                if (videoByDates.size() > 0) {
                                    gridAdapter.updateData(refresh, videoByDates);
                                } else {
                                    if (refresh) {
                                        showEmptyDataView();
                                    } else {
                                        Toast.makeText(TodayVideoListActivity.this, getResources().getString(R.string.loadToBottom), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                if (refresh) {
                                    showEmptyDataView();
                                }
                            }
                        }
                    }
                });

            }

            @Override
            public void onFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isDestroyed()) {
                            hiddenLoadingView();
                            if (refresh) {
                                showEmptyDataView();
                            }
                        }
                    }
                });
            }
        });
    }

    public void hiddenLoadingView() {
        loading = false;
        if (null != progressRelativeLayout)
            progressRelativeLayout.dismissDialog();
        if (null != loadingContentTextView)
            loadingContentTextView.setVisibility(View.VISIBLE);
    }

    private void showEmptyDataView() {
        emptyDataRelativeLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        contentTextView.setVisibility(View.GONE);
    }

    private void initRecycleView() {
        GridLayoutManager layoutManage = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(layoutManage);
        gridAdapter = new TodayVideoListAdapter(mContext);
        recyclerView.setAdapter(gridAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!loading && newState == RecyclerView.SCROLL_STATE_IDLE && pullUp) {
                    //避免卡顿
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (gridLayoutManager.findLastVisibleItemPosition() + 2 >= gridLayoutManager.getItemCount()) {
                        mPageCount += 1;
                        getVideoByDate(false, mPageCount);
                    }
                }
            }

            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                pullUp = dy > 0;
            }
        });
        gridAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if (!isCanClick) {
                    return;
                }
                isCanClick = false;
                for (int i = 0; i < gridAdapter.mData.size(); i++) {
                    MoVideoByDate moVideoByDate = gridAdapter.mData.get(i);
                    if (position != i) {
                        moVideoByDate.setCheck(false);
                    } else {
                        moVideoByDate.setCheck(true);
                    }
                }
                gridAdapter.notifyDataSetChanged();

                DownloadVideoTempleteDataUtil.selectedDateForCache = gridAdapter.mData.get(position).getmDate();
                if (gridAdapter.mData.size() > 0) {
                    ConnectionManager.getInstance().getXmlInfo(gridAdapter.mData.get(position).getmDate(), new DownloadSingleCameraFile.DownloadCallback() {
                        @Override
                        public void onFinishDownLoad(String path) {
                            try {
                                new OneKeyMakeVideoXmlParser().parserXml(new FileInputStream(new File(path)), new OneKeyMakeVideoXmlParser.OnParseXmlCallback() {
                                    @Override
                                    public void onStart() {
                                        progressRelativeLayout.showDialog(false, false);
                                    }

                                    @Override
                                    public void onFinish(List<VideoFile> videoFileList) {

                                        TodayFileListActivity.open(TodayVideoListActivity.this, gridAdapter.mData.get(position).getmDate());
                                        progressRelativeLayout.dismissDialog();
                                    }

                                    @Override
                                    public void onFail(String errorInfo) {
                                        progressRelativeLayout.dismissDialog();
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isCanClick = true;
                        }
                    }, 2000);
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        ConnectionManager.getInstance().setErrorI(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AccessoryManager.getInstance().mIsRunning) {
            AccessoryManager.getInstance().setConnectStateListener(null, "TodayVideoListtActivity");
            finish();
            return;
        }
        ConnectionManager.getInstance().setErrorI(new MoErrorCallback() {
            @Override
            public void onError(MoErrorData data) {
                if (data.event == MoErrorCallback.SD_EVENT) {
                    switch (data.status) {
                        case MoErrorCallback.SD_OUT:
//                            showEmptyDataView();
                            finish();
                            DlgUtils.toast(TodayVideoListActivity.this, getResources().getString(R.string.sdcard_out));
                            break;
                        case MoErrorCallback.SD_IN:
                            getVideoByDate(true, 0);
                            break;
                        case MoErrorCallback.SD_IN_FAIL:
                            break;
                        case MoErrorCallback.SD_FULL:
                            break;
                        case MoErrorCallback.SD_LOW:
                            break;
                    }

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        progressRelativeLayout.destroy();
        super.onDestroy();
        CameraClipEditActivity.finishActivity();
    }

    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressRelativeLayout != null) {
                    progressRelativeLayout.dismissDialog();
                }
                CameraToastUtil.show(getResourceToString(R.string.disconenct_usb), mContext);
                TodayVideoListActivity.this.finish();
            }
        });
    }

    public native TempleteScoreBean[] getTemplete(VideoTag[] videoLableArray, VideoTag[] templeteLableArray);

}
