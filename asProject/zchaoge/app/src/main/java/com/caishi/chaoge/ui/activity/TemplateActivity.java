package com.caishi.chaoge.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseFragment;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.ClassListBean;
import com.caishi.chaoge.bean.RecommendBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.listener.OnUploadFileListener;
import com.caishi.chaoge.manager.UploadManager;
import com.caishi.chaoge.request.GetClassListRequest;
import com.caishi.chaoge.request.RecommendTemplateListRequest;
import com.caishi.chaoge.ui.adapter.TemplateBannerAdapter;
import com.caishi.chaoge.ui.adapter.TemplateFragmentPagerAdapter;
import com.caishi.chaoge.ui.adapter.TemplateListAdapter;
import com.caishi.chaoge.ui.fragment.TemplateFragment;
import com.caishi.chaoge.ui.widget.banner.BannerLayout;
import com.caishi.chaoge.ui.widget.dialog.BaseDialog;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.ui.widget.dialog.SYDialog;
import com.caishi.chaoge.utils.BitmapUtils;
import com.caishi.chaoge.utils.ConstantUtils;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.FileUtils;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.SystemUtils;
import com.caishi.chaoge.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.tablayout.SlidingTabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.othershe.library.NiceImageView;
import com.xiao.nicevideoplayer.NiceVideoPlayer;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.model.TResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateActivity extends BaseActivity implements TakePhoto.TakeResultListener {


    private BannerLayout bl_template_layout;
    private RecyclerView rv_template_templateList;
    private TemplateListAdapter templateListAdapter;
    private List<BaseFragment> fragmentList;
    private SYDialog.Builder dialog;
    private int measuredHeight = 0;
    private TemplateBannerAdapter templateBannerAdapter;
    private int lastPosition = -1;
    private ImageView nImg_template_album;
    public String photoPath;
    private int editFlag = 0;//用来区分模板还是原创  0 模板   1原创
    private SYDialog showDialog;
    private TakePhotoImpl takePhoto;
    private ImmersionBar immersionBar;

    @Override
    public int bindLayout() {
        return R.layout.activity_template;
    }

    @Override
    public void initView(View view) {
        immersionBar = ImmersionBar.with(this).titleBar(R.id.view_template);
        immersionBar.statusBarColor(R.color.white)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
        setBaseTitle("模板", Color.WHITE, "模板库", getResources().getColor(R.color._fe5175), true);
        bl_template_layout = $(R.id.bl_template_layout);
        rv_template_templateList = $(R.id.rv_template_templateList);
        $(R.id.btn_template_start).setOnClickListener(this);
        nImg_template_album = $(R.id.nImg_template_album);
        showDialog();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        measuredHeight = bl_template_layout.getMeasuredHeight();
    }


    @Override
    public void doBusiness() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rv_template_templateList.setLayoutManager(linearLayoutManager);
        templateListAdapter = new TemplateListAdapter();
        rv_template_templateList.setAdapter(templateListAdapter);
        Map<String, Object> map = new HashMap<>();
        map.put("pageSize", 30);
        map.put("since", -1);
        map.put("slipType", ConstantUtils.DOWN);
        RecommendTemplateListRequest.newInstance(mContext).recommend(map, new BaseRequestInterface<ArrayList<RecommendBean>>() {
            @Override
            public void success(int state, String msg, ArrayList<RecommendBean> recommendBeans) {
                recommendBeans.get(0).isSelect = true;
                templateBannerAdapter = new TemplateBannerAdapter(mContext, measuredHeight);
                templateBannerAdapter.setNewData(recommendBeans);
                bl_template_layout.setAdapter(templateBannerAdapter);
                if (null != bl_template_layout.mRecyclerView)
                    templateBannerAdapter.bindToRecyclerView(bl_template_layout.mRecyclerView);
                templateListAdapter.setNewData(recommendBeans);
                templateBannerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        if (position != bl_template_layout.currentIndex)
                            bl_template_layout.setScrollToPosition(position);

                    }
                });
            }

            @Override
            public void error(int state, String msg) {

            }
        });
        getPhoto();
    }

    private void getPhoto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> systemPhotoList = FileUtils.getSystemPhotoList(mContext);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != systemPhotoList) {
                            photoPath = systemPhotoList.get(systemPhotoList.size() - 1);
                            Bitmap circleBitmapByShader =
                                    BitmapUtils.getCircleBitmapByShader(BitmapFactory.decodeFile(photoPath), 200, 200, 10);
                            GlideUtil.loadImg(circleBitmapByShader, nImg_template_album);
                        }
                    }
                });
            }
        }).start();


    }

    @Override
    public void onSaveClick(View v) {
        super.onSaveClick(v);
        showDialog = dialog.show();
        showDialog.setOnBackListener(new BaseDialog.OnBackListener() {
            @Override
            public void onBack(boolean isBack) {
                if (isBack)
                    setFullScreen(true);
            }
        });
        setFullScreen(false);
    }

    @Override
    public void setListener() {
        nImg_template_album.setOnClickListener(this);
        templateListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                bl_template_layout.setScrollToPosition(position);
                List<RecommendBean> templateListBeanArrayList = templateListAdapter.getData();
                for (int i = 0; i < templateListBeanArrayList.size(); i++) {
                    templateListBeanArrayList.get(i).isSelect = i == position;
                }

                templateListAdapter.setNewData(templateListBeanArrayList);


            }
        });

        bl_template_layout.setOnBannerItemScrollListener(new BannerLayout.OnBannerItemScrollListener() {
            @Override
            public void onScroll(int position) {
                if (lastPosition == position)
                    return;
                rv_template_templateList.scrollToPosition(position);
                if (lastPosition != -1) {
                    NiceVideoPlayer viewByPosition = (NiceVideoPlayer) templateBannerAdapter.getViewByPosition(lastPosition, R.id.video_banner_play);
                    ImageView img_banner_image = (ImageView) templateBannerAdapter.getViewByPosition(lastPosition, R.id.img_banner_image);
                    ImageView img_banner_thumb = (ImageView) templateBannerAdapter.getViewByPosition(lastPosition, R.id.img_banner_thumb);
                    if (null != viewByPosition)
                        viewByPosition.release();
                    if (null != img_banner_thumb)
                        img_banner_thumb.setVisibility(View.VISIBLE);
                    if (null != img_banner_image) {
                        img_banner_image.setVisibility(View.VISIBLE);
                        GlideUtil.loadImg(Utils.isUrl(templateBannerAdapter.getData().get(lastPosition).modelCover), img_banner_image);
                    }
                }
                List<RecommendBean> templateListData = templateListAdapter.getData();
                for (int i = 0; i < templateListData.size(); i++) {
                    templateListData.get(i).isSelect = i == position;
                }
                templateListAdapter.setNewData(templateListData);
                lastPosition = position;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != templateBannerAdapter) {
            NiceVideoPlayer viewByPosition = (NiceVideoPlayer) templateBannerAdapter.getViewByPosition(lastPosition, R.id.video_banner_play);
            ImageView img_banner_thumb = (ImageView) templateBannerAdapter.getViewByPosition(lastPosition, R.id.img_banner_thumb);
            if (null != viewByPosition && viewByPosition.isPlaying()) {
                viewByPosition.pause();
                if (null != img_banner_thumb)
                    img_banner_thumb.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {

            case R.id.btn_template_start:
                String modelId = "";
                List<RecommendBean> data = templateListAdapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).isSelect) {
                        modelId = data.get(i).modelId;
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putString("photoPath", photoPath);
                bundle.putString("modelId", modelId);
                bundle.putInt("editFlag", editFlag);
                Utils.umengStatistics(mContext, "lz0006");//开始配音
                startActivity(TemplateEditActivity.class, bundle);

                break;
            case R.id.nImg_template_album:

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


                break;

        }

    }


    private void showDialog() {
        dialog = new SYDialog.Builder(mContext)
                .setDialogView(R.layout.dialog_template_library)
                .setScreenWidthP(1f)
                .setHeight(DisplayMetricsUtil.getScreenHeight(mContext) - ImmersionBar.getStatusBarHeight(this))
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setCancelableOutSide(true)
                .setAnimStyle(R.style.AnimUp)
                .setBuildChildListener(new IDialog.OnBuildListener() {

                    @Override
                    public void onBuildChildView(final IDialog dialog, View view, int layoutRes, final FragmentManager fragmentManager) {
                        view.findViewById(R.id.ll_baseDialogTitle_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setFullScreen(true);
                                dialog.dismiss();
                            }
                        });
                        ((TextView) view.findViewById(R.id.tv_baseDialogTitle_title)).setText("模板库");
                        final ViewPager vp_dialogTemplate_templateList = view.findViewById(R.id.vp_dialogTemplate_templateList);
                        final SlidingTabLayout stl_dialogTemplate_tab = view.findViewById(R.id.stl_dialogTemplate_tab);
                        final TemplateFragmentPagerAdapter templateFragmentPagerAdapter = new TemplateFragmentPagerAdapter(fragmentManager);
                        vp_dialogTemplate_templateList.setAdapter(templateFragmentPagerAdapter);
                        GetClassListRequest.newInstance(mContext).getClassList("model", new BaseRequestInterface<ArrayList<ClassListBean>>() {
                            @Override
                            public void success(int state, String msg, ArrayList<ClassListBean> classListBeans) {
                                fragmentList = new ArrayList<>();
                                if (null != classListBeans) {
                                    String[] strings = new String[classListBeans.size()];
                                    for (int i = 0; i < classListBeans.size(); i++) {
                                        fragmentList.add(TemplateFragment.newInstance(classListBeans.get(i).num));
                                        strings[i] = classListBeans.get(i).name;
                                    }
                                    templateFragmentPagerAdapter.update(true, fragmentList);
                                    vp_dialogTemplate_templateList.setCurrentItem(0);
                                    stl_dialogTemplate_tab.setViewPager(vp_dialogTemplate_templateList, strings);
                                    vp_dialogTemplate_templateList.setOffscreenPageLimit(classListBeans.size());

                                }
                            }

                            @Override
                            public void error(int state, String msg) {

                            }
                        });
                        view.findViewById(R.id.tv_baseDialogTitle_save).setVisibility(View.GONE);


                    }
                });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
        LogUtil.i("takeSuccess：" + result.getImage().getOriginalPath());
        String originalPath = result.getImage().getOriginalPath();
        if (originalPath == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putString("photoPath", photoPath);
        bundle.putString("originalPath", originalPath);
        bundle.putInt("editFlag", 1);
        Utils.umengStatistics(mContext, "lz0006");//开始配音
        startActivity(TemplateEditActivity.class, bundle);


    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    public void setFullScreen(boolean fullScreen) {
        immersionBar.statusBarColor(fullScreen ? R.color.white : R.color.colorBlack)
                .statusBarDarkFont(fullScreen)   //状态栏字体是深色，不写默认为亮色
                .init();


    }
}
