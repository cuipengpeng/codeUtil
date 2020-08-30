package com.test.xcamera.phonealbum.layout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.phonealbum.presenter.VideoEditDetailPresenter;
import com.test.xcamera.phonealbum.widget.ButtonUpDown;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.phonealbum.widget.VideoTransitionDialog;
import com.test.xcamera.phonealbum.widget.subtitle.CaliperLayout;
import com.test.xcamera.phonealbum.widget.subtitle.HighlightMarkView;
import com.test.xcamera.phonealbum.widget.subtitle.TimelineLayout;
import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;
import com.test.xcamera.phonealbum.widget.subtitle.bean.MovieSubtitleTimeline;
import com.test.xcamera.phonealbum.widget.subtitle.bean.TitleSubtitleMark;
import com.test.xcamera.utils.PUtil;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCaption;

import java.util.List;

public class EditSubtitleLayout extends LinearLayout {
    private TimelineLayout mSubtitleTimelineLayout;
    private HighlightMarkView mHighlightMarkView;
    private Mark mTitleSubtitleMarkCopy;
    private TitleSubtitleMark mTitleSubtitleMark;
    private NvsTimelineCaption mNvsTimelineCaption;
    private float mSubtitleMinValue;
    private float mSubtitleMaxValue;
    private boolean mIsAddSubtitle = false;
    private boolean mIsLongPress = false;
    private String mLastSubtitleId = "";
    private TextView mSubtitleTip;
    private ButtonUpDown mSubtitleAdd;
    private ImageView mSubtitleDel;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private Context mContext;
    private VideoEditDetailPresenter mPresenter;

    public EditSubtitleLayout(Context context,
                              NvsTimeline timeline,
                              NvsStreamingContext streamingContext,
                              VideoEditDetailPresenter presenter) {
        this(context, null);
        mContext = context;
        mTimeline = timeline;
        mStreamingContext = streamingContext;
        mPresenter = presenter;
        init(context);
    }

    public EditSubtitleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {
        View root = inflate(context, R.layout.view_video_edit_subtitle, this);
        mSubtitleTip = root.findViewById(R.id.tv_subtitle_tip);
        mSubtitleAdd = root.findViewById(R.id.tv_subtitle_add);
        mSubtitleDel = root.findViewById(R.id.iv_subtitle_del);
        initSubtitleTimeLine();
    }

    private void play(long time) {
        play(time, mTimeline.getDuration());
    }

