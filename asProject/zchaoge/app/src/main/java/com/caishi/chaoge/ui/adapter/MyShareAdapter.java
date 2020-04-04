package com.caishi.chaoge.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.sdk.account.open.aweme.api.TTOpenApi;
import com.bytedance.sdk.account.open.aweme.base.DYMediaContent;
import com.bytedance.sdk.account.open.aweme.base.DYVideoObject;
import com.bytedance.sdk.account.open.aweme.impl.TTOpenApiFactory;
import com.bytedance.sdk.account.open.aweme.share.Share;
import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.ShareBean;
import com.caishi.chaoge.ui.fragment.MyFragment;
import com.caishi.chaoge.utils.ToastUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyShareAdapter extends RecyclerView.Adapter<MyShareAdapter.ViewHolder> {
    FragmentActivity mContext;
    public List<ShareBean> mDataList = new ArrayList<>();

    public MyShareAdapter(FragmentActivity context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<ShareBean> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_share, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.img.setBackgroundResource(mDataList.get(position).imgRes);
        holder.title.setText(mDataList.get(position).title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
                switch (position) {
                    case 0:
                        platform = SHARE_MEDIA.WEIXIN_CIRCLE;
                        break;
                    case 1:
                        platform = SHARE_MEDIA.WEIXIN;
                        break;
                    case 2:
                        platform = SHARE_MEDIA.QQ;
                        break;
                    case 3:
//                        platform = SHARE_MEDIA.;
                        TTOpenApi bdOpenApi;
                        bdOpenApi = TTOpenApiFactory.create(mContext);
                        Share.Request request = new Share.Request();
                        ArrayList<String> mUri = new ArrayList<>();
//                        mUri.add(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"test0011.mp4");
                        mUri.add(MyFragment.videoThumbPath);
                        DYVideoObject videoObject = new DYVideoObject();
                        videoObject.mVideoPaths = mUri;
                        DYMediaContent content = new DYMediaContent();
                        content.mMediaObject = videoObject;
                        request.mMediaContent = content;
                        request.mState = "ss";
                        bdOpenApi.share(request);
                        return;
//                        break;
                    case 4:
                        platform = SHARE_MEDIA.SINA;
                        break;
                    case 5:
                        platform = SHARE_MEDIA.QZONE;
                        break;
                }
                String url = "";
                String title = "";
                String info = "";
                UMImage umImage = new UMImage(mContext, R.drawable.ic_launcher);
//                switch (shareFlag) {
//                    case 0://分享视频
////                                                            url = "http://api.chaogevideo.com/static/share/download.html";
//                        url = shareUrl;
//                        title = shareTitle;
//                        info = "www.chaogevideo.com";
//                        umImage = new UMImage(mContext, imgPath);
//                        break;
//                    case 1://分享主页
////                                                            url = "http://api.chaogevideo.com/static/share/download.html";
//                        url = shareUrl;
//                        title = shareTitle;
//                        info = "www.chaogevideo.com";
//                        umImage = new UMImage(mContext, imgPath);
//                        break;
//
//                    case 2://分享下载链接
//                        url = "http://api.chaogevideo.com/static/share/download.html";
                        url = MyFragment.urlVideoPath;
                        title = MyFragment.shareTitle;
                        info = "www.chaogevideo.com";
                        umImage = new UMImage(mContext, MyFragment.videoThumbPath);
//                        break;
//                }
//
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("userId", userId);
//                paramsMap.put("momentId", momentId);
//                paramsMap.put("desUserId", desUserId);
//                paramsMap.put("likeStatus", 2+"");
//                HttpRequest.post(false , HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.FORWARD, paramsMap, new HttpRequest.HttpResponseCallBank() {
//                    @Override
//                    public void onSuccess(String response) {
//                    }
//
//                    @Override
//                    public void onFailure(String t) {
//                    }
//                });

//                if (textView != null) {
//                    String shareNum = Integer.parseInt(textView.getText().toString()) + 1 + "";
//                    textView.setText(shareNum);
//                }
                UMVideo web = new UMVideo(url);
                web.setTitle(title);//标题
                web.setThumb(umImage);  //缩略图
                web.setDescription(info);//描述
                new ShareAction(mContext)
                        .setPlatform(platform)
                        .withMedia(web)
                        .setCallback(new UMShareListener() {
                            /**
                             * @descrption 分享开始的回调
                             * @param platform 平台类型
                             */
                            @Override
                            public void onStart(SHARE_MEDIA platform) {

                            }

                            /**
                             * @descrption 分享成功的回调
                             * @param platform 平台类型
                             */
                            @Override
                            public void onResult(SHARE_MEDIA platform) {
                                ToastUtils.show(mContext, "分享成功");
                            }

                            /**
                             * @descrption 分享失败的回调
                             * @param platform 平台类型
                             * @param t 错误原因
                             */
                            @Override
                            public void onError(SHARE_MEDIA platform, Throwable t) {
                                ToastUtils.show(mContext, t.getMessage());
                            }

                            /**
                             * @descrption 分享取消的回调
                             * @param platform 平台类型
                             */
                            @Override
                            public void onCancel(SHARE_MEDIA platform) {
                                ToastUtils.show(mContext, "分享取消");
                            }
                        })
                        .share();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_myShare_img)
        ImageView img;
        @BindView(R.id.tv_myShare_title)
        TextView title;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
