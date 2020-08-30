package com.test.xcamera.phonealbum;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.editvideo.ToastUtil;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.usecase.SubTitleConfig;
import com.test.xcamera.phonealbum.widget.subtitle.bean.MovieSubtitleTimeline;
import com.test.xcamera.phonealbum.widget.subtitle.bean.TitleSubtitleMark;
import com.test.xcamera.utils.KeyboardUtils;
import com.test.xcamera.utils.LogAccessory;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 字幕编辑
 */
public class VideoSubtitleEditActivity extends MOBaseActivity{
    private String mMarkId;

    @BindView(R.id.tv_name_value)
    EditText tvContent;
    @BindView(R.id.tv_input_number)
    TextView mTvInputNumber;

    private TitleSubtitleMark mTitleSubtitleMark;

    private int mMaxLength=16;
    public static void startVideoSubtitleEditActivity(Activity context, String param,int request) {
        Intent intent = new Intent(context, VideoSubtitleEditActivity.class);
        intent.putExtra("param", param);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivityForResult(intent,request);
    }

    @Override
    public int initView() {
        return R.layout.activity_video_subtitle_edit;
    }

    @Override
    public void initData() {
        noStatusBar();
        setStatusBarColor(getResources().getColor(R.color.c050505), 0);
        mMarkId = getIntent().getStringExtra("param");
        mTitleSubtitleMark= (TitleSubtitleMark) MovieSubtitleTimeline.getMark(mMarkId);
        if (mTitleSubtitleMark == null) {
            this.finish();
            return;
        }
        mMaxLength= SubTitleConfig.getSubTitleNumber();

        tvContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(mMaxLength) });
        tvContent.setText(mTitleSubtitleMark.getText());
        tvContent.addTextChangedListener(mTextWatcher);
        mTvInputNumber.setText(getString(R.string.video_edit_subtitle_add_tip,mMaxLength));
        KeyboardUtils.openSoftKeyboard(mContext,tvContent);

    }
    private TextWatcher mTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            int number = editable.length();
            mTvInputNumber.setText(number+"/"+mMaxLength);
            if(number>mMaxLength-1){
                ToastUtil.showToast(mContext,getResources().getString(R.string.video_edit_subtitle_add_toast,mMaxLength));
            }
        }
    };
    @OnClick({R.id.iv_tran_back,R.id.iv_tran_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_tran_back:
                this.finish();
                break;
            case R.id.iv_tran_confirm:
                LogAccessory.getInstance().setLogOpen(tvContent.getText().toString());
                mTitleSubtitleMark.setText(tvContent.getText().toString());
                mTitleSubtitleMark.setTitle(tvContent.getText().toString());
                MovieSubtitleTimeline.replaceMark(mTitleSubtitleMark);
                setResult(MyVideoEditDetailActivity.RESULT_OK_IN_SUBTITLE);
                this.finish();
                break;

        }
    }

}
