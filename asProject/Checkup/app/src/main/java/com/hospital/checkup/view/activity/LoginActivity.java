package com.hospital.checkup.view.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
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

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.base.BaseUILocalDataActivity;
import com.hospital.checkup.bluetooth.BleController;
import com.hospital.checkup.bluetooth.ConnectCallback;
import com.hospital.checkup.bluetooth.ScanCallback;
import com.hospital.checkup.http.HttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends BaseUILocalDataActivity {
    @BindView(R.id.et_loginActivity_userName)
    EditText userNameEditText;
    @BindView(R.id.iv_loginActivity_hidePasswdIcon)
    ImageView hidePasswdIconImageView;
    @BindView(R.id.et_loginActivity_passwd)
    EditText passwdEditText;
    @BindView(R.id.btn_loginActivity_login)
    Button loginButton;
    @BindView(R.id.tv_loginActivity_forgetPasswd)
    TextView forgetPasswdTextView;

    @OnClick({R.id.iv_loginActivity_hidePasswdIcon, R.id.tv_loginActivity_forgetPasswd, R.id.btn_loginActivity_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_loginActivity_hidePasswdIcon:
                TransformationMethod method = passwdEditText.getTransformationMethod();
                if (method == HideReturnsTransformationMethod.getInstance()) {
                    hidePasswdIconImageView.setImageResource(R.mipmap.hidden_password);
                    passwdEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    hidePasswdIconImageView.setImageResource(R.drawable.circle_corner_blue_bg_normal_2dp);
                    passwdEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                Spannable spanText = passwdEditText.getText();
                if (spanText != null) {
                    Selection.setSelection(spanText, spanText.length());
                }
                break;
            case R.id.tv_loginActivity_forgetPasswd:
                CalibrationActivity.open(this);
                break;
            case R.id.btn_loginActivity_login:
                startActivity(new Intent(this, MainAcyivity.class));
//                WebViewActivity.open(this, HttpRequest.H5_SETTING_ACCOUNT);
//                login("15201291660", "123456");
//                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
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
        HttpRequest.post(HttpRequest.RequestType.POST,this, HttpRequest.CHECKUP_LOGIN, params, new HttpRequest.HttpResponseCallBack(){

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(LoginActivity.this, "111222", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
