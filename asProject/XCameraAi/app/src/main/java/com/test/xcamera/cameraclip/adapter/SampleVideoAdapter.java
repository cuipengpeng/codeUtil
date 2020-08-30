package com.test.xcamera.cameraclip.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.utils.GlideUtils;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.widget.AlbumPlayController;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SampleVideoAdapter extends BaseRecyclerAdapter<VideoTemplete, SampleVideoAdapter.ViewHolder> {

    public SampleVideoAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.item_sample_video_activity, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        VideoTemplete videoTemplete = mData.get(i);
        if(videoTemplete!=null){
            if(videoTemplete.isNetTemplete()){
//                String thumbnailUrl = Constants.getFileIdToUrl(videoTemplete.getCoverFileId()+"");
                GlideUtils.GlideLoader(mContext, videoTemplete.getCoverUrl(), viewHolder.image);
//                GlideUtils.GlideLoader(mContext, videoUrl, viewHolder.image);
//                String videoUrl = Constants.getFileIdToUrl(videoTemplete.getVideoFileId()+"");
                viewHolder.ijkPlayer.setUrl(videoTemplete.getVideoUrl());
            }else {
                GlideUtils.GlideLoader(mContext, videoTemplete.getLocalSampleVideoPath(), viewHolder.image);
                viewHolder.ijkPlayer.setUrl(videoTemplete.getLocalSampleVideoPath());
            }
            LoggerUtils.printLog("video url= "+viewHolder.ijkPlayer.getmCurrentUrl());

            AlbumPlayController albumPlayController = new AlbumPlayController(mContext);
//        String thumbUrl = Constants.getFileIdToUrl(mData.get(position).getCoverFileId() + "");
//        Glide.with(mContext).load(thumbUrl).apply(options).into(albumPlayController.getThumb());

            albumPlayController.setOnInfoListener(new AlbumPlayController.OnInfoListener() {
                @Override
                public void onInfo() {
                    viewHolder.image.setVisibility(View.GONE);
                }
            });

            PlayerConfig config = new PlayerConfig.Builder().setLooping().build();
            viewHolder.ijkPlayer.setPlayerConfig(config);
            albumPlayController.setMediaPlayer(viewHolder.ijkPlayer);
            viewHolder.ijkPlayer.setVideoController(albumPlayController);
            viewHolder.ijkPlayer.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
            albumPlayController.setCurrentUrl( viewHolder.ijkPlayer.getmCurrentUrl());
        }
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mItemClickListener.onItemClick(viewHolder.itemView, i);
//            }
//        });
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ijk_sampleVideoctivity_item_ijkPlayer)
        IjkVideoView ijkPlayer;
        @BindView(R.id.iv_sampleVideoctivity_item_image)
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
