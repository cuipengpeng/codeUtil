package com.test.xcamera.phonealbum.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.editvideo.NvAsset;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.bean.AssetItem;
import com.test.xcamera.phonealbum.widget.OnItemClickListener;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.DisplayUtils;
import com.test.xcamera.utils.GlideUtils;
import com.test.xcamera.widget.RoundImageView;


import java.util.ArrayList;


public class EditCompoundCaptionAdapter extends RecyclerView.Adapter<EditCompoundCaptionAdapter.ViewHolder> {
    private ArrayList<AssetItem> mAssetList = new ArrayList<>( );
    private Context mContext;
    private OnItemClickListener mOnItemClickListener = null;
    private int mSelectedPos = -1;
    private String uuid="";
    public EditCompoundCaptionAdapter(Context context) {
        mContext = context;
    }

    public void setAssetList(ArrayList<AssetItem> assetArrayList,String uuid) {
        this.mAssetList = assetArrayList;
        this.uuid=uuid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.view_item_asset_compound_catpion_style, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void setSelectedPos(String uuid) {
        if("-1".equals(uuid)){
            mSelectedPos=-1;
        }
        this.uuid = uuid;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AssetItem assetItem = mAssetList.get(position);
        if (assetItem == null)
            return;
        NvAsset asset = assetItem.getAsset( );
        if (asset == null)
            return;
        int itemWidth = DisplayUtils.getwidth((Activity) mContext) / 4- DensityUtils.dp2px(mContext,14);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.itemViewRelativeLayout.getLayoutParams();
        layoutParams.width = itemWidth;
        layoutParams.height = itemWidth;
        holder.itemViewRelativeLayout.setLayoutParams(layoutParams);
        GlideUtils.GlideLoader(mContext, asset.coverUrl, holder.mCaptionAssetCover);


        holder.mSelecteItem.setVisibility(asset.uuid.equals(uuid) ? View.VISIBLE : View.GONE);

        holder.mCaptionStyleText.setTextColor(asset.uuid.equals(uuid)  ? Color.parseColor("#58A8EE") : Color.parseColor("#CCFFFFFF"));
        holder.mCaptionStyleText.setText(asset.name);

        holder.itemView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position,mSelectedPos != position);
                }
                int pos=position;
                if (mSelectedPos == position) {
                    pos=-1;
                }
                uuid=asset.uuid;
                mSelectedPos = pos;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAssetList.size( );
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundImageView mCaptionAssetCover;
        View mSelecteItem;
        TextView mCaptionStyleText;
        RelativeLayout itemViewRelativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mCaptionAssetCover =  itemView.findViewById(R.id.captionStyleAssetCover);
            mSelecteItem = itemView.findViewById(R.id.selectedItem);
            mCaptionStyleText = itemView.findViewById(R.id.captionStyleText);
            itemViewRelativeLayout = itemView.findViewById(R.id.rl_galleryItem_itemView);
        }
    }
}
