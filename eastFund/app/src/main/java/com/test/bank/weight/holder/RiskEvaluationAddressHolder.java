package com.test.bank.weight.holder;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.bank.R;
import com.test.bank.bean.AddressBean;
import com.test.bank.utils.FileUtils;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.ToastUtils;
import com.test.bank.utils.UIUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 55 on 2018/1/9.
 */

public class RiskEvaluationAddressHolder extends BaseHolder<String[]> {

    RelativeLayout rlProvince;
    RelativeLayout rlCity;

    TextView tvProvince;
    TextView tvCity;

    EditText etAddr;

    TextView tvLastItem;
    TextView tvNextItem;

    List<AddressBean> addressList;
    ArrayList<String> provinceList;
    ArrayList<String> cityList;

    OptionsPickerView<String> mOptionsPickerView;

    private String selectedProvince = "北京市";
    private String selectedCity = "北京市";
    private boolean isSelctProvince;
    public static boolean testForOpenAccount = false;
    TextView confirmModify;

    @Override
    protected void initView(View rootView) {
        rlProvince = rootView.findViewById(R.id.rl_holderRiskAddr_province);
        rlCity = rootView.findViewById(R.id.rl_holderRiskAddr_city);
        etAddr = rootView.findViewById(R.id.et_holderRiskAddr);
        tvProvince = rootView.findViewById(R.id.tv_holderRiskAddr_province);
        tvCity = rootView.findViewById(R.id.tv_holderRiskAddr_city);
        tvLastItem = rootView.findViewById(R.id.tv_holderRiskAddr_lastQuestion);
        tvNextItem = rootView.findViewById(R.id.tv_holderRiskAddr_nextQuestion);

        mOptionsPickerView = new OptionsPickerView<>(mContext);
        initAddressData();

        mOptionsPickerView.setTitleSize(16);
        mOptionsPickerView.setTitleColor(Color.parseColor("#393b51"));
        mOptionsPickerView.setTitleStyle(true);

        mOptionsPickerView.setSubmitText("确定");
        mOptionsPickerView.setSubmitTextColor(Color.parseColor("#0084ff"));
        mOptionsPickerView.setSubmitTextSize(15);

        mOptionsPickerView.setCancelText("取消");
        mOptionsPickerView.setCancelTextColor(Color.parseColor("#7e819b"));
        mOptionsPickerView.setCancelTextSize(15);

        mOptionsPickerView.setSelectedTextSize(16);
        mOptionsPickerView.setNotSelectedTextSize(13);


        mOptionsPickerView.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {
//                String sex = list.get(option1);
//                Toast.makeText(MainActivity.this, sex, Toast.LENGTH_SHORT).show();

                LogUtils.e("mOptionsPickerView.onOptionsSelect :: " + option1);
                if (isSelctProvince) { //如果选择的是省份并且与上次选择的不一致，则刷新所选省份对应的城市列表
                    if (!selectedProvince.equals(provinceList.get(option1))) {
                        selectedProvince = provinceList.get(option1);
                        tvProvince.setText(selectedProvince);
                        tvProvince.setTextColor(ContextCompat.getColor(mContext, R.color.color_393b51));
                        mappingCityList();
                    }
                } else {
                    if (!selectedCity.equals(cityList.get(option1))) {
                        selectedCity = cityList.get(option1);
                        tvCity.setText(selectedCity);
                        tvCity.setTextColor(ContextCompat.getColor(mContext, R.color.color_393b51));
                    }
                }
            }
        });
        rlProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelctProvince = true;
//                selectDialog.setData(provinceList, true);
//                selectDialog.show();
                mOptionsPickerView.setTitle("请选择省份");
                mOptionsPickerView.setPicker(provinceList);
                mOptionsPickerView.show();
            }
        });

        rlCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelctProvince = false;
