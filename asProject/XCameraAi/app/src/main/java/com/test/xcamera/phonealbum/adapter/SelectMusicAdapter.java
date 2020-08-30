package com.test.xcamera.phonealbum.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.editvideo.ToastUtil;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.download.DownloadListener;
import com.test.xcamera.download.DownloadUtil;
import com.test.xcamera.download.OnDownloadCallback;
import com.test.xcamera.phonealbum.MyVideoEditActivity;
import com.test.xcamera.phonealbum.SelectMusicActivity;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.util.MediaPlayerUtil;
import com.test.xcamera.util.MusicUtils;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DownLoadRequest;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.GlideUtils;
import com.test.xcamera.utils.Md5Util;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.utils.StorageUtils;
import com.test.xcamera.view.DownMusicProgressDialog;
import com.test.xcamera.widget.NiceImageView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectMusicAdapter extends BaseRecyclerAdapter<MusicBean, SelectMusicAdapter.ViewHolder> {
    private DownMusicProgressDialog mDownProgressDialog;
    private Animation mRotateAnimation;
    private float mRotation = 0;

    public SelectMusicAdapter(Context context, float rotation) {
        super(context);
        mRotation = rotation;
        mRotateAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_anim);
        mDownProgressDialog = new DownMusicProgressDialog(new WeakReference<>(mContext).get());
        mDownProgressDialog.setDialogRotation(rotation);
    }

    public List<MusicBean> getDataList() {
        return mData;
    }

    public void upateData(boolean isRefresh, List<MusicBean> data) {
        if (this.mData == null || data == null) {
            return;
        }
        if (isRefresh) {
            this.mData.clear();
        }
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    private boolean mDisconnectedUSBStatus = true;

    public void setDisconnectedUSBStatus(boolean mDisconnectedUSBStatus) {
        this.mDisconnectedUSBStatus = mDisconnectedUSBStatus;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.item_select_music, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String musicName = mData.get(position).getName();
        holder.musicNameTextView.setText(musicName);
//        String url=Constants.getFileIdToUrl( mData.get(holder.getAdapterPosition()).getCoverFileId());
        GlideUtils.GlideRoundedCornerHeader(mContext, mData.get(holder.getAdapterPosition()).getCoverFileURL(), holder.imgImageView);
        holder.authorTextView.setText(mData.get(position).getSinger());
        holder.duritionTextView.setText(MusicUtils.formatTime(mData.get(position).getDuration() * 1000));
        /**判断文件是否现在*/
        setImageButton(holder, mData.get(position).isSelected());
        holder.btnMusicConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicBean musicBean = mData.get(holder.getAdapterPosition());
                String defPath = isMusicDownLoad(holder.getAdapterPosition(), Md5Util.getMD5(mData.get(holder.getAdapterPosition()).getMusicFileURL()));
                if (TextUtils.isEmpty(defPath)) {
                    downMusic(holder.getAdapterPosition(), musicBean);
                } else {
                    DownloadUtil.getDownLoadLength(mData.get(holder.getAdapterPosition()).getMusicFileURL(), new OnDownloadCallback() {
                        @Override
                        public void onDownLoadLength(long length) {
                            if (musicBean == null) {
                                return;
                            }
                            File file = new File(defPath);
                            Log.i("club:","download_music:file.length():"+file.length()+" down_length:"+length);
                            if (file.length() == length) {
                                musicBean.setPath(defPath);
                                backResult(musicBean);
                            } else {
                                downMusic(holder.getAdapterPosition(), musicBean);
                            }

                        }

                        @Override
                        public void onFail(String msg) {
                            if (mRotation == 0) {
                                ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.video_edit_add_music_download_error));
                            } else {
                                CameraToastUtil.show180(mContext.getResources().getString(R.string.video_edit_add_music_download_error), mContext);
                            }
                        }
                    });


                }
            }
        });
        holder.btnMusicDownkoad.setOnClickListener(view -> {

        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurPosition = holder.getAdapterPosition();
                if (mCurPosition < 0) {
                    return;
                }
                String path = isMusicDownLoad(mCurPosition, mData.get(mCurPosition).getMusicFileURL());
                if (!TextUtils.isEmpty(path)) {
                    mData.get(holder.getAdapterPosition()).setPath(path);
                    mData.get(holder.getAdapterPosition()).setIsLocal(true);
                    mItemClickListener.onItemClick(holder.itemView, mCurPosition);
                } else {
                    MediaPlayerUtil.mIsLoading = true;
                    mData.get(mCurPosition).setIsLocal(false);
                    mData.get(mCurPosition).setPath(mData.get(mCurPosition).getMusicFileURL());
                    mItemClickListener.onItemClick(holder.itemView, mCurPosition);
                }
            }
        });

    }

    public void downMusic(int position, MusicBean musicBean) {
        if (StorageUtils.getAvailableExternalMemorySize() < (200 * 1024 * 1024)) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.stor_no));
            return;
        }
        if (NetworkUtil.isNetworkConnected(mContext)) {
            String path = FileUtils.getMusicSelectPath(Md5Util.getMD5(mData.get(position).getMusicFileURL()), mContext).getAbsolutePath();
            mCurPosition = position;

            if (mDownProgressDialog != null) {
                mDownProgressDialog.refreshProgress(0, 0);
            }
            DownloadUtil.downLoad(mData.get(position).getMusicFileURL(), path, mDownloadListener);
            if (mDownProgressDialog != null) {
                mDownProgressDialog.showDialog(100);
                mDownProgressDialog.setCancelable(false);
            }
            musicBean.setPath(path);
        } else {
            if (mRotation == 0) {
                ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.video_edit_add_music_download_error));
            } else {
                CameraToastUtil.show180(mContext.getResources().getString(R.string.video_edit_add_music_download_error), mContext);
            }
        }
    }

    public void setSelectPosition(int position) {
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setSelected(false);
        }
        if (position != -1) {
            mData.get(position).setSelected(true);
        }
        notifyDataSetChanged();
    }

    public void setNotifyItemChanged() {
        if (mCurPosition == -1) {
            return;
        }
        notifyItemChanged(mCurPosition);
    }

    public void setNotifyItemPlayError() {
        if (mCurPosition == -1) {
            return;
        }
        mData.get(mCurPosition).setSelected(false);
        notifyItemChanged(mCurPosition);
        mCurPosition = -1;
    }

    public void backResult(MusicBean musicBean) {
        if (mDownProgressDialog != null) {
            mDownProgressDialog.destroy();
        }
        Intent intent = new Intent();
        intent.putExtra(SelectMusicActivity.KEY_OF_MUSIC_RESULT, musicBean);
        intent.putExtra("DisconnectedUSBStatus", mDisconnectedUSBStatus);
        ((FragmentActivity) mContext).setResult(MyVideoEditActivity.RESULT_OK_IN_MUSIC, intent);
        ((FragmentActivity) mContext).finish();
    }

    private void setImageButton(ViewHolder holder, boolean isSelect) {
        if (isSelect) {
            if (MediaPlayerUtil.mIsLoading) {
                holder.playStateImageView.setImageResource(R.mipmap.icon_login_loading);
                holder.playStateImageView.startAnimation(mRotateAnimation);
            } else {
                holder.playStateImageView.clearAnimation();
                holder.playStateImageView.setImageResource(R.mipmap.play);
            }
            holder.btnMusicConfirm.setVisibility(View.VISIBLE);

        } else {
            holder.btnMusicConfirm.setVisibility(View.INVISIBLE);
            holder.playStateImageView.setImageResource(R.mipmap.pause);
        }
    }

    private String isMusicDownLoad(int position, String filedId) {
        File file = FileUtils.getMusicSelectPath(Md5Util.getMD5(mData.get(position).getMusicFileURL()), mContext);
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            return "";
        }
    }

    private DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onStart() {
            if (mDownProgressDialog != null) {
                mDownProgressDialog.refreshProgress(0, 0);
            }
        }

        @Override
        public void onProgress(int progress) {
            if (mContext != null) {
                Message msg = Message.obtain();
                msg.what = DownLoadRequest.DOWN_LOADFILE;
                msg.obj = progress;
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onFinish(File path) {
            if (mCurPosition != -1 && mData != null) {
                if (mData.get(mCurPosition).getMusicFileURL().equals(path.getName())) {
                    mData.get(mCurPosition).setPath(path.getAbsolutePath());
                }
            }
            if (mContext != null) {
                handler.sendEmptyMessage(10001);
            }
        }

        @Override
        public void onFail(String errorInfo) {
            if (mContext != null) {
                handler.sendEmptyMessage(10002);
            }
        }
    };
    private int mCurPosition = -1;
    private android.os.Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DownLoadRequest.DOWN_LOADFILE:
                    int progress = (int) msg.obj;
                    if (mDownProgressDialog != null) {
                        mDownProgressDialog.refreshProgress(progress, progress);
                    }
                    break;
                case 10001:
                    mDownProgressDialog.refreshProgress(100, 100);
                    mDownProgressDialog.dismissDialog();
                    handler.sendEmptyMessageDelayed(10003, 200);
                    break;
                case 10002:
                    if (mContext != null) {
                        if (mDownProgressDialog != null) {
                            mDownProgressDialog.dismissDialog();
                        }
                        if (mRotation == 0) {
                            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.video_edit_add_music_download_error));
                        } else {
                            CameraToastUtil.show180(mContext.getResources().getString(R.string.video_edit_add_music_download_error), mContext);
                        }
                    }
                    break;
                case 10003:
                    notifyDataSetChanged();
                    MusicBean musicBean = mData.get(mCurPosition);
                    backResult(musicBean);
                    break;
            }
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_selectMusic_item_musicState)
        ImageView playStateImageView;
        @BindView(R.id.iv_selectMusic_item_img)
        NiceImageView imgImageView;
        @BindView(R.id.tv_selectMusic_item_name)
        TextView musicNameTextView;
        @BindView(R.id.tv_selectMusic_item_author)
        TextView authorTextView;
        @BindView(R.id.tv_selectMusic_item_time)
        TextView duritionTextView;
        @BindView(R.id.btn_music_confirm)
        Button btnMusicConfirm;
        @BindView(R.id.btn_music_download)
        ImageView btnMusicDownkoad;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
