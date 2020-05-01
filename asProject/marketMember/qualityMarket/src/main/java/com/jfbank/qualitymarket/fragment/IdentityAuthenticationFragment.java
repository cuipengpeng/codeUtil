//package com.jfbank.qualitymarket.fragment;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.res.Configuration;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.hardware.Camera;
//import android.media.ExifInterface;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.jfbank.qualitymarket.AppContext;
//import com.jfbank.qualitymarket.R;
//import com.jfbank.qualitymarket.activity.ApplyAmountActivity;
//import com.jfbank.qualitymarket.activity.LoginActivity;
//import com.jfbank.qualitymarket.activity.MainActivity;
//import com.jfbank.qualitymarket.base.BaseFragment;
//import com.jfbank.qualitymarket.dao.StoreService;
//import com.jfbank.qualitymarket.model.IdentityInitBean;
//import com.jfbank.qualitymarket.model.IdentityInitBean.DataBean;
//import com.jfbank.qualitymarket.model.SaveBaseInfoBean;
//import com.jfbank.qualitymarket.net.HttpRequest;
//import com.jfbank.qualitymarket.util.ConstantsUtil;
//import com.jfbank.qualitymarket.util.DensityUtil;
//import com.jfbank.qualitymarket.util.FaceDetectUtil;
//import com.jfbank.qualitymarket.util.IDCard;
//import com.jfbank.qualitymarket.util.LogUtil;
//import com.jfbank.qualitymarket.util.StringUtil;
//import com.jfbank.qualitymarket.util.UserUtils;
//import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
//import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
// import java.util.HashMap; import java.util.Map;
//import com.sensetime.stlivenesslibrary.ui.LivenessActivity;
//
//import org.apache.http.Header;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import static android.app.Activity.RESULT_CANCELED;
//import static android.app.Activity.RESULT_FIRST_USER;
//import static com.jfbank.qualitymarket.AppContext.user;
//
///**
// * 实名认证对应的页面
// *
// * @author 彭爱军
// * @date 2016年8月10日
// */
//public class IdentityAuthenticationFragment extends BaseFragment implements OnClickListener {
//    public static final String TAG = IdentityAuthenticationFragment.class.getName();
//    private TextView detectListText, versionText, resultNote;
//    private ImageView livenessIcon;
//    private LinearLayout setDetectList;
//    private static final int SET_DETECT_LIST_REQUEST_CODE = 0;
//    private static final int START_DETECT_REQUEST_CODE = 1;
//    private String sequence;
//    private static final int PERMISSION_REQUEST = 0;
//    private RelativeLayout resultLayout;
//    public static String storageFolder;
//    private Thread mThread = null;
//    private final int MSGTYPE_CAMERA_CAN_USE = 1;
//    private final int MSGTYPE_CAMERA_CAN_NOT_USE = 0;
//    private static final String[] REQUEST_PERMISSIONS = new String[]{
//            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE};
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSGTYPE_CAMERA_CAN_USE:
//                    startLiveness();
//                    break;
//                case MSGTYPE_CAMERA_CAN_NOT_USE:
//                    AppContext.user.setFaceCode("");
//                    onResume();
//                    Toast.makeText(getActivity(), "调用摄像头权限被拒绝，不能启动活体检测。",
//                            Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
//
//    private final float faceDetectSdkError = 101f;
//    private final String failToFaceDetectScore = "0.1";
//    private String fileId = "";
//    private float faceCode = 0f;
//    private String errorInfo = "";
//    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";// temp
//    // Environment.getExternalStorageDirectory()+"/temp.jpg"
//    // file
//    private  String imageFilePath = "";
//
//
//    /**
//     * 姓名
//     */
//    private EditText mBasic_info_et_name;
//    /**
//     * 身份证号码
//     */
//    private EditText mBasic_info_et_idcard;
//
//    private ApplyAmountActivity mContext;
//    /**
//     * 上传身份证正面
//     */
//    private ImageView mIdentity_authentication_iv_id_front;
//    /**
//     * 上传身份证反面
//     */
//    private ImageView mIdentity_authentication_iv_id_back;
//    /**
//     * 人脸识别的对应状态的图标
//     */
//    private ImageView mIdentity_authentication_iv_face;
//    /**
//     * 表示可以进行人脸识别人图标
//     */
//    private ImageView identity_face_iv_next;
//    /**
//     * 认证结果
//     */
//    private TextView identity_face_tv_result;
//    /**
//     * 认证提示
//     */
//    private TextView identity_face_tv_hint;
//    /**
//     * 点击认证
//     */
//    private RelativeLayout identity_authentication_rl_face;
//    /**
//     * 提交申请额度
//     */
//    private Button mIdentity_authentication_btn_apply;
//    private View view;
//    private OnClickBtnListen mOnClickBtnListen;
//    private final int TAKE_BIG_PICTURE = 101;
//    /**
//     * 标识拍照的类型。
//     */
//    private int flag = -1;
//    private static int screenWidth;
//
//    private String imageStorageDir = Environment.getExternalStorageDirectory().toString() + "/Note";
//
//    private String fileCorrectPaht = "correct.jpg"; // 身份证正面/sdcard/Note//
//    private String fileOppositePaht = "opposite.jpg"; // 身份证反面
//    private String mCorrectFileId /* = "014eb7e1-ca4e-4c34-9c5c-8f7be496bb79" */; // 身份证文件的id
//    private String mOppositeFileId /*
//                                     * = "c2d69d56-089f-4798-aa9a-1f7a26a20d38"
//									 */; // 反面的id
//    private String isFinish; // 标志是否
//    /**
//     * 网编请求时加载框
//     */
//    private LoadingAlertDialog mDialog;
//    private Bitmap bitmap;
//    private Bitmap uploadBitmap;
//    private int screenHeight;
//    private String name;
//    private String idcard;
//    private boolean isGetID = true;        //是否直接获取id
//
//    /**
//     * 添加一个接口,设置点击监听的回调
//     */
//    public interface OnClickBtnListen {
//
//        public void next(int step); // 进入下一个流程
//    }
//
//    public void setmOnClickBtnListen(OnClickBtnListen mOnClickBtnListen) {
//        this.mOnClickBtnListen = mOnClickBtnListen;
//    }
//
//    @SuppressLint("NewApi")
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.e("TAG", "onCreate:" + (null == savedInstanceState));
//        mContext = (ApplyAmountActivity) getActivity();
//        AppContext.user.setFaceCode("");
//        MainActivity.checkPermission(getActivity(), ConstantsUtil.PERMISSION_CAMERA_REQUEST_CODE);
//        /*
//		 * InputMethodManager imm = (InputMethodManager)
//		 * mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//		 * imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
//		 * InputMethodManager.SHOW_FORCED);
//		 */
//    }
//
//    /**
//     * 通过地址获取图片
//     *
//     * @param pathString
//     * @return
//     */
//    private Bitmap getDiskBitmap(String pathString) {
//        Bitmap bitmap = null;
//        try {
//            File file = new File(pathString);
//            if (file.exists()) {
//                bitmap = BitmapFactory.decodeFile(pathString);
//            }
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//
//        return bitmap;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_identity_authentication, container, false);
//        bindViews();
//        setOnClickListen();
//        getDisplayWidth();
//        storageFolder = FaceDetectUtil.storageFolder;
//
//        File dir= new File(imageStorageDir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        return view;
//    }
//
//    /**
//     * 获取屏幕的宽度
//     */
//    private void getDisplayWidth() {
//        Resources resources = this.getResources();
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        screenWidth = dm.widthPixels;
//        screenHeight = dm.heightPixels;
//
//        Log.e("TAG", "screenWidth:" + screenWidth);
//    }
//
//    /**
//     * 设置点击监听
//     */
//    private void setOnClickListen() {
//        mIdentity_authentication_iv_id_front.setOnClickListener(this);
//        mIdentity_authentication_iv_id_back.setOnClickListener(this);
//        identity_authentication_rl_face.setOnClickListener(this);
//        mIdentity_authentication_btn_apply.setOnClickListener(this);
//
//    }
//
//    /**
//     * 网络请求实名获取接口
//     */
//    private void requestGetRealNameInfo() {
//        if (null == mDialog) {
//            mDialog = new LoadingAlertDialog(mContext);
//        }
//        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
//
//        Map<String,String> params = new HashMap<>();
//
//        params.put("ver", AppContext.getAppVersionName(mContext));
//        params.put("Plat", ConstantsUtil.PLAT);
//
//        params.put("uid", user.getUid());
//        params.put("token", user.getToken());
//
//        Log.e("TAG", params.toString());
//
//        HttpRequest.getRealNameInfo(mContext,params, new AsyncResponseCallBack() {
//
//            @Override
//            public void onResult(String arg2) {
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                if (null != arg2 &&  arg2.length()> 0) {
//                    Log.e("TAG", new String(arg2));
//                    explainJsonGetRealNameInfo(new String(arg2));
//                }
//
//            }
//
//            @Override
//            public void onFailed(String path, String msg) {
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                Toast.makeText(mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//    /**
//     * 设置实名认证身份的信息
//     *
//     * @param data
//     */
//    private void setRealNameInfo(DataBean data) {
//        isGetID = data.isNeedEnterNumber();
//        if (data.isNeedEnterNumber()) { // 为true需要手动输入身份相关信息
//            mBasic_info_et_name.setFocusable(true);
//            mBasic_info_et_idcard.setFocusable(true);
//        } else {
//            mBasic_info_et_name.setFocusable(false);
//            mBasic_info_et_idcard.setFocusable(false);
//            mBasic_info_et_name.setText(data.getIdName());
//
//            idcard = data.getIdNumber();
//            Log.e("TAG", "111:" + idcard);
//            mBasic_info_et_idcard.setText(idcard.subSequence(0, 3) + "***********" + idcard.substring(14, idcard.length()));
//        }
//    }
//
//    /**
//     * 解释获取实名初始化的json数据
//     *
//     * @param json
//     */
//    protected void explainJsonGetRealNameInfo(String json) {
//        IdentityInitBean bean = JSON.parseObject(json, IdentityInitBean.class);
//        if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
//            setRealNameInfo(bean.getData());
//        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
//            UserUtils.tokenFailDialog(mContext,bean.getStatusDetail(),null);
//        } else {
//            Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * 初始化View
//     */
//    private void bindViews() {
//        mIdentity_authentication_iv_id_front = (ImageView) view.findViewById(R.id.identity_authentication_iv_id_front);
//        mIdentity_authentication_iv_id_back = (ImageView) view.findViewById(R.id.identity_authentication_iv_id_back);
//        mIdentity_authentication_btn_apply = (Button) view.findViewById(R.id.identity_authentication_btn_apply);
//
//        mIdentity_authentication_iv_face = (ImageView) view.findViewById(R.id.identity_authentication_iv_face);
//        identity_authentication_rl_face = (RelativeLayout) view.findViewById(R.id.identity_authentication_rl_face);
//        identity_face_tv_result = (TextView) view.findViewById(R.id.identity_face_tv_result);
//        identity_face_tv_hint = (TextView) view.findViewById(R.id.identity_face_tv_hint);
//        identity_face_iv_next = (ImageView) view.findViewById(R.id.identity_face_iv_next);
//        // mIdentity_authentication_btn_apply = (Button)
//        // view.findViewById(R.id.base_text1);
//
//        mBasic_info_et_name = (EditText) view.findViewById(R.id.basic_info_et_name);
//        mBasic_info_et_idcard = (EditText) view.findViewById(R.id.basic_info_et_idcard);
//
//        identity_face_tv_result.requestFocus();
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        isFinish += flag;
//        switch (v.getId()) {
//            case R.id.identity_authentication_iv_id_front: // 身份证正面
//                // Toast.makeText(mContext, "点击了身份证正面", Toast.LENGTH_SHORT).show();
//                Log.e("TAG", "fileCorrectPaht:" + fileCorrectPaht);
//                if (!isStorage()) {
//                    Toast.makeText(mContext, "亲，你没有插入SD卡", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//                flag = 1;
//                if (ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions((Activity) getActivity(),
//                            new String[]{Manifest.permission.CAMERA},
//                            ConstantsUtil.PERMISSION_CAMERA_REQUEST_CODE);
//                } else {
//                    doTakePhoto();
//                }
//                break;
//            case R.id.identity_authentication_iv_id_back: // 身份证反面
//                // Toast.makeText(mContext, "点击了身份证反面", Toast.LENGTH_SHORT).show();
//                if (!isStorage()) {
//                    Toast.makeText(mContext, "亲，你没有插入SD卡", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//                flag = 2;
//                if (ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions((Activity) getActivity(),
//                            new String[]{Manifest.permission.CAMERA},
//                            ConstantsUtil.PERMISSION_CAMERA_REQUEST_CODE);
//                } else {
//                    doTakePhoto();
//                }
//                break;
//            case R.id.identity_authentication_rl_face: // 人脸认别认证
//                if (!submitBeforeExamine(false)) {
//                    break;
//                }
//                faceDetect();
//                break;
//            case R.id.identity_authentication_btn_apply: // 提交额度申请。
//                // Toast.makeText(mContext, "点击了提交额度申请", Toast.LENGTH_SHORT).show();
//                if (!submitBeforeExamine(true)) {
//                    break;
//                }
//                requestBitmapFileID();
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 人脸识别检测
//     */
//    public void faceDetect() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                // Request the permission. The result will be received
//                // in onRequestPermissionResult()
//                requestPermissions(
//                        REQUEST_PERMISSIONS,
//                        PERMISSION_REQUEST);
//            } else {
//                // Permission is already available, start camera preview
//                startLiveness();
//            }
//        } else {
//            if (mThread != null && mThread.isAlive()) {
//                return;
//            } else {
//                mThread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (cameraIsCanUse()) {
//                            handler.sendEmptyMessage(MSGTYPE_CAMERA_CAN_USE);
//                        } else {
//                            handler.sendEmptyMessage(MSGTYPE_CAMERA_CAN_NOT_USE);
//                        }
//                    }
//                });
//            }
//            mThread.start();
//        }
//    }
//
//    /**
//     * 调用第三方库开始人脸识别
//     */
//    private void startLiveness() {
//        faceCode = 0f;
//        Intent intent = new Intent();
//        intent.setClass(getActivity(), LivenessActivity.class);
//        /**
//         * EXTRA_MOTION_SEQUENCE 动作检测序列配置，支持四种检测动作， BLINK(眨眼), MOUTH（张嘴）, NOD（点头）, YAW（摇头）, 各个动作以空格隔开。 推荐第一个动作为BLINK.
//         * 默认配置为"BLINK MOUTH NOD YAW"
//         */
//        intent.putExtra(LivenessActivity.EXTRA_MOTION_SEQUENCE, "BLINK MOUTH NOD YAW");
//        /**
//         * SOUND_NOTICE 配置, 传入的soundNotice为boolean值，true为打开, false为关闭, 默认为true.
//         */
//        intent.putExtra(LivenessActivity.SOUND_NOTICE, true);
//        /**
//         * OUTPUT_TYPE 配置, 传入的outputType类型为singleImg （单图）或 multiImg （多图）, 默认为multiImg.
//         */
//        intent.putExtra(LivenessActivity.OUTPUT_TYPE, "multiImg");
//        /**
//         * COMPLEXITY 配置, 传入的complexity类型, 支持四种难度:easy, normal, hard, hell.默认为normal.
//         */
//        intent.putExtra(LivenessActivity.COMPLEXITY, "normal");
//        File livenessFolder = new File(storageFolder);
//        if (!livenessFolder.exists()) {
//            livenessFolder.mkdirs();
//        }
//        // 开始检测之前请删除文件夹下保留的文件
//        deleteFiles(storageFolder);
//        /**
//         * EXTRA_RESULT_PATH 配置， 传入的storageFolder为sdcard下目录, 为了保存检测结果文件, 传入之前请确保该文件夹存在。
//         */
//        intent.putExtra(LivenessActivity.EXTRA_RESULT_PATH, storageFolder);
//        startActivityForResult(intent, START_DETECT_REQUEST_CODE);
//    }
//
//
//    public boolean cameraIsCanUse() {
//        boolean isCanUse = true;
//        Camera mCamera = null;
//        try {
//            mCamera = Camera.open();
//            Camera.Parameters mParameters = mCamera.getParameters();
//            mCamera.setParameters(mParameters);
//        } catch (Exception e) {
//            isCanUse = false;
//        }
//        if (mCamera != null) {
//            try {
//                mCamera.release();
//            } catch (Exception e) {
//                e.printStackTrace();
//                return isCanUse;
//            }
//        }
//        return isCanUse;
//    }
//
//
//    /**
//     * 删除人脸识别时的4张图片
//     *
//     * @param folderPath
//     */
//    public static void deleteFiles(String folderPath) {
//        File dir = new File(folderPath);
//        if (dir == null || !dir.exists() || !dir.isDirectory() || dir.listFiles() == null)
//            return;
//        for (File file : dir.listFiles()) {
//            if (file.isFile())
//                file.delete();
//        }
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    /**
//     * 提交前进行数据检查
//     * flag 标志是否检测人脸识别
//     *
//     * @return
//     */
//    private boolean submitBeforeExamine(boolean flag) {
//        name = mBasic_info_et_name.getText().toString().trim();
//
//        if (isGetID) {
//            idcard = mBasic_info_et_idcard.getText().toString().trim().toLowerCase();
//        }
//        idcard = idcard.toLowerCase();
//
//        if (null == name || "".equals(name) || name.length() < 2 || IDCard.isContainSpecialCharacter(name)) {
//            Toast.makeText(mContext, "请输入正确的姓名", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if ("".equals(idcard) || 18 != idcard.length() || !"".equals(IDCard.IDCardValidate(idcard))) {
//            Toast.makeText(mContext, "请输入正确的身份证号", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (null == mCorrectFileId || null == mOppositeFileId) {
//            Toast.makeText(mContext, "请上传身份证正反面照片", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (flag) {
//            if (StringUtil.isNull(AppContext.user.getFaceCode()) || Float.parseFloat(AppContext.user.getFaceCode()) < 0.7f || Float.parseFloat(AppContext.user.getFaceCode()) > 1.0f) {        //人脸识别
//                Toast.makeText(mContext, "请先进行人脸识别", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    /**
//     * 上传图片文件对应的fileId
//     */
//    private void requestBitmapFileID() {
//        mIdentity_authentication_btn_apply.setEnabled(false);
//        if (null == mDialog) {
//            mDialog = new LoadingAlertDialog(mContext);
//        }
//        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
//
//        Map<String,String> params = new HashMap<>();
//
//        params.put("ver", AppContext.getAppVersionName(mContext));
//        params.put("Plat", ConstantsUtil.PLAT);
//
//        params.put("uid", user.getUid());
//        params.put("token", user.getToken());
//
//        params.put("name", name);
//        params.put("idNumber", idcard);
//
//        params.put("idcardCorrectHeadId", mCorrectFileId);
//        params.put("idcardOppositeHeadId", mOppositeFileId);
//        params.put("faceId", AppContext.user.getFaceId());
//
//        Log.e("TAG", params.toString());
//
//        HttpRequest.checkIdentify4creditline(mContext,params, new AsyncResponseCallBack() {        //TODO	修改成人脸识别后需要修改地址   ！！！
//
//            @Override
//            public void onResult(String arg2) {
//                mIdentity_authentication_btn_apply.setEnabled(true);
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                if (null != arg2 &&  arg2.length()> 0) {
//                    Log.e("TAG", new String(arg2));
//                    explainJsonJobUnit(new String(arg2));
//                }
//
//            }
//
//            @Override
//            public void onFailed(String path, String msg) {
//                mIdentity_authentication_btn_apply.setEnabled(true);
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                Toast.makeText(mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//
//
//    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // 源图片的高度和宽度
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//        if (height > reqHeight || width > reqWidth) {
//            // 计算出实际宽高和目标宽高的比率
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//        return inSampleSize;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (TAKE_BIG_PICTURE == requestCode && -1 == resultCode) {
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                options.inDither = true;// optional
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
//                BitmapFactory.decodeFile(imageFilePath, options);
//                int reqWidth = 800;
//                int reqHeight = 480;
//                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//                options.inJustDecodeBounds =  false;
//                Bitmap srcBitmap = BitmapFactory.decodeFile(imageFilePath, options);
//                //上传到服务器的图片的分辨率为800x480
//                uploadBitmap = Bitmap.createScaledBitmap(srcBitmap, reqWidth, reqHeight, false);
//                Log.e("TAG", uploadBitmap.getWidth() + "::" + uploadBitmap.getHeight() + "::" + uploadBitmap.getByteCount()
//                    + ":::" + (uploadBitmap.getByteCount()/ 1024));
//            if(uploadBitmap != srcBitmap){
//                srcBitmap.recycle();
//            }
//
//            if (null != uploadBitmap) {
//                //上传到服务器的图片和本地显示的不是同一张图片
//                bitmap = Bitmap.createScaledBitmap(uploadBitmap, mIdentity_authentication_iv_id_front.getWidth(), mIdentity_authentication_iv_id_front.getHeight(), false);
//
//                    if(flag == 1){
//                        //身份证正面
//                        saveBitmapToFile(uploadBitmap, fileCorrectPaht);
//                    }else if(flag == 2) {
//                        //身份证反面
//                        saveBitmapToFile(uploadBitmap, fileOppositePaht);
//                    }
//
//                    // 上传图片
//                    requestCheckIdentify4creditline();
//                } else {
//                    Toast.makeText(mContext, "获取图片失败。", Toast.LENGTH_SHORT).show();
//                }
//            return;
//        }
//
//
//
//        switch (requestCode) {
//            case 0:
//                switch (resultCode) {
////                  RESULT_OK:
//                    case -1:
//                        Bundle bundle = data.getExtras();
//                        sequence = bundle.getString("DETECT_LIST");
//                        detectListText.setText(sequence);
//                        break;
//                    case RESULT_CANCELED:
//                        break;
//                    default:
//                        break;
//                }
//                break;
//            case 1:
//                switch (resultCode) {
////                  RESULT_OK:
//                    case -1:
//                        if (FaceDetectUtil.getImageListName(storageFolder) != null && FaceDetectUtil.getImageListName(storageFolder).size() > 0) {
//                            if (FaceDetectUtil.getLoacalBitmap(storageFolder + FaceDetectUtil.getImageListName(storageFolder).get(0)) != null) {
//                                Drawable drawable = new BitmapDrawable(FaceDetectUtil.getLoacalBitmap(storageFolder
//                                        + FaceDetectUtil.getImageListName(storageFolder).get(0)));
//
//                                uploadImageOfIdCard();
//                            }
//                        } else {
//
//                        }
//                        LogUtil.printLog("通过活体检测");
//
//                        break;
//                    case RESULT_CANCELED:
//                        break;
//                    case RESULT_FIRST_USER:
//                        break;
//                    default:
//                        break;
//                }
//                break;
//        }
//
//    }
//
//    /**
//     * 上传人脸识别的身份证照片到bus
//     */
//    private void uploadImageOfIdCard() {
//        String mpath = storageFolder + FaceDetectUtil.getImageListName(storageFolder).get(0);
//        Map<String,String> params = new HashMap<>();
//        params.put("idcard", idcard);
//        params.put("realname", name);
//        params.put("token", AppContext.user.getToken());
//        params.put("type", "sampleimg");
//        params.put("source", "pzsc");
//        params.put("hasDetected", Boolean.toString(true));
//        try {
//            File photoFile = new File(mpath);
//            params.put("imgfile", photoFile);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        HttpRequest.post(mContext,HttpRequest.FACE_DETECT_WEB_URL + HttpRequest.UPLOAD_FACE_URL, params, new AsyncResponseCallBack() {
//
//            @Override
//            public void onResult(String arg2) {
//                // TODO Auto-generated method stub
//                String jsonStr = new String(arg2);
//                LogUtil.printLog("上传人脸识别的身份证照片到bus: " + jsonStr);
//
//                JSONObject jo = JSON.parseObject(jsonStr);
//                int result = jo.getIntValue("success");
//                if (result == 1) {
//                    String imgukey = jo
//                            .getString("filekey");
//                    // 活体采集图片与公安部图片对比
//                    compareWithPoliceDepartment(imgukey);
//                } else {
//                    showFaceDetectStatuse(failToFaceDetectScore);
////                        String errorInfo = "图片上传失败，请重试";
////                        if("一天之内最多只能上传10次".equals(jo.getString("error"))){
////                            errorInfo = "抱歉，认证失败。您今天的认证次数已经到达上限。每天最多只可以认证10次。请明天重试";
////                        }
////                        Toast.makeText(IdentityAuthenticationFragment.this.getActivity(), errorInfo, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int arg0, Header[] arg1,
//                                  byte[] arg2, Throwable arg3) {
//                Toast.makeText(IdentityAuthenticationFragment.this.getActivity(), "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    /**
//     * 刷新人脸识别状态
//     *
//     * @param faceCode
//     */
//    private void showFaceDetectStatuse(String faceCode) {
//        AppContext.user.setFaceCode(faceCode);
//        onResume();
//    }
//
//    private String handleErrorInfo(String errorInfo) {
//        String resultInfo = "服务商网络异常,请稍后再试";
//        if ("身份证号码与姓名不匹配".equals(errorInfo) || "姓名和身份证不匹配".equals(errorInfo)) {
//            resultInfo = "姓名和身份证不匹配,请检查身份信息";
//        } else if ("非法身份证号码".equals(errorInfo) || "身份证号不存在".equals(errorInfo)) {
//            resultInfo = "身份证号不存在,请输入正确的信息";
//        } else if ("客户身份信息验证不通过".equals(errorInfo) || "身份证号和姓名可被查询到并确认一致，但公安网中没有此人的照片故无法比对".equals(errorInfo)) {
//            resultInfo = "身份信息验证失败";
//        }
//
//        return resultInfo;
//    }
//
//    /**
//     * 和公安部图片进行对比获取分值
//     *
//     * @param imgukey
//     */
//    private void compareWithPoliceDepartment(String imgukey) {
//        // TODO Auto-generated method stub
//        Map<String,String> params = new HashMap<>();
//        params.put("idcardukey", imgukey);
//        params.put("imgukey", imgukey);
//        params.put("source", "pzsc");
//
//        HttpRequest.post(mContext,HttpRequest.FACE_DETECT_WEB_URL + HttpRequest.COMPAREMPS_URL, params, new AsyncResponseCallBack() {
//
//            @Override
//            public void onSuccess(int arg0,
//                                  Header[] arg1, byte[] arg2) {
//                // TODO Auto-generated method
//                // stub
//                String s = new String(arg2);
//                LogUtil.printLog("和公安部图片进行对比获取分值" + s);
//
//                JSONObject js = JSON.parseObject(s);
//                int result = js.getIntValue("success");
//                if (result == 1) {
//                    // 对比成功会有"confidence"0-1
//                    faceCode = js.getFloat("confidence");
//                    new StoreService(IdentityAuthenticationFragment.this.getActivity()).saveUserInfo(AppContext.user);
//
//                } else {
//                    //与公安部图片对比失败
//                    errorInfo = js.getString("error_res");
//                    Toast.makeText(IdentityAuthenticationFragment.this.getActivity(), handleErrorInfo(errorInfo), Toast.LENGTH_SHORT).show();
//                    showFaceDetectStatuse(failToFaceDetectScore);
//                }
//
//                fileId = "";
//                File[] fileArray = new File[FaceDetectUtil.getImageListName(storageFolder).size()];
//                for (int i = 0; i < FaceDetectUtil.getImageListName(storageFolder).size(); i++) {
//                    fileArray[i] = new File(storageFolder + FaceDetectUtil.getImageListName(storageFolder).get(i));
//                    uploadFourImages(fileArray[i]);
//                }
//
//            }
//
//            @Override
//            public void onFailed(String path, String msg) {
//                Toast.makeText(IdentityAuthenticationFragment.this.getActivity(), "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    /**
//     * 上传人脸识别的对比分值到万卡商城后台
//     */
//    private void uploadScoreOfFaceDetect() {
//        Map<String,String> params = new HashMap<>();
//        params.put("uid", AppContext.user.getUid());
//        params.put("token", AppContext.user.getToken());
//        params.put("faceCode", faceCode+"");
//        params.put("faceReason", errorInfo);
//
//        String[] fileArray = fileId.split(",");
//        params.put("picOne", fileArray[0]);
//        params.put("picTwo", fileArray[1]);
//        params.put("picThree", fileArray[2]);
//        params.put("picFour", fileArray[3]);
//
//        HttpRequest.post(mContext,HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.UPLOAD_SCORE_URL, params, new AsyncResponseCallBack() {
//
//            @Override
//            public void onSuccess(int arg0, Header[] arg1,
//                                  byte[] arg2) {
//                // TODO Auto-generated method stub
//                String jsonStr = new String(arg2);
//                LogUtil.printLog("上传人脸识别的对比分值到万卡商城后台" + jsonStr);
//
//                JSONObject jo = JSON.parseObject(jsonStr);
//                if (ConstantsUtil.RESPONSE_SUCCEED == jo
//                        .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
//                    AppContext.user.setFaceCode(faceCode + "");
//                    AppContext.user.setFaceId(jo.getString("faceId"));
//                    onResume();
//                } else {
//                    showFaceDetectStatuse(failToFaceDetectScore);
//                }
//
//                deleteFiles(storageFolder);
//
//
//            }
//
//            @Override
//            public void onFailure(int arg0, Header[] arg1,
//                                  byte[] arg2, Throwable arg3) {
//                Toast.makeText(IdentityAuthenticationFragment.this.getActivity(), "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    /**
//     * 上传人脸识别四张图片
//     */
//    private void uploadFourImages(File file) {
//        String mpath = storageFolder + FaceDetectUtil.getImageListName(storageFolder).get(0);
//        Map<String,String> params = new HashMap<>();
//        params.put("uid", AppContext.user.getUid());
//        params.put("token", AppContext.user.getToken());
//
//        try {
//            params.put("imgfile", file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        HttpRequest.updateBitmap(mContext,params, new AsyncResponseCallBack() {
//
//            @Override
//            public void onResult(String arg2) {
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                String str = new String(arg2);
//                LogUtil.printLog("上传图片到阿里云存储" + str);
//
//                if (StringUtil.notEmpty(str)) {
//                    if (StringUtil.isNull(fileId)) {
//                        fileId = str;
//                    } else {
//                        fileId += "," + str;
//                    }
//
//                    if (fileId.split(",").length >= 4) {
//                        uploadScoreOfFaceDetect();
//                    }
//                } else {
//                    showFaceDetectStatuse(failToFaceDetectScore);
//                }
//
//
//            }
//
//            @Override
//            public void onFailed(String path, String msg) {
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                LogUtil.printLog("阿里云存储图片" + new String(arg2));
//                Log.e("TAG", "arg0:" + arg0 + " ," + arg3.toString());
//                Toast.makeText(mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//    /**
//     * 读取图片属性：旋转的角度
//     *
//     * @param path 图片绝对路径
//     * @return degree旋转的角度
//     */
//    public static int readPictureDegree(String path) {
//        int degree = 0;
//        try {
//            ExifInterface exifInterface = new ExifInterface(path);
//            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//            Log.e("TAG", "orientation:" + orientation);
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    degree = 90;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    degree = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    degree = 270;
//                    break;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return degree;
//    }
//
//    /**
//     * 旋转图片
//     *
//     * @param angle
//     * @param bitmap
//     * @return Bitmap
//     */
//    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
//        // 旋转图片 动作
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        // 创建新的图片
//        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        return resizedBitmap;
//    }
//
//    /**
//     * 上传图片
//     */
//    private void requestCheckIdentify4creditline() {
//        if (null == mDialog) {
//            mDialog = new LoadingAlertDialog(mContext);
//        }
//        Map<String,String> params = new HashMap<>();
//        String path = Environment.getExternalStorageDirectory().toString() + "/Note";
//        File file = null;
//        try {
//            if (1 == flag) {
//                file = new File(path + "/" + fileCorrectPaht); // 身份证正面图片
//            } else if (2 == flag) {
//                file = new File(path + "/" + fileOppositePaht); // 身份证反面图片
//            }
//            if (null == file) {
//                Toast.makeText(mContext, "拍照获取图片失败，请重新拍照。", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            params.put("imgfile", file);
//        } catch (FileNotFoundException e) {
//            Log.e("TAG", e.toString());
//        }
//        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
//        params.put("uid", user.getUid());
//        params.put("token", user.getToken());
//
//        params.put("ver", AppContext.getAppVersionName(mContext));
//        params.put("Plat", ConstantsUtil.PLAT);
//
//        Log.e("TAG", params.toString());
//
//        HttpRequest.updateBitmap(mContext,params, new AsyncResponseCallBack() {
//
//            @Override
//            public void onResult(String arg2) {
//                if (null != arg2 &&  arg2.length()> 0) {
//                    LogUtil.printLog("上传图片响应：" + new String(arg2));
//                    // explainJsonJobUnit(new String(arg2));
//                    saveFileId(new String(arg2));
//                }
//
//            }
//
//            @Override
//            public void onFailed(String path, String msg) {
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                Toast.makeText(mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    protected void explainJsonJobUnit(String json) {
//        SaveBaseInfoBean bean = JSON.parseObject(json, SaveBaseInfoBean.class);
//        if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
//            int step = -1;
//            if (null != bean.getStep() || !"".equals(bean.getData().getStep())) {
//                step = Integer.parseInt(bean.getData().getStep());
//            }
//            AppContext.user.setIdName(name);
//            Log.e("TAG", name + "::::" + AppContext.user.getIdName());
//            mOnClickBtnListen.next(step);
//
//        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
//            UserUtils.tokenFailDialog(mContext,bean.getStatusDetail(),null);
//        } else {
//            Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    /**
//     * 保存文件id,并且显示图片
//     *
//     * @param str
//     */
//    private void saveFileId(String str) {
//        if (str.length() < 10) { // 成功为32位加密的id. 这里的数值没有实际意义
//            Toast.makeText(mContext, "上传图片失败", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        switch (flag) {
//            case 1:
//                if (null != bitmap) {
//                    Log.e("TAG", bitmap.getHeight() + "::" + bitmap.getWidth());
//                    if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                        if (bitmap.getHeight() > bitmap.getWidth()) {
//                            //mIdentity_authentication_iv_id_front.setImageBitmap(rotaingImageView(-90, bitmap)); //
//                            mIdentity_authentication_iv_id_front.setImageBitmap(bitmap);
//                        } else {
//                            mIdentity_authentication_iv_id_front.setImageBitmap(bitmap); //
//                        }
//                    } else {
//                        mIdentity_authentication_iv_id_front.setImageBitmap(bitmap);
//                    }
//
//                }
//                mCorrectFileId = str;
//                break;
//            case 2:
//                if (null != bitmap) {
//                    if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                        if (bitmap.getHeight() > bitmap.getWidth()) {
//                            //mIdentity_authentication_iv_id_back.setImageBitmap(rotaingImageView(-90, bitmap));// readPictureDegree(fileOppositePaht)
//                            mIdentity_authentication_iv_id_back.setImageBitmap(bitmap);                                                                                // IMAGE_FILE_LOCATION
//                        } else {
//                            mIdentity_authentication_iv_id_back.setImageBitmap(bitmap);
//                        }
//                    } else {
//                        mIdentity_authentication_iv_id_back.setImageBitmap(bitmap);
//                    }
//
//                }
//                mOppositeFileId = str;
//                break;
//
//            default:
//                break;
//        }
//        Log.e("TAG", "1111111111111111111111111111");
//        if (mDialog != null && mDialog.isShowing()) {
//            mDialog.dismiss();
//        }
//    }
//
//
//    /**
//     * Save Bitmap to a file.保存图片到SD卡。
//     *
//     * @param bitmap
//     * @param fileName
//     * @return error message if the saving is failed. null if the saving is
//     * successful.
//     */
//    public void saveBitmapToFile(Bitmap bitmap, String fileName){
//        BufferedOutputStream os = null;
//        try {
//            File file = new File(imageStorageDir, fileName);
//            Log.e("TAG", file.getAbsolutePath().toString() + "::::" + file.getAbsolutePath());
//            os = new BufferedOutputStream(new FileOutputStream(file));
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
//        } catch (Exception e) {
//            Log.e("TAG", "saveBitmapToFile:" + e.toString());
//        } finally {
//            if (os != null) {
//                try {
//                    os.close();
//                    bitmap.recycle();
//                } catch (IOException e) {
//                    Log.e("TAG", "saveBitmapToFile:" + e.toString());
//                }
//            }
//        }
//    }
//
//    /**
//     * 判断是否有sd卡
//     *
//     * @return
//     */
//    private boolean isStorage() {
//        try {
//            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 拍照
//     */
//    private void doTakePhoto() {
//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//            imageFilePath = imageStorageDir +"/temp.jpg";
//            File file = new File(imageFilePath);
//            if (!file.exists()) {
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// action is capture
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//            startActivityForResult(intent, TAKE_BIG_PICTURE);
//        }else {
//            Toast.makeText(mContext, "请确认插入SD卡", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        float faceCode = -1f;
//        if (StringUtil.notEmpty(AppContext.user.getFaceCode())) {
//            faceCode = Float.valueOf(AppContext.user.getFaceCode());
//        }
//
//        if (faceCode >= 0.7f && faceCode <= 1) {
//            showFaceState(2);
//        } else if (faceCode >= 0 && faceCode < 0.7) {
//            showFaceState(3);
//        } else if (faceCode == faceDetectSdkError) {
//            showFaceState(1);
//        } else {
//            showFaceState(0);
//        }
//
//        requestGetRealNameInfo();
//    }
//
//    @Override
//    public String getPageName() {
//        return getString(R.string.str_pagename_identifyauth_frag);
//    }
//
//    /**
//     * 显示人脸识别的状态
//     *
//     * @param state
//     */
//    private void showFaceState(int state) {
//        switch (state) {
//            case 0:                //等待验证
//                mIdentity_authentication_iv_face.setImageResource(R.drawable.icon_rlsb);
//                identity_authentication_rl_face.setEnabled(true);
//                identity_face_tv_result.setText("等待验证");
//                identity_face_tv_result.setTextColor(0xff333333);
//                identity_face_tv_hint.setText("点击进行验证");
//                identity_face_iv_next.setVisibility(View.VISIBLE);
//                break;
//            case 1:                //程序出错
//                mIdentity_authentication_iv_face.setImageResource(R.drawable.icon_cxcc);
//                identity_authentication_rl_face.setEnabled(true);
//                identity_face_tv_result.setText("程序出错");
//                identity_face_tv_result.setTextColor(0xffff5050);
//                identity_face_tv_hint.setText("请重新验证");
//                identity_face_iv_next.setVisibility(View.VISIBLE);
//                break;
//            case 2:                //通过
//                mIdentity_authentication_iv_face.setImageResource(R.drawable.icon_yzcg);
//                identity_authentication_rl_face.setEnabled(false);
//                identity_face_tv_result.setText("恭喜你，验证成功！");
//                identity_face_tv_result.setTextColor(0xff77d253);
//                identity_face_tv_hint.setText("您已经通过验证");
//                identity_face_iv_next.setVisibility(View.GONE);
//                identity_authentication_rl_face.setPadding(DensityUtil.dip2px(mContext, 65), 0, 30, 0);
//                break;
//            case 3:                //未通过
//                mIdentity_authentication_iv_face.setImageResource(R.drawable.icon_yzwtg);
//                identity_authentication_rl_face.setEnabled(true);
//                identity_face_tv_result.setText("验证未通过");
//                identity_face_tv_result.setTextColor(0xffff5050);
//                identity_face_tv_hint.setText("请重新验证");
//                identity_face_iv_next.setVisibility(View.VISIBLE);
//                break;
//        }
//    }
//
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        Log.e("TAG", "onSaveInstanceState");
//    }
//
//    @Override
//    public void onDestroy() {
//        // TODO Auto-generated method stub
//        super.onDestroy();
//    }
//
//}
