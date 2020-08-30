package com.test.xcamera.album.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.editvideo.TimeUtil;
import com.test.xcamera.R;
import com.test.xcamera.album.MomediaWrraper;
import com.test.xcamera.album.sectionrec.SectionedRecyclerViewAdapter;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoVideo;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.CustomGlideUtils;
import com.test.xcamera.utils.GlideUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 相机照片列表页面
 */
public class CameraPhotoListAdapter extends SectionedRecyclerViewAdapter<CameraPhotoListAdapter.CountHeaderViewHolder,
        CameraPhotoListAdapter.CountItemViewHolder,
        CameraPhotoListAdapter.CountFooterViewHolder> {

    private final String TAG = "CameraPhotoListAdapter";

    private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");


    private Context context;
    private MyAlbumAdapterCallback selectedPhotoCountCallback;
    private OnItemClickListener onItemClickListener;

    private MomediaWrraper adapterDataList;
    //被选择的照片放在该集合中
    private ArrayList<MoAlbumItem> selectedPhotoList = new ArrayList<>();

    /**
     * 清空已经选择的照片
     */
    public void clearSelectedPhotoList() {
        if (selectedPhotoList.size() > 0) {
            selectedPhotoList.clear();
            if (selectedPhotoCountCallback != null) {
                selectedPhotoCountCallback.clearAlbume();
            }
        }
    }

    public boolean isSelectedStatus() {
        return isSelectedStatus;
    }

    public void destory() {
        selectedPhotoCountCallback = null;
        context = null;
        onItemClickListener = null;
        if (adapterDataList != null) {
            adapterDataList.clear();
            adapterDataList = null;
        }
        if (selectedPhotoList != null) {
            selectedPhotoList.clear();
            selectedPhotoList = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    /**
     * 该页面是不是选择的状态
     */
    private boolean isSelectedStatus;
    //防止在 相册页面快速点击照片,会打开两次activity
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            View view = (View) msg.obj;
            if (view == null)
                return;
            view.setEnabled(true);
        }
    };

    public interface OnItemClickListener {
        void onItemClick(ArrayList<MoAlbumItem> list, int position);
    }

    /**
     * 选择照片时候的回调
     */
    public interface MyAlbumAdapterCallback {
        void selectedAlbumCount(int selectedPhotoCount);

        void clearAlbume();
    }


    public CameraPhotoListAdapter(Context context,
                                  MomediaWrraper listList, MyAlbumAdapterCallback
                                          selectedPhotoCountCallback) {
        this.context = context;
        this.adapterDataList = listList;
        this.selectedPhotoCountCallback = selectedPhotoCountCallback;
    }

    /**
     * 全选
     */
    public void selectedAllPhoto() {
        selectedPhotoList.clear();
        selectedPhotoList.addAll(adapterDataList.getListUIitemBeans());
        notifyDataSetChanged();
        if (selectedPhotoCountCallback != null) {
            selectedPhotoCountCallback.selectedAlbumCount(selectedPhotoList.size());
        }
    }

    public MomediaWrraper getMomediaWrraper() {
        return adapterDataList;
    }

    public void removeChoosed() {
        adapterDataList.getListUIitemBeans().removeAll(selectedPhotoList);
    }

    public ArrayList<MoAlbumItem> getSelectedPhotoList() {
        return selectedPhotoList;
    }

    public int getSelecetPhotoCount() {
        return selectedPhotoList != null ? selectedPhotoList.size() : 0;
    }

    @Override
    protected int getSectionCount() {
        return adapterDataList.sectionCount();
    }

    private void isLocalData(ArrayList<MoAlbumItem> listUIitemBeans) {
        ArrayList<MoAlbumItem> temp = new ArrayList<>();
        for (MoAlbumItem moAlbumItem : listUIitemBeans) {
            String s = moAlbumItem.getmThumbnail().getmUri();
            File file = new File(s);
            if (!file.exists()) {
            } else {
                temp.add(moAlbumItem);
            }
        }
        adapterDataList.clear();
        adapterDataList.addAll(temp);
    }

    @Override
    protected int getItemCountForSection(int section) {

        return adapterDataList.itemCountInsection(section);
    }

    @Override
    protected boolean hasFooterInSection(int section) {
        return false;
    }

    @Override
    protected CountHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        return new CountHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_view, parent, false));
    }

    //这个是添加叫布局的地方
    @Override
    protected CountFooterViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        return new CountFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_footer, parent, false));
