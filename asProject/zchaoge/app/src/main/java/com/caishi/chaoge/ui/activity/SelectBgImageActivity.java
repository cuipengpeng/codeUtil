package com.caishi.chaoge.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.ClassListBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.adapter.ScenarioPagerAdapter;
import com.caishi.chaoge.ui.fragment.SelectBgImageFragment;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.SystemUtils;
import com.caishi.chaoge.utils.ToastUtils;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.model.InvokeParam;
import org.devio.takephoto.model.TContextWrap;
import org.devio.takephoto.model.TException;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.permission.InvokeListener;
import org.devio.takephoto.permission.PermissionManager;
import org.devio.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;

public class SelectBgImageActivity extends BaseUILocalDataActivity implements TakePhoto.TakeResultListener, InvokeListener, SelectBgImageFragment.OnSelectImageListener {
    @BindView(R.id.stl_SelectBgImage_tabLayout)
    SlidingTabLayout stl_selectBgImage_tabLayout;
    @BindView(R.id.vp_SelectBgImage_list)
    ViewPager vp_SelectBgImage_list;
    @BindView(R.id.img_SelectBgImage_album)
    public ImageView img_SelectBgImage_album;
    @BindView(R.id.img_SelectBgImage_photo)
    public ImageView img_SelectBgImage_photo;

    private List<ClassListBean> classListBeanList;
    private ArrayList<Fragment> fragmentList;
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private String path = "";
    private String backGroundId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            getTakePhoto().onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
//            ToastUtils.show(this, "选择文件类型不匹配，请重新选择");
            e.printStackTrace();
        }
    }

    @Override
    protected String getPageTitle() {
        return "背景";
    }

    @Override
    public int getSubLayoutId() {
        return R.layout.activity_select_bg_image;
    }

    @Override
    public void initPageData() {
        baseRightMenuTextView.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) baseRightMenuTextView.getLayoutParams();
        layoutParams.width = DisplayMetricsUtil.dip2px(this, 60);
        layoutParams.height = DisplayMetricsUtil.dip2px(this, 30);
        baseRightMenuTextView.setLayoutParams(layoutParams);
        baseRightMenuTextView.setTextColor(Color.WHITE);
        baseRightMenuTextView.setBackgroundResource(R.drawable.selector_transcribe_next);
        baseRightMenuTextView.setText("完成");

        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("type", "backGround");

        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.GET_CLASS_LIST, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                List<ClassListBean> classListBeans = gson.fromJson(response, new TypeToken<List<ClassListBean>>() {
                }.getType());
                classListBeanList = classListBeans;
                ArrayList<String> strings = new ArrayList<>();
                fragmentList = new ArrayList<>();
                for (int i = 0; i < classListBeanList.size(); i++) {
                    fragmentList.add(SelectBgImageFragment.newInstance(classListBeanList.get(i).num, i));
                    ((SelectBgImageFragment) (fragmentList.get(i))).setOnSelectImageListener(SelectBgImageActivity.this);
                    strings.add(classListBeanList.get(i).name);
                }
                ScenarioPagerAdapter scenarioPagerAdapter = new ScenarioPagerAdapter(getSupportFragmentManager(), fragmentList);
                vp_SelectBgImage_list.setAdapter(scenarioPagerAdapter);
                vp_SelectBgImage_list.setCurrentItem(0);
                stl_selectBgImage_tabLayout.setViewPager(vp_SelectBgImage_list, strings.toArray(new String[strings.size()]));

            }

            @Override
            public void onFailure(String t) {
            }
        });
    }

    @OnClick({R.id.img_SelectBgImage_album, R.id.img_SelectBgImage_photo, R.id.tv_base_rightMenu})
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.tv_base_rightMenu:
                Intent intent = new Intent();
                intent.putExtra("path", path);
                intent.putExtra("backGroundId", backGroundId);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.img_SelectBgImage_album:
                if ("vivo".equalsIgnoreCase(SystemUtils.getDeviceBrand())
                        || "OPPO".equalsIgnoreCase(SystemUtils.getDeviceBrand())
                        || "OnePlus".equalsIgnoreCase(SystemUtils.getDeviceBrand())) {
                    try {
                        getTakePhoto().onPickFromDocuments();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getTakePhoto().onPickFromGallery();
                }
                break;
            case R.id.img_SelectBgImage_photo:
                Uri uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), System.currentTimeMillis() + ".png"));
                getTakePhoto().onPickFromCapture(uri);
                break;

        }


    }

    @Override
    public void takeSuccess(TResult result) {
        backGroundId = "";
        String path = result.getImage().getOriginalPath();
        Intent intent = new Intent();
        intent.putExtra("path", path);
        intent.putExtra("backGroundId", backGroundId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public void onSelectImage(int position, String path,String backGroundId) {
        this.path = path;
        this.backGroundId = backGroundId;
        for (int i = 0; i < fragmentList.size(); i++) {
            if (i != position) {
                ((SelectBgImageFragment) (fragmentList.get(i))).upData();
            }
        }
    }
}
