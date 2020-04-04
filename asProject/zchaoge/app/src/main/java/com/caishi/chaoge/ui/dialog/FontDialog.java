package com.caishi.chaoge.ui.dialog;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.FontBean;
import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.bean.ModuleMaterialBean;
import com.caishi.chaoge.bean.OriginalBean;
import com.caishi.chaoge.bean.TextBean;
import com.caishi.chaoge.request.ColorListRequest;
import com.caishi.chaoge.request.FontListRequest;
import com.caishi.chaoge.ui.adapter.FontEditAdapter;
import com.caishi.chaoge.ui.adapter.OriginalAdapter;
import com.caishi.chaoge.ui.adapter.TextColorEditAdapter;
import com.caishi.chaoge.ui.widget.dialog.BaseDialog;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.ui.widget.dialog.SYDialog;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.FileUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.caishi.chaoge.utils.ConstantUtils.DOWNLOAD_FONT_PATH;

public class FontDialog {

    private SYDialog fontDialog;
    private BaseActivity context;
    private boolean fontIsVisibility = false;
    private boolean colorIsVisibility = false;
    private FontEditAdapter fontEditAdapter;
    private TextColorEditAdapter textColorEditAdapter;
    private int lastPositionFont = -1;
    private ArrayList<OriginalBean> originalList;//传过来的数据整理之后的
    private OriginalAdapter originalAdapter;
    private ArrayList<TextBean> textBeanList;
    private List<FontBean> fontBeanList;
    private List<FontBean> requestFontList;
    private List<String> strings;

    private OnDialogKeyListener onDialogKeyListener;

    public static FontDialog newInstance() {
        return new FontDialog();
    }

    public void init(BaseActivity context) {
        this.context = context;
        getData();
    }

    /**
     * showFontDialog
     */
    public void showFontDialog(List<LrcBean> textInfoList, List<String> colorList,
                               String fontFilePath, boolean isEditor, final int classFlag) {

        final List<File> fontFiles = FileUtils.getAllFiles(DOWNLOAD_FONT_PATH);
        if (fontFiles.size() > 0) {
            for (int j = 0; j < fontFiles.size(); j++) {
                for (int i = 0; i < requestFontList.size(); i++) {
                    String path;
                    path = requestFontList.get(i).fontUrl;
                    requestFontList.get(i).isSelect = false;
                    String name = path.substring(path.lastIndexOf("/") + 1);
                    if (fontFiles.get(j).getName().equals(name)) {
                        requestFontList.get(i).isDownload = true;
                        requestFontList.get(i).fontUrl = fontFiles.get(j).getAbsolutePath();
                    }
                }
            }
        }
        fontBeanList = new ArrayList<>();
        fontBeanList.addAll(requestFontList);

        textBeanList = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            TextBean textBean = new TextBean();
            textBean.isSelect = false;
            textBean.itemType = 1;
            textBean.color = strings.get(i);
            textBeanList.add(textBean);
        }


        originalList = new ArrayList<>();
        if (null != textInfoList && textInfoList.size() > 0) {
            for (int i = 0; i < textInfoList.size(); i++) {
                OriginalBean originalBean = new OriginalBean();
                originalBean.text = textInfoList.get(i).getLrc();
                originalBean.isEditor = isEditor;
                originalBean.editFlag = classFlag;
                originalBean.startTime = textInfoList.get(i).getStart();
                originalBean.endTime = textInfoList.get(i).getEnd();
                if (colorList != null && colorList.size() > 0) {
                    originalBean.specialColor = colorList.get(i);
                }
                originalBean.isSelect = false;
                originalBean.fontPath = fontFilePath != null ? fontFilePath : "";
                originalList.add(originalBean);
            }
        }

