 package com.test.xcamera.phonealbum.adapter;

 import android.content.Context;
 import android.support.v7.widget.RecyclerView;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.ImageView;
 import android.widget.RelativeLayout;

 import com.bumptech.glide.Glide;
 import com.editvideo.MediaConstant;
 import com.test.xcamera.R;
 import com.test.xcamera.phonealbum.bean.VideoFrameBean;
 import com.test.xcamera.phonealbum.widget.VideoEditManger;
 import com.test.xcamera.utils.PUtil;
 import com.test.xcamera.utils.glide.VideoFrameUtils;

 import java.util.ArrayList;
 import java.util.List;

 /**
  * Created by DELL on 2019/7/4.
  */

 public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

     public List<Object> mData = new ArrayList<>();
     public List<VideoFrameBean> mBeans = new ArrayList<>();
     private Context mContext;
     private boolean emptyView ;
     private boolean mIsDragIng=false;
     private RelativeLayout.LayoutParams mLayoutParams;
     private int mWidth;

     public ImageAdapter(Context context, boolean emptyView) {
         mContext = context;
         this.emptyView = emptyView;
         mWidth= PUtil.dip2px(mContext, VideoEditManger.VIDEO_FRAME_WIDTH);
     }



     public void updateData(boolean refresh,boolean mIsDragIng, List<Object> mediaDataList, List<VideoFrameBean> beans){
         if(refresh){
             mData.clear();
             mBeans.clear();
         }
         this.mIsDragIng = mIsDragIng;
         mData.addAll(mediaDataList);
         mBeans.addAll(beans);
         notifyDataSetChanged();
     }

     @Override
     public final ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
         View view = View.inflate(mContext, R.layout.item_my_video_edit_thumbnail_subview, null);
         return new ViewHolder(view);
     }

     @Override
     public final void onBindViewHolder(final ViewHolder viewHolder, final int position) {
         if(emptyView ){
             mLayoutParams  = (RelativeLayout.LayoutParams) viewHolder.imageView.getLayoutParams();
             if(mIsDragIng){
                 mLayoutParams.width = mWidth;

             }else {
                 mLayoutParams.width =PUtil.getScreenW(mContext)/2;

             }
             viewHolder.imageView.setLayoutParams(mLayoutParams);
         } else {
             VideoFrameBean bean=mBeans.get(viewHolder.getAdapterPosition());
             mLayoutParams  = (RelativeLayout.LayoutParams) viewHolder.imageView.getLayoutParams();
             if(mIsDragIng){
                 mLayoutParams.width=mWidth;
             }else {
                 mLayoutParams.width=bean.getFrameWidth();
             }
             viewHolder.imageView.setLayoutParams(mLayoutParams);
             if(bean.getType()== MediaConstant.IMAGE){

                 Glide.with(mContext).load( bean.getMediaPath()).into(viewHolder.imageView);
             }else {
                 VideoFrameUtils.loadVideoThumbnail(mContext,bean.getMediaPath(),viewHolder.imageView,bean.getTime());
             }
             if(position==mData.size()-1){
                 viewHolder.imageView.setPadding(0,0,PUtil.dip2px(mContext,5),0);
             }else {
                 viewHolder.imageView.setPadding(0,0,0,0);

             }
         }


     }

     @Override
     public int getItemCount() {
         return emptyView ? 1 : mData.size();
     }

     class ViewHolder extends  RecyclerView.ViewHolder
      {
         public ImageView imageView;

         public ViewHolder(View itemView) {
             super(itemView);
             imageView = itemView.findViewById(R.id.iv_myVideoEditActivity_item_img);
         }
     }
 }
