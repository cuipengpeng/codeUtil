 package com.test.xcamera.phonealbum.adapter;

 import android.content.Context;
 import android.support.v7.widget.RecyclerView;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.ImageView;
 import android.widget.LinearLayout;
 import android.widget.TextView;

 import com.framwork.base.adapter.BaseRecyclerAdapter;
 import com.test.xcamera.R;
 import com.test.xcamera.phonealbum.bean.MusicBean;
 import com.test.xcamera.utils.DensityUtils;

 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 import butterknife.BindView;
 import butterknife.ButterKnife;

 /**
  * Created by DELL on 2019/7/4.
  */

 public class BeautyAdapter extends BaseRecyclerAdapter<MusicBean, BeautyAdapter.ViewHolder> {
    public Map<String, Float> beautyValueMap = new HashMap<>();

     public BeautyAdapter(Context context) {
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
     public final BeautyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
         View view = View.inflate(mContext, R.layout.my_video_edit_detail_item_beauty, null);
         return new ViewHolder(view);
     }

     @Override
     public final void onBindViewHolder(final BeautyAdapter.ViewHolder viewHolder, final int position) {
         viewHolder.tvRightTime.setText(mData.get(position).getName());
         if(mData.get(position).isSelected()){
             viewHolder.tvRightTime.setTextColor(mContext.getResources().getColor(R.color.appThemeColor));
             viewHolder.ivImage.setBackgroundResource(mData.get(position).getSelectedImgRes());
             viewHolder.viewBg.setVisibility(View.INVISIBLE);
         }else {
             viewHolder.tvRightTime.setTextColor(mContext.getResources().getColor(R.color.color_666666));
             viewHolder.ivImage.setBackgroundResource(mData.get(position).getImgRes());
             viewHolder.viewBg.setVisibility(View.INVISIBLE);

         }
         if(beautyValueMap.containsKey(mData.get(position).getBeautyKey())
                 && beautyValueMap.get(mData.get(position).getBeautyKey()) != mData.get(position).getDefaultValue()){
             viewHolder.changedPoint.setVisibility(View.VISIBLE);
         }else {
             viewHolder.changedPoint.setVisibility(View.GONE);
         }
         ViewGroup.LayoutParams layoutParams=viewHolder.rl_dragItem_itemView.getLayoutParams();
         layoutParams.width= DensityUtils.dp2px(mContext,80);
         viewHolder.rl_dragItem_itemView.setLayoutParams(layoutParams);
         viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mItemClickListener.onItemClick(viewHolder.itemView, position);
             }
         });

     }
     class ViewHolder extends  RecyclerView.ViewHolder
      {
         @BindView(R.id.rl_dragItem_itemView)
         LinearLayout rl_dragItem_itemView;
         @BindView(R.id.iv_myVideoEditDetailActivity_item_img)
         ImageView ivImage;
         @BindView(R.id.iv_myVideoEditDetailActivity_item_changedPoint)
         ImageView changedPoint;
         @BindView(R.id.viewBg)
         View viewBg;
         @BindView(R.id.tv_myVideoEditDetailActivity_item_name)
         TextView tvRightTime;

         public ViewHolder(View itemView) {
             super(itemView);
             ButterKnife.bind(this, itemView);
         }
     }
 }