        fontDialog = new SYDialog.Builder(context)
                .setDialogView(R.layout.dialog_font)
                .setScreenWidthP(1f)
                .setHeight(DisplayMetricsUtil.getScreenHeight(context) - ImmersionBar.getStatusBarHeight(context))
//                .setScreenHeightP(1f)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setWindowBackgroundP(0.8f)
                .setCancelableOutSide(false)
                .setAnimStyle(R.style.AnimUp)
                .setBuildChildListener(new IDialog.OnBuildListener() {
                    @Override
                    public void onBuildChildView(final IDialog dialog, View view, int layoutRes, final FragmentManager fragmentManager) {
                        ImmersionBar.with(fontDialog)
                                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                                .init();
                        initFontDialog(view, classFlag);
                    }
                }).show();
    }


    private void initFontDialog(View view, final int classFlag) {


        view.findViewById(R.id.ll_baseDialogTitle_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontDialog.dismiss();
                if (null != onDialogKeyListener)
                    onDialogKeyListener.onBack();

            }
        });
        ((TextView) view.findViewById(R.id.tv_baseDialogTitle_title)).setText("文字");
        view.findViewById(R.id.tv_baseDialogTitle_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontDialog.dismiss();
                if (null != onDialogKeyListener) {
                    List<String> colorList = new ArrayList<>();
                    List<LrcBean> lrcList = new ArrayList<>();

                    for (int i = 0; i < originalList.size(); i++) {
                        colorList.add(originalList.get(i).specialColor);
                        LrcBean lrcBean = new LrcBean();
                        lrcBean.setLrc(originalList.get(i).text);
                        lrcBean.setStart(originalList.get(i).startTime);
                        lrcBean.setEnd(originalList.get(i).endTime);
                        lrcList.add(lrcBean);
                    }

                    onDialogKeyListener.onAffirm(colorList, lrcList, originalList.get(0).fontPath);

                }

            }
        });
        final TextView tv_font_fontState = view.findViewById(R.id.tv_font_fontState);
        final TextView tv_font_fontColor = view.findViewById(R.id.tv_font_fontColor);
        final RecyclerView rv_font_info = view.findViewById(R.id.rv_font_info);

        originalAdapter = new OriginalAdapter(context.mContext);
        originalAdapter.setData(originalList);
        setRecyclerView(rv_font_info, originalAdapter);
        final RecyclerView rv_font_font = view.findViewById(R.id.rv_font_font);
        final RecyclerView rv_font_color = view.findViewById(R.id.rv_font_color);
        if (classFlag != -1) {
            switch (classFlag) {
                case 0://字体
                    tv_font_fontState.setSelected(true);
                    tv_font_fontState.setTextColor(Color.WHITE);
                    tv_font_fontColor.setTextColor(context.getResources().getColor(R.color._fe5175));
                    tv_font_fontColor.setSelected(false);
                    rv_font_color.setVisibility(View.GONE);
                    translateAnimationIn(rv_font_font, context.getResources().getDimension(R.dimen._118dp));
                    fontIsVisibility = true;
                    colorIsVisibility = false;
                    break;
                case 1://颜色
                    tv_font_fontState.setSelected(false);
                    tv_font_fontState.setTextColor(context.getResources().getColor(R.color._fe5175));
                    tv_font_fontColor.setTextColor(Color.WHITE);
                    tv_font_fontColor.setSelected(true);


                    rv_font_font.setVisibility(View.GONE);
                    translateAnimationIn(rv_font_color, context.getResources().getDimension(R.dimen._63dp));
                    fontIsVisibility = false;
                    colorIsVisibility = true;
                    break;

            }
        }
        tv_font_fontState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < originalList.size(); i++) {
                    originalList.get(i).editFlag = 0;
                }
                originalAdapter.notifyDataSetChanged();
                tv_font_fontState.setSelected(true);
                tv_font_fontState.setTextColor(Color.WHITE);
                tv_font_fontColor.setTextColor(context.getResources().getColor(R.color._fe5175));
                tv_font_fontColor.setSelected(false);

                if (fontIsVisibility) {
                    translateAnimationOut(rv_font_font, context.getResources().getDimension(R.dimen._118dp));
                    fontIsVisibility = false;
                    return;
                }

                rv_font_color.setVisibility(View.INVISIBLE);
                translateAnimationIn(rv_font_font, context.getResources().getDimension(R.dimen._118dp));
                fontIsVisibility = true;
                colorIsVisibility = false;
            }
        });
        tv_font_fontColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < originalList.size(); i++) {
                    originalList.get(i).editFlag = 1;
                }
                originalAdapter.notifyDataSetChanged();

                tv_font_fontState.setSelected(false);
                tv_font_fontState.setTextColor(context.getResources().getColor(R.color._fe5175));
                tv_font_fontColor.setTextColor(Color.WHITE);
                tv_font_fontColor.setSelected(true);
                if (colorIsVisibility) {
                    translateAnimationOut(rv_font_color, context.getResources().getDimension(R.dimen._63dp));
                    colorIsVisibility = false;
                    return;
                }
                rv_font_font.setVisibility(View.INVISIBLE);
                translateAnimationIn(rv_font_color, context.getResources().getDimension(R.dimen._63dp));
                fontIsVisibility = false;
                colorIsVisibility = true;
            }
        });
        fontEditAdapter = new FontEditAdapter(context);
        textColorEditAdapter = new TextColorEditAdapter();
        fontEditAdapter.bindToRecyclerView(rv_font_font);
        setRecyclerView(rv_font_font, fontEditAdapter);
        setRecyclerView(rv_font_color, textColorEditAdapter);
        fontEditAdapter.setNewData(fontBeanList);
        textColorEditAdapter.setNewData(textBeanList);
        fontEditAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                fontBeanList = fontEditAdapter.getData();
                if (lastPositionFont == position)
                    return;
                ProgressBar pb_itemFont_progress = (ProgressBar) fontEditAdapter.getViewByPosition(position, R.id.pb_itemFont_progress);
                if (fontBeanList.get(position).isDownload) {
                    String path = "";
                    path = fontBeanList.get(position).fontUrl;
                    for (int i = 0; i < fontBeanList.size(); i++) {
                        if (fontBeanList.get(i).isSelect) {
                            fontBeanList.get(i).isSelect = false;
                            fontEditAdapter.notifyItemChanged(i);
                        }
                        fontBeanList.get(i).isSelect =
                                (position == i);
                    }
                    if (fontBeanList.get(position).isDownload)
                        fontEditAdapter.notifyItemChanged(position);
                    else
                        fontEditAdapter.notifyDataSetChanged();
                    for (int i = 0; i < originalList.size(); i++) {
                        originalList.get(i).fontPath = path;
                    }
                    originalAdapter.notifyDataSetChanged();
                } else {
                    downloadFont(fontBeanList.get(position).fontUrl, pb_itemFont_progress, position);
                }
                lastPositionFont = position;


            }
        });
        textColorEditAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<TextBean> data = textColorEditAdapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    data.get(i).isSelect = i == position;
                }
                for (int i = 0; i < originalList.size(); i++) {
                    if (originalList.get(i).isSelect) {
                        originalList.get(i).specialColor = data.get(position).color;
                        originalList.get(i).isSelect = false;
                    }
                }
                originalAdapter.notifyDataSetChanged();
                textColorEditAdapter.notifyDataSetChanged();
            }
        });
        originalAdapter.setOnUpdateListener(new OriginalAdapter.OnUpdateListener() {
            @Override
            public void onUpdate(ArrayList<OriginalBean> list) {
                originalList = list;
            }
        });
        fontDialog.setOnBackListener(new BaseDialog.OnBackListener() {//dialog时返回按钮监听
            @Override
            public void onBack(boolean isBack) {
                if (isBack) {
                    if (null != onDialogKeyListener)
                        onDialogKeyListener.onBack();
                }
            }
        });

    }


    private void translateAnimationIn(RecyclerView recyclerView, float end) {
        recyclerView.setVisibility(View.VISIBLE);
        // 获得当前按钮的位置
        ObjectAnimator animator = ObjectAnimator.ofFloat(recyclerView, "translationX",
                DisplayMetricsUtil.getScreenWidth(context),
                DisplayMetricsUtil.getScreenWidth(context) - end);
        animator.setDuration(300);
        animator.start();
    }

    private void translateAnimationOut(RecyclerView recyclerView, float start) {
        recyclerView.setVisibility(View.VISIBLE);
        // 获得当前按钮的位置
        ObjectAnimator animator = ObjectAnimator.ofFloat(recyclerView, "translationX",
                DisplayMetricsUtil.getScreenWidth(context) - start,
                DisplayMetricsUtil.getScreenWidth(context));
        animator.setDuration(300);
        animator.start();
    }

    private void setRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
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
                        fontEditAdapter.setData(position, fontBeanList.get(position));
                    }

                    @Override
                    public void onError(ApiException e) {
                        //下载失败
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    public void setOnDialogKeyListener(OnDialogKeyListener onDialogKeyListener) {
        this.onDialogKeyListener = onDialogKeyListener;
    }

    public void getData() {
        FontListRequest.newInstance(context).getFontList(new BaseRequestInterface<ArrayList<FontBean>>() {
            @Override
            public void success(int state, String msg, ArrayList<FontBean> fontBeans) {
                requestFontList = fontBeans;
            }

            @Override
            public void error(int state, String msg) {

            }
        });
        ColorListRequest.newInstance(context).getColorList(new BaseRequestInterface<ArrayList<String>>() {
            @Override
            public void success(int state, String msg, ArrayList<String> strList) {
                strings = strList;
            }

            @Override
            public void error(int state, String msg) {

            }
        });
    }


    public interface OnDialogKeyListener {
        void onBack();


        void onAffirm(List<String> colorList, List<LrcBean> lrcList, String fontPath);

    }


    /**
     * 关闭弹窗 注意dialog=null;防止内存泄漏
     */
    public void dismissDialog() {
        if (fontDialog != null) {
            fontDialog.dismiss();
            fontDialog = null;
        }
    }


}
