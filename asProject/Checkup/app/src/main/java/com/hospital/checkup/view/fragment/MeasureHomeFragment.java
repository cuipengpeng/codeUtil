package com.hospital.checkup.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseUILocalDataFragment;
import com.hospital.checkup.bean.TestModelBean;
import com.hospital.checkup.http.HttpRequest;
import com.hospital.checkup.utils.LogUtils;
import com.hospital.checkup.utils.ScreenUtils;
import com.hospital.checkup.utils.StringUtil;
import com.hospital.checkup.view.activity.CalibrationActivity;
import com.hospital.checkup.view.activity.WebViewActivity;
import com.hospital.checkup.widget.RegionImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class MeasureHomeFragment extends BaseUILocalDataFragment {
    @BindView(R.id.iv_measureHomeActivity_body)
    RegionImageView bodyImageView;
    @BindView(R.id.ll_measureHomeActivity_addMeasurer)
    LinearLayout addMeasurerLinearLayout;
    @BindView(R.id.tv_measureHomeActivity_measureBodyArea)
    TextView measureBodyTextView;
    @BindView(R.id.ll_measureHomeActivity_measureBodyArea)
    LinearLayout measureBodyAreaLinearLayout;
    @BindView(R.id.rl_measureHomeActivity_measureLeftOrRight)
    RelativeLayout measureLeftOrRightRelativeLayout;
    @BindView(R.id.tv_measureHomeActivity_measureContent)
    public TextView measureContentTextView;
    @BindView(R.id.tv_measureHomeActivity_measureLeftOrRight)
    public TextView measureLeftOrRightTextView;
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
    public static final int BODY_CODE_7 = 7;
    public static final int BODY_CODE_8 = 8;

    private List<TestModelBean> mTestModelBeanList = new ArrayList<>();
    private List<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private Map<Integer, TestModelBean> modelCodeMap = new HashMap();

    @OnClick({R.id.btn_measureHomeActivity_measure, R.id.ll_measureHomeActivity_addDoctor, R.id.ll_measureHomeActivity_addMeasurer,
            R.id.ll_measureHomeActivity_measureBodyArea, R.id.ll_measureHomeActivity_measureContent, R.id.ll_measureHomeActivity_measureLeftOrRight})
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
            case R.id.ll_measureHomeActivity_measureContent:
                showPickerView(BODY_CODE_1);
                break;
            case R.id.ll_measureHomeActivity_measureLeftOrRight:
                showMeasureLeftOrRightDialog(getActivity());
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
        getModelDataList();

        bodyImageView.setOnImageViewAreaClickListener(new RegionImageView.OnImageViewAreaClickListener() {
            @Override
            public void onAreaClick(int areaIndex) {
//                Toast.makeText(BaseApplication.applicationContext, areaIndex+"", Toast.LENGTH_SHORT).show();
                showPickerView(areaIndex);
            }
        });
    }


    public void getModelDataList(){
        Map<String, String> params = new HashMap();
        HttpRequest.post(HttpRequest.RequestType.GET,this, HttpRequest.TEST_MODEL_LIST, params, new HttpRequest.HttpResponseCallBack(){

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LogUtils.printLog(response.body().toString());
                mTestModelBeanList = JSON.parseArray(response.body().toString().trim(), TestModelBean.class);
                if(mTestModelBeanList.size()>0){
                    TestModelBean  testModelBean = mTestModelBeanList.get(0);
                    measureBodyTextView.setText(testModelBean.getModelName());
                    measureContentTextView.setText(testModelBean.getChildren().get(0).getModelName()+"、"+testModelBean.getChildren().get(0).getChildren().get(0).getModelName());
                    for(int i = 0; i< mTestModelBeanList.size(); i++){
                        modelCodeMap.put(mTestModelBeanList.get(i).getModelCode(), mTestModelBeanList.get(i));
                    }
                    initJsonData();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void showPickerView(int modelCode) {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1): "";

                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

                String opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";

//                String tx = opt1tx + opt2tx + opt3tx;
//                Toast.makeText(BaseApplication.applicationContext, tx, Toast.LENGTH_SHORT).show();

                if(StringUtil.notEmpty(opt1tx) && (opt1tx.contains("躯干")|| opt1tx.contains("颈"))){
                    measureLeftOrRightRelativeLayout.setVisibility(View.GONE);
                }else {
                    measureLeftOrRightRelativeLayout.setVisibility(View.VISIBLE);
                }
                measureBodyTextView.setText(opt1tx);
                measureContentTextView.setText(opt2tx+"、"+opt3tx);
            }
        })
        .setOutSideCancelable(false)
        .setTitleText("测量选择")
        .setDividerColor(Color.BLACK)
        .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
        .setContentTextSize(20)
        .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器

        for(int i=0; i<options1Items.size(); i++){
            if(modelCodeMap.get(modelCode).getModelName().equals(options1Items.get(i))){
                pvOptions.setSelectOptions(i);
                break;
            }
        }
        pvOptions.show();
    }

    private void initJsonData() {//解析数据
        for (int i = 0; i < mTestModelBeanList.size(); i++) {//
            options1Items.add(mTestModelBeanList.get(i).getModelName());
            ArrayList<String> cityList = new ArrayList<>();//（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//（第三极）
            for (int c = 0; c < mTestModelBeanList.get(i).getChildren().size(); c++) {
                String cityName = mTestModelBeanList.get(i).getChildren().get(c).getModelName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();
                for(int j=0; j<mTestModelBeanList.get(i).getChildren().get(c).getChildren().size();j++){
                    city_AreaList.add(mTestModelBeanList.get(i).getChildren().get(c).getChildren().get(j).getModelName());
                }
                province_AreaList.add(city_AreaList);
            }
            options2Items.add(cityList);
            options3Items.add(province_AreaList);
        }
    }

    public void showMeasureLeftOrRightDialog(Context context){
        Dialog dialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_actvity_switch_env, null);
        TextView cancelTextView = contentView.findViewById(R.id.tv_testerDetailActivity_cancel);
        TextView leftTextView = contentView.findViewById(R.id.tv_testerDetailActivity_male);
        TextView rightTextView = contentView.findViewById(R.id.tv_testerDetailActivity_female);
        leftTextView.setText("左");
        rightTextView.setText("右");

        cancelTextView.setOnClickListener(v -> dialog.dismiss());
        leftTextView.setOnClickListener(v -> {
            measureLeftOrRightTextView.setText("左");
            dialog.dismiss();
        });
        rightTextView.setOnClickListener(v -> {
            measureLeftOrRightTextView.setText("右");
            dialog.dismiss();
        });
        dialog.setContentView(contentView);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 0;
        lp.width = ScreenUtils.getScreenWidth(context);
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    public static void open(Context context) {
        Intent intent = new Intent(context, MeasureHomeFragment.class);
        context.startActivity(intent);
    }

}
