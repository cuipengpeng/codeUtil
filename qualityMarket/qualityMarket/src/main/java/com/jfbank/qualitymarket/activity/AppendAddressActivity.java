package com.jfbank.qualitymarket.activity;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyco.animation.NewsPaperEnter;
import com.jfbank.qualitymarket.ActivitysManage;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.fragment.PopDialogFragment;
import com.jfbank.qualitymarket.model.AddressInfoBean;
import com.jfbank.qualitymarket.model.DeleteReceiptAddressBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.IDCard;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import qiu.niorgai.StatusBarCompat;

/**
 * 新增地址页面
 *
 * @author 彭爱军
 * @date 2016年8月10日
 */
public class AppendAddressActivity extends BaseActivity implements OnClickListener {
    RelativeLayout rlTitle;
    /**
     * 显示标题内容
     */
    TextView tvTitle;
    /**
     * 返回
     */
    ImageView ivBack;

    /**
     * 收货人名字
     */
    private EditText mAppend_address_et_name;
    /**
     * 收货人手机号码
     */
    private EditText mAppend_address_et_phone;
    /**
     * 地区信息
     */
    private RelativeLayout mAppend_address_rl_area;
    /**
     * 详细地址
     */
    private EditText mAppend_address_et_detailedness_address;
    /**
     * 保存
     */
    private Button mAppend_address_btn_save;

