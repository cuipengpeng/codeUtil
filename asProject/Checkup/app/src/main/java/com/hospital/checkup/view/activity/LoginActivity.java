package com.hospital.checkup.view.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.base.BaseUILocalDataActivity;
import com.hospital.checkup.bean.UserInfoBean;
import com.hospital.checkup.bluetooth.BleController;
import com.hospital.checkup.bluetooth.ConnectCallback;
import com.hospital.checkup.bluetooth.ScanCallback;
import com.hospital.checkup.http.HttpRequest;
import com.hospital.checkup.view.fragment.WebViewFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends BaseUILocalDataActivity {
    @BindView(R.id.et_loginActivity_userName)
    EditText userNameEditText;
    @BindView(R.id.et_loginActivity_passwd)
    EditText passwdEditText;
    @BindView(R.id.iv_loginActivity_hidePasswdIcon)
    ImageView hidePasswdIconImageView;
    @BindView(R.id.btn_loginActivity_login)
    Button loginButton;
    @BindView(R.id.tv_loginActivity_forgetPasswd)
    TextView forgetPasswdTextView;

    private Map<String, String> userMap = new HashMap<>();

    @OnClick({R.id.iv_loginActivity_hidePasswdIcon, R.id.tv_loginActivity_forgetPasswd, R.id.tv_loginActivity_register, R.id.btn_loginActivity_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_loginActivity_hidePasswdIcon:
                TransformationMethod method = passwdEditText.getTransformationMethod();
                if (method == HideReturnsTransformationMethod.getInstance()) {
                    hidePasswdIconImageView.setImageResource(R.mipmap.hidden_password);
                    passwdEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    hidePasswdIconImageView.setImageResource(R.mipmap.show_password);
                    passwdEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                Spannable spanText = passwdEditText.getText();
                if (spanText != null) {
                    Selection.setSelection(spanText, spanText.length());
                }
                break;
            case R.id.tv_loginActivity_forgetPasswd:
                WebViewActivity.open(this, HttpRequest.H5_RESET_PASSWORD);
                //WebViewActivity.open(this, HttpRequest.H5_SETTING_ACCOUNT);
                //startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                break;
            case R.id.tv_loginActivity_register:
                WebViewActivity.open(this, HttpRequest.H5_REGISTER);
                break;
            case R.id.btn_loginActivity_login:
                String userName = userNameEditText.getText().toString().trim();
                String password = passwdEditText.getText().toString().trim();
                login("132012341660", "123456");

//                if(userMap.keySet().contains(userName)){
//                    if(userMap.get(userName).equalsIgnoreCase(password)){
//                        login("132012341660", "123456");
//                    }else {
//                        Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
//                    }
//                }else {
//                    Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
//                }
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initPageData() {
        userMap.put("123456","123456");
        userMap.put("1","1");
        showBaseUITitle = false;
        BleController.getInstance().init(BaseApplication.applicationContext);
        XXPermissions.with(this).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        .constantRequest().request(new OnPermission() {

            @Override
            public void hasPermission(List<String> granted, boolean all) {

            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {

            }
        });
    }


    public void login(String username, String passwd){
        Map<String, String> params = new HashMap();
        params.put("username", username);
        params.put("password", passwd);
        HttpRequest.post(HttpRequest.RequestType.GET,this, HttpRequest.CHECKUP_LOGIN, params, new HttpRequest.HttpResponseCallBack(){

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                BaseApplication.userInfo = JSON.parseObject(response.body().toString().trim(), UserInfoBean.class);
                startActivity(new Intent(LoginActivity.this, MainAcyivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
    }

    public static void open(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
