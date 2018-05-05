package com.test.bank.weight.holder;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.utils.ActivityManager;
import com.test.bank.utils.DensityUtil;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.UIUtils;

import static com.test.bank.base.BaseApplication.getContext;

/**
 * Created by 55 on 2017/12/12.
 */

public class RecommandReasonItem extends BaseHolder<String> {

    TextView textView;

    public RecommandReasonItem(OnGetLineCountListener onGetLineCountListener) {
        super();
        this.onGetLineCountListener = onGetLineCountListener;
    }

    @Override
    protected void initView(View rootView) {
        textView = rootView.findViewById(R.id.tv_item_recommand_reason);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_recommand_reason;
    }

    @Override
    protected void updateView() {
        if (onGetLineCountListener != null) {
            int lineCount = measureTextViewLineCount(data, 15);
            LogUtils.e("updateView:::: measureTextViewHeight.lineCount: " + lineCount);
            int maxLines = onGetLineCountListener.onGetLineCount(lineCount);
            if (maxLines != -1 && maxLines > 0) {
                Log.d(TAG, "updateView: maxLines: " + maxLines);
                textView.setMaxLines(maxLines);
//                textView.setLines(maxLines);
//                textView.setEllipsize(TextUtils.TruncateAt.END);
            }
        }
        UIUtils.setText(textView, data, "--");
    }

    private int measureTextViewLineCount(String text, int textSizeDP) {
        int deviceWidth = DensityUtil.getScreenWidth() - DensityUtil.dip2px(50);
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizeDP);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getLineCount();
    }

    private OnGetLineCountListener onGetLineCountListener;

    public interface OnGetLineCountListener {
        int onGetLineCount(int lineCount);
    }

    @Override
    protected View createRootView() {
        TextView textView = new TextView(ActivityManager.getInstance().currentActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginValues = DensityUtil.dip2px(15);
        params.setMargins(marginValues, marginValues, marginValues, 0);
        textView.setLayoutParams(params);
        textView.setMaxLines(3);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTextColor(Color.parseColor("#7e819b"));
        if (mContext != null) {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.circle_dp6_7e819b);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setCompoundDrawablePadding(DensityUtil.dip2px(15));
        }
        return null;
    }
}
