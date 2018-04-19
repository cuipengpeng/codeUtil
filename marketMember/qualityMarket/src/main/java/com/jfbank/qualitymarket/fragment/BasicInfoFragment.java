//package com.jfbank.qualitymarket.fragment;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.provider.ContactsContract;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSON;
//import com.jfbank.qualitymarket.AppContext;
//import com.jfbank.qualitymarket.R;
//import com.jfbank.qualitymarket.activity.ApplyAmountActivity;
//import com.jfbank.qualitymarket.activity.LoginActivity;
//import com.jfbank.qualitymarket.activity.ProvinceAddressActivity;
//import com.jfbank.qualitymarket.base.BaseFragment;
//import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
//import com.jfbank.qualitymarket.dao.StoreService;
//import com.jfbank.qualitymarket.fragment.RelationSelectionDialogFragment.OnClickListen;
//import com.jfbank.qualitymarket.model.AddressInfoBean;
//import com.jfbank.qualitymarket.model.BasicInfoBean;
//import com.jfbank.qualitymarket.model.BasicInfoBean.BasicInfoData.DataRelations;
//import com.jfbank.qualitymarket.model.ContactBean;
//import com.jfbank.qualitymarket.model.SaveBaseInfoBean;
//import com.jfbank.qualitymarket.net.HttpRequest;
//import com.jfbank.qualitymarket.util.CommonUtils;
//import com.jfbank.qualitymarket.util.ConstantsUtil;
//import com.jfbank.qualitymarket.util.IDCard;
//import com.jfbank.qualitymarket.util.PhoneTools;
//import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * 基本信息对应的页面
// *
// * @author 彭爱军
// * @date 2016年8月10日
// */
//public class BasicInfoFragment extends BaseFragment implements OnClickListener {
//    private static final int REQUEST_CONTACT = 100;
//    private static final int EXTRA_REQUST_UPLOADCONTACTS = 0x11;
//    private ApplyAmountActivity mContext;
//    /**
//     * QQ
//     */
//    private EditText mBasic_info_et_qq_code;
//    /**
//     * 工作地址
//     */
//    private TextView mBasic_info_tv_job_address;
//    /**
//     * 与本人关系
//     */
//    private TextView mBasic_info_tv_relation;
//    /**
//     * 通讯录
//     */
//    private TextView mBasic_info_tv_contacts;
//    /**
//     * 联系人的电话
//     */
//    private EditText mBasic_info_et_phone;
//    /**
//     * 联系人的名字
//     */
//    private EditText mBasic_info_et_contacts_name;
//    /**
//     * 地址选择
//     */
//    private RelativeLayout mBasic_info_rl_job_address;
//    /**
//     * 与本人关系选择
//     */
//    private RelativeLayout mBasic_info_rl_relation;
//    /**
//     * 下一步
//     */
//    private Button mBasic_info_btn_next;
//    private View view;
//
//    /**
//     * 工作单位地址
//     */
//    private EditText basic_info_tv_job_unit;
//    /**
//     * 工作单位电话区号
//     */
//    private EditText basic_info_tv_job_area_code;
//    /**
//     * 工作单位电话号码
//     */
//    private EditText basic_info_tv_job_area_phone;
//    private String jobAreaPhone;
//    private String job_unit;
//    private String qq_code;
//    private String job_address;
//    private String relation;
//    private String contacts_name;
//    private OnClickBtnListen mOnClickBtnListen;
//
//    private String mContactsName; // 从联系人中获取到的名字
//    private String mContactsPhone; // 从联系人中获取到的手机号
//    /**
//     * 省
//     */
//    private String mProvince;
//    /**
//     * 市
//     */
//    private String mCity;
//    /**
//     * 省编码
//     */
//    private String mProvinceCode;
//    /**
//     * 市编码
//     */
//    private String mCityCode;
//    /**
//     * 联系人数据的数据
//     */
//    private String[] mJobUnitStr;
//    /**
//     * 工作单位选择中的code码
//     */
//    private String mCompanyAreaCode;
//
//    private List<DataRelations> mDataRelations;
//    private final int requestCode = 101;
//    /**
//     * 网编请求时加载框
//     */
//    private LoadingAlertDialog mDialog;
//    private boolean isUploadContacts;        //是否上传通讯录.保证通讯录上传一次
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
//    /**
//     * 获取基本信息
//     */
//    private void getBaseInfo() {
//        qq_code = mBasic_info_et_qq_code.getText().toString().trim();
//        job_address = mBasic_info_tv_job_address.getText().toString().trim();
//        contacts_name = mBasic_info_et_contacts_name.getText().toString().trim();
//        mContactsPhone = mBasic_info_et_phone.getText().toString().trim();
//
//        job_unit = basic_info_tv_job_unit.getText().toString().trim();
//        jobAreaPhone = basic_info_tv_job_area_code.getText().toString().trim() + "-" + basic_info_tv_job_area_phone.getText().toString().trim();
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mContext = (ApplyAmountActivity) getActivity();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_basic_info, container, false);
//        bindViews();
//        setOnClickListen();
//        return view;
//    }
//
//    /**
//     * 设置监听
//     */
//    private void setOnClickListen() {
//        mBasic_info_tv_contacts.setOnClickListener(this);
//        mBasic_info_btn_next.setOnClickListener(this);
//        mBasic_info_rl_job_address.setOnClickListener(this);
//        mBasic_info_rl_relation.setOnClickListener(this);
//
//        requestPrepareData4base4creditline();
//    }
//
//    /**
//     * 初始化View
//     */
//    private void bindViews() {
//        mBasic_info_et_qq_code = (EditText) view.findViewById(R.id.basic_info_et_qq_code);
//        mBasic_info_tv_job_address = (TextView) view.findViewById(R.id.basic_info_tv_job_address);
//        mBasic_info_tv_relation = (TextView) view.findViewById(R.id.basic_info_tv_relation);
//        mBasic_info_tv_contacts = (TextView) view.findViewById(R.id.basic_info_tv_contacts);
//        mBasic_info_et_contacts_name = (EditText) view.findViewById(R.id.basic_info_et_contacts_name);
//        mBasic_info_et_phone = (EditText) view.findViewById(R.id.basic_info_et_phone);
//        mBasic_info_btn_next = (Button) view.findViewById(R.id.basic_info_btn_next);
//        mBasic_info_rl_job_address = (RelativeLayout) view.findViewById(R.id.basic_info_rl_job_address);
//        mBasic_info_rl_relation = (RelativeLayout) view.findViewById(R.id.basic_info_rl_relation);
//
//        basic_info_tv_job_unit = (EditText) view.findViewById(R.id.basic_info_tv_job_unit);
//        basic_info_tv_job_area_phone = (EditText) view.findViewById(R.id.basic_info_tv_job_area_phone);
//        basic_info_tv_job_area_code = (EditText) view.findViewById(R.id.basic_info_tv_job_area_code);
//
//        mBasic_info_tv_job_address.requestFocus();        //获取焦点
//
//        Log.e("TAG", "ssssss : " + AppContext.user.getIdName());
//    }
//
//    /**
//     * 网络请求初始化基本信息
//     */
//    private void requestPrepareData4base4creditline() {
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
//        params.put("uid", AppContext.user.getUid());
//        params.put("token", AppContext.user.getToken());
//
//        Log.e("TAG", params.toString());
//
//        HttpRequest.PrepareData4base4creditline(mContext,params, new AsyncResponseCallBack() {
//
//            @Override
//            public void onResult(String arg2) {
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                if (null != arg2 && arg2.length() > 0) {
//                    Log.e("TAG", new String(arg2));
//                    explainJson(new String(arg2));
//                }
//
//            }
//
//            @Override
//            public void onFailed(String path, String msg) {
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                Log.e("TAG", "arg0:" + msg);
//                Toast.makeText(mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//    }
//
//    /**
//     * 解释json
//     *
//     * @param json
//     */
//    protected void explainJson(String json) {
//        BasicInfoBean bean = JSON.parseObject(json, BasicInfoBean.class);
//        if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
//            mDataRelations = bean.getData().getRelations();
//        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
//            showDialog(bean.getStatusDetail(), true);
//        } else {
//            Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * token失效时弹出对话框
//     *
//     * @param content
//     * @param isLogin
//     */
//    private void showDialog(String content, final boolean isLogin) {
//        final PopDialogFragment dialog = PopDialogFragment.newDialog(false, false, null, content, null, null, "确定");
//        dialog.setOnClickListen(new PopDialogFragment.OnClickListen() {
//
//            @Override
//            public void rightClick() {
//                if (isLogin) {
//                    new StoreService(mContext).clearUserInfo();
//                    Intent loginIntent = new Intent(mContext.getApplication(), LoginActivity.class);
//                    loginIntent.putExtra(LoginActivity.KEY_OF_COME_FROM, LoginActivity.TOKEN_FAIL_TAG);
//                    startActivity(loginIntent);
//                }
//                dialog.dismiss();
//            }
//
//            @Override
//            public void leftClick() {
//                dialog.dismiss();
//            }
//        });
//        dialog.show(mContext.getSupportFragmentManager(), "TAG");
//    }
//
//    @Override
//    public void onClick(View v) {
//        Intent intent = new Intent();
//        switch (v.getId()) {
//            case R.id.basic_info_tv_contacts: // 进入通讯录
//                startContacts();
//                break;
//            case R.id.basic_info_btn_next: // 下一步
//                // Toast.makeText(mContext, "正在建设中。。。", Toast.LENGTH_SHORT).show();
//                if (!submitBeforeExamine()) {
//                    break;
//                }
//                requestSaveBaseInfo();
//                break;
//            case R.id.basic_info_rl_job_address:
//                intent.setClass(mContext, ProvinceAddressActivity.class);        //选择城市
//                intent.putExtra("IS_BASE_INFO", true);
//                startActivity(intent);
//                break;
//            case R.id.basic_info_rl_relation:                                //选择关系
//                if (null != mDataRelations && mDataRelations.size() > 0) {
//                    mJobUnitStr = new String[mDataRelations.size()];
//                    for (int i = 0; i < mDataRelations.size(); i++) {
//                        mJobUnitStr[i] = mDataRelations.get(i).getValue();
//                    }
//                    popDialog(false);
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 进入通讯录
//     */
//    private void startContacts() {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_PICK);
//        intent.setData(ContactsContract.Contacts.CONTENT_URI);
//        startActivityForResult(intent, REQUEST_CONTACT);
//    }
//
//    /**
//     * 弹出对话框
//     *
//     * @param isJobUnit
//     */
//    private void popDialog(final boolean isJobUnit) {
//
//        final RelationSelectionDialogFragment dialog = RelationSelectionDialogFragment.newDialog(mJobUnitStr);
//        dialog.setOnClickListen(new OnClickListen() {
//
//            @Override
//            public void confirm(int selectedIndex, String item) {
//                //Toast.makeText(mContext, "选择了"+selectedIndex+"::"+item, Toast.LENGTH_SHORT).show();
//                if (isJobUnit) {
//
//                } else {
//                    relation = mDataRelations.get(selectedIndex - 1).getKey();
//                    Log.e("TAG", relation);
//                    mBasic_info_tv_relation.setText(item);
//                    mBasic_info_tv_relation.setTextColor(0xff333333);
//                    mBasic_info_tv_relation.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//                }
//                dialog.dismiss();
//            }
//
//            @Override
//            public void cancel() {
//                dialog.dismiss();
//
//            }
//        });
//        dialog.show(getFragmentManager(), "TAG");
//    }
//
//
//    /**
//     * 提交前进行数据检查
//     */
//    private boolean submitBeforeExamine() {
//        getBaseInfo();
//
//        if (null == qq_code || "".equals(qq_code) || qq_code.length() < 5) {        //判断QQ是否有效。
//            Toast.makeText(mContext, "请输入正确的qq号码", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (null == job_address || "".equals(job_address) || job_address.contains("请选择")) {        //判断QQ是否有效。
//            Toast.makeText(mContext, "请选择您的所在城市", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (null == job_unit || "".equals(job_unit) || job_unit.length() < 5 || IDCard.isContainSpecialCharacter(job_unit)) {        //判断QQ是否有效。
//            Toast.makeText(mContext, "请输入正确的单位名称", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (null == jobAreaPhone || "".equals(jobAreaPhone) || !check(jobAreaPhone)) {        //判断QQ是否有效。
//            Toast.makeText(mContext, "请输入正确的单位号码", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (null == relation || "".equals(relation)) {        //判断QQ是否有效。
//            Toast.makeText(mContext, "请选择联系人与您的关系", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (null == mContactsPhone || "".equals(mContactsPhone)) {
//            Toast.makeText(mContext, "请从通讯录中选择联系人手机号", Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (!CommonUtils.isMobilePhoneVerify(mContactsPhone) || mContactsPhone.equals(AppContext.user.getMobile())) {
//            Toast.makeText(mContext, "请选择正确的手机号码", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (null == contacts_name || "".equals(contacts_name) || IDCard.isContainSpecialCharacter(contacts_name)) {        //判断联系人是否有效。
//            Toast.makeText(mContext, "请输入正确的联系人姓名", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (contacts_name.equals(AppContext.user.getIdName())) {        //判断QQ是否有效。
//            Toast.makeText(mContext, "请填写正确的紧急联系人", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//
//    /**
//     * 判断是否为电话号码
//     *
//     * @param phonenumber
//     * @return
//     */
//    public boolean check(String phonenumber) {
//        String phone = "0\\d{2,3}-\\d{7,8}";
//        Pattern p = Pattern.compile(phone);
//        Matcher m = p.matcher(phonenumber);
//
//        return m.matches();
//    }
//
//    /**
//     * 调用保存基本信息接口
//     */
//    private void requestSaveBaseInfo() {
//        getBaseInfo();
//        if (null == mDialog) {
//            mDialog = new LoadingAlertDialog(mContext);
//        }
//        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
//        Map<String,String> params = new HashMap<>();
//
//        params.put("uid", AppContext.user.getUid());
//        params.put("token", AppContext.user.getToken());
//
//        params.put("ver", AppContext.getAppVersionName(mContext));
//        params.put("Plat", ConstantsUtil.PLAT);
//
//        params.put("qq", qq_code);
//
//        params.put("company", job_unit);
//        params.put("companyPhone", jobAreaPhone);
//
//        params.put("emergContact", contacts_name); // 紧急联系人
//        params.put("emergRelation", relation); // 紧急联系人与本人关系
//        params.put("emergPhone", mContactsPhone);
//
//        params.put("companyProvinceCode", mProvinceCode);
//        params.put("companyProvince", mProvince);
//        params.put("companyCityCode", mCityCode);
//        params.put("companyCity", mCity);
//
//        Log.e("TAG", params.toString());
//
//        HttpRequest.saveBaseInfo(mContext,params, new AsyncResponseCallBack() {
//
//            @Override
//            public void onResult(String arg2) {
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                if (null != arg2 && arg2.length() > 0) {
//                    Log.e("TAG", new String(arg2));
//                    explainSaveBaseInfoJson(new String(arg2));
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
//
//    }
//
//    /**
//     * 解释保存基本信息的json
//     *
//     * @param json
//     */
//    protected void explainSaveBaseInfoJson(String json) {
//        SaveBaseInfoBean bean = JSON.parseObject(json, SaveBaseInfoBean.class);
//        if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
//            isUploadContacts = !bean.getData().isFinishedUploadContracts();
//            if (isUploadContacts) {
//                AppContext.user.setIsContact("0");        //说明上传失败。重新赋值
//                new StoreService(getActivity()).saveUserInfo(AppContext.user);
//                showDialog("数据异常，请联系客服处理", false);
//                return;
//            }
//            AppContext.user.setIsContact("1");
//            new StoreService(getActivity()).saveUserInfo(AppContext.user);
//            mOnClickBtnListen.next(Integer.parseInt(bean.getData().getStep()));
//        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
//            showDialog(bean.getStatusDetail(), true);
//        } else {
//            Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CONTACT && Activity.RESULT_OK == resultCode) {
//            if (null == data) {
//                Toast.makeText(mContext, "请重新选择联系人", Toast.LENGTH_SHORT).show();
//                return;
//            } else {
//                selectContact(data);
//            }
//        }
//    }
//
//    /**
//     * 选择通讯录
//     *
//     * @param data
//     */
//    private void selectContact(Intent data) {
//        try {
//            ContactBean contactBean = PhoneTools.getPhoneContact(getActivity(), data);
//            if (contactBean != null) {
//                if (TextUtils.isEmpty(contactBean.getMobilePhone())) {
//                    Toast.makeText(getActivity(), "该联系人手机号为空", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (CommonUtils.isMobilePhoneVerify(contactBean.getMobilePhone())) {
//                        mContactsPhone = contactBean.getMobilePhone();
//                        mContactsName = contactBean.getContactName();
//                        mBasic_info_et_phone.setText(mContactsPhone);
//                        mBasic_info_et_contacts_name.setText(mContactsName);
//                        if (isUploadContacts) {
//                            uploadContacts();
//                        }else{
//                        }
//                    } else {
//                        Toast.makeText(getActivity(), "请选择正确格式的手机号", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            } else {
//                Toast.makeText(getActivity(), "请先设置允许查看通讯录权限", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(getActivity(), "请先设置允许查看通讯录权限", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 上传通讯录
//     */
//    private void uploadContacts() {
//        Map<String,String> params = new HashMap<>();
//
//        AppContext.user.setIsContact("1");            //重新赋值。防止黑屏或者切换到主页面后回来再次上传通讯录。
//        new StoreService(getActivity()).saveUserInfo(AppContext.user);
//        isUploadContacts = false;
//        params.put("uid", AppContext.user.getUid());
//        params.put("token", AppContext.user.getToken());
//
//        params.put("ver", AppContext.getAppVersionName(mContext));
//        params.put("Plat", ConstantsUtil.PLAT);
//
//        List<ContactBean> contactList = new ArrayList<ContactBean>(); // 存储
//        List<ContactBean> phoneList = PhoneTools.getContactPhone(mContext);  //手机通讯录有数据
//        if (!CommonUtils.isEmptyList(phoneList)) {
//            for (ContactBean contactBean : phoneList) { // 遍历手机通讯录
//                contactList.add(contactBean);
//            }
//        }
//        List<ContactBean> simSimContractList = PhoneTools.getSimContracts(mContext);//遍历sim卡通讯录
//        if (!CommonUtils.isEmptyList(simSimContractList)) {
//            for (com.jfbank.qualitymarket.model.ContactBean contactBean :simSimContractList) { // 遍历sim
//                if (contactList.contains(contactBean)) {
//                    continue;
//                }
//                contactList.add(contactBean);
//            }
//        }
//
//        String json = com.alibaba.fastjson.JSONArray.toJSONString(contactList);
//        params.put("userContactlist", json);
//        Log.e("TAG", params.toString());
//
//        HttpRequest.uploadContacts(mContext,params, new AsyncResponseCallBack() {
//
//            @Override
//            public void onResult(String arg2) {
//                if (null != arg2 && arg2.length() > 0) {
//                    Log.e("TAG(uploadContacts)", new String(arg2));
//                }
//
//            }
//
//            @Override
//            public void onFailed(String path, String msg) {
//                Toast.makeText(mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        isUploadContacts = 1 != Integer.parseInt(AppContext.user.getIsContact()) ? true : false;
//        Log.e("TAG", "isUploadContacts:" + isUploadContacts + " ::" + AppContext.user.getIsContact());
//        AddressInfoBean bean = AppContext.getAddressInfo();
//        if (null == bean || !bean.isOK()) {
//            mBasic_info_tv_job_address.setText("请选择");
//            job_address = null;
//            //job_unit = null;
//            mBasic_info_tv_job_address.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.right_arrow), null);
//            return;
//        }
//
//        mProvince = bean.getAddProvince();
//        mProvinceCode = bean.getAddProvinceCode();
//        mCity = bean.getAddCity();
//        mCityCode = bean.getAddCityCode();
//        mBasic_info_tv_job_address.setText(mCity);
//        mBasic_info_tv_job_address.setTextColor(0xff333333);
//        mBasic_info_tv_job_address.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);        //去年右边显示的图片
//        if (bean.isCleanJobUnit()) {
//            //job_unit = null;
//            AppContext.getAddressInfo().setCleanJobUnit(false);
//        }
//    }
//
//    @Override
//    public String getPageName() {
//        return getString(R.string.str_pagename_basicinfo);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//
//}
