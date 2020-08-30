package com.test.xcamera.personal;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.User;
import com.test.xcamera.bean.UserInfo;
import com.test.xcamera.login.LoginActivty;
import com.test.xcamera.personal.bean.UserInformationParam;
import com.test.xcamera.personal.presenter.UserPresenterImpl;
import com.test.xcamera.personal.usecase.OnGetUserInfoCallBack;
import com.test.xcamera.personal.util.ImageSelectUtils;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.GlideUtils;
import com.test.xcamera.utils.PermissionUtils;
import com.test.xcamera.utils.SPUtils;

import java.io.File;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class PersonInformationActivity extends MOBaseActivity implements PersonUserInterface {
    private final int TAKE_PHOTO_REQ = 1;
    private final int CUT_PHOTO_REQ = 2;
    private final int UPDATE_USER = 3;
    private final int IMAGE_WH = 400;
    String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public final int ALBUM_PERMISSIONS_CODE = 3000;
    private User.UserDetail mUserDetail;
    private User.UserDetail mEditUserDetail;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.right_tv_titlee)
    TextView mTvRightTitle;
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_name_value)
    TextView tvName;
    @BindView(R.id.tv_introduce_value)
    TextView tvContent;
    @BindView(R.id.liner_unlogin)
    LinearLayout unlogin;
    private UserPresenterImpl mUserPresenter;

    private File mTakePhotoFile;
    private File mCutPhotoFile;
    private UserInfo mUserInfo;
    private long mClickTime = 0;

    public static void startPersonInformationActivity(Context context) {
        Intent intent = new Intent(context, PersonInformationActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int initView() {
        return R.layout.activity_person_infomation;
    }

    @Override
    public void initData() {
        mTvRightTitle.setEnabled(false);
        tvMiddleTitle.setText(R.string.person_edit_title);
        mUserDetail = AiCameraApplication.userDetail;
        mEditUserDetail = mUserDetail.copy();
        setEditUser(mEditUserDetail);
        setReviewStatus(mUserDetail.getReviewStatus());
        mUserPresenter = new UserPresenterImpl(this, this);
        if (mUserPresenter != null) {
            mUserPresenter.getUserInfo(new OnGetUserInfoCallBack() {
                @Override
                public void onUserInfoCallBack(UserInfo userInfo) {
                    mUserInfo = userInfo;
                    if (mUserInfo != null && mUserInfo.getData() != null) {
                        setReviewStatus(userInfo.getData().getReviewStatus());
                    }
                    mUserDetail = AiCameraApplication.userDetail;
                    mEditUserDetail = mUserDetail.copy();
                    setEditUser(mEditUserDetail);
                }
            });
        }
    }

    private void setEditUser(User.UserDetail detail) {
        if (AiCameraApplication.isLogin()) {
            tvName.setText(detail.getNickname());
            if (!TextUtils.isEmpty(detail.getDescription())) {
                tvContent.setText(detail.getDescription());
            }
            GlideUtils.GlideLoaderHeader(this, Constants.getFileIdToUrl(AiCameraApplication.userDetail.getAvatarFileId()), ivHead);
        }
    }

    private boolean checkExamine(int status) {
        if (mUserDetail != null) {
            if (mUserDetail.getReviewStatus() == 0) {
                show(getString(R.string.person_edit_examine_check));
                return false;
            }
        }
        return true;
    }

    /**
     * 0审核中 1审核通过 2审核失败
     *
     * @param status
     */
    public void setReviewStatus(int status) {
        if (mTvRightTitle == null) {
            return;
        }
        if (status == 0) {
            mTvRightTitle.setEnabled(false);
            mTvRightTitle.setText(getString(R.string.person_edit_examine));
        } else {
            mTvRightTitle.setEnabled(true);
            mTvRightTitle.setText(getString(R.string.person_edit_save));

        }
        mTvRightTitle.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @OnClick({R.id.rl_name, R.id.rl_introduce, R.id.left_iv_title, R.id.iv_head, R.id.tv_edit_tip, R.id.liner_unlogin, R.id.right_tv_titlee})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_head:
                if (checkExamine(mUserDetail.getReviewStatus()))
                    check_permisson();
                break;
            case R.id.tv_edit_tip:
                if (checkExamine(mUserDetail.getReviewStatus())) {
                    check_permisson();
                }
                break;
            case R.id.rl_name:
                if (checkExamine(mUserDetail.getReviewStatus()))
                    startUserEdit(UserInformationParam.USER_INFORMATION_TYPE_NAME);
                break;
            case R.id.rl_introduce:
                if (checkExamine(mUserDetail.getReviewStatus()))
                    startUserEdit(UserInformationParam.USER_INFORMATION_INTRODUCE);
                break;
            case R.id.left_iv_title:
                back();
                break;
            case R.id.liner_unlogin:
                UNLoginDialog deleteDialog = new UNLoginDialog(this);
                deleteDialog.showGoneTitleDialog();
                deleteDialog.setDialogContent(15, getString(R.string.person_get_out));
                deleteDialog.setButtonClickListener(new UNLoginDialog.ButtonClickListener() {
                    @Override
                    public void sureButton(Dialog mDialog) {
                        SPUtils.unLogin(mContext);
                        PersonInformationActivity.this.finish();
                    }

                    @Override
                    public void cancelButton(Dialog mDialog) {

                    }
                });

                break;
            case R.id.right_tv_titlee:
                commitUserInfo();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (mUserDetail.getReviewStatus() == 0) {
            finish();
            return;
        }
        UNLoginDialog deleteDialog = new UNLoginDialog(this);
        deleteDialog.showVisibilityTitleDialog(14, getString(R.string.person_get_out_edit));
        deleteDialog.setDialogContent(12, getString(R.string.person_get_out_edit_context));
        deleteDialog.setButtonClickListener(new UNLoginDialog.ButtonClickListener() {
            @Override
            public void sureButton(Dialog mDialog) {
                PersonInformationActivity.this.finish();
            }

            @Override
            public void cancelButton(Dialog mDialog) {

            }
        });
    }

    private void startUserEdit(int type) {
        UserInformationParam param = new UserInformationParam();
        if (type == UserInformationParam.USER_INFORMATION_TYPE_NAME) {
            param.setName(getString(R.string.person_edit_name));
            param.setTile(getString(R.string.person_edit_name));
            param.setValue(mEditUserDetail.getNickname());
        } else if (type == UserInformationParam.USER_INFORMATION_INTRODUCE) {
            param.setName(getString(R.string.person_edit_introduce));
            param.setTile(getString(R.string.person_edit_introduce));
            param.setValue(mEditUserDetail.getDescription());
        }
        param.setType(type);
        PersonInformationEditActivity.startPersonInformationEditActivity(this, param, UPDATE_USER);
    }

    @Override
    public User.UserDetail getUserMassage() {
        return mEditUserDetail;
    }

    @Override
    public void editUserCallBack(String msg) {
        if (ivHead != null) {
            mUserDetail = SPUtils.getUser(this);
            GlideUtils.GlideLoaderHeader(this, mUserDetail.getAvatarFilePath(), ivHead);
            setReviewStatus(mUserDetail.getReviewStatus());
        }
        finish();
    }

    @Override
    public void unTokenCallBack(String msg) {
        SPUtils.unLogin(mContext);
        Intent intent = new Intent(this, LoginActivty.class);
        startActivity(intent);
    }

    public void startSelectPicture() {
        mTakePhotoFile = ImageSelectUtils.getPhotoSelect(this, "selectPicture");
        Uri uri = Uri.fromFile(mTakePhotoFile);
        startActivityForResult(ImageSelectUtils.getPhotoSelectIntent(uri), TAKE_PHOTO_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQ && resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == TAKE_PHOTO_REQ) {
            // 处理图片选择结果
            mCutPhotoFile = ImageSelectUtils.getPhotoSelect(this, "selectPicture_avatar" + System.currentTimeMillis());
            Uri cutUri = Uri.fromFile(mCutPhotoFile);
            if (data != null) {
                startActivityForResult(ImageSelectUtils.getImageCropIntent(data.getData(), cutUri, IMAGE_WH, IMAGE_WH), CUT_PHOTO_REQ);
            } else {
                Uri uri = ImageSelectUtils.getContentUri(this.getApplicationContext(), mTakePhotoFile);
                Intent intent = ImageSelectUtils.getImageCropIntent(uri, cutUri, IMAGE_WH, IMAGE_WH);
                startActivityForResult(intent, CUT_PHOTO_REQ);
            }
        } else if (requestCode == CUT_PHOTO_REQ) {/*头像选择回调*/
            if (mCutPhotoFile.exists()) {
                GlideUtils.GlideLoaderHeaderSkip(this, mCutPhotoFile.getAbsolutePath(), ivHead);

            } else {
                GlideUtils.GlideLoaderHeader(this, mUserDetail.getAvatarFilePath(), ivHead);
            }
        } else if (UPDATE_USER == requestCode) {
            if (data != null) {
                UserInformationParam param = data.getParcelableExtra("data");
                if (param.getType() == UserInformationParam.USER_INFORMATION_TYPE_NAME) {
                    mEditUserDetail.setNickname(param.getValue());
                } else if (param.getType() == UserInformationParam.USER_INFORMATION_INTRODUCE) {
                    mEditUserDetail.setDescription(param.getValue());
                }
                tvName.setText(mEditUserDetail.getNickname());
                if (!TextUtils.isEmpty(mEditUserDetail.getDescription())) {
                    tvContent.setText(mEditUserDetail.getDescription());
                }
            }
        }
    }

    public void commitUserInfo() {
        if (System.currentTimeMillis() - mClickTime > 1000) {
            mClickTime = System.currentTimeMillis();
            if (mCutPhotoFile != null && mCutPhotoFile.exists()) {
                mUserPresenter.uploadFile(mCutPhotoFile.getAbsolutePath());
            } else {
                mUserPresenter.userProfile("-1");
            }

        }
    }

    public void check_permisson() {
        PermissionUtils.checkAndRequestMorePermissions(mContext, PERMISSIONS, ALBUM_PERMISSIONS_CODE, new PermissionUtils.PermissionRequestSuccessCallBack() {
            @Override
            public void onHasPermission() {
                // 权限已被授予
                startSelectPicture();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALBUM_PERMISSIONS_CODE:
                PermissionUtils.onRequestMorePermissionsResult(mContext, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        startSelectPicture();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        CameraToastUtil.show(getString(R.string.person_edit_permission, Arrays.toString(permission)), mContext);
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        CameraToastUtil.show(getString(R.string.person_edit_permission, Arrays.toString(permission)), mContext);

                    }
                });
        }

    }

    @Override
    protected void onDestroy() {
        if (mCutPhotoFile != null && mCutPhotoFile.exists()) {
            mCutPhotoFile.delete();
        }
        super.onDestroy();

    }
}
