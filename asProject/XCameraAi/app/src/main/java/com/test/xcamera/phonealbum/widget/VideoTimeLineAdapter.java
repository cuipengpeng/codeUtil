 package com.test.xcamera.phonealbum.widget;

 import android.content.Context;
 import android.support.v7.widget.RecyclerView;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.LinearLayout;
 import android.widget.TextView;


 import com.test.xcamera.R;
 import com.test.xcamera.phonealbum.bean.BaseThumbBean;
 import com.test.xcamera.phonealbum.bean.VideoTimeLineBean;
 import com.test.xcamera.util.MusicUtils;

 import java.util.ArrayList;
 import java.util.List;

 public class VideoTimeLineAdapter extends RecyclerView.Adapter<VideoTimeLineAdapter.ViewHolder> {

     public List<VideoTimeLineBean> mBeans = new ArrayList<>();
     private Context mContext;
     public VideoTimeLineAdapter(Context context) {
         mContext = context;
     }

     public void updateData(boolean refresh, List<VideoTimeLineBean> beans){
         if(refresh){
             mBeans.clear();
         }
         mBeans.addAll(beans);
         notifyDataSetChanged();
     }

     public List<BaseThumbBean> getmBeans() {
         List<BaseThumbBean> baseThumbBeans=new ArrayList<>();
         for (BaseThumbBean base: mBeans){
             baseThumbBeans.add(base);
         }
         return baseThumbBeans;
     }

     @Override
     public final ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
         View view = View.inflate(mContext, R.layout.item_my_video_edit_time, null);
         return new ViewHolder(view);
     }

     @Override
     public final void onBindViewHolder(final ViewHolder viewHolder, final int position) {
         VideoTimeLineBean bean=mBeans.get(viewHolder.getAdapterPosition());
         if(bean.isEmptyView()){
             LinearLayout.LayoutParams layoutParams  = (LinearLayout.LayoutParams) viewHolder.root.getLayoutParams();
              layoutParams.width = bean.getViewFrameWidth();
              viewHolder.root.setLayoutParams(layoutParams);
              viewHolder.textView.setVisibility(View.GONE);
              viewHolder.iv_image_time.setVisibility(View.GONE);
         }else {
             LinearLayout.LayoutParams layoutParams  = (LinearLayout.LayoutParams) viewHolder.root.getLayoutParams();
             layoutParams.width = bean.getViewFrameWidth();
             viewHolder.root.setLayoutParams(layoutParams);
             viewHolder.textView.setText(MusicUtils.formatTimeStrWithUs((long)bean.getTime()));
             viewHolder.textView.setVisibility(View.VISIBLE);
             viewHolder.iv_image_time.setVisibility(View.VISIBLE);

         }
     }

     @Override
     public int getItemCount() {
         return  mBeans.size();
     }

     class ViewHolder extends  RecyclerView.ViewHolder
      {  public LinearLayout root;
         public TextView textView;
         public View iv_image_time;

         public ViewHolder(View itemView) {
             super(itemView);
             root=itemView.findViewById(R.id.root_time_line);
             textView = itemView.findViewById(R.id.tv_time);
             iv_image_time = itemView.findViewById(R.id.iv_image_time);
         }
     }
 }
