package com.caishi.chaoge.ui.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.caishi.chaoge.utils.FFmpegCmd;
import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.EventBusBean.EventMusicBean;
import com.caishi.chaoge.bean.MusicBean;
import com.caishi.chaoge.ui.activity.SelectMusicActivity;
import com.caishi.chaoge.ui.widget.DoubleSlideSeekBar;
import com.caishi.chaoge.ui.widget.WeakHandler;
import com.caishi.chaoge.utils.ConstantUtils;
import com.caishi.chaoge.utils.DownloadUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.MediaPlayerUtil;
import com.caishi.chaoge.utils.StringUtil;
import com.caishi.chaoge.utils.Utils;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.othershe.library.NiceImageView;
import com.xiao.nicevideoplayer.NiceVideoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectMusicAdapter extends RecyclerView.Adapter<SelectMusicAdapter.ViewHolder> {
    public static final String FILE_PROTOCAL = "file://";
    FragmentActivity mContext;
    public List<MusicBean> mDataList = new ArrayList<>();
    int lastClickPosstion = -1;
    boolean hasPermission = false;
    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;

    public SelectMusicAdapter(FragmentActivity context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<MusicBean> data) {
        if (isRefresh) {
            this.mDataList.clear();
            lastClickPosstion = -1;
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music_select, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Glide.with(mContext).load(Utils.isUrl(mDataList.get(position).imageUrl)).into(holder.ivSelectMusicItemImg);
//        GlideUtil.loadRoundImg(Utils.isUrl(mDataList.get(position).imageUrl), holder.ivSelectMusicItemImg);
        String musicName = "";
        if (StringUtil.notEmpty(mDataList.get(position).title)) {
            musicName = mDataList.get(position).title;
            if (StringUtil.notEmpty(mDataList.get(position).author)) {
                musicName = mDataList.get(position).title + " - " + mDataList.get(position).author;
            }
        } else if (StringUtil.notEmpty(mDataList.get(position).author)) {
            musicName = mDataList.get(position).author;
        }
        holder.tvSelectMusicItemName.setText(musicName);
        holder.tvSelectMusicItemTime.setText(Utils.setTimeText((int) (mDataList.get(position).duration * 1000)));

        holder.cutMusicImageView.setOnClickListener(new View.OnClickListener() {
            int startTime = 0;
            int endTime = (int) mDataList.get(position).duration;

            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext,R.style.protocalDialog);
                View view = View.inflate(mContext,R.layout.dialog_cut_music,null);
                dialog.setContentView(view);
                Window window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                //设置弹出动画
//                window.setWindowAnimations(R.style.main_menu_animStyle);
                //设置对话框大小
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                final String cutMusicFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/test01_cut.mp3";

                final DoubleSlideSeekBar doubleSlideSeekBar = dialog.findViewById(R.id.sb_cutMusicDialog_cutMusic);
                doubleSlideSeekBar.setBigValue((int)mDataList.get(position).duration);
                doubleSlideSeekBar.setBigRange((int)mDataList.get(position).duration);
                doubleSlideSeekBar.setOnRangeListener(new DoubleSlideSeekBar.onRangeListener() {
                    @Override
                    public void onRangeTouchDown() {
                        cancelUpdateProgressTimer();
                    }

                    @Override
                    public void onRange(float low, float big){
                    }

                    @Override
                    public void onRangeTouchUp(float low, float big) {
                        startTime = (int) low;
                        endTime = (int) big;


                        String inpuFile = mDataList.get(position).absolutePath;
                        int duration = endTime - startTime;
                        String cutAudioCmd = "ffmpeg -y -i %s -acodec copy -ss %d -t %d %s";
                        cutAudioCmd = String.format(cutAudioCmd, inpuFile, startTime, duration, cutMusicFile);
                        //LogUtil.printLog(cutAudioCmd);
                        String[] commandLine =  cutAudioCmd.split(" ");//以空格分割为字符串数组
                        FFmpegCmd.execute(commandLine, new FFmpegCmd.OnHandleListener() {
                            @Override
                            public void onBegin() {
                                LogUtil.printLog("handle audio onBegin...");
                            }

                            @Override
                            public void onEnd(int result) {
                                LogUtil.printLog("handle audio onEnd...");
                                if (null != ((SelectMusicActivity) mContext).mediaPlayerManager) {
                                    ((SelectMusicActivity) mContext).mediaPlayerManager.setOnPreparedCallBack(new MediaPlayerUtil.OnPreparedCallBack() {
                                        @Override
                                        public void onPrepared() {
                                            startUpdateProgressTimer(((SelectMusicActivity) mContext).mediaPlayerManager, doubleSlideSeekBar);
                                        }
                                    });
                                    ((SelectMusicActivity) mContext).mediaPlayerManager.changeUrl(FILE_PROTOCAL + cutMusicFile, true);
                                }
                            }
                        });
                    }
                });

                dialog.findViewById(R.id.ll_cutMusicDialog_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.btn_cutMusicDialog_use).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        EventMusicBean musicBean = new EventMusicBean();
                        musicBean.musicID = mDataList.get(position).musicId;
                        if(new File(cutMusicFile).exists()){
                            musicBean.musicPath = cutMusicFile;
                        }else {
                            musicBean.musicPath = mDataList.get(position).absolutePath;
                        }
                        musicBean.musictitle = mDataList.get(position).title;
                        musicBean.musicAuthor = mDataList.get(position).author;
                        musicBean.musicImgUrl = mDataList.get(position).imageUrl;
                        Intent intent = new Intent();
                        intent.putExtra(SelectMusicActivity.KEY_OF_MUSIC_RESULT, musicBean);
                        mContext.setResult(mContext.RESULT_OK, intent);
                        mContext.finish();
                    }
                });
            }
        });

        holder.btnMusicConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventMusicBean musicBean = new EventMusicBean();
                musicBean.musicID = mDataList.get(position).musicId;
                musicBean.musicPath = mDataList.get(position).absolutePath;
                musicBean.musictitle = mDataList.get(position).title;
                musicBean.musicAuthor = mDataList.get(position).author;
                musicBean.musicImgUrl = mDataList.get(position).imageUrl;
                Intent intent = new Intent();
                intent.putExtra(SelectMusicActivity.KEY_OF_MUSIC_RESULT, musicBean);
                mContext.setResult(mContext.RESULT_OK, intent);
                mContext.finish();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XXPermissions.with(mContext)
                        .constantRequest()
                        //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES)
                        .permission(Permission.WRITE_EXTERNAL_STORAGE)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                hasPermission = true;

                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {
                                hasPermission = false;
                                Toast.makeText(mContext, "请打开朝歌的 存储 权限", Toast.LENGTH_LONG).show();
                                XXPermissions.gotoPermissionSettings(mContext);
                            }
                        });

                if (!hasPermission) {
                    return;
                }

                //当前列表正在播放音乐
                if (((SelectMusicActivity) mContext).mediaPlayerManager != null && ((SelectMusicActivity) mContext).mediaPlayerManager.isPlay()) {
                    holder.ivSelectMusicItemMusicState.setImageResource(R.drawable.ic_start);
                    holder.btnMusicConfirm.setVisibility(View.VISIBLE);
                    holder.cutMusicImageView.setVisibility(View.VISIBLE);
                    ObjectAnimator translationX = new ObjectAnimator().ofFloat(holder.btnMusicConfirm, "translationX", 0, 400);
                    translationX.setDuration(500);
                    translationX.setRepeatCount(0);
                    translationX.setRepeatMode(ValueAnimator.RESTART);
                    translationX.start();

                    //点击正在播放的条目
                    if (mDataList.get(position).isClick) {
                        mDataList.get(position).isClick = false;
                        ((SelectMusicActivity) mContext).mediaPlayerManager.pauseByHand();
                        return;
                    } else {
                        //点击其他条目
                        ((SelectMusicActivity) mContext).mediaPlayerManager.pause();
                    }
                }

                //当前item正在下载
                if (mDataList.get(position).isDownloading && holder.ivSelectMusicItemLoading.getVisibility()==View.VISIBLE) {
                    return;
                }

                for (int i = 0; i < mDataList.size(); i++) {
                    mDataList.get(i).isClick = false;
                }
                lastClickPosstion = position;
                mDataList.get(position).isClick = true;
                notifyDataSetChanged();
            }
        });

        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.ivSelectMusicItemLoading, View.ROTATION.getName(), 0f, 360f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setDuration(800);
        animator.start();

        if (mDataList.get(position).isClick) {
            if (mDataList.get(position).musicUrl == null || "".equals(mDataList.get(position).musicUrl)) {
                Toast.makeText(mContext, "音乐异常，请选择其他音乐", Toast.LENGTH_SHORT).show();
                return;
            }
            holder.ivSelectMusicItemLoading.setVisibility(View.VISIBLE);
            holder.ivSelectMusicItemMusicState.setVisibility(View.GONE);

            mDataList.get(position).isDownloading = true;
            DownloadUtil.getInstance().downloadSmallFile(mDataList.get(position).musicUrl, ConstantUtils.DOWNLOAD_MP3_PATH, "", true, position, new WeakHandler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    mDataList.get(msg.arg1).isDownloading = false;
                    switch (msg.what) {
                        case 1:
                        case 3:
                            if (msg.arg1 >= mDataList.size()) {
                                return false;
                            }
                            if (lastClickPosstion == msg.arg1) {
                                holder.btnMusicConfirm.setVisibility(View.VISIBLE);
                                holder.cutMusicImageView.setVisibility(View.VISIBLE);
                                ObjectAnimator translationX = new ObjectAnimator().ofFloat(holder.btnMusicConfirm, "translationX", 400, 0);
                                translationX.setDuration(500);
                                translationX.setRepeatCount(0);
                                translationX.setRepeatMode(ValueAnimator.RESTART);
                                translationX.start();

                                mDataList.get(msg.arg1).absolutePath = ((String) msg.obj);
                                holder.ivSelectMusicItemLoading.setVisibility(View.GONE);
                                holder.ivSelectMusicItemMusicState.setVisibility(View.VISIBLE);
                                holder.ivSelectMusicItemMusicState.setImageResource(R.drawable.ic_play1);

                                if (null != ((SelectMusicActivity) mContext).mediaPlayerManager
                                        && ((SelectMusicActivity) mContext).mediaPlayerManager.isPlay() && !((SelectMusicActivity) mContext).mediaPlayerManager.getUrl().equals(FILE_PROTOCAL + mDataList.get(lastClickPosstion).absolutePath)) {
                                    ((SelectMusicActivity) mContext).mediaPlayerManager.pause();
                                    ((SelectMusicActivity) mContext).mediaPlayerManager.changeUrl(FILE_PROTOCAL + mDataList.get(msg.arg1).absolutePath, true);
                                }

                                if (null != ((SelectMusicActivity) mContext).mediaPlayerManager && !((SelectMusicActivity) mContext).mediaPlayerManager.isPlay()) {
                                    ((SelectMusicActivity) mContext).mediaPlayerManager.changeUrl(FILE_PROTOCAL + mDataList.get(msg.arg1).absolutePath, true);
                                }
                            }
                            break;
                        case 2:
//                            if (null !=  ((SelectMusicActivity)mContext).mediaPlayerManager){
//                                ((SelectMusicActivity)mContext).mediaPlayerManager.release();
//                            }
                            if (null != ((SelectMusicActivity) mContext).mediaPlayerManager && ((SelectMusicActivity) mContext).mediaPlayerManager.isPlay()) {
                                ((SelectMusicActivity) mContext).mediaPlayerManager.pause();
                            }
                            for (int i = 0; i < mDataList.size(); i++) {
                                mDataList.get(i).isClick = false;
                            }
                            notifyDataSetChanged();
                            break;
                    }
                    return false;
                }
            }));
        } else {
            holder.ivSelectMusicItemLoading.setVisibility(View.GONE);
            holder.ivSelectMusicItemMusicState.setVisibility(View.VISIBLE);
            holder.ivSelectMusicItemMusicState.setImageResource(R.drawable.ic_start);
            holder.btnMusicConfirm.setVisibility(View.GONE);
            holder.cutMusicImageView.setVisibility(View.GONE);
        }
    }

    /**
     * 开启更新进度的计时器。
     */
    protected void startUpdateProgressTimer(final MediaPlayerUtil mediaPlayerUtil, final DoubleSlideSeekBar seekBar) {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    seekBar.post(new Runnable() {
                        @Override
                        public void run() {
                            long position = mediaPlayerUtil.getCurrentPosition();
                            long duration = mediaPlayerUtil.getDuration();
                            float progress =  ((float) position) / ((float) duration);
                            seekBar.setPlayProgress(progress);
                        }
                    });
                }
            };
        }

        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 150);
    }

    /**
     * 取消更新进度的计时器。
     */
    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_selectMusic_item_musicState)
        ImageView ivSelectMusicItemMusicState;
        @BindView(R.id.iv_selectMusic_item_img)
        NiceImageView ivSelectMusicItemImg;
        @BindView(R.id.iv_selectMusic_item_loading)
        ImageView ivSelectMusicItemLoading;
        @BindView(R.id.iv_selectMusic_item_cutMusic)
        ImageView cutMusicImageView;
        @BindView(R.id.rl_selectMusic_item_img)
        RelativeLayout rlSelectMusicItemImg;
        @BindView(R.id.tv_selectMusic_item_name)
        TextView tvSelectMusicItemName;
        @BindView(R.id.tv_selectMusic_item_time)
        TextView tvSelectMusicItemTime;
        @BindView(R.id.btn_music_confirm)
        Button btnMusicConfirm;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
