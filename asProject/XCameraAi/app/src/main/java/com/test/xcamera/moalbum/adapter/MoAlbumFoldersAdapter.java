package com.test.xcamera.moalbum.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.xcamera.R;
import com.test.xcamera.moalbum.bean.MoAlbumFolder;
import com.test.xcamera.moalbum.interfaces.MoAlbumFolderItemClickListener;
import com.test.xcamera.utils.CustomGlideUtils;
import com.test.xcamera.utils.GlideUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zll on 2019/11/21.
 */

public class MoAlbumFoldersAdapter extends RecyclerView.Adapter<MoAlbumFoldersAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MoAlbumFolder> mFoldersList;
    private MoAlbumFolderItemClickListener mItemClickListener;

    public MoAlbumFoldersAdapter(Context context) {
        mContext = context;
    }

    public void setData(ArrayList<MoAlbumFolder> classifies) {
        mFoldersList = classifies;
//        if (classifies.size() > 0) {
//            isLocalData(classifies);
//        }
        notifyDataSetChanged();
    }

    private void isLocalData(ArrayList<MoAlbumFolder> classifies) {
        ArrayList<MoAlbumFolder> temp = new ArrayList<>();
        for (MoAlbumFolder moAlbumFolder : classifies) {
            String fileThumbnailUri = moAlbumFolder.getThumbnailUri();
            if (fileThumbnailUri != null && !fileThumbnailUri.startsWith("mox")) {
                File file = new File(fileThumbnailUri);
                if (file != null && file.exists()) {
                    temp.add(moAlbumFolder);
                }
            } else {
                temp.add(moAlbumFolder);
            }
        }
        mFoldersList = temp;
    }

    public void setOnItemClickListener(MoAlbumFolderItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mo_album_classify_adapter_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MoAlbumFolder folder = mFoldersList.get(position);
        viewHolder.name.setText(folder.getName());
        viewHolder.count.setText(folder.getCount() + "");
        if (folder.isCameraAlbum() && !TextUtils.isEmpty(folder.getThumbnailUri())) {
            CustomGlideUtils.loadAlbumPhotoThumbnail(mContext,folder.getThumbnailUri(),viewHolder.thumbnail,0L);
        } else {
            Glide.with(mContext).load(folder.getThumbnailUri()).apply(GlideUtils.options).thumbnail(0.5f)
                    .into(viewHolder.thumbnail);
//            GlideUtils.GlideLoader(mContext, folder.getThumbnailUri(), viewHolder.thumbnail);
        }
        if (folder.isChecked()) {
            viewHolder.mParentView.setSelected(true);
        } else {
            viewHolder.mParentView.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        if (mFoldersList != null && mFoldersList.size() > 0) {
            return mFoldersList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView count;
        private ImageView thumbnail;
        private View mParentView;

        public ViewHolder(View itemView) {
            super(itemView);

            mParentView = itemView.findViewById(R.id.mo_album_classify_adapter_layout);
            name = itemView.findViewById(R.id.mo_album_classify_adapter_layout_name);
            count = itemView.findViewById(R.id.mo_album_classify_adapter_layout_count);
            thumbnail = itemView.findViewById(R.id.mo_album_classify_adapter_layout_thumbnail);

            if (mItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getAdapterPosition() < 0) {
                            return;
                        }
                        mItemClickListener.onFoldersItemClickListener(getAdapterPosition(),
                                mFoldersList.get(getAdapterPosition()));
                    }
                });
            }
        }
    }
}
