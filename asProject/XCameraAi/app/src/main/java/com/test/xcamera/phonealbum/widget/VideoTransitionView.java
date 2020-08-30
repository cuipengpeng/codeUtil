package com.test.xcamera.phonealbum.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.TimelineData;
import com.editvideo.dataInfo.TransitionInfo;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.adapter.VideoFilterAdapter;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.phonealbum.usecase.VideoTransitionData;
import com.test.xcamera.utils.CenterLayoutManager;
import com.test.xcamera.utils.proxy.Perform;
import com.test.xcamera.utils.proxy.click.NonDuplicateFactory;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoTrack;

import java.util.ArrayList;
import java.util.List;

public class VideoTransitionView extends FrameLayout implements View.OnClickListener {
    private RecyclerView transitionRecyclerView;
    private TextView mTranApplyAll;
    private ImageView mTranBack;
    private ImageView mTranComfirm;
    private VideoFilterAdapter transitionAdapter;
    private List<MusicBean> transitionBeanList = new ArrayList<>();
    private TransitionInfo mTransitionInfo;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private int mSelectPosition = -1;
    private ArrayList<ClipInfo> mClipInfoArrayList;


    public VideoTransitionView(Context context, NvsTimeline timeline, NvsVideoTrack videoTrack) {
        this(context, null);
        mTimeline = timeline;
        mVideoTrack = videoTrack;
        init(context);
        mClipInfoArrayList = TimelineData.init().cloneClipInfoData();
    }

    public VideoTransitionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {
        View root = inflate(context, R.layout.view_video_transition, this);
        mTranApplyAll = root.findViewById(R.id.tv_apply_all);
        mTranBack = root.findViewById(R.id.iv_tran_back);
        mTranComfirm = root.findViewById(R.id.iv_tran_confirm);
        mTranApplyAll.setOnClickListener(this);
        mTranBack.setOnClickListener(this);
        mTranComfirm.setOnClickListener(this);
        transitionRecyclerView = root.findViewById(R.id.transitionRecyclerView);
        CenterLayoutManager transitionLayoutManager = new CenterLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        transitionRecyclerView.setLayoutManager(transitionLayoutManager);
        transitionAdapter = new VideoFilterAdapter(context);
        transitionRecyclerView.setAdapter(transitionAdapter);
        mTransitionInfo = new TransitionInfo();
        transitionAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        transitionLayoutManager.smoothScrollToPosition(transitionRecyclerView, new RecyclerView.State(), pos);
                        for (int i = 0; i < transitionAdapter.mData.size(); i++) {
                            transitionAdapter.mData.get(i).setSelected(false);
                        }
                        transitionAdapter.mData.get(pos).setSelected(true);
                        transitionAdapter.notifyDataSetChanged();

                        if (pos == 0) {
                            mTransitionInfo.setTransitionId(null);
                        } else if (pos > 0 && pos < 4) {
                            mTransitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_BUILTIN);
                            mTransitionInfo.setTransitionId(transitionAdapter.mData.get(pos).getId());
                        } else {
                            mTransitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_PACKAGE);
                            mTransitionInfo.setTransitionId(transitionAdapter.mData.get(pos).getId());

                        }
                        VideoEditManger.setVideoTransition(mSelectPosition, mTimeline, mTransitionInfo);
                        if (mOnVideoTranCallBack != null) {
                            mOnVideoTranCallBack.OnVideoTransitionPreview();
                        }
                    }
                });
            }
        });
    }

    public void initData() {
        transitionBeanList.addAll(VideoTransitionData.getInstance().getTransitionDataList(mTransitionInfo));
        transitionAdapter.updateData(true, transitionBeanList);
    }

    public void setVideoTransitionData(int position) {
        if (position < 0) {
            return;
        }
        mSelectPosition = position;
        mTransitionInfo = TimelineData.instance().getClipInfoData().get(position).getTransitionInfo();
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_apply_all:
                if (mTransitionInfo != null) {
                    NonDuplicateFactory.proxy(new Perform() {
                        @Override
                        public void perform() {
                            VideoTransitionDialog dialog = new VideoTransitionDialog(getContext(), 0);
                            dialog.show();
                            dialog.setOnTransitionDialog(new VideoTransitionDialog.OnTransitionDialog() {
                                @Override
                                public void onTransitionConfirm() {
                                    VideoEditManger.setVideoTransition(-1, mTimeline, mTransitionInfo);
                                    if (mOnVideoTranCallBack != null) {
                                        mOnVideoTranCallBack.OnVideoTransitionPreview();
                                    }
                                }
                            });
                        }
                    });


                }
                break;
            case R.id.iv_tran_back:
                if (mOnVideoTranCallBack != null) {
                    NonDuplicateFactory.proxy(new Perform() {
                        @Override
                        public void perform() {
                            if (mClipInfoArrayList != null) {
                                TimelineData.instance().setClipInfoData(mClipInfoArrayList);
                            }
                            VideoEditManger.setVideoTransition(-1, mTimeline, null);
                            mOnVideoTranCallBack.OnVideoTransitionBack();
                        }
                    });

                }
                break;
            case R.id.iv_tran_confirm:
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        if (mOnVideoTranCallBack != null) {
                            mOnVideoTranCallBack.OnVideoTransitionBack();
                        }
                    }
                });
                break;
        }
    }

    private OnVideoTranCallBack mOnVideoTranCallBack;

    public void setOnVideoTranCallBack(OnVideoTranCallBack mOnVideoTranCallBack) {
        this.mOnVideoTranCallBack = mOnVideoTranCallBack;
    }

    public interface OnVideoTranCallBack {
        void OnVideoTransitionBack();

        void OnVideoTransitionPreview();
    }
}
