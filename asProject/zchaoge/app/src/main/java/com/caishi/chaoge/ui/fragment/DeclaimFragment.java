package com.caishi.chaoge.ui.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseFragment;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.BackgroundBean;
import com.caishi.chaoge.bean.EffectBean;
import com.caishi.chaoge.bean.EventBusBean.AudioVolumeBean;
import com.caishi.chaoge.bean.EventBusBean.EventMusicBean;
import com.caishi.chaoge.bean.EventBusBean.IssueBean;
import com.caishi.chaoge.bean.FontBean;
import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.bean.MainFragmentBean;
import com.caishi.chaoge.bean.ModuleMaterialBean;
import com.caishi.chaoge.bean.TextBean;
import com.caishi.chaoge.bean.UnderscoreBean;
import com.caishi.chaoge.manager.MediaPlayerManager;
import com.caishi.chaoge.manager.ModelManager;
import com.caishi.chaoge.manager.VideoEditManager;
import com.caishi.chaoge.request.MainFragmentRequest;
import com.caishi.chaoge.ui.activity.DeclaimActivity;
import com.caishi.chaoge.ui.activity.RecordActivity;
import com.caishi.chaoge.ui.activity.SelectBgImageActivity;
import com.caishi.chaoge.ui.activity.SelectMusicActivity;
import com.caishi.chaoge.ui.adapter.DeclaimBackgroundAdapter;
import com.caishi.chaoge.ui.adapter.EffectAdapter;
import com.caishi.chaoge.ui.adapter.FontAdapter;
import com.caishi.chaoge.ui.adapter.TextColorAdapter;
import com.caishi.chaoge.ui.adapter.UnderscoreAdapter;
import com.caishi.chaoge.ui.dialog.FontDialog;
import com.caishi.chaoge.ui.widget.MyCountDownTimer;
import com.caishi.chaoge.ui.widget.dialog.DialogUtil;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.utils.FileUtils;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.SystemUtils;
import com.caishi.chaoge.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;


import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.model.TResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.caishi.chaoge.utils.ConstantUtils.DOWNLOAD_BG_PATH;
import static com.caishi.chaoge.utils.ConstantUtils.DOWNLOAD_FONT_PATH;
import static com.caishi.chaoge.utils.ConstantUtils.DOWNLOAD_MP3_PATH;
import static com.caishi.chaoge.utils.ConstantUtils.SINGLE_TEXT_DURATION;

public class DeclaimFragment extends BaseFragment implements TakePhoto.TakeResultListener {
    //字效
//    String[] effects = new String[]{"自由翻转"};
    String[] effects = new String[]{"自由翻转", "口吐文字", "多行横排", "多行竖排", "单行横排", "单行竖排"};
    //背景图
    int[] backgrounds = new int[]{R.drawable.im_photoalbum, R.drawable.im_material};
    //配乐
    int[] underscores = new int[]{R.drawable.im_nomusic,/* R.drawable.im_local,*/ R.drawable.im_musiclibrary};
    private static final String TAB_FLAG = "tabFlag";
    /**
     * 字效
     */
    private RecyclerView rv_mainFragment_effect;
    /**
     * 文字
     */
    private LinearLayout ll_mainFragment_text;
    /**
     * 背景
     */
    private RecyclerView rv_mainFragment_background;
    /**
     * 配乐
     */
    private RecyclerView rv_mainFragment_underscore;
    /**
     * 音量
     */
    private LinearLayout ll_mainFragment_volume;
    /**
     * 文字颜色
     */
    private RecyclerView rv_text_list;
    /**
     * 文字字体
     */
    private RecyclerView rv_font_list;


    private ArrayList<MainFragmentBean.ModelList> modelList;
    private ArrayList<EffectBean> effectBeanList;
    private ArrayList<TextBean> textBeanList;
    private ArrayList<FontBean> fontBeanList;
    private ArrayList<BackgroundBean> backgroundList;
    private ArrayList<UnderscoreBean> underscoreList;
    private int lastPositionBackground = -1;
    private int lastPositionUnderscore = -1;
    private int lastPositionFont = -1;
    private int lastPositionColor = -1;
    private int lastPositionEffect = -1;
    private DeclaimBackgroundAdapter backgroundAdapter;
    private UnderscoreAdapter underscoreAdapter;
    private FontAdapter fontAdapter;
    private VideoEditManager videoEditManager;
    private SeekBar sb_mainFrag_person;
    private SeekBar sb_mainFrag_video;
    private SeekBar sb_mainFrag_bg;
    private DeclaimActivity declaimActivity;
    private TakePhotoImpl takePhoto;
    private EffectAdapter effectAdapter;
    private TextColorAdapter textColorAdapter;
    public boolean updateUI;//绘制字幕控制框的标识
    public boolean showMouthText;//绘制字幕控制框的标识
    private FontDialog fontDialog;
    private MediaPlayerManager mediaPlayerManager;

