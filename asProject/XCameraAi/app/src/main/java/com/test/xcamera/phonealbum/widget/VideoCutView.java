package com.test.xcamera.phonealbum.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.editvideo.dataInfo.TimelineData;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.adapter.ThumbnailAdapter2;
import com.test.xcamera.phonealbum.adapter.ThumbnailAdapter3;
import com.test.xcamera.phonealbum.adapter.TimeSpan;
import com.test.xcamera.phonealbum.bean.ThumbnailBean;
import com.test.xcamera.phonealbum.bean.VideoTimeLineBean;
import com.test.xcamera.phonealbum.widget.drag.ItemVideoTouchHelperCallback;
import com.test.xcamera.phonealbum.widget.drag.OnStartDragListener;
import com.test.xcamera.utils.ItemTouchHelperCallBack;
import com.test.xcamera.utils.PUtil;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;

import java.util.List;


public class VideoCutView extends FrameLayout implements OnStartDragListener {
    private static final int SEEK_BAR_MODE_VOICE = 201;
    private RecyclerView mThumbnailRecyclerView;
    private RecyclerView mBottomThumbnailRecyclerView;
    private RecyclerView mTimeLineRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mLinearLayoutBottomManager;
    private LinearLayoutManager mLinearLayoutTimeManager;
    private ThumbnailAdapter2 mThumbnailAdapter;
    private ThumbnailAdapter3 mThumbnailBottomAdapter;
    private VideoTimeLineAdapter mVideoTimeLineAdapter;
    private int mSelectedItemPosition = 0;
    private boolean showSeekBar = false;

    public VideoCutView(Context context) {
        this(context, null);
    }

    private RecycleViewTimeLinUtils mRecycleViewUtils;
    private RecycleViewTimeLinUtils mBottomRecycleViewUtils;
    private RecycleViewTimeLinUtils mTimeLineViewUtils;
    private LinearLayout mLLVideoDelItem;

