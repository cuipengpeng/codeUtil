package com.test.xcamera.cameraclip;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dueeeke.videoplayer.player.IjkVideoView;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.cameraclip.adapter.SampleVideoAdapter;
import com.test.xcamera.cameraclip.adapter.SampleVideoItemTouchCallBack;
import com.test.xcamera.cameraclip.adapter.SampleVideoOverLayLayoutManager;
import com.test.xcamera.cameraclip.bean.VideoSegment;
import com.test.xcamera.home.GoUpActivity;
import com.test.xcamera.utils.AsyncCopyAssertRunnable;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.DownloadWebFileUtil;
import com.test.xcamera.utils.Md5Util;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.utils.StringUtil;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;
import com.test.xcamera.widget.AlbumPlayController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SampleVideoActivity extends MOBaseActivity{
    @BindView(R.id.rv_sampleVideoActivity_recycleView)
    RecyclerView recyclerView;
    @BindView(R.id.left_iv_title)
    ImageView leftIvTitle;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.tv_sampleVideoActivity_use)
    TextView useTextView;
    @BindView(R.id.progress1)
    ProgressBar progressBar;
//    @BindView(R.id.rl_sampleVideoActivity_progress)
//    RelativeLayout progressRelativeLayout;
    @BindView(R.id.tv_sampleVideoActivity_loadingContent)
    TextView loadingContentTextView;

    CommonDownloadDialog progressRelativeLayout;
    private  DownloadVideoTempleteDataUtil downloadVideoTempleteDataUtil;
    private SampleVideoAdapter gridAdapter;
    private List<VideoTemplete> videoTempleteList = new ArrayList<>();
    private List<VideoTemplete> mLocalVideoTempleteList = new ArrayList<>();
    private List<VideoSegment> mAllSortedVideoSegmentList = new ArrayList<>();
    private NetVideoTempleteHelper netVideoTempleteHelper;
    private VideoTemplete selectVideoTemplete;
    private VideoTemplete currentShowVideoTemplete;
    public static final String KEY_OF_SAMPLE_VIDEO_TEMPLETE = "sampleVideoTempleteKey";
    private AlbumPlayController videoControler;

    @OnClick({R.id.left_iv_title, R.id.tv_sampleVideoActivity_use, R.id.rl_sampleVideoActivity_progress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_iv_title:
                onBackPressed();
                break;
            case R.id.tv_sampleVideoActivity_use:
                if(currentShowVideoTemplete.isNetTemplete()){
                    if (NetworkUtil.isNetworkAvailable(SampleVideoActivity.this)) {
                        progressRelativeLayout.showDialog(false, false);
                        netVideoTempleteHelper.downloadTempleteZipFile(SampleVideoActivity.this, currentShowVideoTemplete, false,  new NetVideoTempleteHelper.NetTempleteCallback() {
                            @Override
                            public void onProgress(int progress) {

                                String format = String.format(mContext.getResources().getString(R.string.download_save_progress), progress) + "%";
                                progressRelativeLayout.setProgress(format);
//                                loadingContentTextView.setText(getResources().getString(R.string.loadingData)+" "+progress+"%");
                            }

                            @Override
                            public void onSuccess(VideoTemplete videoTemplete) {
                                if(!isDestroyed()){
                                    if (AccessoryManager.getInstance().mIsRunning) {

                                        VideoTemplete filledDatavideoTemplete = OneKeyMakeVideoHelper.newFillVideoTempleteData(videoTemplete, mAllSortedVideoSegmentList);
                                        if (OneKeyMakeVideoHelper.makeVideoType==OneKeyMakeVideoHelper.MakeVideoType.SIDE_KEY
                                                &&GoUpActivity.checkVideoSegmentCount(SampleVideoActivity.this, filledDatavideoTemplete)) {
                                            progressRelativeLayout.dismissDialog();
                                            return;
                                        }
                                        downloadVideoTempleteDataUtil.startPlayDownload(true, videoTemplete, true);
                                    } else {
                                        Toast.makeText(SampleVideoActivity.this, getResources().getString(R.string.connectCamera), Toast.LENGTH_SHORT).show();
                                        progressRelativeLayout.dismissDialog();
                                    }

                                }
                            }

                            @Override
                            public void onFail(String error) {
                                if(!isDestroyed()) {
                                    String msg = getResources().getString(R.string.netTempleteDownloadException);
                                    if (StringUtil.notEmpty(error)) {
                                        msg = error;
                                    }
                                    Toast.makeText(SampleVideoActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    progressRelativeLayout.dismissDialog();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(SampleVideoActivity.this, getResources().getString(R.string.netConnectException), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if (AccessoryManager.getInstance().mIsRunning) {
                        VideoTemplete videoTemplete = OneKeyMakeVideoHelper.newFillVideoTempleteData(currentShowVideoTemplete, mAllSortedVideoSegmentList);
                        if (GoUpActivity.checkVideoSegmentCount(SampleVideoActivity.this, videoTemplete)) {
                            return;
                        }
                        downloadVideoTempleteDataUtil.startPlayDownload(true, videoTemplete, true);
                    } else {
                        Toast.makeText(SampleVideoActivity.this, getResources().getString(R.string.connectCamera), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mLocalVideoTempleteList = OneKeyMakeVideoHelper.convertTempleteJsonToBean(this);
        for(VideoTemplete videoTemplete: mLocalVideoTempleteList){
            String assertRelativePath = Constants.template_path+videoTemplete.getExample_video();
            String sufferFix = assertRelativePath.substring(assertRelativePath.lastIndexOf("."));
            String desVideoPath = Constants.sampleVideoLocalPath+"/"+Md5Util.getMD5(assertRelativePath)+sufferFix;
            File desFile = new File(desVideoPath);
            videoTemplete.setLocalSampleVideoPath(desVideoPath);
            if(!desFile.exists()){
                DownloadWebFileUtil.getInstance().execute(new AsyncCopyAssertRunnable(this, assertRelativePath));
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public int initView() {
        return R.layout.activity_sample_video;
    }

    @Override
    public void initData() {
        netVideoTempleteHelper = new NetVideoTempleteHelper();
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
                Intent intent = new Intent();
                intent.putExtra(KEY_OF_SAMPLE_VIDEO_TEMPLETE, videoTemplete);
                setResult(201, intent);
                finish();
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
                    Toast.makeText(SampleVideoActivity.this, getResources().getString(R.string.cannotCancelWhenDownload), Toast.LENGTH_SHORT).show();
                }
            }
        });
        leftIvTitle.setImageResource(R.mipmap.backicon);
        tvMiddleTitle.setText("");
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_ff7700), PorterDuff.Mode.SRC_IN);


//        mAllSortedVideoSegmentList = getIntent().getParcelableArrayListExtra(CameraClipEditActivity.KEY_OF_ALL_VIDEO_SEGMENT_LIST);
        mAllSortedVideoSegmentList = AiCameraApplication.mApplication.mAllSortedVideoSegmentList;
        selectVideoTemplete = getIntent().getParcelableExtra(CameraClipEditActivity.KEY_OF_TEMPLETE);
        currentShowVideoTemplete = getIntent().getParcelableExtra(CameraClipEditActivity.KEY_OF_CLICK_TEMPLETE);
        videoTempleteList = getIntent().getParcelableArrayListExtra(CameraClipEditActivity.KEY_OF_NET_TEMPLETE_LIST);

        for(int i=0; i<videoTempleteList.size();i++) {
            VideoTemplete templete = videoTempleteList.get(i);
            for (int j = 0; j < mLocalVideoTempleteList.size(); j++) {
                if (!templete.isNetTemplete() && mLocalVideoTempleteList.get(j).getIcon().equalsIgnoreCase(templete.getIcon())) {
                    templete.setLocalSampleVideoPath(mLocalVideoTempleteList.get(j).getLocalSampleVideoPath());
                }
            }
        }

        VideoTemplete videoTemplete = null;
        for(int i=0; i<videoTempleteList.size();i++){
            if(currentShowVideoTemplete.isNetTemplete()){
                if(currentShowVideoTemplete.getId() == videoTempleteList.get(i).getId()){
                    videoTemplete = videoTempleteList.remove(i);
                    break;
                }
            }else {
                if(currentShowVideoTemplete.getIcon().equalsIgnoreCase(videoTempleteList.get(i).getIcon())){
                    videoTemplete = videoTempleteList.remove(i);
                    break;
                }
            }
        }
        //adapter中当前显示放在最后一个
        videoTempleteList.add(videoTemplete);
        checkUseTextView();

        initRecycleView();
    }

    private void initRecycleView() {
        recyclerView.setLayoutManager(new SampleVideoOverLayLayoutManager());
        gridAdapter = new SampleVideoAdapter(mContext);
        recyclerView.setAdapter(gridAdapter);
        SampleVideoItemTouchCallBack sampleVideoItemTouchCallBack = new SampleVideoItemTouchCallBack(recyclerView, gridAdapter, gridAdapter.mData);
        sampleVideoItemTouchCallBack.setOnScrollListener(new SampleVideoItemTouchCallBack.OnScrollListener(){

            @Override
            public void onScrollOver(int position) {
                currentShowVideoTemplete = gridAdapter.mData.get(position);
                checkUseTextView();
            }
        });
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(sampleVideoItemTouchCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                int position = recyclerView.getLayoutManager().getPosition(view);
                if (position == (videoTempleteList.size()-1)) {
                    View itemView = recyclerView.getLayoutManager().findViewByPosition(position);
                    if (itemView != null) {
//                        itemView.findViewById(R.id.iv_sampleVideoctivity_item_image).setVisibility(View.GONE);
                        IjkVideoView ijkVideoView = itemView.findViewById(R.id.ijk_sampleVideoctivity_item_ijkPlayer);
                        videoControler= (AlbumPlayController) ijkVideoView.mVideoController;
                        ijkVideoView.start();
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                view.findViewById(R.id.iv_sampleVideoctivity_item_image).setVisibility(View.VISIBLE);
                IjkVideoView ijkVideoView = view.findViewById(R.id.ijk_feedAdapter_videoPlayer);
                if (ijkVideoView != null) {
                    ijkVideoView.release();
                }
            }
        });

        gridAdapter.updateData(true, videoTempleteList);
    }

    public void checkUseTextView() {
        useTextView.setText(getResources().getString(R.string.use));
        useTextView.setBackgroundResource(R.drawable.circle_corner_green_bg_normal_2dp);
        useTextView.setClickable(true);
        if(currentShowVideoTemplete.isCheck()){
            useTextView.setText(getResources().getString(R.string.using));
            useTextView.setClickable(false);
            useTextView.setBackgroundResource(R.drawable.circle_corner_gray_bg_normal_2dp);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(videoControler!=null){
            videoControler.pause();
        }
    }

    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();

           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(progressRelativeLayout!=null ){
                        progressRelativeLayout.dismissDialog();
                    }
                    CameraToastUtil.show(getResourceToString(R.string.disconenct_usb), mContext);
                    SampleVideoActivity.this.finish();
                }
            });
    }


    public static void open(FragmentActivity context, VideoTemplete videoTemplete, VideoTemplete clickVideoTemplete, List<VideoTemplete> netVideoTempleteList){
        Intent intent = new Intent(context, SampleVideoActivity.class);
        intent.putExtra(CameraClipEditActivity.KEY_OF_TEMPLETE, videoTemplete);
        intent.putExtra(CameraClipEditActivity.KEY_OF_CLICK_TEMPLETE, clickVideoTemplete);
        intent.putParcelableArrayListExtra(CameraClipEditActivity.KEY_OF_NET_TEMPLETE_LIST, (ArrayList<? extends Parcelable>) netVideoTempleteList);
        context.startActivityForResult(intent,301);
    }

}