    private void play(long time, long endTime) {
        mStreamingContext.playbackTimeline(mTimeline, time, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);

    }
   private long mLsatTime=0;
    /**
     * 字幕数据初始化
     */
    private void initSubtitleTimeLine() {
        mSubtitleAdd.setTag(true);
        mSubtitleAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!mIsAddSubtitle){
                    if(System.currentTimeMillis()-mLsatTime<500){
                        mLsatTime=System.currentTimeMillis();
                        return false;
                    }
                }
                mLsatTime=System.currentTimeMillis();
                if (!(boolean) mSubtitleAdd.getTag()) {
                    return false;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
                    float time = (float) mStreamingContext.getTimelineCurrentPosition(mTimeline) / VideoEditManger.VIDEO_microsecond_TIME;
                    Log.i("club", "club:OnTouch：down:time:" + time);
                    mIsAddSubtitle = true;
                    mTitleSubtitleMark = mPresenter.createMark(time);
                    mTitleSubtitleMark.setZValue(VideoEditManger.getCurCaptionZVal(mTimeline));
                    play(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                    MovieSubtitleTimeline.addMark(mTitleSubtitleMark);
                    mNvsTimelineCaption = VideoEditManger.addSubTitle(mTimeline, mTitleSubtitleMark);
                    mTitleSubtitleMark.setZValue(mNvsTimelineCaption.getZValue());
                    mSubtitleAdd.setBackground(getResources().getDrawable(R.drawable.circle_corner_red_border_bg));
                    mSubtitleAdd.setTextColor(getResources().getColorStateList(R.color.appThemeColor));
                    mSubtitleAdd.setText(getContext().getString(R.string.video_edit_subtitle_add_ing));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_POINTER_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    Log.i("club", "club:OnTouch：up");

                    stopSubtitleAdd(1);
                }
                return false;
            }
        });
        mSubtitleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((boolean) mSubtitleAdd.getTag()) {
                    return ;
                }
                if (mTitleSubtitleMark != null) {
                    stopSubtitleAdd(2);
                }
            }
        });
        mSubtitleDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoTransitionDialog dialog = new VideoTransitionDialog(mContext, 1);
                dialog.show();
                dialog.setOnTransitionDialog(new VideoTransitionDialog.OnTransitionDialog() {
                    @Override
                    public void onTransitionConfirm() {
                        if (mTitleSubtitleMark != null) {
                            MovieSubtitleTimeline.deleteMark(mTitleSubtitleMark);
                            VideoEditManger.delSubTitle(mTimeline, mTitleSubtitleMark);
                            mSubtitleTimelineLayout.updateTopTimeline(MovieSubtitleTimeline.getMarkList());
                            setSubtitleStatus((float) getCurPlayTime() / VideoEditManger.VIDEO_microsecond_TIME * 10);
//                            seek(getCurPlayTime());
                            if (mOnSubTitleCallBack != null) {
                                mOnSubTitleCallBack.subtitleSeek(getCurPlayTime());
                            }
                        }
                    }
                });

            }
        });

        FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) mSubtitleTip.getLayoutParams();
        mLayoutParams.width = PUtil.getScreenW(getContext()) / 2 - PUtil.dip2px(getContext(), 30);
        mSubtitleTip.setLayoutParams(mLayoutParams);
        mSubtitleTimelineLayout = findViewById(R.id.timeline_layout);
        mSubtitleTimelineLayout.setScaleList((int) (((float) mTimeline.getDuration() / VideoEditManger.VIDEO_microsecond_TIME) * 10));
        mSubtitleTimelineLayout.updateTopTimeline(MovieSubtitleTimeline.getMarkList());
        mSubtitleTimelineLayout.setWheelScrollingListener(new CaliperLayout.OnWheelScrollListener() {
            @Override
            public void onChanged(CaliperLayout wheel, float value) {
                onScrollingSubtitle(value);
            }

            @Override
            public void onScrollingStarted(CaliperLayout wheel, float value) {
                mStreamingContext.stop();
            }

            @Override
            public void onScrollingFinished(CaliperLayout wheel, float value) {
                onScrollingSubtitle(value);
            }
        });
        mSubtitleTimelineLayout.setDragViewScrollingListener(new CaliperLayout.OnDragViewScrollListener() {
            @Override
            public void onPressed(Mark mark) {
                mIsLongPress = true;
                mTitleSubtitleMarkCopy = mark.copy();
                float startTime = mTitleSubtitleMarkCopy.getStart() + 1;
                if (startTime > mTitleSubtitleMarkCopy.getEnd()) {
                    startTime = mTitleSubtitleMarkCopy.getEnd();
                }
                NvsTimelineCaption caption = VideoEditManger.getNvsTimelineCaption(mTimeline, (TitleSubtitleMark) mTitleSubtitleMarkCopy);
                if (caption != null) {
                    mNvsTimelineCaption = caption;
                }
                Log.i("club", "club_onChanged onPressed:"
                        + mark.getStart() + " end:" + mark.getEnd() + "  mark1="
                );
                mSubtitleMinValue = 0;
                mSubtitleMaxValue = ((float) mTimeline.getDuration() / VideoEditManger.VIDEO_microsecond_TIME * 10);
                mSubtitleMinValue = (float) mPresenter.getApproximateMin(mTitleSubtitleMarkCopy.getStart(), mSubtitleMinValue);
                mSubtitleMaxValue = (float) mPresenter.getApproximateMax(mTitleSubtitleMarkCopy.getEnd(), mSubtitleMaxValue);
            }

            @Override
            public void onChanged(float value, Mark mark) {
//                MovieSubtitleTimeline.replaceMark(mark);
                Log.i("club", "club_onChanged:"
                        + mark.getStart() + " end:" + mark.getEnd() + "  mark1="
                        + mSubtitleMinValue + " end:" + mSubtitleMaxValue
                        + " mark.getDuration()：" + mark.getDuration()
                );
                MovieSubtitleTimeline.replaceMark(mark);
                mSubtitleTimelineLayout.updateTopTimeline(MovieSubtitleTimeline.getMarkList());
            }

            @Override
            public void onDragFinished(float value, Mark mark) {
                mIsLongPress = false;
                Log.i("club", "club_onDragFinished:" + mark.getStart() + " end:" + mark.getEnd());
                if (mark.getEnd() >= mSubtitleMaxValue) {
                    mark.setStart(mSubtitleMaxValue - mark.getDuration());
                    mark.setEnd(mSubtitleMaxValue);
                    mark.setStartPositionInClip(mSubtitleMaxValue - mTitleSubtitleMarkCopy.getDuration());
                    mark.setEndPositionInClip(mSubtitleMaxValue);
                } else if (mark.getStart() <= mSubtitleMinValue) {
                    mark.setStart(mSubtitleMinValue);
                    mark.setEnd(mSubtitleMinValue + mark.getDuration());
                    mark.setStartPositionInClip(mSubtitleMinValue);
                    mark.setEndPositionInClip(mSubtitleMinValue + mTitleSubtitleMarkCopy.getDuration());
                }
                NvsTimelineCaption caption = VideoEditManger.getNvsTimelineCaption(mTimeline, mTitleSubtitleMark);
                if (caption != null) {
                    mNvsTimelineCaption = caption;
                }
                MovieSubtitleTimeline.replaceMark(mark);


                mSubtitleTimelineLayout.updateTopTimeline(MovieSubtitleTimeline.getMarkList());

            }
        });
        mSubtitleTimelineLayout.setOnDragMarkListener(new TimelineLayout.OnDragMarkListener() {
            @Override
            public void onTopDragMarkPressed(Mark mark) {
                Log.i("club", "club_onTopDragMarkPressed:" + mark.getStart() + " end:" + mark.getEnd());

            }

            @Override
            public void onTopDragMarkReleased(HighlightMarkView markView, Mark origin, Mark dest) {

            }

            @Override
            public void onTopDragMarkClicked(List<Mark> overlapped) {
                if (overlapped.size() > 0) {
                    setSubtitle(overlapped.get(0));
                    if (mTitleSubtitleMark != null) {
                        mLastSubtitleId = mTitleSubtitleMark.getId();
                    } else {
                        mLastSubtitleId = "";
                    }
                    mTitleSubtitleMark = (TitleSubtitleMark) overlapped.get(0);

                    NvsTimelineCaption caption = VideoEditManger.getNvsTimelineCaption(mTimeline, mTitleSubtitleMark);
                    if (caption != null) {
                        mNvsTimelineCaption = caption;
                    }
                    mSubtitleTimelineLayout.updateTopTimeline(MovieSubtitleTimeline.getMarkList());
                    if (!mLastSubtitleId.equals(mTitleSubtitleMark.getId())) {
                        long end = (long) (overlapped.get(0).getEnd() / 10 * VideoEditManger.VIDEO_microsecond_TIME);
                        if (end < mStreamingContext.getTimelineCurrentPosition(mTimeline)) {
                            onScrollingSubtitle(overlapped.get(0).getEnd() - 1f);
                            setSubtitleTimeLine(overlapped.get(0).getEnd() / 10 - 0.1f);
                        } else {
                            onScrollingSubtitle(overlapped.get(0).getStart() + 1f);
                            setSubtitleTimeLine(overlapped.get(0).getStart() / 10 + 0.1f);
                        }
                    }
                }
            }
        });
        mSubtitleTimelineLayout.setOnMarkEditCut(new HighlightMarkView.OnMarkEditCut() {
            @Override
            public void onMarkEditCut(boolean isDown, int position, Mark mark) {
                MovieSubtitleTimeline.replaceMark(mark);
                mSubtitleTimelineLayout.updateTopTimeline(MovieSubtitleTimeline.getMarkList());
            }
        });
    }

    public void setScaleList(int max) {
        mSubtitleTimelineLayout.setScaleList(max);

    }

    public void setScrollingSubtitle() {
        if (mNvsTimelineCaption != null) {
            onScrollingSubtitle(mTitleSubtitleMark.getStart());
            setSubtitleTimeLine(mTitleSubtitleMark.getStart() / 10);
        }
    }

    public boolean isIsAddSubtitle() {
        return mIsAddSubtitle;
    }

    public boolean isIsLongPress() {
        return mIsLongPress;
    }

    private void onScrollingSubtitle(float value) {
        if (mOnSubTitleCallBack != null) {
            mOnSubTitleCallBack.onScrollingSubtitle(value);
        }
        /*mCurTime = (long) (value * VideoEditManger.VIDEO_microsecond_TIME / 10);
        seek(mCurTime);
        mVideoCutView.scrollThumbToPosition(mCurTime, true);
        setDurationTextView((int) mCurTime);*/
        int tranX = (int) (value / 10 * PUtil.dip2px(getContext(), VideoEditManger.VIDEO_FRAME_WIDTH));
        mSubtitleTip.setTranslationX(-tranX);
        setSubtitleStatus(value / 10);
    }

    /**
     * 停止字幕添加启动字幕编辑,进入视频编辑
     */
    private synchronized void stopSubtitleAdd(int type) {
        Log.i("club","club:stopSubtitleAdd:"+type);
        mIsAddSubtitle = false;
        mStreamingContext.stop();
        if (mTitleSubtitleMark == null) {
            return;
        }
        if (mTitleSubtitleMark.getDuration() < 5) {//字幕时长小于0.5 自定清除
            VideoEditManger.delSubTitle(mTimeline, mTitleSubtitleMark);
            MovieSubtitleTimeline.deleteMark(mTitleSubtitleMark);
            setSubtitleStatus((float) getCurPlayTime() / VideoEditManger.VIDEO_microsecond_TIME);
            mSubtitleTimelineLayout.updateTopTimeline(MovieSubtitleTimeline.getMarkList());
            return;
        }
        if (mOnSubTitleCallBack != null) {
            mOnSubTitleCallBack.onSubtitleAdd(mTitleSubtitleMark.getId());
        }
        VideoEditManger.updateSubTitle(mNvsTimelineCaption, mTitleSubtitleMark);
        float time = (float) mStreamingContext.getTimelineCurrentPosition(mTimeline) / VideoEditManger.VIDEO_microsecond_TIME;
        setSubtitleStatus(time);
    }

    private long getCurPlayTime() {
        return mStreamingContext.getTimelineCurrentPosition(mTimeline);
    }

    public void setSubtitle(Mark mark) {
        for (Mark mark1 : MovieSubtitleTimeline.getMarkList()) {
            mark1.setMarkStatus(Mark.NORMAL);
        }
        if (mark != null) {
            mark.setMarkStatus(Mark.EDIT);
            MovieSubtitleTimeline.replaceMark(mark);
        }
    }

    /**
     * 設置字幕数据
     *
     * @param time
     */
    public void setSubtitleTimeLine(float time) {
        if (mOnSubTitleCallBack != null) {
            mOnSubTitleCallBack.setSubtitleTimeLine(time);
        }
//        mVideoPlayLayout.showRunes(MovieSubtitleTimeline.getHookedMarkList(time * 10), (long) (time * VideoEditManger.VIDEO_microsecond_TIME));
        if (mSubtitleTimelineLayout == null) {
            return;
        }
        int tranX = (int) (time * PUtil.dip2px(getContext(), VideoEditManger.VIDEO_FRAME_WIDTH));
        mSubtitleTip.setTranslationX(-tranX);
        float transformedPosition = time * 10;
        int scale = (int) transformedPosition;
        float offset = transformedPosition - scale;
        mSubtitleTimelineLayout.selectScale(scale, offset);
        if (mIsAddSubtitle && mTitleSubtitleMark != null) {
            mPresenter.updateMarkTime(mTitleSubtitleMark, mTitleSubtitleMark.getStart() / 10, time);
            mSubtitleTimelineLayout.updateTopTimeline(MovieSubtitleTimeline.getMarkList());

            if (MovieSubtitleTimeline.getHookedMarkList((time+0.05f) * 10).size() > 0) {
                for (Mark mark : MovieSubtitleTimeline.getHookedMarkList((time+0.05f) * 10)) {
                    if (!mark.getId().equals(mTitleSubtitleMark.getId())) {
                        stopSubtitleAdd(3);
                        break;
                    }
                }

            }
        } else {
            setSubtitleStatus(time);
        }

    }

    public void updateTopTimeline() {
        mSubtitleTimelineLayout.updateTopTimeline(MovieSubtitleTimeline.getMarkList());
    }

    private void setSubtitleStatus(float time) {
        if (mIsAddSubtitle) {//如果正在添加字幕禁止改变状态

            return;
        }
        if (MovieSubtitleTimeline.getHookedMarkList(time * 10).size() > 0) {
            mSubtitleAdd.setTag(false);
            mSubtitleAdd.setBackground(getResources().getDrawable(R.drawable.circle_corner_red_border_bg));
            mSubtitleAdd.setTextColor(getResources().getColorStateList(R.color.appThemeColor));
            mSubtitleAdd.setText(getContext().getString(R.string.video_edit_subtitle_edit));
            mSubtitleDel.setVisibility(View.VISIBLE);
            mTitleSubtitleMark = (TitleSubtitleMark) MovieSubtitleTimeline.getHookedMarkList(time * 10).get(0);
            if (mTimeline == null) {
                return;
            }
            NvsTimelineCaption caption = VideoEditManger.getNvsTimelineCaption(mTimeline, mTitleSubtitleMark);
            if (caption != null) {
                mNvsTimelineCaption = caption;
            }
            VideoEditManger.updateSubTitle(mNvsTimelineCaption, mTitleSubtitleMark);
            setSubtitle(mTitleSubtitleMark);
            mSubtitleTimelineLayout.updateTopTimeline(MovieSubtitleTimeline.getMarkList());

        } else {
            mTitleSubtitleMark = null;
            setSubtitle(null);
            mSubtitleTimelineLayout.updateTopTimeline(MovieSubtitleTimeline.getMarkList());
            mSubtitleAdd.setTag(true);
            mSubtitleAdd.setBackground(getResources().getDrawable(R.drawable.circle_corner_green_bg_normal_2dp));
            mSubtitleAdd.setTextColor(getResources().getColorStateList(R.color.white));
            mSubtitleAdd.setText(getContext().getString(R.string.video_edit_subtitle_add));
            mSubtitleDel.setVisibility(View.GONE);
        }

    }

    private OnSubTitleCallBack mOnSubTitleCallBack;

    public void setOnSubTitleCallBack(OnSubTitleCallBack mOnSubTitleCallBack) {
        this.mOnSubTitleCallBack = mOnSubTitleCallBack;
    }

    public interface OnSubTitleCallBack {
        void subtitleSeek(long time);

        void setSubtitleTimeLine(float time);

        void onSubtitleAdd(String markId);

        void onScrollingSubtitle(float value);
    }
}
