package com.test.xcamera.phonealbum.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.editvideo.Constants;
import com.editvideo.timelineEditor.NvsTimelineEditor;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.MyVideoEditDetailActivity;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 2019/7/4.
 */

public class ThumbnailAdapter extends BaseRecyclerAdapter<ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc>, ThumbnailAdapter.ViewHolder> {

    public ThumbnailAdapter(Context context) {
        super(context);
    }

    public void updateData(boolean refresh, List<ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc>> mediaDataList){
        if(refresh){
            mData.clear();
        }
        mData.addAll(mediaDataList);
        notifyDataSetChanged();
    }

    @Override
    public final ThumbnailAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(mContext, R.layout.item_thumbnail, null);
        return new ViewHolder(view);
    }

    @Override
    public final void onBindViewHolder(final ThumbnailAdapter.ViewHolder viewHolder, final int position) {
                ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc> sequenceDataList = mData.get(position);
                viewHolder.mTimelineEditor.setPixelPerMicrosecond(((MyVideoEditDetailActivity)mContext).pixelMicrosecond);
                switch (position){
                    case 0:
                        viewHolder.mTimelineEditor.getMultiThumbnailSequenceView().setBackgroundColor(Color.RED);
                    break;
                    case 1:
                        viewHolder.mTimelineEditor.getMultiThumbnailSequenceView().setBackgroundColor(Color.GREEN);
                    break;
                    case 2:
                        viewHolder.mTimelineEditor.getMultiThumbnailSequenceView().setBackgroundColor(Color.GRAY);
                    break;
                    case 3:
                        viewHolder.mTimelineEditor.getMultiThumbnailSequenceView().setBackgroundColor(Color.BLUE);
                    break;
                    case 4:
                        viewHolder.mTimelineEditor.getMultiThumbnailSequenceView().setBackgroundColor(Color.YELLOW);
                    break;
                }
//        viewHolder.mTimelineEditor.initTimelineEditor(sequenceDataList, sequenceDataList.get(0).inPoint-sequenceDataList.get(0).outPoint);
        viewHolder.mTimelineEditor.initTimelineEditor(sequenceDataList, 4*Constants.NS_TIME_BASE);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(viewHolder.itemView, position);
            }
        });
    }
    class ViewHolder extends  RecyclerView.ViewHolder{

         @BindView(R.id.tle_myVideoEditDetailActivity_item_thumbnail)
         NvsTimelineEditor mTimelineEditor;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
