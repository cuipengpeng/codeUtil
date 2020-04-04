package com.caishi.chaoge.ui.adapter;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.HomeDataBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.activity.LoginActivity;
import com.caishi.chaoge.ui.activity.UserDetailsActivity;
import com.caishi.chaoge.ui.activity.VideoActivity;
import com.caishi.chaoge.ui.dialog.HomeDialog;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.NetConnectUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.StringUtil;
import com.caishi.chaoge.utils.ToastUtils;
import com.caishi.chaoge.utils.Utils;
import com.xiao.nicevideoplayer.NiceVideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.caishi.chaoge.utils.ConstantUtils.LOGIN_FLAG;

public class VideoRecycleViewAdapter extends RecyclerView.Adapter<VideoRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "HomeRecycleView2Adapter";
    private FragmentActivity mContext;
    public ArrayList<HomeDataBean> homeDataList = new ArrayList<>();
    private int statue;
    OnItemClickListener onItemClickListener;
    private int likeCount;
    public boolean showCommentDialog = false;

    public VideoRecycleViewAdapter(FragmentActivity baseActivity) {
        this.mContext = baseActivity;
    }

    public void updateData(boolean refresh, List<HomeDataBean> data) {
        if (refresh) {
            this.homeDataList.clear();
        }
        this.homeDataList.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_recycleview, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        likeCount = homeDataList.get(position).getLikeCount();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(holder, holder.getAdapterPosition());
            }
        });
        LogUtil.i("Utils.isUrl(homeDataList.get(position).getCover()====" + "===position====" + position + "======" +
                Utils.isUrl(homeDataList.get(position).getCover()));
        holder.iv_video_thumb.setVisibility(View.VISIBLE);
        GlideUtil.loadImg(Utils.isUrl(homeDataList.get(position).getCover()), holder.iv_video_thumb, R.drawable.im_video_loading);
        //视频地址
        holder.video_itemHome_play.setUp(Utils.isUrl(homeDataList.get(position).getVideoUrl()), true, null);
        holder.video_itemHome_play.setOnHintSurface(new NiceVideoPlayer.OnHintSurface() {
            @Override
            public void onInfo() {
                holder.iv_video_thumb.setVisibility(View.INVISIBLE);
            }
        });
        holder.video_itemHome_play.setOnVideoPrepared(new NiceVideoPlayer.OnVideoPrepared() {
            @Override
            public void onPrepared() {

            }
        });

        GlideUtil.loadCircleImg(Utils.isUrl(homeDataList.get(position).getAvatar()), holder.img_itemHome_userHead);//头像
        holder.tv_itemHome_userName.setText(homeDataList.get(position).getNickname());//昵称
        holder.tv_itemHome_userInfo.setText(homeDataList.get(position).getContent());//内容
        holder.tv_itemHome_praise.setText(homeDataList.get(position).getLikeCount() + "");//点赞数
        holder.tv_itemHome_comment.setText(homeDataList.get(position).getCommentCount() + "");//评论数
        holder.tv_itemHome_share.setText(homeDataList.get(position).getForwardCount() + "");//转发数

        if (homeDataList.get(position).getLikeStatus() == 1) {//点赞
            holder.img_itemHome_praise.setImageResource(R.drawable.ic_praise_later);
        } else {
            holder.img_itemHome_praise.setImageResource(R.drawable.ic_praise);
        }

        if (homeDataList.get(position).getFollowStatus() == 1) {//关注
            holder.img_itemHome_attention.setVisibility(View.INVISIBLE);
        } else if (homeDataList.get(position).getFollowStatus() == 3) {
            holder.img_itemHome_attention.setVisibility(View.INVISIBLE);
        } else {
            holder.img_itemHome_attention.setVisibility(View.VISIBLE);
            holder.img_itemHome_attention.setAnimation("attention.json");
            holder.img_itemHome_attention.setProgress(0);
        }

        //点赞
        holder.img_itemHome_praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startLogin())
                    return;

                final HomeDataBean itemData = homeDataList.get(holder.getAdapterPosition());
                if (itemData.getLikeStatus() == 1) {
                    statue = 1;
                } else {
                    statue = 0;
                }

                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put("userId", SPUtils.readCurrentLoginUserInfo(mContext).userId);
                paramsMap.put("momentId", itemData.getMomentId());
                paramsMap.put("desUserId", itemData.getUserId());
                paramsMap.put("likeStatus", statue + "");

                HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.LIKE, paramsMap, new HttpRequest.HttpResponseCallBank() {
                    @Override
                    public void onSuccess(String response) {
                        if (statue == 1) {
                            itemData.setLikeStatus(0);
                            itemData.setLikeCount(itemData.getLikeCount() - 1);
                            holder.tv_itemHome_praise.setText(itemData.getLikeCount() + "");
                            holder.img_itemHome_praise.setImageResource(R.drawable.ic_praise);
                        } else {
                            itemData.setLikeStatus(1);
                            itemData.setLikeCount(itemData.getLikeCount() + 1);
                            holder.tv_itemHome_praise.setText(itemData.getLikeCount() + "");
                            holder.img_itemHome_praise.setImageResource(R.drawable.ic_praise_later);
                        }
                    }

                    @Override
                    public void onFailure(String t) {
                    }
                });
            }
        });
        //评论
        holder.img_itemHome_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeDialog.newInstance().showCommentDialog(mContext, VideoRecycleViewAdapter.this, homeDataList.get(holder.getAdapterPosition()).getMomentId(),
                        Integer.parseInt(holder.tv_itemHome_comment.getText().toString()), holder.tv_itemHome_comment);
            }
        });
        //分享
        holder.img_itemHome_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = homeDataList.get(holder.getAdapterPosition()).getContent();
                if (TextUtils.isEmpty(str)) {
                    String str1 = homeDataList.get(holder.getAdapterPosition()).getNickname();
                    String title = str1.length() > 10 ? str1.substring(0, 10) + "..." : str1;
                    str = title + "的朝歌视频，快来看看吧!";
                }
                //    String title = str.length() > 10 ? str.substring(0, 11) + "..." : str;
                String url = RequestURL.SHARE_VIDEO + "?im=" +
                        homeDataList.get(holder.getAdapterPosition()).getMomentId() + "&iu=" + homeDataList.get(holder.getAdapterPosition()).getUserId();
                HomeDialog homeDialog = HomeDialog.newInstance();

                String userId;
                if (SPUtils.isLogin(mContext)) {
                    userId = SPUtils.readCurrentLoginUserInfo(mContext).userId;
                } else {
                    //TODO 传设备号登陆的userId
                    userId = "123456";
                }
                homeDialog.showShareDialog(mContext, 0, str,
                        Utils.isUrl(homeDataList.get(holder.getAdapterPosition()).getCover()), url,
                        Utils.isUrl(homeDataList.get(holder.getAdapterPosition()).getVideoUrl()), userId,
                        homeDataList.get(holder.getAdapterPosition()).getMomentId(), homeDataList.get(holder.getAdapterPosition()).getUserId(),
                        holder.tv_itemHome_share, position);

                homeDialog.setOnDeleteVideoListener(new HomeDialog.OnDeleteVideoListener() {
                    @Override
                    public void deleteVideo(int state) {
                        homeDataList.remove(position);
//                        homeDataList.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });
            }
        });
        //头像
        holder.img_itemHome_userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPUtils.isLogin(mContext) && homeDataList.get(holder.getAdapterPosition()).getUserId().equals(SPUtils.readCurrentLoginUserInfo(mContext).userId)) {
//                    List<Fragment> fragments = mContext.getSupportFragmentManager().getFragments();
////                    MainFragment mainFragment = (MainFragment) fragments.get(0);
//                    ((MainActivity) mContext).switchLayout(3);
                } else {
                    UserDetailsActivity.open(mContext, homeDataList.get(holder.getAdapterPosition()).getUserId());
                }


            }
        });
        //关注
        holder.img_itemHome_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetConnectUtil.isNetConnected(mContext)) {
                    ToastUtils.showCentreToast(mContext, "请检查网络连接是否正常");
                    return;
                }
                if (startLogin())
                    return;
                holder.img_itemHome_attention.playAnimation();
                holder.img_itemHome_attention.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.img_itemHome_attention.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        holder.img_itemHome_attention.setVisibility(View.INVISIBLE);
                        holder.img_itemHome_attention.setEnabled(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });


                Map<String, String> paramsMap = new HashMap<String, String>();
                paramsMap.put("userId", homeDataList.get(holder.getAdapterPosition()).getUserId());
                paramsMap.put("status", 1 + "");

                HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.FOLLOW, paramsMap, new HttpRequest.HttpResponseCallBank() {
                    @Override
                    public void onSuccess(String response) {
                    }

                    @Override
                    public void onFailure(String t) {
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return homeDataList.size();
    }

    private boolean startLogin() {
        if (!SPUtils.isLogin(mContext)) {
            LoginActivity.open(mContext, -1);
        }
        return !SPUtils.isLogin(mContext);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.video_itemHome_play)
        public NiceVideoPlayer video_itemHome_play;
        @BindView(R.id.rl_itemHome)
        RelativeLayout rl_itemHome;
        @BindView(R.id.iv_video_thumb)
        ImageView iv_video_thumb;
        /**
         * 头像
         */
        @BindView(R.id.img_itemHome_userHead)
        ImageView img_itemHome_userHead;
        /**
         * 关注图片
         */
        @BindView(R.id.img_itemHome_attention)
        LottieAnimationView img_itemHome_attention;
        /**
         * 点赞图片
         */
        @BindView(R.id.img_itemHome_praise)
        ImageView img_itemHome_praise;
        /**
         * 评论图片
         */
        @BindView(R.id.img_itemHome_comment)
        ImageView img_itemHome_comment;
        /**
         * 分享图片
         */
        @BindView(R.id.img_itemHome_share)
        ImageView img_itemHome_share;
        /**
         * 点赞数
         */
        @BindView(R.id.tv_itemHome_praise)
        TextView tv_itemHome_praise;
        /**
         * 评论数
         */
        @BindView(R.id.tv_itemHome_comment)
        public TextView tv_itemHome_comment;
        /**
         * 分享数
         */
        @BindView(R.id.tv_itemHome_share)
        TextView tv_itemHome_share;
        /**
         * 用户名
         */
        @BindView(R.id.tv_itemHome_userName)
        TextView tv_itemHome_userName;
        /**
         * 内容
         */
        @BindView(R.id.tv_itemHome_userInfo)
        TextView tv_itemHome_userInfo;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;


    }

    public interface OnItemClickListener {
        public void onItemClick(VideoRecycleViewAdapter.ViewHolder viewHolder, int position);
    }

}