    private ItemTouchHelper mItemTouchHelper;
    private ImageView mDeleteIconImageView;
    private TextView mDeleteIconText;

    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private boolean mIsDragIng = false;
    ItemVideoTouchHelperCallback mTtemVideoTouchHelperCallback;
    private FrameLayout mFrameLayoutContext;
    private TextView mImportVideo;
    private SeekRangeBar mSeekRangeBar;
    int isCreatScroll=0;
    public VideoCutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        if (mOnViewFinishInflate != null) {
            mOnViewFinishInflate.onFinishInflate();
        }
    }

    private void init(Context context) {
        View root = inflate(context, R.layout.view_video_cut, this);
        mFrameLayoutContext=root.findViewById(R.id.mFrameLayoutContext);
        mImportVideo=root.findViewById(R.id.mImportVideo);
        mLLVideoDelItem = root.findViewById(R.id.mLLVideoDelItem);
        mDeleteIconImageView = root.findViewById(R.id.mDeleteIconImageView);
        mDeleteIconText = root.findViewById(R.id.mDeleteIconText);
        mLLVideoDelItem.setVisibility(GONE);
        mImportVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnVideoCutCallBack!=null){
                    mOnVideoCutCallBack.onVideoImport(mSelectedItemPosition+1);
                }
            }
        });
        initSeekRangeBar();
        initThumbView(root,context);
        initBottomThumbView(root,context);
        initTimeLineView(root,context);
    }
    private boolean mIsThumbScroll;
    /**
     * 初始化缩略图View
     */
    private void initThumbView(View root,Context context){

        mLinearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        mThumbnailRecyclerView = root.findViewById(R.id.mThumbnailRecyclerView);
        mThumbnailRecyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                mIsThumbScroll=false;
                return false;
            }
        });
        mThumbnailRecyclerView.setLayoutManager(mLinearLayoutManager);
        mThumbnailAdapter = new ThumbnailAdapter2(context);
        mThumbnailAdapter.setDragStartListener(this);
        mThumbnailAdapter.setOnVideoCutThumbCallBack(new ThumbnailAdapter2.OnVideoCutThumbCallBack() {
            @Override
            public void onVideoTransition(int position) {
                showVideoTransition(position);
            }

            @Override
            public void onVideoOnClick(int position) {
                int selectPosition=position-1;
                if(selectPosition<0||selectPosition>=TimelineData.instance().getClipCount()){
                    return;
                }
                if(selectPosition==mSelectedItemPosition){
                    return;
                }
                NvsVideoClip nvsVideoClip=mVideoTrack.getClipByIndex(selectPosition);
                if(nvsVideoClip==null){
                    return;
                }
                long time;
                if(selectPosition<mSelectedItemPosition){
                    time=nvsVideoClip.getOutPoint()-100000;
                }else {
                    time=nvsVideoClip.getInPoint()+20000;

                }
                mSelectedItemPosition=selectPosition;
                scrollThumbToPosition(time);
            }
        });

        mThumbnailAdapter.setOnTrimChangeListener(new TimeSpan.OnTrimChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onChange(int currentPosition, boolean dragLeft, int distance) {
                Log.i("onChange", "currentPosition:" + currentPosition
                        + " dragLeft:" + dragLeft
                        + " distance:" + distance);
                float zOrder = mLinearLayoutManager.findViewByPosition(currentPosition).getZ();
                if (dragLeft) {
                    for (int i = currentPosition - 1; i >= 0; i--) {
                        View itemView = mLinearLayoutManager.findViewByPosition(i);
                        //未显示出来的view是null的，过滤掉
                        if (itemView == null) {
                            continue;
                        }
                        zOrder++;
                        itemView.setZ(zOrder);
                        mThumbnailAdapter.mData.get(i).setzOrder(zOrder);
                        itemView.setX(itemView.getX() + distance);
                    }
                } else {
                    for (int i = currentPosition + 1; i < mLinearLayoutManager.getItemCount(); i++) {
                        View itemView = mLinearLayoutManager.findViewByPosition(i);
                        //未显示出来的view是null的，过滤掉
                        if (itemView == null) {
                            continue;
                        }
                        zOrder++;
                        itemView.setZ(zOrder);
                        mThumbnailAdapter.mData.get(i).setzOrder(zOrder);
                        itemView.setX(itemView.getX() + distance);
                    }
                }

            }
        });
        mThumbnailRecyclerView.setAdapter(mThumbnailAdapter);

        mRecycleViewUtils = new RecycleViewTimeLinUtils(mThumbnailRecyclerView);
        mTtemVideoTouchHelperCallback = new ItemVideoTouchHelperCallback(mLLVideoDelItem, mThumbnailAdapter);
        mTtemVideoTouchHelperCallback.setDragListener(new ItemTouchHelperCallBack.DragListener() {
            @Override
            public void onDragStart() {
                mLLVideoDelItem.setVisibility(VISIBLE);
                if (mOnVideoCutCallBack != null) {
                    mOnVideoCutCallBack.onDragStart();
                }
            }

            @Override
            public void onDragFinish(boolean delete, int selectedPosition) {
                mDragFinishHandler.removeCallbacksAndMessages(null);
                onVideoClipDragFinish(delete,selectedPosition);
            }

            @Override
            public void onDragAreaChange(boolean isInside) {
                if (isInside) {
                    mDeleteIconImageView.setImageResource(R.drawable.ic_edit_deleted);
                    mDeleteIconText.setText(R.string.remove);
                } else {
                    mDeleteIconImageView.setImageResource(R.drawable.ic_edit_no_delete);
                    mDeleteIconText.setText(R.string.touch_text);
                }
            }
        });
        mItemTouchHelper = new ItemTouchHelper(mTtemVideoTouchHelperCallback);
        mItemTouchHelper.attachToRecyclerView(mThumbnailRecyclerView);
        mThumbnailRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int scrollX = mRecycleViewUtils.getScrollX(mThumbnailAdapter.getmBeans());
                Log.i("club","club:scrollx:"+scrollX);
                if(scrollX==-1){
                    return;
                }
                if (!mIsDragIng) {
                    if(!mIsThumbScroll&&mIsUser){
                        mRecycleViewUtils.scrollToX(getContext(), mLinearLayoutTimeManager, scrollX, mVideoTimeLineAdapter.getmBeans());
                        mRecycleViewUtils.scrollToX(getContext(), mLinearLayoutBottomManager, scrollX, mThumbnailBottomAdapter.getmBeans());
                        setThumbSelectStatus(scrollX,-1);
                    }
                }

            }
        });
    }
    private Handler mDragFinishHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            boolean delete=(boolean)msg.obj;
            int position=msg.arg1;
            onVideoClipDragFinish(delete, position);
        }
    };
    /**
     * 移动结束回调
     * @param delete
     * @param selectedPosition
     */
    private void onVideoClipDragFinish(boolean delete, int selectedPosition) {

        Log.i("club","club_onDragFinish:selectedPosition:"+selectedPosition+
                " getPositionTo:"+ mThumbnailAdapter.getPositionTo());
        mLLVideoDelItem.setVisibility(View.GONE);
        mIsDragIng = false;
        if (delete) {
            if(TimelineData.instance().getClipInfoData().size()==1){
                if (mOnVideoCutCallBack != null) {
                    mOnVideoCutCallBack.closeFinish();
                }
                return;
            }
            VideoEditManger.setVideoDel(mThumbnailAdapter.getPositionTo(), mTimeline);
            mThumbnailAdapter.setRemovePosition(mThumbnailAdapter.getSelectLongPosition());
        } else {
            VideoEditManger.setVideoMove(mThumbnailAdapter.getPositionFrom(), mThumbnailAdapter.getPositionTo(), mTimeline);
        }
        setRecyclerViewWidthAndHeight(false);
        if (mOnVideoCutCallBack != null) {
            mOnVideoCutCallBack.onDragFinish(delete, selectedPosition);
        }
        RecyclerChangCallBack.getInstance(mThumbnailRecyclerView).setOnChangCallBack(new RecyclerChangCallBack.OnChangCallBack() {
            @Override
            public void CallBack(boolean isSucc) {
                mThumbnailAdapter.setIsDragIng(mIsDragIng);

                long time;
                if (delete) {
                    refreshVideoTimeLineAdapter();
                    int pos=mThumbnailAdapter.getPositionDel()-1;
                    if(pos<0){
                        time=0;
                    }else {
                        time=mVideoTrack.getClipByIndex(pos).getOutPoint();
                    }
                }else {
                    NvsVideoClip clip=mVideoTrack.getClipByIndex(mThumbnailAdapter.getPositionTo());
                    if(clip!=null){
                        time=clip.getInPoint();
                    }else {
                        time=0;
                    }
                    if(mThumbnailAdapter.getPositionTo()>0){
                        time=time+5000;
                    }
                }
                scrollThumbToPosition(time);
                mBottomThumbnailRecyclerView.setVisibility(VISIBLE);
                refreshThumbnailAdapter(mSelectedItemPosition);
                setThumbSelectStatus(VideoEditManger.tranTimeToWidth(getContext(),time,mVideoTrack),time);
            }
        });

    }
    /**
     * 初始化缩略图底部图片View
     */
    private void initBottomThumbView(View root,Context context){
        mBottomThumbnailRecyclerView = root.findViewById(R.id.mBottomThumbnailRecyclerView);
        mLinearLayoutBottomManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        mBottomThumbnailRecyclerView.setLayoutManager(mLinearLayoutBottomManager);
        mThumbnailBottomAdapter=new ThumbnailAdapter3(getContext());
        mBottomThumbnailRecyclerView.setAdapter(mThumbnailBottomAdapter);
        mBottomRecycleViewUtils = new RecycleViewTimeLinUtils(mBottomThumbnailRecyclerView);

    }
    /**
     * 初始化TimeLinView
     */
    private void initTimeLineView(View root,Context context){
        mLinearLayoutTimeManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        mTimeLineRecyclerView = root.findViewById(R.id.mTimeLineRecyclerView);
        mVideoTimeLineAdapter = new VideoTimeLineAdapter(context);
        mTimeLineRecyclerView.setLayoutManager(mLinearLayoutTimeManager);
        mTimeLineRecyclerView.setAdapter(mVideoTimeLineAdapter);
        mTimeLineViewUtils = new RecycleViewTimeLinUtils(mTimeLineRecyclerView);
        mTimeLineRecyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                mIsThumbScroll=true;
                return false;
            }
        });
        mTimeLineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int scrollX = mTimeLineViewUtils.getScrollX(mVideoTimeLineAdapter.getmBeans());
                if (!mIsDragIng&&isCreatScroll>4&&mIsThumbScroll&&mIsUser) {

                    mRecycleViewUtils.scrollToX(getContext()
                            , mLinearLayoutManager
                            , scrollX
                            , mThumbnailAdapter.getmBeans());
                    mRecycleViewUtils.scrollToX(getContext()
                            , mLinearLayoutBottomManager
                            , scrollX
                            , mThumbnailBottomAdapter.getmBeans());

                }
                setThumbSelectStatus(scrollX,-1);
                isCreatScroll++;
            }
        });

    }
    private void setThumbSelectStatus(int scrollX,long time){
        if(mIsUser){
            updateSeekRangeBar(time,scrollX);
        }
        if (mOnVideoCutCallBack != null) {
            long curTime=time;
             if(curTime==-1){
                 curTime = VideoEditManger.tranWidthToTime(getContext(), scrollX, mVideoTrack);
             }
            mOnVideoCutCallBack.onVideoCutDurationCallBack(curTime,mSelectedItemPosition,mIsUser);
        }
        int i=mThumbnailAdapter.getScrollPosition(scrollX);
        if (i == mSelectedItemPosition + 1&&time!=-1) {
            return;
        }
        RecyclerChangCallBack.getInstance(mThumbnailRecyclerView).setOnChangCallBack(isSucc -> {
            mThumbnailAdapter.setSelectPosition(i);
        });
        mSelectedItemPosition = i - 1;
        if (mOnVideoCutCallBack != null) {
            mOnVideoCutCallBack.onVideoCutSelectCallBack(mSelectedItemPosition);
        }

    }

    /**
     * 显示转场
     * @param position
     */
    private void showVideoTransition(final int position) {
        mFrameLayoutContext.removeAllViews();
        mFrameLayoutContext.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.tranlate_dialog_out));

        mFrameLayoutContext.setVisibility(VISIBLE);
        mFrameLayoutContext.setOnClickListener(view -> { });
        VideoTransitionView view=new VideoTransitionView(getContext(),mTimeline,mVideoTrack);
        view.setOnVideoTranCallBack(new VideoTransitionView.OnVideoTranCallBack() {
            @Override
            public void OnVideoTransitionBack() {
                mFrameLayoutContext.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.tranlate_dialog_in));
                mFrameLayoutContext.setVisibility(GONE);
            }

            @Override
            public void OnVideoTransitionPreview() {
                if(mOnVideoCutCallBack!=null){
                    mOnVideoCutCallBack.OnVideoPreview(position);
                }
            }
        });
        view.setVideoTransitionData(position);
        mFrameLayoutContext.addView(view);

    }

    public void setSelectedItemPosition(int mSelectedItemPosition) {
        this.mSelectedItemPosition = mSelectedItemPosition;
    }

    public void initData(int selectedPosition) {
        refreshThumbnailAdapter(selectedPosition);
        refreshVideoTimeLineAdapter();
    }

    public void refreshThumbnailAdapter(int selectedPosition) {
        List<ThumbnailBean> beans = VideoEditManger.getFrameInVideoWithMeisheSDK(getContext(), mVideoTrack,selectedPosition);
        mThumbnailAdapter.updateData(true, beans);
        List<ThumbnailBean> bottom = VideoEditManger.getFrameInVideoWithMeisheSDK3(getContext(), mVideoTrack);
        mThumbnailBottomAdapter.updateData(true, bottom);
    }

    public void refreshVideoTimeLineAdapter() {
        List<VideoTimeLineBean> beansTime = VideoEditManger.getVideoTimeLineBeanList(getContext(), mVideoTrack);
        mVideoTimeLineAdapter.updateData(true, beansTime);
    }

    public void setRecyclerViewWidthAndHeight(boolean mIsDragIng) {
        if (mIsDragIng) {
            FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) mThumbnailRecyclerView.getLayoutParams();
            mLayoutParams.width = LayoutParams.WRAP_CONTENT;
            mLayoutParams.height = LayoutParams.MATCH_PARENT;
            mThumbnailRecyclerView.setLayoutParams(mLayoutParams);
        } else {
            FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) mThumbnailRecyclerView.getLayoutParams();
            mLayoutParams.width = LayoutParams.MATCH_PARENT;
            mLayoutParams.height = PUtil.dip2px(getContext(), 85);
            mThumbnailRecyclerView.setLayoutParams(mLayoutParams);
        }
    }

    private float mMotionEventActionX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mMotionEventActionX = event.getX();
            mIsTouchEvent=false;
        }
        return super.onTouchEvent(event);
    }
    boolean mIsTouchEvent=true;
    boolean mIsUser=false;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mMotionEventActionX = ev.getX();
            float mMotionEventActionY = ev.getRawY();
            mIsTouchEvent=false;
            int[] itemLocation = new int[2];
            mTimeLineRecyclerView.getLocationOnScreen(itemLocation);
            float itemY =  itemLocation[1];
            int height=mTimeLineRecyclerView.getHeight();
            if(itemY<mMotionEventActionY&&mMotionEventActionY<itemY+height){
                if (mOnVideoCutCallBack != null) {
                    mOnVideoCutCallBack.onVideoStop();
                }
                mIsThumbScroll=true;
                mIsUser=true;
            }
            mThumbnailRecyclerView.getLocationOnScreen(itemLocation);
            itemY = itemLocation[1];
            height=mThumbnailRecyclerView.getHeight();
            if(itemY<mMotionEventActionY&&mMotionEventActionY<itemY+height){
                if (mOnVideoCutCallBack != null) {
                    mOnVideoCutCallBack.onVideoStop();
                }
                mIsThumbScroll=false;
                mIsUser=true;
            }

        }else
        if (ev.getAction() == MotionEvent.ACTION_UP||ev.getAction() == MotionEvent.ACTION_POINTER_UP) {
            /*确保松开长按后数据正常恢复*/
            if(mIsDragIng){
                Message message=mDragFinishHandler.obtainMessage();
                message.arg1=mSelectedItemPosition;
                message.obj=false;
                mDragFinishHandler.sendMessageDelayed(message,200);
            }

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mOnViewFinishInflate != null) {
            mOnViewFinishInflate.onFinishInflate();
        }
    }

    public void setInitVideoData(NvsTimeline timeline,int position,long mCurTime, NvsVideoTrack videoTrack) {

        setVideoData(timeline,videoTrack);
        mSelectedItemPosition=position;
        mOnVideoCutCallBack.onVideoCutDurationCallBack(mCurTime,mSelectedItemPosition,true);
        mSelectedItemPosition=position-1;
        scrollThumbToPosition(mCurTime,false);
    }
    public void initSeekRangeBar(){
        mSeekRangeBar=findViewById(R.id.mSeekRangeBar);
        mSeekRangeBar.setVisibility(GONE);
        mSeekRangeBar.setOnSeekRangeCallBack(new SeekRangeBar.OnSeekRangeCallBack() {
            @Override
            public void onChangCallBack(boolean isUp, float start, float end) {
                Log.i("club","onSeekRangeCallBack:isUp:"+isUp
                        +" start:"+start
                        +" end:"+end
                );
                VideoEditManger.setVideoCutX(mSelectedItemPosition,start,end,isUp,mTimeline,mVideoTrack);
                initData(mSelectedItemPosition);
            }
        });
    }
    public void updateSeekRangeBar(long time,int x){
        if(mVideoTrack.getClipByIndex(mSelectedItemPosition)==null){
            return;
        }
        long in=mVideoTrack.getClipByIndex(mSelectedItemPosition).getInPoint();
        long out=mVideoTrack.getClipByIndex(mSelectedItemPosition).getOutPoint();
        int timeIn=VideoEditManger.tranTimeToWidth(getContext(),in,mVideoTrack);
        int timeOut=VideoEditManger.tranTimeToWidth(getContext(),out,mVideoTrack);
        int width=VideoEditManger.tranTimeToWidth(getContext(),mVideoTrack.getDuration(),mVideoTrack);

        mSeekRangeBar.selectScale(-x);
        mSeekRangeBar.setChangStart(timeIn);
        mSeekRangeBar.setChangEnd(timeOut);
        mSeekRangeBar.setScaleList(width);
        Log.i("club","scrollThumbToPosition:time:"+time
                +" x:"+x
                +" in:"+in
                +" out:"+out
                +" timeIn:"+timeIn
                +" timeOut:"+timeOut
                +" width:"+width
                +" mSelectedItemPosition:"+mSelectedItemPosition
        );
    }
    public void scrollThumbToPosition(long time){

        int x=VideoEditManger.tranTimeToWidth(getContext(),time,mVideoTrack);
        updateSeekRangeBar(time,x);
        RecyclerChangCallBack.getInstance(mThumbnailRecyclerView).setOnChangCallBack(isSucc -> {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTimeLineViewUtils.scrollToX(getContext()
                            , mLinearLayoutManager
                            , x
                            , mThumbnailAdapter.getmBeans());

                }
            }, 10);
        });

        RecyclerChangCallBack.getInstance(mTimeLineRecyclerView).setOnChangCallBack(isSucc -> {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecycleViewUtils.scrollToX(getContext(), mLinearLayoutTimeManager, x, mVideoTimeLineAdapter.getmBeans());
                }
            }, 10);
        });
        RecyclerChangCallBack.getInstance(mBottomThumbnailRecyclerView).setOnChangCallBack(isSucc -> {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecycleViewUtils.scrollToX(getContext()
                            , mLinearLayoutBottomManager
                            , x
                            , mThumbnailBottomAdapter.getmBeans());
                }
            }, 10);
        });
    }
    public void scrollThumbToPosition(long time,boolean isPlay){
        NvsVideoClip videoClip=mVideoTrack.getClipByTimelinePosition(time);
        if(videoClip==null){
            return;
        }
        int index=videoClip.getIndex();
        if(index!=mSelectedItemPosition){
            mSelectedItemPosition=index;
            mThumbnailAdapter.setSelectPosition(mSelectedItemPosition+1);
        }
        mIsUser=false;
        scrollThumbToPosition(time);


    }
    public void setVideoData(NvsTimeline timeline, NvsVideoTrack videoTrack) {
        mTimeline = timeline;
        mVideoTrack = videoTrack;

        initData(mSelectedItemPosition);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDragFinishHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onStartDrag(final RecyclerView.ViewHolder viewHolder) {
        mDragFinishHandler.removeCallbacksAndMessages(null);
        mBottomThumbnailRecyclerView.setVisibility(GONE);
        mIsDragIng = true;
        setRecyclerViewWidthAndHeight(true);
        mTtemVideoTouchHelperCallback.setMotionEventActionX(mMotionEventActionX);
        /**
         * recycler  刷新成功再调用移动 删除接口
         */
        RecyclerChangCallBack.getInstance(mThumbnailRecyclerView).setOnChangCallBack(isSucc -> {
            /**
             * 2020-01-16 zjh 记个日记：为了视屏时间轴缩放后，自动调起ItemTouchHelper.startDrag移动事件、尝试了很多方法
             * 1.首先notifyDataSetChanged刷新缩放
             * 2.滚动到缩放后长按触发的item到可视区，因为非可视区域recycle自动重用回收机制会把非可视区域的item 回收
             * 3.滚动结束后启动startDrag
             */
            mLinearLayoutManager.scrollToPositionWithOffset(viewHolder.getAdapterPosition(), (int) mMotionEventActionX);
            RecyclerChangCallBack.getInstance(mThumbnailRecyclerView).setOnChangCallBack(isSucc1 -> mItemTouchHelper.startDrag(viewHolder));

        });

    }

    private OnViewFinishInflate mOnViewFinishInflate;

    public void setOnViewFinishInflate(OnViewFinishInflate mOnViewFinishInflate) {
        this.mOnViewFinishInflate = mOnViewFinishInflate;

    }

    public interface OnViewFinishInflate {
        void onFinishInflate();
    }

    private OnVideoCutCallBack mOnVideoCutCallBack;

    public void setOnVideoCutCallBack(OnVideoCutCallBack mOnVideoCutCallBack) {
        this.mOnVideoCutCallBack = mOnVideoCutCallBack;
    }

    public interface OnVideoCutCallBack {
        /**
         * 视屏预览
         * @param selectPosition
         */
        void OnVideoPreview(int selectPosition);

        /**
         * 视屏导入
         * @param selectPosition
         */
        void onVideoImport(int selectPosition);
        /**
         * 选中的clip
         *
         * @param selectPosition
         */
        void onVideoCutSelectCallBack(int selectPosition);

        /**
         * 当前滚动到的时间
         *
         * @param curTime
         */
        void onVideoCutDurationCallBack(long curTime,int selectPosition,boolean isUser);
        void onVideoStop();

        void onDragStart();

        void onDragFinish(boolean delete, int selectedPosition);

        void closeFinish();
    }
}
