package com.jfbank.qualitymarket.base;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
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
public abstract class BaseMvpActivity<T extends BasePresenter, E extends BaseModel> extends BaseActivity implements BaseView {
    public T mPresenter;
    public E mModel;
    @InjectView(R.id.tv_title)
    public TextView tvTitle;
    @InjectView(R.id.iv_back)
    public ImageView ivBack;
    @InjectView(R.id.iv_right_menu)
    public ImageView ivRightMenu;
    @InjectView(R.id.tv_right_menu)
    public TextView tvRightMenu;
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
    public LinearLayout llTitle;

    private LoadingAlertDialog mDialogProgress;
    public static String TAG = "";
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;

    public boolean isInidData() {
        return isInidData;
    }

    public void setInidData(boolean inidData) {
        isInidData = inidData;
    }

    boolean isInidData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public TakePhoto getTakePhoto() {
        return super.getTakePhoto();
    }

    protected void init() {
        TAG = getClass().getName();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(getLayoutResID());
        mPresenter = TUtil.getT(this, 0);
        mModel = TUtil.getT(this, 1);
        mPresenter.setVM(this, this, mModel);
        initDialog();
        initView();
        if (rlTitle != null) {
            CommonUtils.setTitle(this, rlTitle);
        }
        if (isInidData)
            initData();
    }

    @OnClick(R.id.iv_back)
    public void onBack(View view) {
        finish();
    }

    @OnClick(R.id.btn_error_retry)
    public void onErrorRetry(View view) {
        initData();
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
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.activity_base);
        View contentView = LayoutInflater.from(this).inflate(layoutResID, null);
        content = (LinearLayout) findViewById(R.id.content);
        content.addView(contentView, new LinearLayout.LayoutParams(-1, -1));
        llTitle= (LinearLayout) findViewById(R.id.ll_title);
        ButterKnife.inject(this);
    }

    private void initDialog() {
        mDialogProgress = new LoadingAlertDialog(this);
        mDialogProgress.setTitle("正在加载中...");
        mDialogProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mPresenter.cancel();
            }
        });
    }

    /**
     * 显示大图
     *
     * @param parent
     * @param photoAddress
     * @param curSelectedPos
     */
    @Override
    public void showPhotoList(ViewGroup parent, @NonNull ArrayList<String> photoAddress, int curSelectedPos, int picType) {
        ArrayList<Rect> mRects = new ArrayList<>();
        int childCount = parent.getChildCount();
        if (childCount > photoAddress.size()) {
            childCount = photoAddress.size();
        }
        mRects.clear();
        try {
            if (childCount >= 0) {
                for (int i = 0; i < childCount; i++) {
                    View v = parent.getChildAt(i);
                    Rect bound = new Rect();
                    v.getGlobalVisibleRect(bound);
                    mRects.add(bound);
                }
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "view可能为空哦");
        }
    }

    @Override
    public void showPhotoOne(View view, String photoAddress, int picType) {
        ArrayList<Rect> mRects = new ArrayList<>();
        ArrayList<String> photoAddressList = new ArrayList<>();
        photoAddressList.add(photoAddress);
        mRects.clear();
        try {
            Rect bound = new Rect();
            view.getGlobalVisibleRect(bound);
            mRects.add(bound);
        } catch (
                NullPointerException e) {
            Log.e(TAG, "view可能为空哦");
        }
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
    public void onNetFailure(String url, final String msg) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    public void showDialog(final String msg) {
        showDialog(false, msg);
    }


    @Override
    public void onFinishReFreshView() {

    }

    @Override
    public void onFinishLoadMoreView() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }


    @Override
    public void disMissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialogProgress.isShowing())
                    mDialogProgress.dismiss();
            }
        });
    }

    @Override
    public void onViewDestory() {
        disMissDialog();
    }

    /**
     * 统一toast
     *
     * @return
     */
    public void msgToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    public void onTokenFailure(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UserUtils.tokenFailDialog(mContext, msg, TAG);
            }
        });
    }
}
