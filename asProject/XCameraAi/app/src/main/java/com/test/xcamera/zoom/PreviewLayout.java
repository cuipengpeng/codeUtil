package com.test.xcamera.zoom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.xcamera.view.CustomPopWindow;
import com.test.xcamera.zoom.library.uk.co.senab.photoview.PhotoView;
import com.test.xcamera.zoom.library.uk.co.senab.photoview.PhotoViewAttacher;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.test.xcamera.R;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.picasso.Picasso;
import com.editvideo.MediaConstant;
import com.editvideo.MediaData;
import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.TimelineData;

import java.util.ArrayList;
import java.util.List;

import static com.editvideo.Constants.POINT16V9;
import static com.editvideo.Constants.POINT1V1;
import static com.editvideo.Constants.POINT3V4;
import static com.editvideo.Constants.POINT4V3;
import static com.editvideo.Constants.POINT9V16;
import static com.editvideo.Util.getVideoEditResolution;


/**
 * PreviewLayout
 * <p/>
 * Created by woxingxiao on 2016-10-24.
 */
public class PreviewLayout extends FrameLayout implements ViewPager.OnPageChangeListener, View.OnClickListener, CustomPopWindow.OnViewClickListener {

    public static final long ANIM_DURATION = 300;

    private View mBackgroundView;
    private HackyViewPager mViewPager;
    private ImageView mScalableImageView;

    private List<MoAlbumItem> mThumbViewInfoList = new ArrayList<>();
    private int mIndex;
    private Rect mStartBounds = new Rect();
    private Rect mFinalBounds = new Rect();
    private boolean isAnimFinished = true;

    private Context mcontext;
    private ImageView is_stop;
    private ImageView iv_left_title;
    private TextView tv_middle_title;
    private ImageView iv_right_title;
    private int mListCount = 0;

    public PreviewLayout(Context context) {
        this(context, null);
        this.mcontext = context;
    }

    public PreviewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_preview, this, true);
        mBackgroundView = findViewById(R.id.background_view);
        mViewPager = findViewById(R.id.view_pager);
        mScalableImageView = findViewById(R.id.scalable_image_view);

        is_stop = findViewById(R.id.is_stop);

        mScalableImageView.setPivotX(0f);
        mScalableImageView.setPivotY(0f);
        mScalableImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);


        //
        iv_left_title = findViewById(R.id.iv_left_title);
        tv_middle_title = findViewById(R.id.tv_middle_title);
        iv_right_title = findViewById(R.id.iv_right_title);


        iv_left_title.setOnClickListener(this);
        tv_middle_title.setOnClickListener(this);
        iv_right_title.setOnClickListener(this);

    }

    public void setData(final List<MoAlbumItem> list, final int index) {
        if (list == null || list.isEmpty() || index < 0) {
            return;
        }

        this.mThumbViewInfoList = list;
        this.mIndex = index;
        mListCount = list.size();
//        mStartBounds = mThumbViewInfoList.get(mIndex).getBounds();
//        if(mStartBounds==null){
//            mStartBounds= new Rect();
//        }

        post(new Runnable() {
            @Override
            public void run() {
                ImagePagerAdapter adapter = new ImagePagerAdapter();
                mViewPager.setAdapter(adapter);
                mViewPager.setCurrentItem(mIndex);
                mViewPager.addOnPageChangeListener(PreviewLayout.this);

                mScalableImageView.setX(mStartBounds.left);
                mScalableImageView.setY(mStartBounds.top);
                //动画好了只后的效果
//                Glide.with(getContext()).load(mThumbViewInfoList.get(mIndex).getmImage().getmUri()).apply(options).into(mScalableImageView);


                is_stop.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, CameraVideoPlayActivity.class);
//                        intent.putExtra("mediaData", mThumbViewInfoList.get(mIndex));
//                        intent.putExtra("movideo", mThumbViewInfoList.get(mIndex).getmVideo());
//                        mContext.startActivity(intent);
                    }
                });

                MoAlbumItem mediaData = mThumbViewInfoList.get(mIndex);
                if (mediaData.getmType().equals("image") || !mediaData.getmType().equals("video")) {
                    is_stop.setVisibility(GONE);
                } else {
                    is_stop.setVisibility(VISIBLE);
                }

                tv_middle_title.setText((index + 1) + "/" + mListCount);
            }
        });
    }

    RequestOptions options = new RequestOptions()
