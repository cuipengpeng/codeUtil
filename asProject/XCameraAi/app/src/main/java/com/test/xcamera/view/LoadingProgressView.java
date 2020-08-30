package com.test.xcamera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.R;

import butterknife.BindView;

public class LoadingProgressView extends RelativeLayout {
    @BindView(R.id.pb_loadingProgress_loading)
    ProgressBar loadingProgressBar;
    @BindView(R.id.tv_loadingProgress_loadingContent)
    TextView loadingContentTextView;
    @BindView(R.id.rl_loadingProgress_loadingItem)
    RelativeLayout loadingItemRelativeLayout;


    public LoadingProgressView(Context context) {
        this(context, null);
    }

    public LoadingProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.loading_progress_view, this);
    }

    public void setProgress(int progress){
        loadingContentTextView.setText(getResources().getString(R.string.loadingData)+" "+progress+"%");
    }

}
