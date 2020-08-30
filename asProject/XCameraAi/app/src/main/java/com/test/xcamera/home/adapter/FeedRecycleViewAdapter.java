package com.test.xcamera.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dueeeke.videoplayer.CoverImageView;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.editvideo.ToastUtil;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.ClickLikeAndShare;
import com.test.xcamera.bean.FeedList;
import com.test.xcamera.cameraclip.DownloadVideoTempleteDataUtil;
import com.test.xcamera.h5_page.H5BasePageActivity;
import com.test.xcamera.h5_page.SqueezePageActivity;
import com.test.xcamera.home.fragment.FeedFragment;
import com.test.xcamera.login.LoginActivty;
import com.test.xcamera.statistic.StatisticShare;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.DownloadWebFileUtil;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.utils.PUtil;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.utils.StorageUtils;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;
import com.test.xcamera.widget.DouYinController;
import com.test.xcamera.widget.LoveLayout;
import com.moxiang.common.share.IShare;
import com.moxiang.common.share.ShareManager;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FeedRecycleViewAdapter extends BaseRecyclerAdapter<FeedList.Feed, FeedRecycleViewAdapter.ViewHolder> {
    private Fragment mFragment;
    private final String TAG = "FeedRecycleViewAdapter";
    private final CommonDownloadDialog commonDownloadDialog;
    private Handler handler = new Handler();
    private boolean enableClick = true;
    private boolean isHideCommunityIcon;

    public FeedRecycleViewAdapter(Context context, Fragment fragment) {
        super(context);
        mFragment = fragment;
        commonDownloadDialog = new CommonDownloadDialog(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    RequestOptions options = new RequestOptions()
            .placeholder(R.mipmap.bj_net)//图片加载出来前，显示的图片
            .fallback(R.mipmap.bj_net) //url为空的时候,显示的图片
            .error(R.mipmap.bj_net);//图片加载失败后，显示的图片;

    RequestOptions squeezeOptions = new RequestOptions()
            .placeholder(R.mipmap.bj_net)//图片加载出来前，显示的图片
            .fallback(R.mipmap.bj_net) //url为空的时候,显示的图片
            .error(R.mipmap.bj_net);//图片加载失败后，显示的图片;

    private long time = 0;

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        FeedList.Feed feedBean = mData.get(position);

        Glide.with(mContext).load(feedBean.getCoverUrl()).into(holder.coverImageView);
        holder.coverImageView.setVisibility(View.GONE);

        DouYinController mDouYinController = new DouYinController(mContext);
        ImageView thumb = mDouYinController.getThumb();
        Glide.with(mContext).load(feedBean.getCoverUrl()).apply(options).into(thumb);

        mDouYinController.setOnInfoListener(new DouYinController.OnInfoListener() {
            @Override
            public void onInfo() {
                holder.coverImageView.setVisibility(View.INVISIBLE);
            }
        });

        setIJKLayoutAndUrl(holder, feedBean, mDouYinController);

        setLayoutData(holder, feedBean);

        holder.squeezeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((System.currentTimeMillis() - time) > 2000) {
                    H5BasePageActivity.openActivity(mContext, feedBean.getLink(), SqueezePageActivity.class);
                }
                time = System.currentTimeMillis();
            }
        });
        holder.likeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SPUtils.isLogin(mContext)) {
                    Intent intent = new Intent(mContext, LoginActivty.class);
                    mContext.startActivity(intent);
                    return;
                }
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.net_enable), Toast.LENGTH_LONG).show();
                    return;
                }
                if (feedBean.isLike()) {
                    cancelLike(feedBean, holder);
                } else {
                    clickLike(feedBean, holder);
                }
            }
        });
        holder.shareTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickShare(feedBean, holder);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClick(view, position);
            }
        });
        holder.communityIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCommunity();
            }
        });
        holder.mLoveLayout.setCallBack(new LoveLayout.OnCallBack() {
            @Override
            public void onLikeListener() {
                Log.i("Feed", "LoveLayout_onLikeListener " + !feedBean.isLike());
                if (!feedBean.isLike()) {
                    if (!SPUtils.isLogin(mContext)) {
                        Intent intent = new Intent(mContext, LoginActivty.class);
                        mContext.startActivity(intent);
                        return;
                    }
                    clickLike(feedBean, holder);
                }
            }

            @Override
            public void onPauseListener() {
                Log.i("Feed", "LoveLayout_onPauseListener " + holder.ijkVideoView.isPlaying());
                if (holder.ijkVideoView.isPlaying()) {
                    holder.ijkVideoView.pause();
                } else {
                    holder.ijkVideoView.start();
                }
            }
        });
    }

    private void setLayoutData(@NonNull ViewHolder holder, FeedList.Feed feedBean) {
        holder.userNameTextView.setText(feedBean.getAuthorName());
        holder.contentTextView.setText(feedBean.getDescription());
        holder.musicNameTextView.setText(feedBean.getDescription());

        if (!TextUtils.isEmpty(feedBean.getLink())) {
            holder.squeezeLayout.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(feedBean.getIconUrl()).apply(squeezeOptions).into(holder.squeezeIcon);
            holder.squeezeeScribe.setText(feedBean.getContent());
        } else {
            holder.squeezeLayout.setVisibility(View.GONE);
        }

        if (isHideCommunityIcon) {
            holder.communityIcon.setVisibility(View.VISIBLE);
        } else {
            holder.communityIcon.setVisibility(View.GONE);
        }

        if (feedBean.isLike()) {
            setTextImage(holder.likeTextView, R.mipmap.btn_praise);
        } else {
            setTextImage(holder.likeTextView, R.mipmap.btn_praise_no);
        }

        holder.likeTextView.setText((feedBean.getLikeNum() >= 10000) ? parseNumber(feedBean.getLikeNum()) + "w" : feedBean.getLikeNum() + "");
        holder.shareTextView.setText((feedBean.getShareNum() >= 10000) ? parseNumber(feedBean.getShareNum()) + "w" : feedBean.getShareNum() + "");
    }

    private void setIJKLayoutAndUrl(@NonNull ViewHolder holder, FeedList.Feed feedBean, DouYinController mDouYinController) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.ijkVideoView.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        int w = (int) (PUtil.getScreenH(mContext) * 9 / 16f + DensityUtils.dp2px(mContext, 20));
        lp.width = w;
        int h = lp.height;
        if (h < w * 16f / 9) {
            h = (int) (w * 16f / 9 + DensityUtils.dp2px(mContext, 0));
        }
        lp.height = h;
        holder.ijkVideoView.setLayoutParams(lp);
        holder.ijkVideoView.setUrl(feedBean.getVideoUrl());
        PlayerConfig config = new PlayerConfig.Builder().setLooping().usingAndroidMediaPlayer().build();
        holder.ijkVideoView.setPlayerConfig(config);
        holder.ijkVideoView.setVideoController(mDouYinController);
        holder.ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT); //播放的设置的屏幕参数问题
    }

    private void goToCommunity() {
        if (mContext == null) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.net_enable), Toast.LENGTH_LONG).show();
            return;
        }
        ((FeedFragment) mFragment).loginCommunity();
    }


    private void cancelLike(FeedList.Feed feedBean, ViewHolder holder) {
        ApiImpl.getInstance().cancelLike(feedBean.getOpusId(), new CallBack<ClickLikeAndShare>() {
            @Override
            public void onSuccess(ClickLikeAndShare clickLikeAndShare) {
                if (clickLikeAndShare.isSucess()) {
                    like(false, clickLikeAndShare, feedBean, holder);
                } else if (clickLikeAndShare.getCode() == Constants.TOKEN_INVALID) {
                    SPUtils.unLogin(AiCameraApplication.mApplication);
                    Intent intent = new Intent(mContext, LoginActivty.class);
                    mContext.startActivity(intent);
                }

            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });

    }

    private void clickShare(FeedList.Feed feedBean, @NonNull ViewHolder holder) {
        if (!enableClick) {
            return;
        }
        enableClick = false;

        share(feedBean, holder);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                enableClick = true;
            }
        }, 2000);
    }

    private void clickLike(FeedList.Feed feedBean, @NonNull ViewHolder holder) {
        ApiImpl.getInstance().clickLike(feedBean.getOpusId(), new CallBack<ClickLikeAndShare>() {
            @Override
            public void onSuccess(ClickLikeAndShare like) {
                if (like.isSucess()) {
                    like(true, like, feedBean, holder);
                } else if (like.getCode() == Constants.TOKEN_INVALID) {
                    SPUtils.unLogin(AiCameraApplication.mApplication);
                    Intent intent = new Intent(mContext, LoginActivty.class);
                    mContext.startActivity(intent);
                }

            }

            @Override
            public void onFailure(Throwable e) {
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    private void like(boolean isLike, ClickLikeAndShare like, FeedList.Feed feedBean, @NonNull ViewHolder holder) {
        if (isLike) {
            feedBean.setLikeNum(feedBean.getLikeNum() + 1);
            setTextImage(holder.likeTextView, R.mipmap.btn_praise);
        } else {
            feedBean.setLikeNum(feedBean.getLikeNum() - 1);
            setTextImage(holder.likeTextView, R.mipmap.btn_praise_no);
        }
        holder.likeTextView.setText((feedBean.getLikeNum() >= 10000)
                ? parseNumber(feedBean.getLikeNum()) + "w" : feedBean.getLikeNum() + "");
        feedBean.setLike(isLike);
    }

    public void setTextImage(TextView textImage, int imagid) {
        Drawable drawable = mContext.getResources().getDrawable(imagid);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textImage.setCompoundDrawables(null, drawable, null, null);
    }

    private float parseNumber(int number) {
        if (number >= 10000) {
            int a = number / 10000;
            int b = number % 10000;
            float b1 = b / 10000;
            return a + b1;
        }
        return number;
    }

    /**
     * 分享
     */
    public void share(FeedList.Feed feedBean, ViewHolder holder) {
        ShareManager.ShareEntity shareEntity = new ShareManager.ShareEntity();
        shareEntity.setBackHandle(true);
        shareEntity.setOnStatisticShare(new ShareManager.OnStatisticShare() {
            @Override
            public void onCallBack(ShareManager.ShareChooser shareChooser) {
                StatisticShare.getInstance().shareTo(shareChooser, StatisticShare.Home_Feed);
            }
        });

        ShareManager.getInstance().showMoSharePlatform(mFragment.getActivity(), shareEntity, new IShare() {
            @Override
            public void goHome() {

            }

            @Override
            public void goPublishPage() {

            }

            @Override
            public void onItemClick(ShareManager.ShareChooser shareChooser) {
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.net_enable), Toast.LENGTH_LONG).show();
                    return;
                }
                ApiImpl.getInstance().clickShare(feedBean.getOpusId(), new CallBack<ClickLikeAndShare>() {
                    @Override
                    public void onSuccess(ClickLikeAndShare clickLikeAndShare) {
                        if (clickLikeAndShare.isSucess()) {
                            feedBean.setShareNum(feedBean.getShareNum() + 1);
                            holder.shareTextView.setText((feedBean.getShareNum() >= 10000) ? parseNumber(feedBean.getShareNum()) + "w" : feedBean.getShareNum() + "");
                        } else {
                            Toast.makeText(mContext, clickLikeAndShare.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {

                    }

                    @Override
                    public void onCompleted() {

                    }
                });
                downloadFile(feedBean, shareChooser);
            }

            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {

            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        }, null);

    }

    private void downloadFile(FeedList.Feed feed, ShareManager.ShareChooser shareChooser) {
        if (StorageUtils.getAvailableExternalMemorySize() < DownloadVideoTempleteDataUtil.MIN_STORAGE) {
            ToastUtil.showToast(AiCameraApplication.mApplication, AiCameraApplication.mApplication.getResources().getString(R.string.stor_no));
            return;
        }
        String videoUrl = Constants.getFileIdToUrl(feed.getVideoFileId() + "");
        String fileName = videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.length()) + ".mp4";
        DownloadWebFileUtil.getInstance().downloadBigFile(videoUrl, Constants.mFileDirector, fileName, new DownloadWebFileUtil.DownloadProgressCallback() {
            @Override
            public void onStart() {
                commonDownloadDialog.showDialog(false);
                commonDownloadDialog.setCancelable(false);
            }

            @Override
            public void onProgress(int progress) {
                String format = String.format(mContext.getResources().getString(R.string.download_save_progress), progress) + "%";
                commonDownloadDialog.setProgress(format);
            }

            @Override
            public void onFinish(File path) {
                Log.i(TAG, "onFinish: 下载成功!!!!!!");
                commonDownloadDialog.dismissDialog();
                ShareManager.ShareEntity shareEntity = new ShareManager.ShareEntity();
                shareEntity.setThumbUrl(path.getAbsolutePath());
                shareEntity.setBackHandle(false);
                shareEntity.setOnStatisticShare(new ShareManager.OnStatisticShare() {
                    @Override
                    public void onCallBack(ShareManager.ShareChooser shareChooser) {
                        StatisticShare.getInstance().shareTo(shareChooser, StatisticShare.Home_Feed);
                    }
                });
                ShareManager.getInstance().shareTo(mFragment.getActivity(), shareEntity, shareChooser, new IShare() {
                    @Override
                    public void goHome() {

                    }

                    @Override
                    public void goPublishPage() {

                    }

                    @Override
                    public void onItemClick(ShareManager.ShareChooser shareChooser) {

                    }

                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                });
            }

            @Override
            public void onFail(String errorInfo) {
                commonDownloadDialog.dismissDialog();
            }
        });
    }

    public void setCommunityIconHide(boolean isHideCommunityIcon) {
        this.isHideCommunityIcon = isHideCommunityIcon;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ijk_feedAdapter_videoPlayer)
        IjkVideoView ijkVideoView;
        @BindView(R.id.iv_feedAdapter_photoView)
        CoverImageView coverImageView;
        @BindView(R.id.iv_feedAdapter_coverImageView01)
        ImageView coverImageView01;
        @BindView(R.id.iv_feedAdapter_coverImageView02)
        ImageView coverImageView02;
        @BindView(R.id.fl_feedAdapter_container)
        FrameLayout container;
        @BindView(R.id.tv_feedAdapter_share)
        TextView shareTextView;
        @BindView(R.id.tv_feedAdapter_like)
        TextView likeTextView;
        @BindView(R.id.tv_feedAdapter_musicName)
        TextView musicNameTextView;
        @BindView(R.id.tv_feedAdapter_userName)
        TextView userNameTextView;
        @BindView(R.id.tv_feedAdapter_content)
        TextView contentTextView;
        @BindView(R.id.communityIcon)
        TextView communityIcon;
        @BindView(R.id.mLoveLayout)
        LoveLayout mLoveLayout;
        @BindView(R.id.squeezeLayout)
        LinearLayout squeezeLayout;
        @BindView(R.id.squeezeIcon)
        ImageView squeezeIcon;
        @BindView(R.id.squeezeeScribe)
        TextView squeezeeScribe;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