//            .placeholder(R.mipmap.ic_launcher)//图片加载出来前，显示的图片
            .fallback(R.mipmap.bank_thumbnail_local) //url为空的时候,显示的图片
            .error(R.mipmap.bank_thumbnail_local).dontAnimate();//图片加载失败后，显示的图片;

    public void startScaleUpAnimation() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Point globalOffset = new Point();
                getGlobalVisibleRect(mFinalBounds, globalOffset);
                mFinalBounds.offset(-globalOffset.x, -globalOffset.y);

                LayoutParams lp = new LayoutParams(
                        mStartBounds.width(),
                        mStartBounds.width() * mFinalBounds.height() / mFinalBounds.width()
                );
                mScalableImageView.setLayoutParams(lp);

                startAnim();

                if (Build.VERSION.SDK_INT >= 16) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        mIndex = position;

        MoAlbumItem mediaData = mThumbViewInfoList.get(mIndex);
        if (mediaData.getmType().equals("image") || !mediaData.getmType().equals("video")) {
            is_stop.setVisibility(GONE);
        } else {
            is_stop.setVisibility(VISIBLE);
        }

        tv_middle_title.setText((mIndex + 1) + "/" + mListCount);

//        mStartBounds = mThumbViewInfoList.get(mIndex).getBounds();
//        if (mStartBounds == null) {
//            return;
//        }
//
//          computeStartScale();


    }

    private void startAnim() {
        if (!isAnimFinished) return;

        float startScale = computeStartScale();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mBackgroundView, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(mScalableImageView, View.X, mStartBounds.left, mFinalBounds.left),
                ObjectAnimator.ofFloat(mScalableImageView, View.Y, mStartBounds.top, mFinalBounds.top),
                ObjectAnimator.ofFloat(mScalableImageView, View.SCALE_X, 1f / startScale),
                ObjectAnimator.ofFloat(mScalableImageView, View.SCALE_Y, 1f / startScale));
        animatorSet.setDuration(ANIM_DURATION);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimFinished = false;
                mViewPager.setAlpha(0f);
                mScalableImageView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimFinished = true;
                mViewPager.setAlpha(1f);
                mScalableImageView.setVisibility(View.INVISIBLE);
            }
        });
        animatorSet.start();
    }

    private float computeStartScale() {
        float startScale;
        if ((float) mFinalBounds.width() / mFinalBounds.height()
                > (float) mStartBounds.width() / mStartBounds.height()) {
            // Extend start bounds horizontally （以竖直方向为参考缩放）
            startScale = (float) mStartBounds.height() / mFinalBounds.height();
            float startWidth = startScale * mFinalBounds.width();
            float deltaWidth = (startWidth - mStartBounds.width()) / 2;
            mStartBounds.left -= deltaWidth;
            mStartBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically （以水平方向为参考缩放）
            startScale = (float) mStartBounds.width() / mFinalBounds.width();
            float startHeight = startScale * mFinalBounds.height();
            float deltaHeight = (startHeight - mStartBounds.height()) / 2;
            mStartBounds.top -= deltaHeight;
            mStartBounds.bottom += deltaHeight;
        }

        return startScale;
    }

    public void startScaleDownAnimation() {
        if (!isAnimFinished) return;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mScalableImageView, View.X, mStartBounds.left),
                ObjectAnimator.ofFloat(mScalableImageView, View.Y, mStartBounds.top),
                ObjectAnimator.ofFloat(mScalableImageView, View.SCALE_X, 1f),
                ObjectAnimator.ofFloat(mScalableImageView, View.SCALE_Y, 1f));
        animatorSet.setDuration(ANIM_DURATION);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimFinished = false;
                Glide.with(getContext()).load(mThumbViewInfoList.get(mIndex).getmImage().getmUri()).into(mScalableImageView);
                mScalableImageView.setVisibility(View.VISIBLE);
                mViewPager.setAlpha(0f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimFinished = true;
                FrameLayout contentContainer = ((Activity) getContext()).findViewById(R.id.frame_layout);
                contentContainer.removeView(PreviewLayout.this);
            }
        });
        animatorSet.start();
    }


    private FrameLayout myFrame;

    public void setMyFrame(FrameLayout myFrame) {
        this.myFrame = myFrame;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_title:
                myFrame.setVisibility(GONE);
                break;
            case R.id.tv_middle_title:

                //todo
                break;
            case R.id.iv_right_title:
                //进入到单个编辑

                new CustomPopWindow.PopupWindowBuilder(mcontext)
                        .setView(R.layout.pop_select_makesize)
//                            .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
//                            .setBgDarkAlpha(alphaOnPop) // 控制亮度
                        .setViewClickListener(this)
                        .create()
                        .showAtLocation(myFrame, Gravity.CENTER, 0, 0);
                break;
        }


    }

    @Override
    public void onViewClick(CustomPopWindow popWindow, View view) {
        int i = view.getId();
        if (i == R.id.button16v9) {
            selectCreateRatio(POINT16V9);

        } else if (i ==R.id.button1v1) {
            selectCreateRatio(POINT1V1);

        } else if (i == R.id.button9v16) {
            selectCreateRatio(POINT9V16);

        } else if (i == R.id.button3v4) {
            selectCreateRatio(POINT3V4);

        } else if (i == R.id.button4v3) {
            selectCreateRatio(POINT4V3);

        } else {
        }
    }

    private void selectCreateRatio(int makeRatio) {
        ArrayList<ClipInfo> pathList = getClipInfoList();
        String path="";
        if(pathList.size()>0){
            path=pathList.get(0).getFilePath();
        }
        TimelineData.instance().setVideoResolution(getVideoEditResolution(makeRatio,path));
        TimelineData.instance().setClipInfoData(pathList);
        TimelineData.instance().setMakeRatio(makeRatio);
//        AppManager.getInstance().jumpActivity((Activity) mcontext, VideoEditActivity.class, null);
//        AppManager.getInstance().finishActivity();
    }

    private ArrayList<ClipInfo> getClipInfoList() {
        ArrayList<ClipInfo> pathList = new ArrayList<>();
//        for (MediaData mediaData : getMediaDataList()) {
//         MediaData  mediaData=mThumbViewInfoList.get(mIndex);
        MoAlbumItem albumItem = mThumbViewInfoList.get(mIndex);
        MediaData mediaData = new MediaData();
        mediaData.setThumbPath(albumItem.getmThumbnail().getmUri());
        mediaData.setType("video".equals(albumItem.getmType()) ? MediaConstant.VIDEO : MediaConstant.IMAGE);

        if (mediaData.getType() == MediaConstant.VIDEO) {
            mediaData.setDuration(albumItem.getmVideo().getmDuration());
        } else {
            mediaData.setPath(albumItem.getmImage().getmUri());
        }
        mediaData.setData(System.currentTimeMillis());
        //剪辑信息只要了 path
        ClipInfo clipInfo = new ClipInfo();
        clipInfo.setFilePath(albumItem.getmImage().getmUri());
        pathList.add(clipInfo);
//        }
        return pathList;
    }


    private class ImagePagerAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return mThumbViewInfoList != null ? mThumbViewInfoList.size() : 0;
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            PhotoView photoView = new PhotoView(getContext());
//


//            Glide.with(mContext).load(mThumbViewInfoList.get(position).getUrl()).apply(options).into(photoView);


            String path = "";
            if (mThumbViewInfoList.get(mIndex).getmType().equals("image")) {
                path = mThumbViewInfoList.get(mIndex).getmImage().getmUri();
            } else {
                path = mThumbViewInfoList.get(mIndex).getmThumbnail().getmUri();
            }
            Picasso.with(getContext()).load(path).placeholder(R.mipmap.bank_thumbnail_local)
                    .error(R.mipmap.bank_thumbnail_local).into(photoView);


            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
//                    startScaleDownAnimation();
                }
            });


            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}
