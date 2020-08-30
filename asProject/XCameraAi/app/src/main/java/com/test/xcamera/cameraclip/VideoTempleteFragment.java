package com.test.xcamera.cameraclip;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.cameraclip.adapter.CameraTemplateAdater;
import com.test.xcamera.cameraclip.bean.VideoSegment;
import com.test.xcamera.home.GoUpActivity;
import com.test.xcamera.home.adapter.FeedRecycleViewAdapter;
import com.test.xcamera.statistic.StatisticOneKeyMakeVideo;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.utils.StringUtil;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoTempleteFragment extends Fragment {

    @BindView(R.id.rv_videoTempleteFragment_videoTempleteList)
    RecyclerView videoTempleteRecycleView;

    public CameraTemplateAdater videoTempleteAdapter;
    private int downloadTempletePosition = 0;
    private List<VideoTemplete> mTempleteList= new ArrayList<>();

    private  DownloadVideoTempleteDataUtil downloadVideoTempleteDataUtil;
    private CommonDownloadDialog progressRelativeLayout;
    private NetVideoTempleteHelper netVideoTempleteHelper;
    private List<VideoSegment> mAllSortedVideoSegmentList = new ArrayList<>();
    private TempleteDownloadFinishCallback mTempleteDownloadFinishCallback;

    public static Fragment newInstance(List<VideoTemplete> templeteList, CommonDownloadDialog progressRelativeLayout){
        VideoTempleteFragment videoTempleteFragment = new VideoTempleteFragment();
        videoTempleteFragment.mTempleteList = templeteList;
        videoTempleteFragment.progressRelativeLayout = progressRelativeLayout;
        return videoTempleteFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View subScreenView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_video_templete, container,false);
        ButterKnife.bind(this, subScreenView);
        return subScreenView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecycleView();
        mAllSortedVideoSegmentList = AiCameraApplication.mApplication.mAllSortedVideoSegmentList;
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
                String format = String.format(getActivity().getResources().getString(R.string.download_save_progress), progress) + "%";
                if(progressRelativeLayout!=null){
                    progressRelativeLayout.setProgress(format);
                }
            }

            @Override
            public void onFinish(VideoTemplete videoTemplete) {
                progressRelativeLayout.dismissDialog();
                for(int i=0; i<mTempleteList.size(); i++){
                    mTempleteList.get(i).setCheck(false);
                    mTempleteList.get(i).setHasClickOnce(false);
                }
                mTempleteList.get(downloadTempletePosition).setCheck(true);
                videoTempleteAdapter.notifyDataSetChanged();
                mTempleteDownloadFinishCallback.onDownloadFinish(videoTemplete);
            }

            @Override
            public void onFail(String errorInfo) {
                progressRelativeLayout.dismissDialog();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    private void initRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        videoTempleteRecycleView.setLayoutManager(linearLayoutManager);
        videoTempleteAdapter = new CameraTemplateAdater(getActivity());
        videoTempleteRecycleView.setAdapter(videoTempleteAdapter);
        videoTempleteAdapter.updateData(true, mTempleteList);

        videoTempleteAdapter.setOnRecyclerItemClickListener(new FeedRecycleViewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(! mTempleteList.get(position).isCheck()){
                    if(mTempleteList.get(position).isHasClickOnce()){
                        StatisticOneKeyMakeVideo.getInstance().setOnEvent(StatisticOneKeyMakeVideo.SIDE_KEY_SELECT_TEMPLETE_ID, mTempleteList.get(position).getId()+"");
                        downloadTempletePosition = position;
                        downloadTemplete(mTempleteList.get(position));
                    }else {
                        for(int i=0; i<mTempleteList.size(); i++){
                            mTempleteList.get(i).setHasClickOnce(false);
                        }
                        mTempleteList.get(position).setHasClickOnce(true);
                        videoTempleteAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
    }

    
    private void downloadTemplete(VideoTemplete downloadVideoTemplete){
        if(downloadVideoTemplete.isNetTemplete()){
            if (NetworkUtil.isNetworkAvailable(getActivity())) {
                progressRelativeLayout.showDialog(false, false);
                netVideoTempleteHelper.downloadTempleteZipFile(getActivity(), downloadVideoTemplete, false,  new NetVideoTempleteHelper.NetTempleteCallback() {
                    @Override
                    public void onProgress(int progress) {
                        String format = String.format(getActivity().getResources().getString(R.string.download_save_progress), progress) + "%";
                        progressRelativeLayout.setProgress(format);
                    }

                    @Override
                    public void onSuccess(VideoTemplete videoTemplete) {
                            if (AccessoryManager.getInstance().mIsRunning) {
                                VideoTemplete filledDatavideoTemplete = OneKeyMakeVideoHelper.newFillVideoTempleteData(videoTemplete, mAllSortedVideoSegmentList);
                                if (OneKeyMakeVideoHelper.makeVideoType==OneKeyMakeVideoHelper.MakeVideoType.SIDE_KEY
                                        &&GoUpActivity.checkVideoSegmentCount(getActivity(), filledDatavideoTemplete)) {
                                    progressRelativeLayout.dismissDialog();
                                    return;
                                }
                                downloadVideoTempleteDataUtil.startPlayDownload(true, videoTemplete, false);
                            } else {
                                Toast.makeText(getActivity(), getResources().getString(R.string.connectCamera), Toast.LENGTH_SHORT).show();
                                progressRelativeLayout.dismissDialog();
                            }
                    }

                    @Override
                    public void onFail(String error) {
                            String msg = getResources().getString(R.string.netTempleteDownloadException);
                            if (StringUtil.notEmpty(error)) {
                                msg = error;
                            }
                            Toast.makeText(AiCameraApplication.mApplication, msg, Toast.LENGTH_SHORT).show();
                            progressRelativeLayout.dismissDialog();
                    }
                });
            }else {
                Toast.makeText(AiCameraApplication.mApplication, getResources().getString(R.string.netConnectException), Toast.LENGTH_SHORT).show();
            }
        }else {
            if (AccessoryManager.getInstance().mIsRunning) {
                VideoTemplete videoTemplete = OneKeyMakeVideoHelper.newFillVideoTempleteData(downloadVideoTemplete, mAllSortedVideoSegmentList);
                if (GoUpActivity.checkVideoSegmentCount(getActivity(), videoTemplete)) {
                    return;
                }
                downloadVideoTempleteDataUtil.startPlayDownload(true, videoTemplete, false);
            } else {
                Toast.makeText(AiCameraApplication.mApplication, getResources().getString(R.string.connectCamera), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void setTempleteDownloadFinishCallback(TempleteDownloadFinishCallback clickListener) {
        this.mTempleteDownloadFinishCallback = clickListener;
    }

    public interface TempleteDownloadFinishCallback {
        void onDownloadFinish(VideoTemplete videoTemplete);
    }
}