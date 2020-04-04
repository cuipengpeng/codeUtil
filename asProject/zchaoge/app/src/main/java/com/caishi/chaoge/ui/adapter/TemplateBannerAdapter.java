package com.caishi.chaoge.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.RecommendBean;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiao.nicevideoplayer.NiceVideoPlayer;

public class TemplateBannerAdapter extends BaseQuickAdapter<RecommendBean, BaseViewHolder> {
    private Context context;
    private int height;

    public TemplateBannerAdapter(Context context, int height) {
        super(R.layout.item_template_banner);
        this.context = context;
        this.height = height;
    }


    @Override
    protected void convert(BaseViewHolder helper, RecommendBean item) {
        final NiceVideoPlayer video_banner_play = helper.getView(R.id.video_banner_play);
        final ImageView img_banner_thumb = helper.getView(R.id.img_banner_thumb);
        final LinearLayout ll_banner_thumb = helper.getView(R.id.ll_banner_thumb);
        final ImageView img_banner_image = helper.getView(R.id.img_banner_image);
        final ImageView img_banner_loading = helper.getView(R.id.img_banner_loading);
        RelativeLayout rl_banner_layout = helper.getView(R.id.rl_banner_layout);
        TextView tv_banner_time = helper.getView(R.id.tv_banner_time);
        ViewGroup.LayoutParams layoutParams =
                rl_banner_layout.getLayoutParams();
        layoutParams.width = 7 * height / 16;
        rl_banner_layout.setLayoutParams(layoutParams);
        GlideUtil.loadImg(Utils.isUrl(item.modelCover), img_banner_image);
        video_banner_play.setUp(Utils.isUrl(item.modelVideo), true, null);
        video_banner_play.setLooping(true);
        video_banner_play.setTAG(TAG);
        video_banner_play.setPosition(helper.getAdapterPosition());
        video_banner_play.setOnHintSurface(new NiceVideoPlayer.OnHintSurface() {
            @Override
            public void onInfo() {
                img_banner_image.setVisibility(View.INVISIBLE);
            }

        });
        video_banner_play.setOnVideoPrepared(new NiceVideoPlayer.OnVideoPrepared() {
            @Override
            public void onPrepared() {
                img_banner_loading.clearAnimation();
                img_banner_loading.setVisibility(View.GONE);
            }
        });
        ll_banner_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_banner_loading.clearAnimation();
                if (video_banner_play.isPaused()) {
                    video_banner_play.restart();
                    img_banner_thumb.setVisibility(View.INVISIBLE);
                } else {
                    if (video_banner_play.isPlaying()) {
                        video_banner_play.pause();
                        img_banner_thumb.setVisibility(View.VISIBLE);
                    } else {
                        video_banner_play.start(false);
                        img_banner_loading.setVisibility(View.VISIBLE);
                        img_banner_thumb.setVisibility(View.INVISIBLE);
                        setAnim(img_banner_loading);
                    }

                }

            }
        });
        tv_banner_time.setVisibility(item.backGroundDuration != 0 ? View.VISIBLE : View.GONE);
        tv_banner_time.setText(item.backGroundDuration + "S");
    }

    private void setAnim(ImageView imageView) {
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(1000);//设置动画持续周期
        rotate.setRepeatCount(-1);//设置重复次数
        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        imageView.startAnimation(rotate);
    }
}
