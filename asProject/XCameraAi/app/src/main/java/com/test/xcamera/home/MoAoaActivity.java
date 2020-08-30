package com.test.xcamera.home;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.test.xcamera.accrssory.AccessoryCommunicator;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.accrssory.USBReceiverStatus;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.utils.LogAccessory;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.widget.ActivityContainerHome;
import com.moxiang.common.logging.Logcat;

/**
 * zjh AOA
 */
public class MoAoaActivity extends Activity {
    public static MoAoaActivity mMoAoaActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMoAoaActivity = this;
        AccessoryCommunicator.mIsStartMoAoaActivity = true;
        UsbAccessory accessory = getIntent().getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
        LogAccessory.getInstance().showLog(USBReceiverStatus.TYPE_MoAoaActivity, "打开：onReceive _MoAoaActivity");

        Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg(String.format("MoAoaActivity msg==>accessory is null:%b  mIsRunning:%b  activityStack:%d", (accessory == null), AccessoryManager.getInstance().mIsRunning,
                ActivityContainerHome.getInstance().getActivityStackSize())).out();

        ActivityContainerHome.getInstance().finishFPVActivty();

        if (accessory != null && !AccessoryManager.getInstance().mIsRunning) {
            if (ActivityContainerHome.getInstance().getActivityStackSize() <= 0) {
                int pkgType = (int) SPUtils.get(this, ActivationHelper.PKG_TYPE, 0);
                if (pkgType != 3) {
                    Intent intent = new Intent(MoAoaActivity.this, GoUpActivity.class);
                    startActivity(intent);
                }
            }
            Intent intent = getIntent();
            intent.setAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
            LogAccessory.getInstance().showLog(USBReceiverStatus.TYPE_MoAoaActivity, "执行：onReceive _MoAoaActivity");
            AccessoryManager.getInstance().mCommunicator.onReceive(USBReceiverStatus.TYPE_MoAoaActivity, this, intent, new AccessoryCommunicator.OnAccessoryCallBack() {
                @Override
                public void onCallBack(boolean isSucc) {
                    finish();
                }
            });
            finish();
        } else {
            finish();
        }

    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN || ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            finish();
            return super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void finish() {
        mMoAoaActivity = null;
        AccessoryCommunicator.mIsStartMoAoaActivity = false;
        super.finish();
    }

    @Override
    protected void onDestroy() {
        mMoAoaActivity = null;
        AccessoryCommunicator.mIsStartMoAoaActivity = false;
        super.onDestroy();
    }
}
