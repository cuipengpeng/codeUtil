package com.test.xcamera.view.basedialog.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.utils.SPUtils;

/**
 * Created by 周 on 2020/5/7.
 */

public class UserPrivacyDialog implements View.OnClickListener {

    private final Activity context;
    private final PrivacyClickListaner privacyClickListaner;
    private Dialog mDialog;
    private String str = "";

    public UserPrivacyDialog(Activity context, PrivacyClickListaner privacyClickListaner) {
        this.privacyClickListaner = privacyClickListaner;
        this.context = context;

        str = context.getResources().getString(R.string.read_user_server) +
                "<a href='0'>" + context.getResources().getString(R.string.user_server_agreement) + "</a>" +
                context.getResources().getString(R.string.he) +
                "<a href='1'>" + context.getResources().getString(R.string.privacy_server) + "</a>";

        View view = View.inflate(context, R.layout.dialog_user_privacy, null);
        mDialog = new Dialog(context);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        mDialog.getWindow().setAttributes(params);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initView(view);
    }

    private void initView(View view) {
        Button tongYi = view.findViewById(R.id.tongYi);
        tongYi.setOnClickListener(this);
        TextView buTongYi = view.findViewById(R.id.buTongYi);
        buTongYi.setOnClickListener(this);
        TextView privacyTextView = view.findViewById(R.id.privacyTextView);
        privacyTextView.setText(getClickableHtml(str));
        privacyTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog.cancel();
            mDialog = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tongYi:
                if (mDialog != null) {
                    SPUtils.put(context.getBaseContext(), "privacy_flag", "privacy");
                    mDialog.dismiss();
                    mDialog.cancel();
                    mDialog = null;
                    if (privacyClickListaner != null) {
                        privacyClickListaner.consent();
                    }
                }
                break;
            case R.id.buTongYi:
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog.cancel();
                    mDialog = null;
                    if (context != null) {
                        context.finishAffinity();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
                break;
        }
    }

    public interface PrivacyClickListaner {
        public void clickView(int flag);

        public void consent();
    }

    private void setLinkClickable(final SpannableStringBuilder clickableHtmlBuilder,
                                  final URLSpan urlSpan) {
        int start = clickableHtmlBuilder.getSpanStart(urlSpan);
        int end = clickableHtmlBuilder.getSpanEnd(urlSpan);
        int flags = clickableHtmlBuilder.getSpanFlags(urlSpan);
        ClickableSpan clickableSpan = new ClickableSpan() {

            public void onClick(View view) {
                String url = urlSpan.getURL();
                if (!TextUtils.isEmpty(url) && privacyClickListaner != null) {
                    if (url.equals("1")) {
                        privacyClickListaner.clickView(1);
                    } else {
                        privacyClickListaner.clickView(0);
                    }
                }
            }

            public void updateDrawState(TextPaint ds) {
                //设置颜色
                ds.setColor(Color.argb(255, 255, 119, 0));
                //设置是否要下划线
                ds.setUnderlineText(false);
            }
        };
        clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags);
    }

    private CharSequence getClickableHtml(String html) {
        Spanned spannedHtml = Html.fromHtml(html);
        SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(spannedHtml);
        URLSpan[] urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
        for (final URLSpan span : urls) {
            setLinkClickable(clickableHtmlBuilder, span);
        }
        return clickableHtmlBuilder;
    }
}
