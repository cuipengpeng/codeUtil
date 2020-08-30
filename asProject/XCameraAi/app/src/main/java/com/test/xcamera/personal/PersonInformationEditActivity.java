package com.test.xcamera.personal;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.editvideo.ToastUtil;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.bean.User;
import com.test.xcamera.login.LoginActivty;
import com.test.xcamera.personal.bean.UserInformationParam;
import com.test.xcamera.personal.presenter.UserPresenterImpl;
import com.test.xcamera.utils.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人信息修改
 */
public class PersonInformationEditActivity extends MOBaseActivity implements PersonUserInterface {
    private final String IMAGE_ID_INVALID = "-1";
    private UserInformationParam mUserInformationParam;
    @BindView(R.id.tv_middle_title)
    TextView mTvMiddleTitle;
    @BindView(R.id.right_tv_titlee)
    TextView mTvRightTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_name_value)
    EditText tvContent;
    @BindView(R.id.tv_input_number)
    TextView mTvInputNumber;

    private UserPresenterImpl mUserPresenter;
    private int mMaxLength = 10;

    public static void startPersonInformationEditActivity(Activity context, UserInformationParam param, int request) {
        Intent intent = new Intent(context, PersonInformationEditActivity.class);
        intent.putExtra("param", param);
        context.startActivityForResult(intent, request);
    }

    @Override
    public int initView() {
        return R.layout.activity_person_infomation_edit;
    }

    @Override
    public void initData() {
        mUserPresenter = new UserPresenterImpl(this, this);
        mUserInformationParam = getIntent().getParcelableExtra("param");
        if (mUserInformationParam == null) {
            this.finish();
        }
        mTvMiddleTitle.setText(mUserInformationParam.getTile());
        mTvRightTitle.setText(getString(R.string.video_edit_apply));
        mTvRightTitle.setVisibility(View.VISIBLE);
        tvName.setText(mUserInformationParam.getName());
        if (mUserInformationParam.getType() == UserInformationParam.USER_INFORMATION_TYPE_NAME) {
            mMaxLength = 20;
        } else {
            mMaxLength = 90;
        }
        tvContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxLength)});
        tvContent.setText(mUserInformationParam.getValue());
        if (TextUtils.isEmpty(mUserInformationParam.getValue())) {
            mTvRightTitle.setEnabled(false);
            mTvRightTitle.setTextColor(getResources().getColorStateList(R.color.color_77788B));
        } else {
            mTvRightTitle.setEnabled(true);
            mTvRightTitle.setTextColor(getResources().getColorStateList(R.color.white));

        }
        tvContent.setHint(getString(R.string.person_edit_input) + mUserInformationParam.getName());
        tvContent.setHintTextColor(getResources().getColorStateList(R.color.color_77788B));
        tvContent.addTextChangedListener(mTextWatcher);
        mTvInputNumber.setText(mUserInformationParam.getValue().length() + "/" + mMaxLength);
        tvContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!TextUtils.isEmpty(v.getText())) {
                    int number = v.getText().length();
                    if (number > mMaxLength - 1) {
                        ToastUtil.showToast(mContext, mUserInformationParam.getName() + getResources().getString(R.string.person_subtitle_add_toast, mMaxLength));
                    }
                }

                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });

    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            int number = editable.length();
            mTvInputNumber.setText(number + "/" + mMaxLength);
            if (number == 0) {
                mTvRightTitle.setEnabled(false);
                mTvRightTitle.setTextColor(getResources().getColorStateList(R.color.color_77788B));
            } else {
                mTvRightTitle.setEnabled(true);
                mTvRightTitle.setTextColor(getResources().getColorStateList(R.color.white));

            }
            if (number > mMaxLength - 1) {
                ToastUtil.showToast(mContext, mUserInformationParam.getName() + getResources().getString(R.string.person_subtitle_add_toast, mMaxLength));
            }
        }
    };

    @OnClick({R.id.left_iv_title, R.id.right_iv_title, R.id.right_tv_titlee})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_iv_title:
                this.finish();
                break;
            case R.id.right_tv_titlee:
/*
                mUserPresenter.userProfile(IMAGE_ID_INVALID);
*/
                getUserMassage();
                Intent intent = getIntent();
                intent.putExtra("data", mUserInformationParam);
                setResult(2020, intent);
                finish();
                break;

        }
    }

    @Override
    public User.UserDetail getUserMassage() {
        User.UserDetail userDetail = SPUtils.getUser(this);
        if (TextUtils.isEmpty(userDetail.getAvatarFileId())) {
            userDetail.setAvatarFileId(IMAGE_ID_INVALID);
        }
        if (TextUtils.isEmpty(tvContent.getText())) {
            return userDetail;
        }
        String text = tvContent.getText().toString();
        mUserInformationParam.setValue(text);
        if (mUserInformationParam.getType() == UserInformationParam.USER_INFORMATION_TYPE_NAME) {
            userDetail.setNickname(text);
        } else if (mUserInformationParam.getType() == UserInformationParam.USER_INFORMATION_INTRODUCE) {
            userDetail.setDescription(text);
        }
        return userDetail;
    }

    @Override
    public void editUserCallBack(String msg) {
        this.finish();
    }

    @Override
    public void unTokenCallBack(String msg) {
        SPUtils.unLogin(mContext);
        Intent intent = new Intent(this, LoginActivty.class);
        startActivity(intent);
    }
}