//        return null;
    }

    @Override
    protected CountItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new CountItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertical_view, parent, false));
    }

    @Override
    protected void onBindSectionHeaderViewHolder(CountHeaderViewHolder holder, int section) {
        if (adapterDataList.getSection(section).size() > 0) {
            long time = adapterDataList.getSection(section).get(0).getmCreateTime();
            int length = String.valueOf(time).length();
            if (length == 10) {
                time = time * 1000;
            }
            String date = this.format.format(new Date(time));
            long l = System.currentTimeMillis();
            String today = this.format.format(new Date(l));
            String yesterday = this.format.format(new Date(l - 24 * 3600000));
            if (date.equals(today)) {
                holder.tvDate.setText(context.getResources().getString(R.string.today));
            } else if (date.equals(yesterday)) {
                holder.tvDate.setText("昨天");
            } else {
                holder.tvDate.setText(date);
            }
        }
    }

    @Override
    protected void onBindSectionFooterViewHolder(CountFooterViewHolder holder, int section) {

    }

    @Override
    protected void onBindItemViewHolder(final CountItemViewHolder holder, final int section, final int position) {

        //init view status
        holder.likeIcon.setVisibility(View.GONE);
        holder.markIcon.setVisibility(View.GONE);
        holder.alreadySelecte.setVisibility(View.GONE);
        holder.videoTime.setVisibility(View.GONE);

        final MoAlbumItem albumItem = adapterDataList.getSection(section).get(position);
        String thumbnailFileUri = albumItem.getmThumbnail().getmUri();
        if (thumbnailFileUri == null)
            return;
        if (albumItem.ismIsCamrea()) {
            int rotate = albumItem.getRotate();
            float angle = 0;
            if (rotate == 0) {
                angle = 0;
            } else if (rotate == 1) {
                angle = -90;
            } else if (rotate == 2) {
                angle = 180;
            } else if (rotate == 3) {
                angle = 90;
            }
            holder.itemBg.setRotation(angle);
            CustomGlideUtils.loadAlbumPhotoThumbnail(context, thumbnailFileUri, holder.itemBg, 0L);
        } else {
            if (albumItem.isVideo()) {
                Glide.with(context).load(Uri.fromFile(new File(albumItem.getmThumbnail().getmUri())))
                        .apply(GlideUtils.options).into(holder.itemBg);
            } else {
                Glide.with(context).load(albumItem.getmThumbnail().getmUri()).apply(GlideUtils.options)
                        .thumbnail(0.5f)
                        .into(holder.itemBg);
            }
        }

        if (albumItem.ismIsCamrea()) {
            if (albumItem.getCollect() == 1) {
                holder.likeIcon.setVisibility(View.VISIBLE);
                holder.likeIcon.setImageResource(R.mipmap.alreadylike);
            }
        } else {
            holder.likeIcon.setVisibility(View.GONE);
        }

        if (albumItem.isVideo()) {
            if (albumItem.ismIsCamrea()) {
                if (albumItem.getmVideo().getMarkList() != null &&
                        albumItem.getmVideo().getMarkList().length > 0) {
                    holder.markIcon.setVisibility(View.VISIBLE);
                }
            } else {
                holder.markIcon.setVisibility(View.GONE);
            }

            holder.videoTime.setVisibility(View.VISIBLE);
            String time = TimeUtil.secToTime((int) (albumItem.getmVideo().getmDuration() / 1000) < 1 ? 1 : (int) (albumItem.getmVideo().getmDuration() * 1.0f / 1000 + 0.5));
            Log.i(TAG, "onBindItemViewHolder: " + albumItem.getmVideo().getmDuration());
            holder.videoTime.setText(time);
        }

        if (isSelectedStatus && selectedPhotoList.contains(albumItem)) {
            holder.alreadySelecte.setVisibility(View.VISIBLE);
        } else {
            holder.alreadySelecte.setVisibility(View.GONE);
        }

        //刷新的时候保留之前已有状态,是否是被选择的
        if (albumItem.isChecked) {
            holder.alreadySelecte.setVisibility(View.VISIBLE);
        } else {
            holder.alreadySelecte.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(albumItem.getmThumbnail().getmUri())) {
                    return;
                }
                if (isSelectedStatus) {
                    holder.alreadySelecte.setVisibility(!albumItem.isChecked ? View.VISIBLE : View.GONE);

                    if (!albumItem.isChecked) {
                        albumItem.isChecked = true;
                        selectedPhotoList.add(albumItem);
                    } else {
                        selectedPhotoList.remove(albumItem);
                        albumItem.isChecked = false;
                    }
                    if (selectedPhotoCountCallback != null) {
                        selectedPhotoCountCallback.selectedAlbumCount(selectedPhotoList.size());
                    }
                } else {
                    Message message = new Message();
                    message.what = 0;
                    message.obj = holder.itemView;
                    handler.sendMessageDelayed(message, 1000);
                    holder.itemView.setEnabled(false);

                    if (!albumItem.isImage() && albumItem.ismIsCamrea()) {
                        MoImage moImage = albumItem.getmThumbnail();
                        if (moImage.getmSize() == 0 || moImage.getmWidth() == 0 || moImage.getmHeight() == 0) {
                            CameraToastUtil.show(context.getResources().getString(R.string.thumbnail_error), context);
                            return;
                        } else {
                            MoVideo moVideo = albumItem.getmVideo();
                            if (moVideo != null && (moVideo.getmDuration() == 0
                                    || moVideo.getmWidth() == 0 || moVideo.getmHeight() == 0)) {
                                CameraToastUtil.show(context.getResources().getString(R.string.video_error), context);
                                return;
                            }
                        }
                    }

                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(adapterDataList.getListUIitemBeans(),
                                adapterDataList.getListUIitemBeans().indexOf(albumItem));
                    }
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public void setSelectedStatus(boolean selectedStatus) {
        this.isSelectedStatus = selectedStatus;
    }

    public static class CountHeaderViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout llHead;
        TextView tvDate;
        TextView tvSize;
        TextView tvChoose;

        public CountHeaderViewHolder(View convertView) {
            super(convertView);
            llHead = convertView.findViewById(R.id.ll_head);
            tvDate = convertView.findViewById(R.id.tv_date);
            tvSize = convertView.findViewById(R.id.tv_size);
            tvChoose = convertView.findViewById(R.id.tv_choose);

        }
    }

    public static class CountItemViewHolder extends RecyclerView.ViewHolder {
        // View chooseCover;
        RelativeLayout itemRootLayout;
        ImageView itemBg;
        TextView videoTime;
        ImageView alreadySelecte;
        // ImageView defaultSelecte;
        View itemView;
        ImageView likeIcon;
        ImageView markIcon;

        public CountItemViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            itemRootLayout = itemView.findViewById(R.id.itemRootLayout);
            itemBg = itemView.findViewById(R.id.itemBg);
            videoTime = itemView.findViewById(R.id.videoTime);
            alreadySelecte = itemView.findViewById(R.id.alreadySelecte);
            // defaultSelecte = itemView.findViewById(R.id.defaultSelecte);
            //chooseCover = itemView.findViewById(R.id.choose_cover);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            markIcon = itemView.findViewById(R.id.markIcon);

        }
    }

    public static class CountFooterViewHolder extends RecyclerView.ViewHolder {

        public CountFooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
