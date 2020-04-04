package com.caishi.chaoge.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseApplication;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.MineDataBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.listener.OnUploadFileListener;
import com.caishi.chaoge.manager.UploadManager;
import com.caishi.chaoge.ui.adapter.CityAdapter;
import com.caishi.chaoge.ui.widget.MineDialog;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.Utils;
import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.model.TResult;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.caishi.chaoge.utils.ConstantUtils.EDIT_FLAG;

public class UserInfoActivity extends BaseUILocalDataActivity implements TakePhoto.TakeResultListener {
    @BindView(R.id.img_userInfo_head)
    ImageView img_userInfo_head;
    @BindView(R.id.tv_userInfo_name)
    TextView tv_userInfo_name;
    @BindView(R.id.rl_userInfo_name)
    RelativeLayout rl_userInfo_name;
    @BindView(R.id.tv_userInfo_sex)
    TextView tv_userInfo_sex;
    @BindView(R.id.rl_userInfo_sex)
    RelativeLayout rl_userInfo_sex;
    @BindView(R.id.tv_userInfo_age)
    TextView tv_userInfo_age;
    @BindView(R.id.rl_userInfo_age)
    RelativeLayout rl_userInfo_age;
    @BindView(R.id.tv_userInfo_city)
    TextView tv_userInfo_city;
    @BindView(R.id.rl_userInfo_city)
    RelativeLayout rl_userInfo_city;
    @BindView(R.id.tv_userInfo_sign)
    TextView tv_userInfo_sign;
    @BindView(R.id.rl_userInfo_sign)
    RelativeLayout rl_userInfo_sign;

    private TakePhoto takePhoto;
    private String filePath;
    private MineDataBean mineDataBean;
    private final int TYPE_SEX = 101;
    private final int TYPE_AGE = 102;
    private final int TYPE_AREA = 103;

