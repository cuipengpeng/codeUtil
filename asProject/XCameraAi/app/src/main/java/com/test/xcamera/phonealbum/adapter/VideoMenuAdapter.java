 package com.test.xcamera.phonealbum.adapter;

 import android.content.Context;
 import android.support.v7.widget.RecyclerView;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.ImageView;
 import android.widget.TextView;

 import com.framwork.base.adapter.BaseRecyclerAdapter;
 import com.test.xcamera.R;
 import com.test.xcamera.phonealbum.bean.MusicBean;

 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 import butterknife.BindView;
 import butterknife.ButterKnife;

 /**
  * Created by DELL on 2019/7/4.
  */

 public class VideoMenuAdapter extends BaseRecyclerAdapter<MusicBean, VideoMenuAdapter.ViewHolder> {
    public Map<String, Float> beautyValueMap = new HashMap<>();

     public VideoMenuAdapter(Context context) {
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
     public final VideoMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
         View view = View.inflate(mContext, R.layout.my_video_edit_detail_menu_item, null);
         return new ViewHolder(view);
     }

     @Override
     public final void onBindViewHolder(final VideoMenuAdapter.ViewHolder viewHolder, final int position) {
         viewHolder.tvRightTime.setText(mData.get(position).getName());
         if(mData.get(position).isSelected()){
             viewHolder.tvRightTime.setTextColor(mContext.getResources().getColor(R.color.appThemeColor));
             viewHolder.ivImage.setBackgroundResource(mData.get(position).getSelectedImgRes());
         }else {
             viewHolder.tvRightTime.setTextColor(mContext.getResources().getColor(R.color.color_666666));
             viewHolder.ivImage.setBackgroundResource(mData.get(position).getImgRes());

         }
         if(beautyValueMap.containsKey(mData.get(position).getBeautyKey())
                 && beautyValueMap.get(mData.get(position).getBeautyKey()) != mData.get(position).getDefaultValue()){
             viewHolder.changedPoint.setVisibility(View.VISIBLE);
         }else {
             viewHolder.changedPoint.setVisibility(View.GONE);
         }

         viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mItemClickListener.onItemClick(viewHolder.itemView, position);
             }
         });

     }
     class ViewHolder extends  RecyclerView.ViewHolder
      {
         @BindView(R.id.iv_myVideoEditDetailActivity_item_img)
         ImageView ivImage;
         @BindView(R.id.iv_myVideoEditDetailActivity_item_changedPoint)
         ImageView changedPoint;

         @BindView(R.id.tv_myVideoEditDetailActivity_item_name)
         TextView tvRightTime;

         public ViewHolder(View itemView) {
             super(itemView);
             ButterKnife.bind(this, itemView);
         }
     }
 }
