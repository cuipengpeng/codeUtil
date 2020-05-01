package com.jfbank.qualitymarket.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.JumpUtil;
import com.jfbank.qualitymarket.util.ToastUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadLayout;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jph.takephoto.app.TakePhoto;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Activity基类
 * Created by z2wenfa on 2016/8/25.
 */
public abstract class BaseMvpFragment<T extends BasePresenter, E extends BaseModel> extends BaseFragment implements BaseView {
    public T mPresenter;
    public E mModel;
    private LoadingAlertDialog mDialogProgress;
    public static String TAG = "";
    public LinearLayout content;
    @InjectView(R.id.iv_empty)
    public ImageView ivEmpty;
    @InjectView(R.id.tv_empty)
    public TextView tvEmpty;
    @InjectView(R.id.btn_empty_retry)
    public Button btnEmptyRetry;
    @InjectView(R.id.iv_error)
    public ImageView ivError;
    @InjectView(R.id.tv_error)
    public TextView tvError;
    @InjectView(R.id.btn_error_retry)
    public Button btnErrorRetry;
    @InjectView(R.id.tv_loading)
    public TextView tvLoading;
    @InjectView(R.id.loadlayout)
    public LoadLayout loadlayout;

    public boolean isInidData() {
        return isInidData;
    }

    public void setInidData(boolean inidData) {
        isInidData = inidData;
    }

    boolean isInidData = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        View contentView = LayoutInflater.from(mContext).inflate(getLayoutResID(), null);
        content = (LinearLayout) view.findViewById(R.id.content);
        content.addView(contentView, new LinearLayout.LayoutParams(-1, -1));
        ButterKnife.inject(this, view);
        view.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public TakePhoto getTakePhoto() {
        return super.getTakePhoto();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    protected void init() {
        TAG = getClass().getName();
        mPresenter = TUtil.getT(this, 0);
        mModel = TUtil.getT(this, 1);
        mPresenter.setVM(mContext, this, mModel);
        initDialog();
        initView();
        if (isInidData)
            initData();
    }

    private void initDialog() {
        mDialogProgress = new LoadingAlertDialog(mContext);
        mDialogProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mPresenter.cancel();
            }
        });
    }

    @Override
    public void onFinishReFreshView() {

    }

    @Override
    public void onFinishLoadMoreView() {

    }

    /**
     * 获得Layout文件id
     *
     * @return
     */
    protected abstract int getLayoutResID();

    protected abstract void initData();

    protected abstract void initView();

    @Override
    public void showDialog() {
        showDialog(false, ConstantsUtil.NETWORK_REQUEST_IN);
    }

    @Override
    public void showDialog(boolean isCancel) {
        showDialog(isCancel, ConstantsUtil.NETWORK_REQUEST_IN);
    }

    @Override
    public void showDialog(boolean isCancel, final String msg) {
        mDialogProgress.setCancelable(isCancel);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mDialogProgress.isShowing()) {
                    mDialogProgress.show(msg);
                } else {
                    mDialogProgress.setText(msg);
                }
            }
        });
    }

    @Override
    public void showDialog(final String msg) {
        showDialog(false, msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void disMissDialog() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialogProgress.isShowing())
                    mDialogProgress.dismiss();
            }
        });
    }

    @OnClick(R.id.btn_error_retry)
    public void onErrorRetry(View view) {
        initData();
    }

    @Override
    public void onNetFailure(String url, final String msg) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(msg);
            }
        });
    }


    @Override
    public void showContent() {
        loadlayout.showContent();
    }

    @Override
    public void showEmpty() {
        loadlayout.showEmpty();
    }

    @Override
    public void setEmpty(int resId, String msg, boolean isRetry, String retry) {
        if (resId != -1)
            ivEmpty.setImageResource(resId);
        if (!TextUtils.isEmpty(msg))
            tvEmpty.setText(msg);
        if (isRetry) {
            btnEmptyRetry.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(retry)) {
                btnEmptyRetry.setText(retry);
            }
        } else {
            btnEmptyRetry.setVisibility(View.GONE);
        }
    }

    @Override
    public void setError(int resId, String msg, boolean isRetry, String retry) {
        if (resId != -1)
            ivError.setImageResource(resId);
        if (!TextUtils.isEmpty(msg))
            tvError.setText(msg);
        if (isRetry) {
            btnErrorRetry.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(retry)) {
                btnErrorRetry.setText(retry);
            }
        } else {
            btnErrorRetry.setVisibility(View.GONE);
        }

    }

    @Override
    public void showError() {
        loadlayout.showError();
    }

    @Override
    public void showLoading() {
        loadlayout.showLoading();
    }

    @Override
    public void setLoading(String msg) {
        tvLoading.setText(msg);
    }


    @Override
    public void onViewDestory() {
        disMissDialog();
    }

    @Override
    public void showPhotoList(ViewGroup parent, @NonNull ArrayList<String> photoAddress, int curSelectedPos, int picType) {
    }

    @Override
    public void showPhotoOne(View view, String photoAddress, int picType) {
    }

    /**
     * 统一toast
     *
     * @return
     */
    public void msgToast(final String msg) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    public void onTokenFailure(final String msg) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UserUtils.tokenFailDialog(mContext, msg, TAG);
            }
        });
    }

    @Override
    public void finish() {
        mContext.finish();
    }
}