    public static DeclaimFragment newInstance() {
        DeclaimFragment fragment = new DeclaimFragment();
        Bundle args = new Bundle();
        args.putInt(TAB_FLAG, 0);
        fragment.setArguments(args);
        return fragment;
    }


    public void setViewVisibility(int tabFlag) {
        updateUI = false;
        if (null != effectBeanList && tabFlag == 1) {
            for (int i = 0; i < effectBeanList.size(); i++) {
                if (effectBeanList.get(i).isSelect) {
                    if (i == 4 || i == 5) {
                        updateUI = true;
                        declaimActivity.showRect();
                    }

                    if (i == 1) {
                        showMouthText = true;
                    }
                }
            }
        }
        declaimActivity.mCaptionDrawRect.setVisibility(updateUI ? View.VISIBLE : View.GONE);
        declaimActivity.mouthPositionImageView.setVisibility(showMouthText ? View.VISIBLE : View.GONE);
        //字效
        rv_mainFragment_effect.setVisibility(tabFlag == 1 ? View.VISIBLE : View.GONE);
        //字体
        ll_mainFragment_text.setVisibility(tabFlag == 2 ? View.VISIBLE : View.GONE);
        //背景
        rv_mainFragment_background.setVisibility(tabFlag == 0 ? View.VISIBLE : View.GONE);
        //音乐
        rv_mainFragment_underscore.setVisibility(tabFlag == 3 ? View.VISIBLE : View.GONE);
        //音量
        ll_mainFragment_volume.setVisibility(tabFlag == 4 ? View.VISIBLE : View.GONE);

    }

    @Override
    protected int initContentView() {
        return R.layout.fragment_declaim;
    }

    @Override
    protected void initView(View view) {
        declaimActivity = ((DeclaimActivity) mContext);
        rv_mainFragment_effect = $(R.id.rv_mainFragment_effect);
        ll_mainFragment_text = $(R.id.ll_mainFragment_text);
        rv_text_list = $(R.id.rv_text_list);
        rv_font_list = $(R.id.rv_font_list);
        rv_mainFragment_background = $(R.id.rv_mainFragment_background);
        rv_mainFragment_underscore = $(R.id.rv_mainFragment_underscore);
        ll_mainFragment_volume = $(R.id.ll_mainFragment_volume);
        sb_mainFrag_person = $(R.id.sb_mainFrag_person);
        sb_mainFrag_bg = $(R.id.sb_mainFrag_bg);
        sb_mainFrag_video = $(R.id.sb_mainFrag_video);
        videoEditManager = declaimActivity.videoEditManager;
        mediaPlayerManager = MediaPlayerManager.newInstance(mContext);
    }

    @Override
    public void doBusiness() {
        MainFragmentRequest.newInstance((BaseActivity) getContext()).basicData(new BaseRequestInterface<MainFragmentBean>() {
            @Override
            public void success(int state, String msg, MainFragmentBean mainFragmentBean) {
                setData(mainFragmentBean);
            }

            @Override
            public void error(int state, String msg) {

            }
        });
    }


