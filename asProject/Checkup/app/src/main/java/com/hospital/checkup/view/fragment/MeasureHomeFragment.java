package com.hospital.checkup.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.base.BaseUILocalDataFragment;
import com.hospital.checkup.bean.TestModelBean;
import com.hospital.checkup.http.HttpRequest;
import com.hospital.checkup.view.activity.CalibrationActivity;
import com.hospital.checkup.view.activity.TesterDetailActivity;
import com.hospital.checkup.view.activity.WebViewActivity;
import com.hospital.checkup.widget.RegionImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MeasureHomeFragment extends BaseUILocalDataFragment {
    @BindView(R.id.iv_measureHomeActivity_body)
    RegionImageView bodyImageView;
    @BindView(R.id.ll_measureHomeActivity_addMeasurer)
    LinearLayout llMeasureHomeActivityAdddMeasurer;
    @BindView(R.id.tv_measureHomeActivity_measureBodyArea)
    TextView tvMeasureHomeActivityMeasureBodyArea;
    @BindView(R.id.ll_measureHomeActivity_measureBodyArea)
    LinearLayout llMeasureHomeActivityMeasureBodyArea;
    @BindView(R.id.tv_measureHomeActivity_measureContent)
    public TextView measureContentTextView;
    @BindView(R.id.tv_measureHomeActivity_doctorName)
    public TextView doctorNameTextView;
    @BindView(R.id.tv_measureHomeActivity_measurerName)
    public TextView measurerNameTextView;
    @BindView(R.id.iv_measureHomeActivity_addMeasurer)
    public ImageView addUserImageView;
    @BindView(R.id.iv_measureHomeActivity_addDoctor)
    public ImageView addDoctorImageView;
    @BindView(R.id.btn_measureHomeActivity_measure)
    Button measureButton;
    @BindView(R.id.ll_measureHomeActivity_addDoctor)
    LinearLayout llMeasureHomeActivityAddDoctor;

    public static final int BODY_CODE_1 = 1;
    public static final int BODY_CODE_2 = 2;
    public static final int BODY_CODE_3 = 3;
    public static final int BODY_CODE_4 = 4;
    public static final int BODY_CODE_5 = 5;
    public static final int BODY_CODE_6 = 6;

    private List<TestModelBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    @OnClick({R.id.btn_measureHomeActivity_measure, R.id.ll_measureHomeActivity_addDoctor, R.id.ll_measureHomeActivity_addMeasurer,
            R.id.ll_measureHomeActivity_measureBodyArea, R.id.ll_measureHomeActivity_measureContent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_measureHomeActivity_measure:
                CalibrationActivity.open(getActivity());
                break;
            case R.id.ll_measureHomeActivity_addDoctor:
                WebViewActivity.open(getActivity(), HttpRequest.H5_ADD_DOCTOR, true);
                break;
            case R.id.ll_measureHomeActivity_addMeasurer:
                WebViewActivity.open(getActivity(), HttpRequest.H5_ADD_TESTER, true);
                break;
            case R.id.ll_measureHomeActivity_measureBodyArea:
                break;
            case R.id.ll_measureHomeActivity_measureContent:
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "测量";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_measure_home;
    }

    @Override
    protected void initPageData() {
        bodyImageView.setOnImageViewAreaClickListener(new RegionImageView.OnImageViewAreaClickListener() {
            @Override
            public void onAreaClick(int areaIndex) {
                Toast.makeText(BaseApplication.applicationContext, "----" + areaIndex, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showPickerView() {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getModelName() : "";

                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

                String opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";

                String tx = opt1tx + opt2tx + opt3tx;
                Toast.makeText(BaseApplication.applicationContext, tx, Toast.LENGTH_SHORT).show();
            }
        })

                .setTitleText("测量选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
//        pvOptions.setSelectOptions();
        pvOptions.show();
    }

    private void initJsonData() {//解析数据
        for (int i = 0; i < options1Items.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < options1Items.get(i).getChildren().size(); c++) {//遍历该省份的所有城市
                String cityName = options1Items.get(i).getChildren().get(c).getModelName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表
                for(int j=0; j<options1Items.get(i).getChildren().get(c).getChildren().size();j++){
                    city_AreaList.add(options1Items.get(i).getChildren().get(c).getChildren().get(j).getModelName());
                }
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }
            options2Items.add(cityList);
            options3Items.add(province_AreaList);
        }
    }

    public static void open(Context context) {
        Intent intent = new Intent(context, MeasureHomeFragment.class);
        context.startActivity(intent);
    }

}
