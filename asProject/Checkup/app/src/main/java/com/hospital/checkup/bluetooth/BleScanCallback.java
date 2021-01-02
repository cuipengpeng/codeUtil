package com.hospital.checkup.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.hospital.checkup.utils.LogUtils;


public abstract class BleScanCallback implements BluetoothAdapter.LeScanCallback {

    /**
     * 扫描完成回调
     */
    public abstract void onStopScanBle();
}
