package com.hospital.checkup.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseUILocalDataActivity;
import com.hospital.checkup.utils.ScreenUtils;
import com.hospital.checkup.view.adapter.TestRecordListAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @OnClick({R.id.tv_testerDetailActivity_genderValue, R.id.tv_testerDetailActivity_startDate, R.id.tv_testerDetailActivity_endDate,
            R.id.tv_testerDetailActivity_birthdayValue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_testerDetailActivity_genderValue:
                showGenderDialog(this);
                break;
            case R.id.tv_testerDetailActivity_startDate:
                currentDateType = SELECT_DATE_TYPE_START_DATE;
                pvCustomLunar.show();
                break;
            case R.id.tv_testerDetailActivity_endDate:
                currentDateType = SELECT_DATE_TYPE_END_DATE;
                pvCustomLunar.show();
                break;
            case R.id.tv_testerDetailActivity_birthdayValue:
                currentDateType = SELECT_DATE_TYPE_BIRTHDAY;
                pvCustomLunar.show();
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
        initLunarPicker();
        mTestRecordListAdapter = new TestRecordListAdapter(this);
        testDateList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        testDateList.setAdapter(mTestRecordListAdapter);
    }

    /**
     * 时间已扩展至 ： 1900 - 至今
     */
    private void initLunarPicker() {
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

    public static void open(Context context){
        Intent intent = new Intent(context, TesterDetailActivity.class);
        context.startActivity(intent);
    }
}
