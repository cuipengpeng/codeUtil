package com.test.xcamera.home.presenter;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.framwork.base.presenter.BasePresenter;
import com.framwork.base.view.BaseViewInterface;
import com.test.xcamera.R;
import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.application.AppContext;
import com.test.xcamera.bean.ClickLikeAndShare;
import com.test.xcamera.bean.FeedList;
import com.test.xcamera.download.DownloadListener;
import com.test.xcamera.download.DownloadUtil;
import com.test.xcamera.home.RecommendFragmentView;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by smz on 2019/11/13.
 */

public class RecommendFragmentPresenter extends BasePresenter {

    private List<FeedList.Feed> mList;

    public RecommendFragmentPresenter(BaseViewInterface viewThis, List<FeedList.Feed> list) {
        super(viewThis);
        this.mList = list;
    }

    public void getFeedList(final int page, int pageCount, boolean isLoadMore) {
        ApiImpl.getInstance().getFeedList(page, pageCount, new CallBack<FeedList>() {
            @Override
            public void onSuccess(FeedList feedList) {
                if (feedList.isSucess()) {
                    if (feedList.getData().size() > 0) {
                        if (page == 1) {
                            mList.clear();
                            mList.addAll(feedList.getData());
                        } else {
                            mList.addAll(feedList.getData());
                        }

                        StringBuilder builder = new StringBuilder();

                        int length = feedList.getData().size();
                        for (int i = 0; i < length; i++) {
                            FeedList.Feed feed = feedList.getData().get(i);
                            if (length > 0 && i == length - 1) {
                                builder.append(feed.getOpusId());
                            } else {
                                builder.append(feed.getOpusId() + ",");
                            }
                        }
                        //添加完之后进行刷新当前的数据
                        ((RecommendFragmentView) viewThis).addView(mList, isLoadMore, feedList.getData());
                        ((RecommendFragmentView) viewThis).Refresh();
                        getLikeAndShareNum(page, builder.toString());
                    } else {
                        CameraToastUtil.show(AppContext.getInstance().getResources().getString(R.string.feed_data_no_more),
                                AppContext.getInstance());
                    }
                } else {
                    CameraToastUtil.show(feedList.getMessage(), (Context) viewThis);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                CameraToastUtil.show(e.toString(), AppContext.getInstance());
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void getLikeAndShareNum(final int page, String opusId) {
        ApiImpl.getInstance().getLikeAndShareNum(opusId, new CallBack<FeedList>() {
            @Override
            public void onSuccess(FeedList feedlist) {
                if (feedlist.isSucess()) {
                    for (int i = 0; i < mList.size(); i++) {
                        FeedList.Feed feedItem = mList.get(i);
                        for (int j = 0; j < feedlist.getData().size(); j++) {
                            FeedList.Feed feedLike = feedlist.getData().get(j);
                            if (feedItem.getOpusId() == feedLike.getOpusId()) {
                                feedItem.setLikeNum(feedLike.getLikeNum());
                                feedItem.setShareNum(feedLike.getShareNum());
                            }
                            if (j == feedlist.getData().size() - 1) {
                                break;
                            }
                        }
                    }
                    ((RecommendFragmentView) viewThis).setCurrentLikeAndShare(mList);
                    ((RecommendFragmentView) viewThis).Refresh();

                } else {
                    CameraToastUtil.show(feedlist.getMessage(), (Context) viewThis);
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

    public void getCurrentLikeAndShareNum(final int position, long opusId) {
        ApiImpl.getInstance().getLikeAndShareNum(opusId + "", new CallBack<FeedList>() {
            @Override
            public void onSuccess(FeedList feedlist) {
                if (feedlist.isSucess()) {

                    //将当前的喜欢下载数量设置上去
                    FeedList.Feed feed = mList.get(position);
                    FeedList.Feed feed1 = feedlist.getData().get(0);
                    feed.setLikeNum(feed1.getLikeNum());
                    feed.setShareNum(feed1.getShareNum());

                    //刷新一下当前view
                    ((RecommendFragmentView) viewThis).setCurrentLikeAndShare();
//                    ((RecommendFragmentView)viewThis).Refresh();
                } else {
                    CameraToastUtil.show(feedlist.getMessage(), (Context) viewThis);
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

    public void clickLike(final int index, FeedList.Feed item, final TextView textView) {
        ApiImpl.getInstance().clickLike(item.getOpusId(), new CallBack<ClickLikeAndShare>() {
            @Override
            public void onSuccess(ClickLikeAndShare like) {
                textView.setClickable(true);
                if (like.isSucess()) {

//                      FeedList.Feed feed1=mList.get(index);
//                      feed1.setLikeNum(feed1.getLikeNum()+1);
                } else {
                    CameraToastUtil.show(like.getMessage(), (Context) viewThis);
                }

            }

            @Override
            public void onFailure(Throwable e) {
                textView.setClickable(true);
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void clickShare(final int index, final FeedList.Feed item, final TextView textView) {
        ApiImpl.getInstance().clickShare(item.getOpusId(), new CallBack<ClickLikeAndShare>() {
            @Override
            public void onSuccess(ClickLikeAndShare share) {
                textView.setClickable(true);
                if (share.isSucess()) {
                    textView.setText(item.getShareNum() + 1 + "");
                    //todo 成功后不做任何处理 即可
                    FeedList.Feed feed1 = mList.get(index);
                    feed1.setShareNum(feed1.getShareNum() + 1);
                } else {
                    CameraToastUtil.show(share.getMessage(), AiCameraApplication.mApplication);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                textView.setClickable(true);
            }

            @Override
            public void onCompleted() {

            }
        });
    }


    /**
     * 下载当前Feed 视频
     *
     * @param item
     */
    public void downloadFeedFlie(final FeedList.Feed item, final Handler handler, final int index, final TextView textView) {
        String path = Constants.mFileDirector + System.currentTimeMillis() + ".MP4";
        DownloadUtil.downLoadFile(item.getVideoFileId(), path, new DownloadListener() {
            @Override
            public void onStart() {


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((RecommendFragmentView) viewThis).getRelativeLayout().setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onProgress(final int progress) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((RecommendFragmentView) viewThis).getProgressBar().setProgress(progress);
                    }
                });
            }

            @Override
            public void onFinish(final File path) {
                //todo   通知相册也  发送下载数量变化接口

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((RecommendFragmentView) viewThis).getRelativeLayout().setVisibility(View.GONE);
                        FileUtil.sendScanBroadcast(AiCameraApplication.mApplication, path.getAbsolutePath());
                        CameraToastUtil.show("已下载到相册中", AiCameraApplication.mApplication);
                        clickShare(index, item, textView);
                    }
                });
            }

            @Override
            public void onFail(String errorInfo) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setClickable(true);
                        CameraToastUtil.show("下载视频失败", AiCameraApplication.mApplication);
                        ((RecommendFragmentView) viewThis).getRelativeLayout().setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}
