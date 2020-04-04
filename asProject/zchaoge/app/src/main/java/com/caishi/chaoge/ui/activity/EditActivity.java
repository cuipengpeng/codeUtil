package com.caishi.chaoge.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseApplication;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.MineDataBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.ui.widget.ClearEditText;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.caishi.chaoge.utils.ConstantUtils.EDIT_FLAG;
import static com.caishi.chaoge.utils.ConstantUtils.EDIT_RETURN_DATA;

public class EditActivity extends BaseUILocalDataActivity {
    @BindView(R.id.cet_edit_userName)
    ClearEditText cet_edit_userName;
    @BindView(R.id.et_edit_userSign)
    EditText et_edit_userSign;
    @BindView(R.id.tv_edit_inputNum)
    TextView tv_edit_inputNum;
    @BindView(R.id.rl_edit_userSign)
    LinearLayout rl_edit_userSign;

    private int editFlag;//1 昵称 2 签名
    private String inputInfo;
    private MineDataBean mineDataBean;

    @OnClick({R.id.tv_base_rightMenu})
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.tv_base_rightMenu://保存
                // 键盘关闭
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                Map<String, String> paramsMap = new HashMap<>();
                if (editFlag == 1) {
                    inputInfo = cet_edit_userName.getText().toString().trim();
                    if (TextUtils.isEmpty(inputInfo)) {
                        Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    paramsMap.put("nickname", inputInfo);
                } else if (editFlag == 2) {
                    inputInfo = et_edit_userSign.getText().toString().trim();
                    if (TextUtils.isEmpty(inputInfo)) {
                        Toast.makeText(this, "请输入个性签名", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    paramsMap.put("remark", inputInfo);
                }

                HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.USER_INFO_UPDATE, paramsMap, new HttpRequest.HttpResponseCallBank() {
                    @Override
                    public void onSuccess(String response) {
                        if (Boolean.valueOf(response)) {
                            if (editFlag == 1) {
                                mineDataBean.nickname = inputInfo;
                            } else if (editFlag == 2) {
                                mineDataBean.remark = inputInfo;
                            }
                            SPUtils.writeThirdAccountBind(EditActivity.this, mineDataBean);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(String t) {
                    }
                });
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_edit;
    }

    @Override
    protected void initPageData() {
        Bundle bundle = getIntent().getExtras();
        editFlag = bundle.getInt(EDIT_FLAG, -1);

        baseTitleTextView.setText((editFlag == 1) ? "修改昵称" : "个性签名");
        baseRightMenuTextView.setVisibility(View.VISIBLE);
        baseRightMenuTextView.setText("保存");
        baseRightMenuTextView.setTextColor(Color.parseColor("#Ffffff"));
        baseRightMenuTextView.setBackgroundResource(R.drawable.shape_main_view);
        RelativeLayout.LayoutParams layoutParams02 = (RelativeLayout.LayoutParams) baseRightMenuTextView.getLayoutParams();
        layoutParams02.width = DisplayMetricsUtil.dip2px(this, 65);
        layoutParams02.height = DisplayMetricsUtil.dip2px(this, 30);
        baseRightMenuTextView.setLayoutParams(layoutParams02);


        mineDataBean = SPUtils.readThirdAccountBind(this, BaseApplication.loginBean.userId);
        if (editFlag == 1) {
            cet_edit_userName.setVisibility(View.VISIBLE);
            cet_edit_userName.setText(mineDataBean.nickname);
            tv_edit_inputNum.setText(cet_edit_userName.getText().length() + "/40");
        } else if (editFlag == 2) {
            rl_edit_userSign.setVisibility(View.VISIBLE);
            et_edit_userSign.setText(mineDataBean.remark);
            tv_edit_inputNum.setText(et_edit_userSign.getText().length() + "/40");
        }

        et_edit_userSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.length() + "/40";
                tv_edit_inputNum.setText(str);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
