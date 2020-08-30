package com.test.xcamera.dymode.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.test.xcamera.R;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.view.VerticalTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zll on 2020/3/28.
 */

public class DyCommonDialog extends RelativeLayout {
    @BindView(R.id.dy_fpv_common_dialog_title)
    VerticalTextView mTitle;
    @BindView(R.id.dy_fpv_common_dialog_confirm)
    VerticalTextView mConfirm;
    @BindView(R.id.dy_fpv_common_dialog_cancel)
    VerticalTextView mCancel;
    @BindView(R.id.dy_fpv_common_dialog_layout)
    RelativeLayout mDialogLayout;
    @BindView(R.id.dy_fpv_common_dialog_parent)
    RelativeLayout mDialogParent;
    @BindView(R.id.dy_fpv_common_dialog_compress_layout)
    LinearLayout mCompressLayout;
    @BindView(R.id.dy_fpv_common_dialog_delete)
    RelativeLayout mDeleteLayout;
    @BindView(R.id.dy_fpv_common_dialog_compress_state)
    VerticalTextView mConfirmState;

    private Context mContext;

    public enum DialogState {
        DELETE, COMPRESS, COMPRESSING
    }

    private DyCommonDialogCallback mDialogCallback;
    private boolean allowCancel;
    private DialogState mState;

    public DyCommonDialog(Context context) {
        super(context);

        init(context);
    }

    public DyCommonDialog(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void setDialogCallback(DyCommonDialogCallback callback) {
        this.mDialogCallback = callback;
    }

    public void setAllowCancel(boolean cancel) {
        this.allowCancel = cancel;
    }

    public boolean allowCancel() {
        return allowCancel;
    }

    public void setDialogState(DialogState state) {
        this.mState = state;
    }

    public DialogState getDialogState() {
        return mState;
    }

    public void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.dy_fpv_common_dialog, this, true);

        ButterKnife.bind(this);

        setDeleteState();
        mConfirmState.setText(getResources().getString(R.string.dy_compress));
    }

    public void showDeleteLayout() {
        setDialogState(DialogState.DELETE);
        setDeleteState();
        mDeleteLayout.setVisibility(VISIBLE);
        mCompressLayout.setVisibility(GONE);
        show();
    }

    public void showCompressLayout() {
        setDialogState(DialogState.COMPRESS);
        setCompressState();
        mDeleteLayout.setVisibility(VISIBLE);
        mCompressLayout.setVisibility(GONE);
        show();
    }

    public void showCompressingLayout() {
        setDialogState(DialogState.COMPRESSING);
        mCompressLayout.setVisibility(VISIBLE);
        mDeleteLayout.setVisibility(GONE);
        show();
    }

    public void setBtnEnable(boolean enable) {
        if (enable) {
            mConfirm.setAlpha(1f);
            mCancel.setAlpha(1f);
        } else {
            mConfirm.setAlpha(0.5f);
            mCancel.setAlpha(0.5f);
        }
        mConfirm.setEnabled(enable);
        mCancel.setEnabled(enable);
    }

    public void setCompressText(String text) {
        mConfirmState.setText(text);
    }

    public void show() {
        this.setVisibility(VISIBLE);
    }

    public void dismiss() {
        this.setVisibility(GONE);
    }

    private void setDeleteState() {
        mTitle.setText("删除上一段视频？");
        mConfirm.setText("删除");
        mCancel.setText("取消");
    }

    private void setCompressState() {
        mTitle.setText("是否合成视频？");
        mConfirm.setText("确定");
        mCancel.setText("取消");
    }

    @OnClick({R.id.dy_fpv_common_dialog_confirm, R.id.dy_fpv_common_dialog_cancel,
            R.id.dy_fpv_common_dialog_layout, R.id.dy_fpv_common_dialog_parent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dy_fpv_common_dialog_confirm:
                if (mState == DialogState.COMPRESS && !NetworkUtil.isNetworkConnected(mContext)) {
                    CameraToastUtil.show90(mContext.getResources().getString(R.string.dy_error_net), mContext);
                    return;
                }
                if (mDialogCallback != null) mDialogCallback.confirm();
                break;
            case R.id.dy_fpv_common_dialog_cancel:
                if (mDialogCallback != null) mDialogCallback.cancel();
                break;
            case R.id.dy_fpv_common_dialog_layout:
                break;
            case R.id.dy_fpv_common_dialog_parent:
                if (allowCancel) {
//                    dismiss();
                }
                break;
        }
    }

    public interface DyCommonDialogCallback {
        void confirm();

        void cancel();
    }
}
