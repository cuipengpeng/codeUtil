package com.hospital.checkup.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.hospital.checkup.utils.LogUtils;


public class BleDevceScanCallback implements BluetoothAdapter.LeScanCallback {
    private ScanCallback mScanCallback;

    public BleDevceScanCallback(ScanCallback scanCallback) {
        this.mScanCallback=scanCallback;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (null != mScanCallback) {
            LogUtils.printLog("deviceName="+device.getName()+"--deviceAddr="+device.getAddress()+"--uuid="+device.getUuids());
            //每次扫描到设备会回调此方法,这里一般做些过滤在添加进list列表
            mScanCallback.onScanning(device, rssi, scanRecord);
        }
    }
}
