package com.caishi.chaoge.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.caishi.chaoge.BuildConfig;
import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.ui.widget.MyPopupDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseUILocalDataActivity{
    @BindView(R.id.tv_settingActivity_customerServicePhone)
    TextView tvSettingActivityCustomerServicePhone;
    @BindView(R.id.tv_about_version)
    TextView tv_about_version;

    @OnClick({R.id.tv_settingActivity_customerServicePhone})
    public void OnClickView(View v) {
        switch (v.getId()) {
            case R.id.tv_settingActivity_customerServicePhone:
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "关于";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initPageData() {
        tv_about_version.setText("Ver " + BuildConfig.VERSION_NAME);
    }

    /**
     * 拨打电话
     *
     * @param context
     */
    public static void callPhone(final Context context, final String phoneNumber) {
        final MyPopupDialog dialog = new MyPopupDialog(context, "拨号", "确定拨打 " + phoneNumber + " 电话么？", "取消", "确定", null, true);
        dialog.setOnClickListen(new MyPopupDialog.OnClickListen() {

            @Override
            public void rightClick() {
                dialog.dismiss();
                if (isCanUseSim(context)) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "请先设置打电话权限", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "未安装SIM卡", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void leftClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     * 是否有SIM卡
     *
     * @param context
     * @return
     */
    public static boolean isCanUseSim(Context context) {
        try {
            TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return TelephonyManager.SIM_STATE_READY == mgr
                    .getSimState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
