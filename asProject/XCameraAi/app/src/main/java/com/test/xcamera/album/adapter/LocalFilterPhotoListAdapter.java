package com.test.xcamera.album.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.album.MomediaWrraper;
import com.test.xcamera.album.sectionrec.SectionedRecyclerViewAdapter;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.utils.GlideUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 相机照片列表页面
 */
public class LocalFilterPhotoListAdapter extends SectionedRecyclerViewAdapter<LocalFilterPhotoListAdapter.CountHeaderViewHolder,
        LocalFilterPhotoListAdapter.CountItemViewHolder,
        LocalFilterPhotoListAdapter.CountFooterViewHolder> {

    private static final String TAG = "CameraPhotoListAdapter";

    private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

    private Context context;
    private SelectedPhotoCountCallback selectedPhotoCountCallback;
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
    public interface SelectedPhotoCountCallback {
        void selectedPhotoCount(int selectedPhotoCount);
    }


    public LocalFilterPhotoListAdapter(Context context, MomediaWrraper listList, SelectedPhotoCountCallback selectedPhotoCountCallback) {
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
            selectedPhotoCountCallback.selectedPhotoCount(selectedPhotoList.size());
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
    }

    @Override
    protected CountItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new CountItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.local_filter_item_vertical_view, parent, false));
    }

    @Override
    protected void onBindSectionHeaderViewHolder(CountHeaderViewHolder holder, int section) {
        if (adapterDataList.getSection(section).size() > 0) {
            String date = this.format.format(new Date(adapterDataList.getSection(section).get(0).getmCreateTime()));
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
        final MoAlbumItem photo = adapterDataList.getSection(section).get(position);
        String thumbnailFileUri = photo.getmThumbnail().getmUri();
        if (thumbnailFileUri == null)
            return;
        GlideUtils.GlideLoader(context, thumbnailFileUri, holder.itemBg);
        if (isSelectedStatus && !photo.isChecked) {
            holder.defaultSelecte.setVisibility(View.VISIBLE);
        } else {
            holder.defaultSelecte.setVisibility(View.GONE);
        }


        holder.alreadySelecte.setVisibility(View.GONE);
        holder.videoTime.setVisibility(View.GONE);
        if (photo.isVideo()) {
            holder.videoTime.setVisibility(View.VISIBLE);
            holder.videoTime.setText(parseTime(photo.getmVideo().getmDuration()));
        }

        if (isSelectedStatus && selectedPhotoList.contains(photo)) {
            holder.alreadySelecte.setVisibility(View.VISIBLE);
            holder.chooseCover.setVisibility(View.VISIBLE);
        } else {
            holder.alreadySelecte.setVisibility(View.GONE);
            holder.chooseCover.setVisibility(View.GONE);
        }

        //刷新的时候保留之前已有状态,是否是被选择的
        if (photo.isChecked) {
            holder.alreadySelecte.setVisibility(View.VISIBLE);
            holder.chooseCover.setVisibility(View.VISIBLE);
        } else {
            holder.alreadySelecte.setVisibility(View.GONE);
            holder.chooseCover.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(photo.getmThumbnail().getmUri())) {
                    return;
                }
                if (isSelectedStatus) {
                    holder.defaultSelecte.setVisibility(!photo.isChecked ? View.GONE : View.VISIBLE);
                    holder.chooseCover.setVisibility(!photo.isChecked ? View.VISIBLE : View.GONE);
                    holder.alreadySelecte.setVisibility(!photo.isChecked ? View.VISIBLE : View.GONE);

                    if (!photo.isChecked) {
                        photo.isChecked = true;
                        photo.setCollect(1);
                        selectedPhotoList.add(photo);
                    } else {
                        selectedPhotoList.remove(photo);
                        //每一次刷新回来的时候对象都是不一样的,所以不能根据对象进行比较,而是要根据tmThumbnailFileUri进行比较
//                        String uriS = photo.getmThumbnail().getmUri();
//                        for (int i = 0; i < selectedPhotoList.size(); i++) {
//                            String mUri = selectedPhotoList.get(i).getmThumbnail().getmUri();
//                            if (mUri.equals(uriS)) {
//                                selectedPhotoList.remove(selectedPhotoList.get(i));
//                            }
//                        }

                        photo.isChecked = false;
                    }
                    if (selectedPhotoCountCallback != null) {
                        selectedPhotoCountCallback.selectedPhotoCount(selectedPhotoList.size());
                    }
                } else {
                    Message message = new Message();
                    message.what = 0;
                    message.obj = holder.itemView;
                    handler.sendMessageDelayed(message, 1000);
                    holder.itemView.setEnabled(false);
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(adapterDataList.getListUIitemBeans(),
                                adapterDataList.getListUIitemBeans().indexOf(photo));
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

    private String parseTime(long time) {

        time = time / 1000;
        int hour = (int) (time / 3600);
        int min = (int) ((time / 60) % 60);
        int sec = (int) (time % 60);

        DecimalFormat df = new DecimalFormat("00");
        String value = String.format("%s:%s:%s", df.format(hour), df.format(min), df.format(sec));
        return value;
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
        View chooseCover;
        RelativeLayout itemRootLayout;
        ImageView itemBg;
        TextView videoTime;
        ImageView alreadySelecte;
        ImageView defaultSelecte;
        View itemView;

        public CountItemViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            itemRootLayout = itemView.findViewById(R.id.itemRootLayout);
            itemBg = itemView.findViewById(R.id.itemBg);
            videoTime = itemView.findViewById(R.id.videoTime);
            alreadySelecte = itemView.findViewById(R.id.alreadySelecte);
            defaultSelecte = itemView.findViewById(R.id.defaultSelecte);
            chooseCover = itemView.findViewById(R.id.choose_cover);

        }
    }

    public static class CountFooterViewHolder extends RecyclerView.ViewHolder {

        public CountFooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
