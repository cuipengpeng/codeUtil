package com.hospital.checkup.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.base.BaseUILocalDataActivity;
import com.hospital.checkup.bean.MeasurerDetailBean;
import com.hospital.checkup.bean.UserInfoBean;
import com.hospital.checkup.http.HttpRequest;
import com.hospital.checkup.utils.LogUtils;
import com.hospital.checkup.utils.ScreenUtils;
import com.hospital.checkup.utils.StringUtil;
import com.hospital.checkup.view.adapter.TestRecordListAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class TesterDetailActivity extends BaseUILocalDataActivity {

    @BindView(R.id.et_testerDetailActivity_testerName)
    EditText testerNameEditText;
    @BindView(R.id.tv_testerDetailActivity_genderValue)
    TextView genderValueTextView;
    @BindView(R.id.tv_testerDetailActivity_birthdayValue)
    TextView birthdayValueTextView;
    @BindView(R.id.et_testerDetailActivity_phoneNum)
    EditText phoneNumEditText;
    @BindView(R.id.et_testerDetailActivity_comment)
    EditText commentEditText;
    @BindView(R.id.tv_testerDetailActivity_startDate)
    TextView startDateTextView;
    @BindView(R.id.tv_testerDetailActivity_endDate)
    TextView endDateTextView;
    @BindView(R.id.tv_testerDetailActivity_testDateList)
    RecyclerView testDateList;

    private TimePickerView pvCustomLunar;
    private final int SELECT_DATE_TYPE_BIRTHDAY=101;
    private final int SELECT_DATE_TYPE_START_DATE=102;
    private final int SELECT_DATE_TYPE_END_DATE=103;
    private int currentDateType = -1;
    private TestRecordListAdapter mTestRecordListAdapter;
    public static final String KEY_OF_JSON_DATA = "jsonDataKey";
    private MeasurerDetailBean measurerDetailBean;

    @OnClick({R.id.tv_testerDetailActivity_genderValue, R.id.tv_testerDetailActivity_startDate, R.id.tv_testerDetailActivity_endDate,
            R.id.tv_testerDetailActivity_birthdayValue, R.id.tv_base_rightMenu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_testerDetailActivity_genderValue:
                showGenderDialog(this);
                break;
            case R.id.tv_testerDetailActivity_startDate:
                showTimePickerView(view, SELECT_DATE_TYPE_START_DATE);
                break;
            case R.id.tv_testerDetailActivity_endDate:
                showTimePickerView(view, SELECT_DATE_TYPE_END_DATE);
                break;
            case R.id.tv_testerDetailActivity_birthdayValue:
                showTimePickerView(view, SELECT_DATE_TYPE_BIRTHDAY);
                break;
            case R.id.tv_base_rightMenu:
                saveMeasurerDetailInfo("");
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "试者详情";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_tester_detail;
    }

    @Override
    protected void initPageData() {
        baseRightMenuTextView.setVisibility(View.VISIBLE);
        baseRightMenuTextView.setText("保存");
        baseRightMenuTextView.setTextColor(getResources().getColor(R.color.chartLineBlue));

        initTimePickerView();
        String jsonData = getIntent().getStringExtra(KEY_OF_JSON_DATA);
        if(StringUtil.notEmpty(jsonData)){
             measurerDetailBean = JSON.parseObject(jsonData, MeasurerDetailBean.class);
             testerNameEditText.setText(measurerDetailBean.getTestName());
             if("0".equals(measurerDetailBean.getTestGender())){
                 genderValueTextView.setText("男");
             }else {
                 genderValueTextView.setText("女");
             }
             birthdayValueTextView.setText(measurerDetailBean.getTestBirth());
             phoneNumEditText.setText(measurerDetailBean.getTestMobile());
             commentEditText.setText(measurerDetailBean.getTestRemark());
        }

        mTestRecordListAdapter = new TestRecordListAdapter(this);
        testDateList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        testDateList.setAdapter(mTestRecordListAdapter);
    }

    /**
     * 时间已扩展至 ： 1900 - 至今
     */
    private void initTimePickerView() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));
        //时间选择器 ，自定义布局
        pvCustomLunar = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String selectedDate = format.format(date);
                switch (currentDateType){
                    case SELECT_DATE_TYPE_BIRTHDAY:
                        birthdayValueTextView.setText(selectedDate);
                        break;
                    case SELECT_DATE_TYPE_START_DATE:
                        startDateTextView.setText(selectedDate);
                        break;
                    case SELECT_DATE_TYPE_END_DATE:
                        endDateTextView.setText(selectedDate);
                        break;
                }
            }
        })  .setDate(selectedDate)
            .setRangDate(startDate, endDate)
            .setLayoutRes(R.layout.pickerview_custom_lunar, new CustomListener() {

                @Override
                public void customLayout(final View v) {
                    final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                    ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                    tvSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pvCustomLunar.returnData();
                            pvCustomLunar.dismiss();
                        }
                    });
                    ivCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pvCustomLunar.dismiss();
                        }
                    });
                }
            })
            .setType(new boolean[]{true, true, true, false, false, false})
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setDividerColor(Color.RED)
            .build();
    }

    public void showGenderDialog(Context context){
        Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_actvity_switch_env, null);
        TextView cancelTextView = contentView.findViewById(R.id.tv_testerDetailActivity_cancel);
        TextView maleTextView = contentView.findViewById(R.id.tv_testerDetailActivity_male);
        TextView femaleTextView = contentView.findViewById(R.id.tv_testerDetailActivity_female);

        cancelTextView.setOnClickListener(v -> dialog.dismiss());
        maleTextView.setOnClickListener(v -> {
            genderValueTextView.setText("男");
            dialog.dismiss();
        });
        femaleTextView.setOnClickListener(v -> {
            genderValueTextView.setText("女");
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

    public void saveMeasurerDetailInfo(String username){
        Map<String, String> params = new HashMap();
//        params.put("username", username);
        HttpRequest.post(HttpRequest.RequestType.GET,this, HttpRequest.SAVE_MEASURER_DETAIL_INFO, params, new HttpRequest.HttpResponseCallBack(){

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                MainAcyivity.open(TesterDetailActivity.this, MainAcyivity.BOTTOM_FRAGMENT01);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void showTimePickerView(View view, int dateType) {
        currentDateType = dateType;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        pvCustomLunar.show();
    }

    public static void open(Context context, String jsonData){
        Intent intent = new Intent(context, TesterDetailActivity.class);
        LogUtils.printLog("json ="+jsonData);
        intent.putExtra(KEY_OF_JSON_DATA, jsonData);
        context.startActivity(intent);
    }
}