//                selectDialog.setData(cityList, false);
//                selectDialog.show();
                mOptionsPickerView.setTitle("请选择城市");
                mOptionsPickerView.setPicker(cityList);
                mOptionsPickerView.show();
            }
        });

        tvLastItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickLastOrNextItemListener != null) {
                    onClickLastOrNextItemListener.onClickLastOrNextItem(true, selectedProvince, selectedCity, etAddr.getText().toString());
                }
            }
        });

        tvNextItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etAddr.getText().toString()) && etAddr.getText().toString().length() < 8) {
                    ToastUtils.showShort("详细地址不能少于8个字符");
                    return;
                }
                if (onClickLastOrNextItemListener != null) {
                    onClickLastOrNextItemListener.onClickLastOrNextItem(false, selectedProvince, selectedCity, etAddr.getText().toString());
                }
            }
        });

        if(testForOpenAccount){
            confirmModify = rootView.findViewById(R.id.tv_holderTestOptionActivity_confirmModify);
            confirmModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(etAddr.getText().toString()) && etAddr.getText().toString().length() < 8) {
                        ToastUtils.showShort("详细地址不能少于8个字符");
                        return;
                    }
                    if (onClickLastOrNextItemListener != null) {
                        onClickLastOrNextItemListener.onClickLastOrNextItem(false, selectedProvince, selectedCity, etAddr.getText().toString());
                    }
                }
            });
            testForOpenAccount = false;
        }
    }

    private void mappingCityList() {
        if (isInitListFinished) {
            cityList.clear();
            for (int i = 0; i < addressList.size(); i++) {
                if (selectedProvince.equals(addressList.get(i).getProvince())) {
                    for (int j = 0; j < addressList.get(i).getCitys().size(); j++) {
                        cityList.add(addressList.get(i).getCitys().get(j).getCity());
                    }
                }
            }
            selectedCity = cityList.get(0);
            tvCity.setText(cityList.get(0));    //更换省份之后自动显示该省份的第一个城市
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtils.e("not init finished.. delay and Retry....");
                    mappingCityList();
                }
            }, 200);
        }
    }


    private void updateContentOnUIThread() {
        selectedProvince = provinceList.get(0);
        tvProvince.setText(provinceList.get(0));
        tvProvince.setTextColor(ContextCompat.getColor(mContext, R.color.color_b9bbca));
        selectedCity = cityList.get(0);
        tvCity.setText(cityList.get(0));
        tvCity.setTextColor(ContextCompat.getColor(mContext, R.color.color_b9bbca));
    }

    @Override
    protected void updateView() {   //显示默认值
        for (int i = 0; i < data.length; i++) {
            if (i == 0) {
                if (!TextUtils.isEmpty(data[0])) {
                    tvProvince.setText(data[0]);
                    selectedProvince = data[0];
                    mappingCityList();
                } else {
                    selectedProvince = addressList.get(0).getProvince();
                    UIUtils.setText(tvProvince, selectedProvince);
                }
            } else if (i == 1) {
                if (!TextUtils.isEmpty(data[1])) {
                    selectedCity = data[1];
                    tvCity.setText(data[1]);
                } else {
                    mappingCityList();
                }
            } else if (i == 2 && !TextUtils.isEmpty(data[2])) {
                etAddr.setText(data[2]);
            }
        }
    }


    @Override
    protected int getLayoutId() {
        if(testForOpenAccount){
            return R.layout.holder_risk_address_for_open_account;
        }else {
            return R.layout.holder_risk_address;
        }
    }

    private boolean isInitListFinished = false;

    private void initAddressData() {
        if (addressList == null) {
            addressList = new ArrayList<>();
        }
        if (provinceList == null) {
            provinceList = new ArrayList<>();
        }
        if (cityList == null) {
            cityList = new ArrayList<>();
        }

        new FileUtils.ReadJsonFromAssertThread("city.txt", new FileUtils.OnReadJsonListener() {
            @Override
            public void onReadSuccess(String jsonResult) {
                LogUtils.e("onReadFinished >>  " + jsonResult);
                Type listType = new TypeToken<List<AddressBean>>() {
                }.getType();
                addressList = new Gson().fromJson(jsonResult, listType);
                for (int i = 0; i < addressList.size(); i++) {
                    provinceList.add(addressList.get(i).getProvince());      //默认显示第一个省份/直辖市
                }
                for (int i = 0; i < addressList.get(0).getCitys().size(); i++) {
                    cityList.add(addressList.get(0).getCitys().get(i).getCity());
                }
                isInitListFinished = true;
                tvProvince.post(new Runnable() {
                    @Override
                    public void run() {
                        updateContentOnUIThread();
                    }
                });
            }

            @Override
            public void onReadFailed(String errorMsg) {
                LogUtils.e("onReadFinished: " + errorMsg);
            }
        }).start();
    }

    private OnClickLastOrNextItemListener onClickLastOrNextItemListener;

    public void setOnClickLastOrNextItemListener(OnClickLastOrNextItemListener onClickLastOrNextItemListener) {
        this.onClickLastOrNextItemListener = onClickLastOrNextItemListener;
    }

    public interface OnClickLastOrNextItemListener {
        void onClickLastOrNextItem(boolean isLastItem, String province, String city, String street);
    }

    public void hidePickerView() {
        if (mOptionsPickerView != null && mOptionsPickerView.isShowing()) {
            mOptionsPickerView.dismiss();
        }
    }
}