    private void setData(MainFragmentBean mainFragmentBean) {
        //模板数据
//        modelList = mainFragmentBean.modelList;
//        TemplateAdapter templateAdapter = new TemplateAdapter();
//        setRecyclerView(rv_mainFragment_template, templateAdapter);
//        int size = modelList.size();
//        for (int i = 0; i < size; i++) {
//            modelList.get(i).isSelect = i == 0;
//        }
//        templateAdapter.setNewData(modelList);
//        templateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                for (int i = 0; i < modelList.size(); i++) {
//                    modelList.get(i).isSelect = i == position;
//                }
//                adapter.notifyDataSetChanged();
//            }
//        });
        fontDialog = FontDialog.newInstance();
        fontDialog.init((BaseActivity) mContext);

        //字效数据
        effectAdapter = new EffectAdapter();
        setRecyclerView(rv_mainFragment_effect, effectAdapter);
        effectBeanList = new ArrayList<>();
        for (int i = 0; i < effects.length; i++) {
            EffectBean effectBean = new EffectBean();
            if (null != videoEditManager && null != videoEditManager.getModuleMaterialBean())
                effectBean.isSelect = i == videoEditManager.getModuleMaterialBean().specialFlag;
            effectBean.value = effects[i];
            effectBeanList.add(effectBean);
        }
        effectAdapter.setNewData(effectBeanList);
        effectAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (lastPositionEffect == position)
                    return;
                for (int i = 0; i < effectBeanList.size(); i++) {
                    effectBeanList.get(i).isSelect = i == position;
                }
                adapter.notifyDataSetChanged();
                videoEditManager.getModuleMaterialBean().specialFlag = position;
                if (position == 4) {
                    videoEditManager.getModuleMaterialBean().leftLocation = 0.5f;
                    videoEditManager.getModuleMaterialBean().topLocation = 0.5f;
                }
                if (position == 5) {
                    videoEditManager.getModuleMaterialBean().leftLocation = 0.5f;
                    videoEditManager.getModuleMaterialBean().topLocation = 0.3f;
                }

                videoEditManager.getModuleMaterialBean().aeModulePath = declaimActivity.modelManager.getAeModulePath(position);//判断并且复制文件
                videoEditManager.getModuleMaterialBean().duration = videoEditManager.getModuleMaterialBean().lrcList.get(
                        videoEditManager.getModuleMaterialBean().lrcList.size() - 1).getEnd() / 1000f;
                updateUI = position >= 4;
                showMouthText = position == 1;
                if (updateUI) {
                    videoEditManager.showRectF = null;
                    videoEditManager.scale = -1;
                    videoEditManager.captionPosition = -1;
                }
                declaimActivity.quitEditCaptionMode(false);
                declaimActivity.mCaptionDrawRect.setVisibility(View.GONE);
                declaimActivity.mouthPositionImageView.setVisibility(View.GONE);
                if (Utils.isEmpty(videoEditManager.getModuleMaterialBean().aeModulePath))
                    videoEditManager.updateVirtualVideo();
                else {
                    if (FileUtils.isExist(videoEditManager.getModuleMaterialBean().aeModulePath) && !declaimActivity.modelManager.isCopy)//AE模板地址存在直接设置
                        videoEditManager.updateVirtualVideo();
                    else {
                        new MyCountDownTimer(500, 500) {
                            @Override
                            public void onFinish() {
                                videoEditManager.updateVirtualVideo();
                            }
                        }.start();

                    }
                }
                lastPositionEffect = position;
            }
        });
        //字体颜色
        textColorAdapter = new TextColorAdapter();
        setRecyclerView(rv_text_list, textColorAdapter);
        textBeanList = new ArrayList<>();
        TextBean textBean = new TextBean();
        textBean.imgRes = R.drawable.im_library;
        textBean.itemType = 0;
        textBeanList.add(textBean);
        for (int i = 0; i < mainFragmentBean.colorList.size(); i++) {
            TextBean textBean1 = new TextBean();
            textBean1.isSelect = false;
            textBean1.itemType = 1;
            textBean1.color = mainFragmentBean.colorList.get(i);
            textBeanList.add(textBean1);
        }
        textColorAdapter.setNewData(textBeanList);
        textColorAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position != 0) {
                    if (lastPositionColor == position)
                        return;
                    for (int i = 0; i < textBeanList.size(); i++) {
                        textBeanList.get(i).isSelect = i == position;
                    }
                    List<String> colorList = new ArrayList<>();

                    for (int i = 0; i < videoEditManager.getCaptionNum(); i++) {
                        colorList.add(textBeanList.get(position).color);
                    }
                    videoEditManager.getModuleMaterialBean().colorList = colorList;
                    videoEditManager.updateVirtualVideo();
                    adapter.notifyDataSetChanged();
                } else {//颜色库
                    declaimActivity.showStatusBar();
                    fontDialog.showFontDialog(videoEditManager.getModuleMaterialBean().lrcList,
                            videoEditManager.getModuleMaterialBean().colorList, videoEditManager.getModuleMaterialBean().fontFilePath,
                            false, 1);
                    fontDialog.setOnDialogKeyListener(new FontDialog.OnDialogKeyListener() {
                        @Override
                        public void onBack() {
                            declaimActivity.hideStatusBar();
                        }

                        @Override
                        public void onAffirm(List<String> colorList, List<LrcBean> lrcList, String fontPath) {
                            declaimActivity.hideStatusBar();
                            setFont(colorList, lrcList, fontPath);
                        }
                    });


                }
                lastPositionColor = position;

            }
        });

        //字体数据
        fontAdapter = new FontAdapter(mContext);
        setRecyclerView(rv_font_list, fontAdapter);
        fontBeanList = new ArrayList<>();
        FontBean fontBean = new FontBean();
        fontBean.itemType = 0;
        fontBean.isDownload = true;
        fontBeanList.add(fontBean);
        ArrayList<MainFragmentBean.FontList> fontList = mainFragmentBean.fontList;
        final List<File> fontFiles = FileUtils.getAllFiles(DOWNLOAD_FONT_PATH);
        if (fontFiles.size() > 0) {
            for (int j = 0; j < fontFiles.size(); j++) {
                for (int i = 0; i < fontList.size(); i++) {
                    String path;
                    path = fontList.get(i).fontUrl;
                    String name = path.substring(path.lastIndexOf("/") + 1);
                    String name1 = fontFiles.get(j).getName();
                    if (name1.equals(name)) {
                        fontList.get(i).isDownload = true;
                        fontList.get(i).fontUrl = fontFiles.get(j).getAbsolutePath();
                    }
                }
            }
        }
        for (int i = 0; i < fontList.size(); i++) {
            FontBean fontBean1 = new FontBean();
            fontBean1.isSelect = false;
            fontBean1.isDownload = fontList.get(i).isDownload;
            fontBean1.itemType = 1;
            fontBean1.fontId = fontList.get(i).fontId;
            fontBean1.fontUrl = fontList.get(i).fontUrl;
            fontBean1.selectUrl = fontList.get(i).selectUrl;
            fontBean1.unSelectUrl = fontList.get(i).unSelectUrl;
            fontBeanList.add(fontBean1);
        }
        fontAdapter.setNewData(fontBeanList);
        fontAdapter.bindToRecyclerView(rv_font_list);
        fontAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position != 0) {
                    if (lastPositionFont == position)
                        return;
                    ProgressBar pb_itemFont_progress = (ProgressBar) fontAdapter.getViewByPosition(position, R.id.pb_itemFont_progress);
                    if (fontBeanList.get(position).isDownload) {
                        //mp3地址
                        String path = "";
                        path = fontBeanList.get(position).fontUrl;
                        for (int i = 0; i < fontBeanList.size(); i++) {
                            if (fontBeanList.get(i).isSelect) {
                                fontBeanList.get(i).isSelect = false;
                                fontAdapter.notifyItemChanged(i);
                            }
                            fontBeanList.get(i).isSelect =
                                    (position == i);
                        }
                        if (fontBeanList.get(position).isDownload)
                            fontAdapter.notifyItemChanged(position);
                        else
                            fontAdapter.notifyDataSetChanged();
                        videoEditManager.getModuleMaterialBean().fontFilePath = path;
                        videoEditManager.updateVirtualVideo();
                    } else {
                        String url = "";
                        url = fontBeanList.get(position).fontUrl;
                        downloadFont(url, pb_itemFont_progress, position);
                    }
                } else {//字体库
                    declaimActivity.showStatusBar();
                    fontDialog.showFontDialog(videoEditManager.getModuleMaterialBean().lrcList,
                            videoEditManager.getModuleMaterialBean().colorList,
                            videoEditManager.getModuleMaterialBean().fontFilePath, false, 0);
                    fontDialog.setOnDialogKeyListener(new FontDialog.OnDialogKeyListener() {
                        @Override
                        public void onBack() {
                            declaimActivity.hideStatusBar();
                        }

                        @Override
                        public void onAffirm(List<String> colorList, List<LrcBean> lrcList, String fontPath) {
                            declaimActivity.hideStatusBar();
                            setFont(colorList, lrcList, fontPath);
                        }
                    });

                }
                lastPositionFont = position;

            }
        });
        //背景图数据
        backgroundList = new ArrayList<>();
        backgroundAdapter = new DeclaimBackgroundAdapter();
        setRecyclerView(rv_mainFragment_background, backgroundAdapter);
        for (int i = 0; i < backgrounds.length; i++) {
            BackgroundBean backgroundBean = new BackgroundBean();
            backgroundBean.itemType = 0;
            backgroundBean.isDownload = true;
            backgroundBean.imgRes = backgrounds[i];
            backgroundList.add(backgroundBean);
        }
        //判断本地是否有数据
        ArrayList<MainFragmentBean.BackGroundList> bgImageList = mainFragmentBean.backGroundList;
        final List<File> backgroundFiles = FileUtils.getAllFiles(DOWNLOAD_BG_PATH);
        if (backgroundFiles.size() > 0) {
            for (int j = 0; j < backgroundFiles.size(); j++) {
                for (int i = 0; i < bgImageList.size(); i++) {
                    String path = "";
                    int bgType = bgImageList.get(i).backGroundClass;
                    if (bgType == 0)
                        path = bgImageList.get(i).imageUrl;
                    else if (bgType == 2) {
                        path = bgImageList.get(i).videoUrl;
                    }
                    String name = path.substring(path.lastIndexOf("/") + 1);
                    if (backgroundFiles.get(j).getName().equals(name)) {
                        bgImageList.get(i).isDownload = true;
                        if (bgType == 0)
                            bgImageList.get(i).imageUrl = backgroundFiles.get(j).getAbsolutePath();
                        else if (bgType == 2) {
                            bgImageList.get(i).videoUrl = backgroundFiles.get(j).getAbsolutePath();
                        }
                    }
                }
            }
        }
        //数据转换
        for (int i = 0; i < bgImageList.size(); i++) {
            BackgroundBean backgroundBean = new BackgroundBean();
            backgroundBean.itemType = 1;
            backgroundBean.isDownload = bgImageList.get(i).isDownload;
            backgroundBean.backGroundClass = bgImageList.get(i).backGroundClass;
            backgroundBean.backGroundId = bgImageList.get(i).backGroundId;
            backgroundBean.cover = bgImageList.get(i).cover;
            backgroundBean.imageUrl = bgImageList.get(i).imageUrl;
            backgroundBean.slideUrl = bgImageList.get(i).slideUrl;
            backgroundBean.targetTime = bgImageList.get(i).targetTime;
            backgroundBean.videoUrl = bgImageList.get(i).videoUrl;
            backgroundList.add(backgroundBean);
        }
        backgroundAdapter.setNewData(backgroundList);
        backgroundAdapter.bindToRecyclerView(rv_mainFragment_background);
        backgroundAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0://本地相册
                        XXPermissions.with(mContext)
                                //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                                .request(new OnPermission() {
                                    @Override
                                    public void hasPermission(List<String> granted, boolean isAll) {
                                        if (isAll) {
                                            if ("vivo".equalsIgnoreCase(SystemUtils.getDeviceBrand())
                                                    || "OPPO".equalsIgnoreCase(SystemUtils.getDeviceBrand())
                                                    || "OnePlus".equalsIgnoreCase(SystemUtils.getDeviceBrand())) {
                                                takePhoto.onPickFromDocuments();
                                            } else {
                                                takePhoto.onPickFromGallery();
                                            }
                                        } else {
                                            showToast("部分权限未正常授予");
                                            XXPermissions.gotoPermissionSettings(mContext);
                                        }
                                    }

                                    @Override
                                    public void noPermission(List<String> denied, boolean quick) {
                                        showToast("被永久拒绝授权，请手动授予权限");
                                        XXPermissions.gotoPermissionSettings(mContext);
                                    }
                                });


                        return;
                    case 1://背景库
                        startActivityForResult(SelectBgImageActivity.class, null, 2003);
                        return;

                }
                if (lastPositionBackground == position) {
                    return;
                }
                ImageView img_itemBackground_progress = (ImageView) backgroundAdapter.getViewByPosition(position, R.id.img_itemBackground_progress);
                ImageView img_itemBackground_state = (ImageView) backgroundAdapter.getViewByPosition(position, R.id.img_itemBackground_state);
                if (backgroundList.get(position).isDownload) {
                    //视频背景地址
                    String path = "";
                    int bgType = backgroundList.get(position).backGroundClass;
                    if (bgType == 0)
                        path = backgroundList.get(position).imageUrl;
                    else if (bgType == 2) {
                        path = backgroundList.get(position).videoUrl;
                    }
                    for (int i = 0; i < backgroundList.size(); i++) {
                        if (backgroundList.get(i).isSelect) {
                            backgroundList.get(i).isSelect = false;
                            backgroundAdapter.notifyItemChanged(i);
                        }
                        backgroundList.get(i).isSelect =
                                (position == i);
                    }
                    if (backgroundList.get(position).isDownload)
                        backgroundAdapter.notifyItemChanged(position);
                    else
                        backgroundAdapter.notifyDataSetChanged();
                    declaimActivity.backGroundId = backgroundList.get(position).backGroundId;
                    videoEditManager.getModuleMaterialBean().bgPath = path;
                    videoEditManager.updateVirtualVideo();
                } else {
                    String url = "";
                    int bgType = backgroundAdapter.getData().get(position).backGroundClass;
                    if (bgType == 0)
                        url = backgroundList.get(position).imageUrl;
                    else if (bgType == 2) {
                        url = backgroundList.get(position).videoUrl;
                    }
                    downloadImage(bgType, url, img_itemBackground_progress, img_itemBackground_state, position);
                }
                lastPositionBackground = position;
            }
        });
        //配乐数据
        underscoreList = new ArrayList<>();
        underscoreAdapter = new UnderscoreAdapter();
        setRecyclerView(rv_mainFragment_underscore, underscoreAdapter);
        for (int i = 0; i < underscores.length; i++) {
            UnderscoreBean underscoreBean = new UnderscoreBean();
            underscoreBean.itemType = 0;
            underscoreBean.isPlay = -1;
            underscoreBean.isDownload = true;
            underscoreBean.imgRes = underscores[i];
            underscoreList.add(underscoreBean);
        }
        ArrayList<MainFragmentBean.MusicList> musicList = mainFragmentBean.musicList;
        final List<File> musicFiles = FileUtils.getAllFiles(DOWNLOAD_MP3_PATH);
        if (musicFiles.size() > 0) {
            for (int j = 0; j < musicFiles.size(); j++) {
                for (int i = 0; i < musicList.size(); i++) {
                    String path;
                    path = musicList.get(i).musicUrl;
                    String name = path.substring(path.lastIndexOf("/") + 1);
                    if (musicFiles.get(j).getName().equals(name)) {
                        musicList.get(i).isDownload = true;
                        musicList.get(i).musicUrl = musicFiles.get(j).getAbsolutePath();
                    }
                }
            }
        }
        //数据转换
        for (int i = 0; i < musicList.size(); i++) {
            UnderscoreBean underscoreBean = new UnderscoreBean();
            underscoreBean.itemType = 1;
            underscoreBean.isPlay = -1;
            underscoreBean.isDownload = musicList.get(i).isDownload;
            underscoreBean.author = musicList.get(i).author;
            underscoreBean.duration = musicList.get(i).duration;
            underscoreBean.imageUrl = musicList.get(i).imageUrl;
            underscoreBean.musicId = musicList.get(i).musicId;
            underscoreBean.musicUrl = musicList.get(i).musicUrl;
            underscoreBean.targetTime = musicList.get(i).targetTime;
            underscoreBean.title = musicList.get(i).title;
            underscoreList.add(underscoreBean);
        }
        underscoreAdapter.setNewData(underscoreList);
        underscoreAdapter.bindToRecyclerView(rv_mainFragment_underscore);
        underscoreAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0://无配乐
                        videoEditManager.getModuleMaterialBean().bgMusicPath = null;
                        videoEditManager.updateVirtualVideo();
                        for (int i = 0; i < underscoreList.size(); i++) {
                            underscoreAdapter.getData().get(i).isSelect = false;
                        }
                        underscoreAdapter.notifyDataSetChanged();
                        lastPositionUnderscore = -1;
                        return;
                    case 1://配乐库
                        startActivityForResult(SelectMusicActivity.class, null, 2004);
                        return;
                }
                ImageView img_itemUnderscore_progress = (ImageView) underscoreAdapter.getViewByPosition(position, R.id.img_itemUnderscore_progress);
                ImageView img_itemUnderscore_state = (ImageView) underscoreAdapter.getViewByPosition(position, R.id.img_itemUnderscore_state);
                final ImageView img_itemUnderscore_play = (ImageView) underscoreAdapter.getViewByPosition(position, R.id.img_itemUnderscore_play);
                if (null != img_itemUnderscore_play)
                    img_itemUnderscore_play.setVisibility(View.VISIBLE);
                if (lastPositionUnderscore == position) {
                    if (mediaPlayerManager.isPlay()) {
                        if (null != img_itemUnderscore_play)
                            img_itemUnderscore_play.setImageResource(R.drawable.ic_play_music);
                        mediaPlayerManager.pause();
                    } else {
                        if (null != img_itemUnderscore_play)
                            img_itemUnderscore_play.setImageResource(R.drawable.ic_suspended_music);
                        mediaPlayerManager.continuePlay();
                    }
                    return;
                }
                if (underscoreList.get(position).isDownload) {
                    //mp3地址
                    String path = "";
                    path = underscoreList.get(position).musicUrl;
                    for (int i = 0; i < underscoreList.size(); i++) {
                        underscoreAdapter.getData().get(i).isSelect = position == i;
                        underscoreAdapter.getData().get(i).isPlay = i == position ? 0 : -1;
                    }

                    mediaPlayerManager.changeUrl(path);
                    if (null != img_itemUnderscore_play) {
                        img_itemUnderscore_play.setImageResource(R.drawable.ic_suspended_music);
                    }
                    declaimActivity.musicId = underscoreList.get(position).musicId;
                    videoEditManager.getModuleMaterialBean().bgMusicPath = path;
                    videoEditManager.updateVirtualVideo();
                    underscoreAdapter.notifyDataSetChanged();
                } else {
                    String url = "";
                    url = underscoreList.get(position).musicUrl;
                    downloadMP3(url, img_itemUnderscore_progress, img_itemUnderscore_state, img_itemUnderscore_play, position);
                }
                lastPositionUnderscore = position;
            }
        });

        sb_mainFrag_person.setMax(300);
        sb_mainFrag_bg.setMax(100);
        sb_mainFrag_video.setMax(100);
