package com.test.xcamera.cameraclip;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.MoErrorData;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.cameraclip.adapter.TodayFileListAdapter;
import com.test.xcamera.cameraclip.bean.VideoFile;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.statistic.StatisticOneKeyMakeVideo;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DateUtils;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.utils.StringUtil;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;
import com.test.xcamera.widget.ActivityContainer;
import com.moxiang.common.logging.Logcat;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: mz
 * Time:  2019/10/28
 */
public class TodayFileListActivity extends MOBaseActivity {
    @BindView(R.id.rv_view)
    RecyclerView recyclerView;
    @BindView(R.id.left_iv_title)
    ImageView leftIvTitle;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.right_tv_titlee)
    TextView rightTvTitlee;
    @BindView(R.id.ll_todayFileListActivity_selectAll)
    LinearLayout selectAllLinearLayout;
    @BindView(R.id.iv_todayFileListActivity_selectAll)
    ImageView selectAllImageView;
    @BindView(R.id.tv_todayFileListActivity_content)
    TextView contentTextView;
    @BindView(R.id.progress1)
    ProgressBar progressBar;
    @BindView(R.id.tv_todayFileListActivity_loadingContent)
    TextView loadingContentTextView;
    @BindView(R.id.rl_todayFileListActivity_emptyData)
    RelativeLayout emptyDataRelativeLayout;

    CommonDownloadDialog progressRelativeLayout;
    private DownloadVideoTempleteDataUtil downloadVideoTempleteDataUtil;
    private OneKeyMakeVideoHelper oneKeyMakeVideoHelper;
    private TodayFileListAdapter gridAdapter;
    private NetVideoTempleteHelper netVideoTempleteHelper;
    private int selectedCount;
    public static final String KEY_OF_DATE= "dateKey";

    @OnClick({R.id.left_iv_title, R.id.right_tv_titlee, R.id.rl_progress, R.id.ll_todayFileListActivity_selectAll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_todayFileListActivity_selectAll:
                if(selectedCount==gridAdapter.mData.size()){
                  for(int i=0; i<gridAdapter.mData.size(); i++) {
                      gridAdapter.mData.get(i).setSelected(false);
                  }
                    gridAdapter.notifyDataSetChanged();
                    selectedCount=0;
                }else {
                    for(int i=0; i<gridAdapter.mData.size(); i++){
                        gridAdapter.mData.get(i).setSelected(true);
                    }
                    gridAdapter.notifyDataSetChanged();
//                    selectedCount = AiCameraApplication.mApplication.mVideoFileList.size();
                    selectedCount = gridAdapter.mData.size();
                }
                checkoutSelectCount();
                break;
            case R.id.left_iv_title:
                onBackPressed();
                break;
            case R.id.right_tv_titlee:
                StatisticOneKeyMakeVideo.getInstance().setOnEvent(StatisticOneKeyMakeVideo.TODAY_WONDERFUL_NEXT);
                makeVideo();
                break;
        }
    }

    private void makeVideo() {
        AiCameraApplication.mApplication.mVideoFileList.clear();
        OneKeyMakeVideoXmlParser.TOTAL_DURATION=0;
        for (int i = 0; i < gridAdapter.mData.size(); i++) {
            if (gridAdapter.mData.get(i).isSelected()) {
                OneKeyMakeVideoXmlParser.TOTAL_DURATION+=(gridAdapter.mData.get(i).getClose_time()-gridAdapter.mData.get(i).getCreate_time());
                AiCameraApplication.mApplication.mVideoFileList.add(gridAdapter.mData.get(i));
            }
        }
        if(OneKeyMakeVideoXmlParser.TOTAL_DURATION<OneKeyMakeVideoXmlParser.MIN_TOTAL_DURATION){
            Toast.makeText(this, getResources().getString(R.string.minVideoFileTotalLength), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            oneKeyMakeVideoHelper.newScoreVideoSegmentForTodayVideoFiles(AiCameraApplication.mApplication.mVideoFileList);

            if (NetworkUtil.isNetworkAvailable(TodayFileListActivity.this)) {
//          if (false) {
                progressRelativeLayout.showDialog(false, false);
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("net templete       timestamp="+System.currentTimeMillis()).out();
                netVideoTempleteHelper.getNetVideoTemplete(TodayFileListActivity.this, oneKeyMakeVideoHelper, new NetVideoTempleteHelper.NetTempleteCallback() {
                    @Override
                    public void onProgress(int progress) {
                        String format = String.format(mContext.getResources().getString(R.string.download_save_progress), progress) + "%";
                        progressRelativeLayout.setProgress(format);
                    }

                    @Override
                    public void onSuccess(VideoTemplete videoTemplete) {
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("net templete success       timestamp="+System.currentTimeMillis()).out();
                        OneKeyMakeVideoHelper.newFillVideoTempleteData(videoTemplete, oneKeyMakeVideoHelper.mAllSortedVideoSegmentList);
                        downloadVideoTempleteDataUtil.startPlayDownload(true, videoTemplete, false);
                    }

                    @Override
                    public void onFail(String error) {
                        String msg = getResources().getString(R.string.netTempleteDownloadException);
                        if (StringUtil.notEmpty(error)) {
                            msg = error;
                        }
                        Toast.makeText(TodayFileListActivity.this, msg, Toast.LENGTH_SHORT).show();
                        if (progressRelativeLayout != null)
                            progressRelativeLayout.dismissDialog();
                    }
                });
            } else {
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("local templete       timestamp="+System.currentTimeMillis()).out();
                VideoTemplete filledDatavideoTemplete = oneKeyMakeVideoHelper.fillVideoTempleteForOneKeyMakeVideo();
                downloadVideoTempleteDataUtil.startPlayDownload(true, filledDatavideoTemplete, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int initView() {
        return R.layout.activity_today_file_list;
    }

    @Override
    public void initData() {
        netVideoTempleteHelper = new NetVideoTempleteHelper();
        oneKeyMakeVideoHelper = new OneKeyMakeVideoHelper(this, OneKeyMakeVideoHelper.MakeVideoType.TODAY_WONDERFUL);
        downloadVideoTempleteDataUtil = new DownloadVideoTempleteDataUtil();
        downloadVideoTempleteDataUtil.setOnFinishDownloadVideoCallback(new DownloadVideoTempleteDataUtil.OnFinishDownloadVideoCallback() {
            @Override
            public void onStart() {
                if(!progressRelativeLayout.isShowing()){
                    progressRelativeLayout.showDialog(false, true);
                }
            }

            @Override
            public void onProgress(int progress) {
                String format = String.format(mContext.getResources().getString(R.string.download_save_progress), progress) + "%";
                if(progressRelativeLayout!=null){
                    progressRelativeLayout.setProgress(format);
                }
            }

            @Override
            public void onFinish(VideoTemplete videoTemplete) {
                progressRelativeLayout.dismissDialog();
                CameraClipEditActivity.open(TodayFileListActivity.this, videoTemplete, netVideoTempleteHelper.mNetVideoTempleteList);
            }

            @Override
            public void onFail(String errorInfo) {
                progressRelativeLayout.dismissDialog();
            }
        });

        progressRelativeLayout = new CommonDownloadDialog(this);
        progressRelativeLayout.setBackKeyListener(new CommonDownloadDialog.BackKeyListener() {
            @Override
            public void onBack(Dialog mDialog) {
                if (progressRelativeLayout.isShowing()) {
//            ConnectionManager.getInstance().stopPlay(null);
                    Toast.makeText(TodayFileListActivity.this, getResources().getString(R.string.cannotCancelWhenDownload), Toast.LENGTH_SHORT).show();
                }
            }
        });
        leftIvTitle.setImageResource(R.mipmap.backicon);
        long date = getIntent().getLongExtra(KEY_OF_DATE, 0);
        String monthStr = DateUtils.DateFormat("MM", date);
        String dayStr = DateUtils.DateFormat("dd", date);
        tvMiddleTitle.setText(monthStr+getResources().getString(R.string.month)+dayStr+getResources().getString(R.string.day));
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_ff7700), PorterDuff.Mode.SRC_IN);
        RelativeLayout.LayoutParams rightTvTitleeParams = (RelativeLayout.LayoutParams) rightTvTitlee.getLayoutParams();
        rightTvTitleeParams.height = DensityUtils.dp2px(mContext, 32);
        rightTvTitleeParams.width = DensityUtils.dp2px(mContext, 56);
        rightTvTitlee.setLayoutParams(rightTvTitleeParams);
        rightTvTitlee.setVisibility(View.VISIBLE);
        rightTvTitlee.setText(getString(R.string.makeVideo));
        rightTvTitlee.setTextColor(Color.WHITE);
        rightTvTitlee.setBackgroundResource(R.drawable.circle_corner_green_bg_normal_2dp);
        ActivityContainer.getInstance().addActivity(this);

        emptyDataRelativeLayout.setVisibility(View.GONE);
        initRecycleView();
    }

    private void initRecycleView() {
        for (int i = 0; i < AiCameraApplication.mApplication.mVideoFileList.size(); i++) {
            AiCameraApplication.mApplication.mVideoFileList.get(i).setSelected(false);
        }
        selectedCount = 0;
        contentTextView.setText("已选 " + selectedCount + "/" + AiCameraApplication.mApplication.mVideoFileList.size());
        GridLayoutManager layoutManage = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(layoutManage);
        gridAdapter = new TodayFileListAdapter(mContext);
        recyclerView.setAdapter(gridAdapter);
        gridAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                VideoFile videoFile = gridAdapter.mData.get(position);
                videoFile.setSelected(!videoFile.isSelected());
                gridAdapter.notifyItemChanged(position);
//                gridAdapter.notifyDataSetChanged();

                if (videoFile.isSelected()) {
                    selectedCount++;
                } else {
                    selectedCount--;
                }
                checkoutSelectCount();
            }
        });
        gridAdapter.updateData(true, AiCameraApplication.mApplication.mVideoFileList);
        checkoutSelectCount();
    }

    private void checkoutSelectCount() {
        if (selectedCount > 0) {
            rightTvTitlee.setClickable(true);
            rightTvTitlee.setTextColor(Color.WHITE);
            rightTvTitlee.setBackgroundResource(R.drawable.circle_corner_green_bg_normal_2dp);
        } else {
            rightTvTitlee.setClickable(false);
            rightTvTitlee.setTextColor(getResources().getColorStateList(R.color.color_666666));
            rightTvTitlee.setBackgroundResource(R.drawable.circle_corner_gray_bg_normal_2dp);
        }
        if(selectedCount==gridAdapter.mData.size()){
            selectAllImageView.setBackgroundResource(R.mipmap.video_file_selected);
        }else {
            selectAllImageView.setBackgroundResource(R.mipmap.video_file_unselect);
        }
        contentTextView.setText("已选 " + selectedCount + "/" + gridAdapter.mData.size());
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectionManager.getInstance().setErrorI(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectionManager.getInstance().setErrorI(new MoErrorCallback() {
            @Override
            public void onError(MoErrorData data) {
                if (data.event == MoErrorCallback.SD_EVENT) {
                    switch (data.status) {
                        case MoErrorCallback.SD_OUT:
//                            showEmptyDataView();
                            DlgUtils.toast(TodayFileListActivity.this, getResources().getString(R.string.sdcard_out));
                            finish();
                            CameraClipEditActivity.finishActivity();
                            break;
                        case MoErrorCallback.SD_IN:
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
    public void disconnectedUSB() {
        super.disconnectedUSB();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressRelativeLayout != null) {
                    progressRelativeLayout.dismissDialog();
                }
                CameraToastUtil.show(getResourceToString(R.string.disconenct_usb), mContext);
                TodayFileListActivity.this.finish();
            }
        });
    }

    public static void open(FragmentActivity context, long date) {
        Intent intent = new Intent(context, TodayFileListActivity.class);
        intent.putExtra(KEY_OF_DATE,date);
        context.startActivity(intent);
    }

}