    @OnClick({R.id.img_userInfo_head, R.id.rl_userInfo_name, R.id.rl_userInfo_sex,
            R.id.rl_userInfo_age, R.id.rl_userInfo_city, R.id.rl_userInfo_sign})
    public void onClickView(View v) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.img_userInfo_head://头像
                XXPermissions.with(mContext)
                        .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                        //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                        .permission(Permission.READ_EXTERNAL_STORAGE, Permission.CAMERA)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                showPhotoDialog();
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {

                            }
                        });
                break;
            case R.id.rl_userInfo_name://昵称
                bundle.putInt(EDIT_FLAG, 1);

                intent.setClass(this, EditActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.rl_userInfo_sex://性别
                showSexDialog();
                break;
            case R.id.rl_userInfo_age://出生日期
                showDateDialog();
                break;
            case R.id.rl_userInfo_city://城市
                intent.setClass(this, SelectProvinceActivity.class);
                startActivityForResult(intent, TYPE_AREA);
                break;
            case R.id.rl_userInfo_sign://个性签名
                bundle.putInt(EDIT_FLAG, 2);

                intent.setClass(this, EditActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "编辑资料";
    }

    @Override
    public int getSubLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    public void initPageData() {
        mineDataBean = SPUtils.readThirdAccountBind(this, BaseApplication.loginBean.userId);
        tv_userInfo_sex.setText("U".equals(mineDataBean.sex) ? "未填写" : mineDataBean.sex.equals("M")?"男":"女");
        tv_userInfo_age.setText(mineDataBean.age + "");
        tv_userInfo_city.setText(TextUtils.isEmpty(mineDataBean.area) ? "未选择" : mineDataBean.area);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mineDataBean = SPUtils.readThirdAccountBind(this, BaseApplication.loginBean.userId);
        tv_userInfo_name.setText(mineDataBean.nickname);
        tv_userInfo_sign.setText(mineDataBean.remark);
        if (mineDataBean.avatar.startsWith("/storage/")) {
            GlideUtil.loadCircleImg(mineDataBean.avatar, img_userInfo_head);
        } else {
            GlideUtil.loadCircleImg(Utils.isUrl(mineDataBean.avatar), img_userInfo_head);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
    public void takeSuccess(TResult result) {//拍照 选择图片成功返回
        Log.i(TAG, "takeSuccess：" + result.getImage().getOriginalPath());
        String originalPath = result.getImage().getOriginalPath();
        if (originalPath == null)
            return;
        mineDataBean.avatar = originalPath;
        SPUtils.writeThirdAccountBind(this, mineDataBean);
        GlideUtil.loadCircleImg(mineDataBean.avatar, img_userInfo_head);
        UploadManager uploadManager = new UploadManager(mContext);
        filePath = uploadManager.getObjKey(UploadManager.JPG);

        uploadManager.upload(filePath, new UploadManager.Body(originalPath), new OnUploadFileListener() {
            @Override
            public void success() {
                updateData(-1, "avatar", filePath);
            }

            @Override
            public void error(String msg) {
                LogUtil.i("上传———失败——原因---" + msg);
            }

            @Override
            public void progress(int progress, long currentSize, long totalSize) {
                LogUtil.i("上传中:  总大小=" + totalSize + "--进度=" + progress + "—当前大小=" + currentSize);
            }
        });


    }

    @Override
    public void takeFail(TResult result, String msg) {//拍照 选择图片失败返回
    }

    @Override
    public void takeCancel() {//拍照 选择图片取消返回
    }

    private void showDateDialog() {
        DatePickDialog dialog = new DatePickDialog(this);
        //设置上下年分限制
        dialog.setYearLimt(50);
        dialog.setStartDate(new Date(System.currentTimeMillis()));
        //设置标题
        dialog.setTitle("选择时间");
        //设置类型
        dialog.setType(DateType.TYPE_YMD);
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat("yyyy-MM-dd");
        //设置选择回调
//        dialog.setOnChangeLisener(new OnChangeLisener() {
//            @Override
//            public void onChanged(Date date) {
//
//
//            }
//        });
        //设置点击确定按钮回调
        dialog.setOnSureLisener(new OnSureLisener() {
            @Override
            public void onSure(Date date) {
//                String constellation = Utils.getConstellation(date);
                int age = Utils.getAge(date.getTime());
                tv_userInfo_age.setText(age+"");
                updateData(TYPE_AGE, "birthday", date.getTime() + "");
            }
        });
        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TYPE_AREA && data != null){
            String str= data.getStringExtra(CityAdapter.KEY_OF_CITY);
            tv_userInfo_city.setText(str);
            updateData(TYPE_AREA, "area", str);
        }else {
            getTakePhoto().onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 显示图片选择的弹框
     */
    private void showPhotoDialog() {

        MineDialog.newInstance().showSelectDialog(this, new IDialog.OnBuildListener() {
            @Override
            public void onBuildChildView(final IDialog dialog, View view, int layoutRes,FragmentManager fragmentManager) {
                Button btn_select_first = view.findViewById(R.id.btn_select_first);
                Button btn_select_second = view.findViewById(R.id.btn_select_second);
                Button btn_select_cancel = view.findViewById(R.id.btn_select_cancel);
                btn_select_first.setText("拍照");
                btn_select_second.setText("相册选择");
                btn_select_first.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        File file = new File(Environment.getExternalStorageDirectory(), "/ChaoGe/Image/" + System.currentTimeMillis() + ".jpg");
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                        Uri imageUri = Uri.fromFile(file);

                        takePhoto.onPickFromCapture(imageUri);//相机
                        dialog.dismiss();

                    }
                });
                btn_select_second.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //相册
//                        if("vivo".equalsIgnoreCase(SystemUtils.getDeviceBrand())
//                                || "OPPO".equalsIgnoreCase(SystemUtils.getDeviceBrand())
//                                || "OnePlus".equalsIgnoreCase(SystemUtils.getDeviceBrand())){
                        takePhoto.onPickFromPicture();
//                        }else {
//                            takePhoto.onPickFromGallery();
//                        }
                        dialog.dismiss();
                    }
                });
                btn_select_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });


            }
        });

    }


    /**
     * 显示性别选择的弹框
     */
    private void showSexDialog() {

        MineDialog.newInstance().showSelectDialog(this, new IDialog.OnBuildListener() {
            @Override
            public void onBuildChildView(final IDialog dialog, View view, int layoutRes,FragmentManager fragmentManager) {
                Button btn_select_first = view.findViewById(R.id.btn_select_first);
                Button btn_select_second = view.findViewById(R.id.btn_select_second);
                Button btn_select_cancel = view.findViewById(R.id.btn_select_cancel);
                btn_select_first.setText("男");
                btn_select_second.setText("女");
                btn_select_first.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tv_userInfo_sex.setText("男");
                        updateData(TYPE_SEX, "sex", "M");
                        dialog.dismiss();
                    }
                });
                btn_select_second.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tv_userInfo_sex.setText("女");
                        updateData(TYPE_SEX, "sex", "F");
                        dialog.dismiss();
                    }
                });
                btn_select_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });


            }
        });

    }


    private void updateData(final int type, String key, final String value) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put(key, value);

        HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.USER_INFO_UPDATE, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                if (Boolean.valueOf(response)) {
                    switch (type) {
                        case TYPE_SEX:
                            mineDataBean.sex = value;
                            break;
                        case TYPE_AGE:
                            mineDataBean.age = Utils.getAge(Long.valueOf(value));
                            break;
                        case TYPE_AREA:
                            mineDataBean.area = value;
                            break;
                    }
                    SPUtils.writeThirdAccountBind(UserInfoActivity.this, mineDataBean);
                }
            }

            @Override
            public void onFailure(String t) {
            }
        });
    }
}