//        String path = videoEditManager.getModuleMaterialBean().bgPath;
//        boolean contains = !Utils.isEmpty(path) && path.contains(".mp4");
//        sb_mainFrag_video.setEnabled(contains);
//        sb_mainFrag_bg.setEnabled(videoEditManager.getModuleMaterialBean().bgMusicPath!=null);
//        sb_mainFrag_person.setEnabled(videoEditManager.getModuleMaterialBean().personMusicPath!=null);
    }

    private void setFont(List<String> colorList, List<LrcBean> lrcList, String fontPath) {
        for (int i = 0; i < fontBeanList.size(); i++) {
            fontBeanList.get(i).isSelect = false;
        }
        fontAdapter.setNewData(fontBeanList);
        lastPositionFont = -1;
        for (int i = 0; i < textBeanList.size(); i++) {
            textBeanList.get(i).isSelect = false;
        }
        textColorAdapter.setNewData(textBeanList);
        lastPositionColor = -1;
        videoEditManager.getModuleMaterialBean().colorList = colorList;
        videoEditManager.getModuleMaterialBean().lrcList = lrcList;
        if (fontPath != null)
            videoEditManager.getModuleMaterialBean().fontFilePath = fontPath;

        videoEditManager.updateVirtualVideo();
    }

    public void updateSeekBarProgress(AudioVolumeBean volume) {
        sb_mainFrag_person.setProgress(volume.personVolume);
        sb_mainFrag_bg.setProgress(volume.bgVolume);
        sb_mainFrag_video.setProgress(volume.videoBgVolume);
    }

    public void updateEffectAdapter(int position) {
        if (effectBeanList != null && effectAdapter != null) {
            for (int i = 0; i < effectBeanList.size(); i++) {
                effectBeanList.get(i).isSelect = i == position;
            }
            effectAdapter.setNewData(effectBeanList);
            lastPositionEffect = position;
        }
    }

    public void updateAdapter() {
        if (textBeanList != null && textColorAdapter != null) {
            for (int i = 0; i < textBeanList.size(); i++) {
                textBeanList.get(i).isSelect = false;
            }
            textColorAdapter.setNewData(textBeanList);
            lastPositionColor = -1;
        }
        if (fontBeanList != null && fontAdapter != null) {
            for (int i = 0; i < fontBeanList.size(); i++) {
                fontBeanList.get(i).isSelect = false;
            }
            fontAdapter.setNewData(fontBeanList);
            lastPositionFont = -1;
        }


        if (backgroundList != null && backgroundAdapter != null) {
            for (int i = 0; i < backgroundList.size(); i++) {
                backgroundList.get(i).isSelect = false;
            }
            backgroundAdapter.setNewData(backgroundList);
            lastPositionBackground = -1;
        }
        if (underscoreList != null && underscoreAdapter != null) {
            for (int i = 0; i < underscoreList.size(); i++) {
                underscoreList.get(i).isSelect = false;
            }
            underscoreAdapter.setNewData(underscoreList);
            lastPositionUnderscore = -1;
        }

    }


    @Override

    public void setListener() {
    }

    public void updateSeekBar() {
        sb_mainFrag_person.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                declaimActivity.volume.personVolume = progress;
                videoEditManager.setVolume(declaimActivity.volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_mainFrag_bg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                declaimActivity.volume.bgVolume = progress;
                videoEditManager.setVolume(declaimActivity.volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_mainFrag_video.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                declaimActivity.volume.videoBgVolume = progress;
                videoEditManager.setVolume(declaimActivity.volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    public void widgetClick(View v) {

    }
    @Override
    public void onPause() {
        super.onPause();
        ImageView img_itemUnderscore_play = null;
        if (null != underscoreList && underscoreList.size() > 0)
            for (int i = 0; i < underscoreList.size(); i++) {
                if (underscoreList.get(i).isPlay == 0 && null != underscoreAdapter) {
                    img_itemUnderscore_play = (ImageView) underscoreAdapter.getViewByPosition(i, R.id.img_itemUnderscore_play);
                }
            }
        if (mediaPlayerManager.isPlay()) {
            mediaPlayerManager.pause();
            if (null != img_itemUnderscore_play) {
                img_itemUnderscore_play.setImageResource(R.drawable.ic_play_music);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(requestCode + "=====requestCode");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2003://背景图片
                    for (int i = 0; i < backgroundList.size(); i++) {
                        backgroundList.get(i).isSelect = false;
                    }
                    backgroundAdapter.setNewData(backgroundList);
                    lastPositionBackground = -1;

                    videoEditManager.getModuleMaterialBean().bgPath = data.getStringExtra("path");
                    declaimActivity.backGroundId = data.getStringExtra("backGroundId");
                    LogUtil.d(videoEditManager.getModuleMaterialBean().bgPath + "=====bgPath");
                    videoEditManager.updateVirtualVideo();
                    break;
                case 2004://背景音乐返回
                    for (int i = 0; i < underscoreList.size(); i++) {
                        underscoreList.get(i).isSelect = false;
                    }
                    underscoreAdapter.setNewData(underscoreList);
                    lastPositionUnderscore = -1;
                    EventMusicBean eventMusicBean = (EventMusicBean) data.getSerializableExtra(SelectMusicActivity.KEY_OF_MUSIC_RESULT);
                    if (null != eventMusicBean) {
                        videoEditManager.getModuleMaterialBean().bgMusicPath = eventMusicBean.musicPath;
                        declaimActivity.musicId = eventMusicBean.musicID;
                        videoEditManager.updateVirtualVideo();
                    } else {
                        videoEditManager.getModuleMaterialBean().bgMusicPath = "";
                        declaimActivity.musicId = "";
                    }

                    break;
            }
            new MyCountDownTimer(2000, 1000) {
                @Override
                public void onFinish() {
                    updateSeekBarProgress(videoEditManager.getVolume());
                }
            }.start();
        }
    }

    private void downloadFont(String url, final ProgressBar progressBar, final int position) {
        String name = url.substring(url.lastIndexOf("/") + 1);
        EasyHttp.downLoad(url)
                .savePath(DOWNLOAD_FONT_PATH)
                .saveName(name)//不设置默认名字是时间戳生成的
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = (int) (bytesRead * 100 / contentLength);
                        progressBar.setProgress(progress);
                    }

                    @Override
                    public void onStart() {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete(String path) {
                        progressBar.setVisibility(View.GONE);
                        lastPositionFont = -1;
                        fontBeanList.get(position).isDownload = true;
                        fontBeanList.get(position).fontUrl = path;
                        fontAdapter.setData(position, fontBeanList.get(position));
                    }

                    @Override
                    public void onError(ApiException e) {
                        //下载失败
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    private void downloadMP3(String url, final ImageView img_itemUnderscore_progress, final ImageView img_itemUnderscore_state, final ImageView img_itemUnderscore_play, final int position) {
        String name = url.substring(url.lastIndexOf("/") + 1);
        EasyHttp.downLoad(url)
                .savePath(DOWNLOAD_MP3_PATH)
                .saveName(name)//不设置默认名字是时间戳生成的
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                    }

                    @Override
                    public void onStart() {
                        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        LinearInterpolator lin = new LinearInterpolator();
                        rotate.setInterpolator(lin);
                        rotate.setDuration(1000);//设置动画持续周期
                        rotate.setRepeatCount(-1);//设置重复次数
                        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                        rotate.setStartOffset(0);//执行前的等待时间
                        img_itemUnderscore_progress.setAnimation(rotate);
                        //开始下载
                        img_itemUnderscore_progress.setVisibility(View.VISIBLE);
                        img_itemUnderscore_state.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onComplete(String path) {
                        img_itemUnderscore_progress.clearAnimation();
                        img_itemUnderscore_progress.setVisibility(View.GONE);
                        lastPositionUnderscore = -1;
                        underscoreList.get(position).isDownload = true;
                        underscoreList.get(position).musicUrl = path;
                        underscoreAdapter.setData(position, underscoreList.get(position));
                    }

                    @Override
                    public void onError(ApiException e) {
                        //下载失败
                        img_itemUnderscore_progress.clearAnimation();
                        img_itemUnderscore_progress.setVisibility(View.GONE);
                    }
                });
    }


    private void downloadImage(final int bgType, String url, final ImageView img_itemBackground_progress, final ImageView img_itemBackground_state, final int position) {
        String name = url.substring(url.lastIndexOf("/") + 1);
        EasyHttp.downLoad(url)
                .savePath(DOWNLOAD_BG_PATH)
                .saveName(name)//不设置默认名字是时间戳生成的
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
//                        int progress = (int) (bytesRead * 100 / contentLength);
//                        pb_progress.setProgress(progress);
                    }

                    @Override
                    public void onStart() {
                        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        LinearInterpolator lin = new LinearInterpolator();
                        rotate.setInterpolator(lin);
                        rotate.setDuration(1000);//设置动画持续周期
                        rotate.setRepeatCount(-1);//设置重复次数
                        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                        rotate.setStartOffset(0);//执行前的等待时间
                        img_itemBackground_progress.setAnimation(rotate);
                        //开始下载
                        img_itemBackground_progress.setVisibility(View.VISIBLE);
                        img_itemBackground_state.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onComplete(String path) {
                        img_itemBackground_progress.clearAnimation();
                        img_itemBackground_progress.setVisibility(View.GONE);
                        lastPositionBackground = -1;
                        backgroundList.get(position).isDownload = true;
                        if (bgType == 0)
                            backgroundList.get(position).imageUrl = path;
                        else if (bgType == 2) {
                            backgroundList.get(position).videoUrl = path;
                        }
                        backgroundAdapter.setData(position, backgroundList.get(position));
                    }

                    @Override
                    public void onError(ApiException e) {
                        //下载失败
                        img_itemBackground_progress.clearAnimation();
                        img_itemBackground_progress.setVisibility(View.GONE);
                    }
                });
    }

    private void setRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = new TakePhotoImpl(this, this);
        }
        return takePhoto;
    }

    @Override
    public void takeSuccess(TResult result) {
        Log.i(TAG, "takeSuccess：" + result.getImage().getOriginalPath());
        String originalPath = result.getImage().getOriginalPath();
        if (originalPath == null)
            return;
        for (int i = 0; i < backgroundList.size(); i++) {
            backgroundList.get(i).isSelect = false;
        }
        backgroundAdapter.setNewData(backgroundList);
        lastPositionBackground = -1;
        videoEditManager.getModuleMaterialBean().bgPath = originalPath;
        declaimActivity.backGroundId = "";
        LogUtil.d(originalPath + "=====bgPath");
        videoEditManager.updateVirtualVideo();
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }
}
