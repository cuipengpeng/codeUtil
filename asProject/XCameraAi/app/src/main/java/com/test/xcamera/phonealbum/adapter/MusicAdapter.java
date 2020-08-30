 package com.test.xcamera.phonealbum.adapter;

 import android.content.Context;
 import android.support.v7.widget.RecyclerView;
 import android.text.TextUtils;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.ImageView;
 import android.widget.RelativeLayout;
 import android.widget.TextView;

 import com.framwork.base.adapter.BaseRecyclerAdapter;
 import com.test.xcamera.R;
 import com.test.xcamera.phonealbum.bean.MusicBean;
 import com.test.xcamera.utils.GlideUtils;

 import java.util.List;

 import butterknife.BindView;
 import butterknife.ButterKnife;

 /**
  * Created by DELL on 2019/7/4.
  */

 public class MusicAdapter extends BaseRecyclerAdapter<MusicBean, MusicAdapter.ViewHolder> {

     public MusicAdapter(Context context) {
         super(context);
     }

     public void updateData(boolean refresh, List<MusicBean> mediaDataList){
         if(refresh){
             mData.clear();
         }
         mData.addAll(mediaDataList);
         notifyDataSetChanged();
     }

     @Override
     public final MusicAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
         View view = View.inflate(mContext, R.layout.item_my_video_edit_music, null);
         return new ViewHolder(view);
     }

     @Override
     public final void onBindViewHolder(final MusicAdapter.ViewHolder viewHolder, final int position) {
         if(mData.get(position).isSelected()){
             viewHolder.tvRightTime.setTextColor(mContext.getResources().getColor(R.color.appThemeColor));
             viewHolder.ivImage.setBackgroundResource(R.drawable.bg_circle_orange);
         }else {
             viewHolder.tvRightTime.setTextColor(mContext.getResources().getColor(R.color.color_666666));
             viewHolder.ivImage.setBackgroundResource(R.drawable.bg_circle_gray);

//             viewHolder.ivImage.setBorderColor(mContext.getResources().getColor(R.color.selectBottomMenuTabColor));
         }
         String musicName = mData.get(position).getName();
         viewHolder.tvRightTime.setText(musicName);
         if(TextUtils.isEmpty(mData.get(viewHolder.getAdapterPosition()).getCoverFileURL())){
             GlideUtils.GlideCircle(mContext,  mData.get(position).getImgRes(), viewHolder.ivImage);
         }else {
             String url= mData.get(viewHolder.getAdapterPosition()).getCoverFileURL();
             GlideUtils.GlideCircle(mContext, url, viewHolder.ivImage);
         }

         viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mItemClickListener.onItemClick(viewHolder.itemView, position);
             }
         });

     }
     public void selectPosition(int pos){
         for (int i = 0; i < mData.size(); i++) {
             mData.get(i).setSelected(false);
         }
         mData.get(pos).setSelected(true);
         notifyDataSetChanged();
     }
     class ViewHolder extends  RecyclerView.ViewHolder
      {
         @BindView(R.id.iv_myVideoEditActivity_item_img)
         ImageView ivImage;
         @BindView(R.id.tv_myVideoEditActivity_item_name)
         TextView tvRightTime;
         @BindView(R.id.rl_albumActivity_dragItem_itemView)
         RelativeLayout itemViewRelativeLayout;

         public ViewHolder(View itemView) {
             super(itemView);
             ButterKnife.bind(this, itemView);
         }
     }
 }
