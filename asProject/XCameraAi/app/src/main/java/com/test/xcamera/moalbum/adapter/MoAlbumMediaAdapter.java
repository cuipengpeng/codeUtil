package com.test.xcamera.moalbum.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by zll on 2019/11/21.
 */

public class MoAlbumMediaAdapter extends RecyclerView.Adapter<MoAlbumMediaAdapter.ViewHolder>{

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail;
        private TextView videoTime;
        private ImageView markImage;
        private ImageView mediaType;
        private ImageView like;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