    @InjectView(R.id.et_appendAddressActivity_verifyCode)
    EditText verifyCodeEditText;
    @InjectView(R.id.btn_appendAddressActivity_getVerifyCode)
    Button getVerifyCodeButton;
    private int countDownTime = ConstantsUtil.COUNT_DOWN;
    private final int COUNT_DOWN_TASK = 20001;
    private final int COUNT_DOWN_OVER = 10001;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case COUNT_DOWN_TASK:
                    //点击获取验证码后的倒计时任务
                    getVerifyCodeButton.setText("剩余" + msg.arg1 + "s");
                    if(countDownTime>0){
                        Message message = new Message();
                        countDownTime -= 1;
                        message.what = COUNT_DOWN_TASK;
                        message.arg1 = countDownTime;
                        handler.sendMessageDelayed(message, 1000);
                    }else {
                        Message message2 = new Message();
                        message2.what = COUNT_DOWN_OVER;
                        handler.sendMessageDelayed(message2, 0);
                    }
                    break;
                case COUNT_DOWN_OVER:
                    //倒计时结束，启用获取验证码按钮
                    enableGetVerifyButton();
                    break;

            }
        };
    };

    /**
     * 显示地址信息
     */
    private TextView mAppend_address_tv_address;

    /**
     * 收货人名字
     */
    private String mReceivingName;
    /**
     * 收货人手机号
     */
    private String mReceivingPhone;
    /**
     * 收货地址
     */
    private String mReceivingAddress;
    /**
     * 选择标签
     */
    private RelativeLayout mAppend_address_rl_label;
    /**
     * 标签名字
     */
    private TextView mAppend_address_tv_label;
    private TextView append_textview3;
    public static boolean isShow = false;        //即编辑地址的时候。 没有点击选择地址时。 显示的地址的标识
    private Intent intent;
    private String addProvince;
    private String addProvinceCode;
    private String addCity;
    private String addCityCode;
    private String addCounty;
    private String addCountyCode;
    private String addTown;
    private String addTownCode;
    private String addArea;
    private String addAreaCode;
    private String addressNo;        //收货地址id
    private boolean mAddOrEdit;
    private TextView append_textview2;
    private String addDefault;
    /**
     * 网编请求时加载框
     */
    private LoadingAlertDialog mDialog;
    private String addresslabel;        //收货人关系标签

    private class MyTextWatcher implements TextWatcher {

        private String address;
        private String name;
        private String phone;
        private String detailednessAddress;
        private String verifyCode;
        //private String label;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            address = mAppend_address_tv_address.getText().toString().trim();
            name = mAppend_address_et_name.getText().toString().trim();
            phone = mAppend_address_et_phone.getText().toString().trim();
            detailednessAddress = mAppend_address_et_detailedness_address.getText().toString().trim();
            verifyCode = verifyCodeEditText.getText().toString().trim();

            //label = mAppend_address_tv_label.getText().toString().trim();
            if (address.length() < 6 || name.length() < 2 || phone.length() != 11 || detailednessAddress.length() < 1 || verifyCode.length()<=0 /*|| label.length() < 2*/) {
                mAppend_address_btn_save.setEnabled(false);
                mAppend_address_btn_save.setBackgroundResource(R.drawable.login_page_button_disabled);
            } else {
                mAppend_address_btn_save.setEnabled(true);
                mAppend_address_btn_save.setBackgroundResource(R.drawable.button_selector);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitysManage.getActivitysManager().addActivity(this);
        setContentView(R.layout.activity_append_address);
        ButterKnife.inject(this);

        bindViews();
    }

    /**
     * 初始化View以及设置监听
     */
    private void bindViews() {

        initViews();

        ivBack.setOnClickListener(this);
        mAppend_address_rl_area.setOnClickListener(this);
        mAppend_address_btn_save.setOnClickListener(this);
        mAppend_address_rl_label.setOnClickListener(this);
        getVerifyCodeButton.setOnClickListener(this);

        intent = getIntent();
        if (null != intent) {
            getIntentData();
        }

        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mAppend_address_tv_address.addTextChangedListener(myTextWatcher);
        mAppend_address_tv_label.addTextChangedListener(myTextWatcher);
        mAppend_address_et_name.addTextChangedListener(myTextWatcher);
        mAppend_address_et_phone.addTextChangedListener(myTextWatcher);
        mAppend_address_et_detailedness_address.addTextChangedListener(myTextWatcher);
        verifyCodeEditText.addTextChangedListener(myTextWatcher);

        AppContext.setAddressInfo(null);    //重新赋值。防止之前保留的数据。
    }

    /**
     * 初始化views
     */
    private void initViews() {
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivBack.setOnClickListener(this);
        mAppend_address_tv_address = (TextView) findViewById(R.id.append_address_tv_address);
        append_textview2 = (TextView) findViewById(R.id.append_textview2);
        append_textview3 = (TextView) findViewById(R.id.append_textview3);
        mAppend_address_et_name = (EditText) findViewById(R.id.append_address_et_name);
        mAppend_address_et_phone = (EditText) findViewById(R.id.append_address_et_phone);
        mAppend_address_rl_area = (RelativeLayout) findViewById(R.id.append_address_rl_area);
        mAppend_address_rl_label = (RelativeLayout) findViewById(R.id.append_address_rl_label);
        mAppend_address_tv_label = (TextView) findViewById(R.id.append_address_tv_label);
        mAppend_address_et_detailedness_address = (EditText) findViewById(R.id.append_address_et_detailedness_address);
        //mAppend_address_et_dawk_code = (EditText) findViewById(R.id.append_address_et_dawk_code);
        mAppend_address_btn_save = (Button) findViewById(R.id.append_address_btn_save);
        append_textview2.requestFocus();
        CommonUtils.setTitle(this,rlTitle);
    }

    /**
     * 获取intent中的值
     */
    private void getIntentData() {

        if (null != intent.getStringExtra("NAME")) {
            mAppend_address_et_name.setText(intent.getStringExtra("NAME"));
        }
        if (null != intent.getStringExtra("PHONE")) {
            mAppend_address_et_phone.setText(intent.getStringExtra("PHONE"));
        }
        if (null != intent.getStringExtra("ADDRESS")) {
            mAppend_address_et_detailedness_address.setText(intent.getStringExtra("ADDRESS"));
        }

        addProvince = intent.getStringExtra("addProvince");
        addProvinceCode = intent.getStringExtra("addProvinceCode");
        addCity = intent.getStringExtra("addCity");
        addCityCode = intent.getStringExtra("addCityCode");
        addCounty = intent.getStringExtra("addCounty");
        addCountyCode = intent.getStringExtra("addCountyCode");
        addTown = intent.getStringExtra("addTown");
        addTownCode = intent.getStringExtra("addTownCode");
        addArea = intent.getStringExtra("addArea");
        addAreaCode = intent.getStringExtra("addAreaCode");
        addressNo = intent.getStringExtra("addressNo");
        addDefault = intent.getStringExtra("addDefault");
        addresslabel = intent.getStringExtra("addresslabel");
        mAddOrEdit = intent.getBooleanExtra("ADD_ADDRESS", false);
        if (mAddOrEdit) {
            tvTitle.setText(R.string.str_pagename_newaddress);
            isShow = false;
        } else {
            tvTitle.setText(R.string.str_pagename_editaddress);
            mAppend_address_tv_address.setText(getAddress());
        }
    }

    /**
     * 获取地址信息
     *
     * @return
     */
    private String getAddress() {
        String address = "";

        if (null != addProvince) {
            address += addProvince;
        }
        if (null != addCity) {
            address += addCity;
        }
        if (null != addCounty) {
            address += addCounty;
        }
        if (null != addTown) {
            address += addTown;
        }
        if (null != addArea) {
            address += addArea;
        }

        return address;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.append_address_rl_area:        //地区信息
                Intent intent = new Intent(this, ProvinceAddressActivity.class);
                intent.putExtra("CODE", "0");
                startActivity(intent);
                break;
            case R.id.append_address_rl_label:        //标签信息
                Intent intent1 = new Intent(this, AddressLabelActivity.class);
                startActivityForResult(intent1, 100);
                break;
            case R.id.append_address_btn_save:        //保存
                if (!submitBeforeExamine()) {
                    break;
                }
                requestAddReceiptAddress();
                break;
            case R.id.btn_appendAddressActivity_getVerifyCode: //获取验证码
                FindPasswordActivity.showImageVerifyCodeDialog(this, AppContext.user.getMobile(), new FindPasswordActivity.ImageVerifyCodeHandler() {
                    @Override
                    public void handlerImageVerifyCode(String imageVerifyCode) {
                        getVerifyCode(AppContext.user.getMobile(), imageVerifyCode);
                    }
                });
                break;
            default:
                break;
        }

    }

    /**
     *
     * 获取短信验证码
      * @param phoneNumber
     * @param imageVerifyCode   获取短信验证码前，先输入图形验证码

     */
    private void getVerifyCode(String phoneNumber, String imageVerifyCode) {
        if (phoneNumber==null || "".equals(phoneNumber)){
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!CommonUtils.isMobilePhoneVerify(phoneNumber) || "147".equals(phoneNumber.substring(0,3))|| "145".equals(phoneNumber.substring(0,3))){		//添加对147和145手机号的限制
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        getVerifyCodeButton.setEnabled(false);
        getVerifyCodeButton.setBackgroundResource(R.drawable.login_page_button_disabled);
        Map<String,String> params = new HashMap<>();
        params.put("mobile", phoneNumber);
//		发送手机验证码,1注册,2修改密码 3 是收货地址验证码
        params.put("type", "3");
        params.put("verifyCode", imageVerifyCode);

        HttpRequest.post(mContext,HttpRequest.QUALITY_MARKET_WEB_URL+HttpRequest.SEND_VERIFY_CODE,params, new AsyncResponseCallBack() {

            @Override
            public void onFailed(String path, String msg) {
                Toast.makeText(AppendAddressActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
                enableGetVerifyButton();
            }


            @Override
            public void onResult(String arg2) {
                String jsonStr = new String(arg2);
                LogUtil.printLog("获取验证码：" + jsonStr);

                Log.e("TAG", jsonStr);
                JSONObject jsonObject = JSON.parseObject(jsonStr);
                if(ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)){

                    // 倒计时60s
                    countDownTime = ConstantsUtil.COUNT_DOWN;
                    Message message = new Message();
                    countDownTime -= 1;
                    message.what = COUNT_DOWN_TASK;
                    message.arg1 = countDownTime;
                    handler.sendMessageDelayed(message, 0);
                }else{
                    enableGetVerifyButton();
                    String errorMsg = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
                    Toast.makeText(AppendAddressActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    /**
     * 启用获取短信验证码button
     */
    private void enableGetVerifyButton() {
        getVerifyCodeButton.setEnabled(true);
        getVerifyCodeButton.setBackgroundResource(R.drawable.button_selector);
        getVerifyCodeButton.setText("获取验证码");
    }

    /**
     * 网络请求添加地址
     */
    private void requestAddReceiptAddress() {
        if (null == mDialog) {
            mDialog = new LoadingAlertDialog(this);
        }
        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
        Map<String,String> params = new HashMap<>();

        setData();

        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("mobile", AppContext.user.getMobile());
        params.put("uname", AppContext.user.getUname());
        params.put("mobileValidCode", verifyCodeEditText.getText().toString().trim());

        params.put("ver", AppContext.getAppVersionName(this));
        params.put("Plat", ConstantsUtil.PLAT);

        params.put("consignee", mReceivingName);            //收货人姓名
        params.put("consigneeMobile", mReceivingPhone);        //收货人手机号码

        params.put("addProvince", addProvince);            // 收货地址-省
        params.put("addProvinceCode", addProvinceCode);    // 收货地址-省编码
        params.put("addCity", addCity);                    // 收货地址-市
        params.put("addCityCode", addCityCode);            // 收货地址-市编码
        params.put("addCounty", addCounty);                // 收货地址-县
        params.put("addCountyCode", addCountyCode);        // 收货地址-县编码

        //params.put("consigneeNo", "consigneeNo");			//收货人编码
        //params.put("addressNo", "12"); 						// 收货地址ID

        params.put("addTown", addTown);                        // 收货地址-镇
        params.put("addTownCode", addTownCode);                // 收货地址-镇编码
        params.put("addArea", addArea);                    // 收货地址-区域（城区或城区以外，几环内几环外）
        params.put("addAreaCode", addAreaCode);                // 收货地址-区域（城区或城区以外，几环内几环外）编码）

        params.put("addDetail", mReceivingAddress);        // 收货地址-详细地址

        params.put("addresslabel", addresslabel);        // 收货人关系标签

        if (null != addressNo && !"".equals(addressNo)) {
            params.put("addressNo", addressNo);
        }
        if (null != addDefault && !"".equals(addDefault)) {
            params.put("addDefault", addDefault);
        } else {
            params.put("addDefault", "2");
        }

        Log.e("TAG", params.toString());

        Log.e("TAG", ":::" + AppContext.user.getUid());

        HttpRequest.addReceiptAddress(mContext,params, new AsyncResponseCallBack() {

            @Override
            public void onResult(String arg2) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (null != arg2 && arg2.length()> 0) {
                    Log.e("TAG", new String(arg2));
                    explainJson(new String(arg2));
                }

            }

            @Override
            public void onFailed(String path, String msg) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                Log.e("TAG", "arg0:" + msg);
                Toast.makeText(AppendAddressActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * 设置数据
     */
    private void setData() {
        AddressInfoBean bean = AppContext.getAddressInfo();
        if (null == bean) {
            return;
        }

        addProvince = bean.getAddProvince();
        addProvinceCode = bean.getAddProvinceCode();
        addCity = bean.getAddCity();
        addCityCode = bean.getAddCityCode();
        addCounty = bean.getAddCounty();
        addCountyCode = bean.getAddCountyCode();
        addTown = bean.getAddTown();
        addTownCode = bean.getAddTownCode();
        addArea = bean.getAddArea();
        addAreaCode = bean.getAddAreaCode();

        if (null == addAreaCode) {
            addArea = "";
            addAreaCode = "";
        }

    }

    /**
     * 08-12 16:26:04.936: E/TAG(32539): {"status":1,"statusDetail":"操作成功","function":"addreceiptaddress"}
     *
     * @param json
     */
    protected void explainJson(String json) {
        DeleteReceiptAddressBean bean = JSON.parseObject(json, DeleteReceiptAddressBean.class);
        if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
            //Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
            if (null != intent && intent.getBooleanExtra(ConfirmOrderActivity.KEY_OF_ADD_CONSIGNEE_ADDRESS, false)) {
                setResult(200, intent);
            } else {

            }
            finish();
        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
            UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
        } else {
            Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击保存前检测
     *
     * @return
     */
    private boolean submitBeforeExamine() {
        mReceivingName = mAppend_address_et_name.getText().toString().trim();
        mReceivingPhone = mAppend_address_et_phone.getText().toString().trim();
        mReceivingAddress = mAppend_address_et_detailedness_address.getText().toString().trim();
        addresslabel = mAppend_address_tv_label.getText().toString().trim();

        if (null == mReceivingName || "".equals(mReceivingName) || mReceivingName.length() < 2 || IDCard.isContainSpecialCharacter(mReceivingName)) {
            Toast.makeText(this, "请输入正确的收货人姓名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (null == mReceivingPhone || "".equals(mReceivingPhone) || !CommonUtils.isMobilePhoneVerify(mReceivingPhone)) {
            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mAddOrEdit) {
            AddressInfoBean bean = AppContext.getAddressInfo();
            if (null == bean || !bean.isOK()) {
                Toast.makeText(this, "请选择收货地址", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (getAddress().length() < 4) {
                Toast.makeText(this, "请选择收货地址", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (null == mReceivingAddress || "".equals(mReceivingAddress) /*|| mReceivingAddress.length() < 4*/) {
            Toast.makeText(this, "请输入详细的收货地址", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*if (null == addresslabel || "".equals(addresslabel) || mReceivingAddress.length() < 4) {
            Toast.makeText(this, "请选择地址标签", Toast.LENGTH_SHORT).show();
			return false;
		}*/

        return true;
    }

    /**
     * 启动activity
     *
     * @param context
     * @param name
     * @param phone
     * @param address
     * @param addProvince
     * @param addProvinceCode
     * @param addCity
     * @param addCityCode
     * @param addCounty
     * @param addCountyCode
     * @param addTown
     * @param addTownCode
     * @param addArea
     * @param addAreaCode
     * @param addressNo       收货地址id
     */
    public static void startActivity(Context context, String name, String phone, String address, String addProvince,
                                     String addProvinceCode, String addCity, String addCityCode, String addCounty, String addCountyCode, String addTown
            , String addTownCode, String addArea, String addAreaCode, String addressNo, String addDefault, String addresslabel) {
        Intent intent = new Intent(context, AppendAddressActivity.class);
        intent.putExtra("NAME", name);
        intent.putExtra("PHONE", phone);
        intent.putExtra("ADDRESS", address);
        intent.putExtra("addProvince", addProvince);
        intent.putExtra("addProvinceCode", addProvinceCode);
        intent.putExtra("addCity", addCity);
        intent.putExtra("addCityCode", addCityCode);
        intent.putExtra("addCounty", addCounty);
        intent.putExtra("addCountyCode", addCountyCode);
        intent.putExtra("addTown", addTown);
        intent.putExtra("addTownCode", addTownCode);
        intent.putExtra("addArea", addArea);
        intent.putExtra("addAreaCode", addAreaCode);
        intent.putExtra("addressNo", addressNo);
        intent.putExtra("addDefault", addDefault);
        intent.putExtra("addresslabel", addresslabel);
        context.startActivity(intent);
        isShow = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AddressInfoBean bean = AppContext.getAddressInfo();
        Log.e("TAG", "isShow:" + isShow + "::" + getAddress());
        if (isShow) {
            append_textview2.setText("");
            mAppend_address_tv_address.setText(getAddress());
            if (null == addresslabel || "".equals(addresslabel)) {
                return;
            }
            mAppend_address_tv_label.setText(addresslabel);
            append_textview3.setText("");
            return;
        }
        if (null == bean || !bean.isOK()) {
            mAppend_address_tv_address.setText("");
            append_textview2.setText("请选择");
            return;
        }

        if (bean.isOK()) {
            setData();
            append_textview2.setText("");
            mAppend_address_tv_address.setText(getAddress());
        }

    }

    @Override
    protected String getPageName() {
        if (getIntent().getBooleanExtra("ADD_ADDRESS", false)) {
            return getString(R.string.str_pagename_newaddress);
        } else {
            return getString(R.string.str_pagename_editaddress);
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent data) {
        super.onActivityResult(arg0, arg1, data);
        if (100 == arg0 && 200 == arg1) {        //选择地址标签
            mAppend_address_tv_label.setText(data.getStringExtra("LABEL"));
            addresslabel = data.getStringExtra("LABEL");
            append_textview3.setText("");
        } else {
            mAppend_address_tv_label.setText("");
            append_textview3.setText("请选择");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivitysManage.getActivitysManager().finishActivity(this);
    }

}
