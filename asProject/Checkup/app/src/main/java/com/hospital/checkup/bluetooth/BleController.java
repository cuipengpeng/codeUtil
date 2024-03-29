package com.hospital.checkup.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.utils.HexUtil;
import com.hospital.checkup.utils.LogUtils;
import com.hospital.checkup.view.activity.CalibrationStartActivity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BleController {

    private static final String TAG = "BleController";

    private static BleController mBleController;
    private Context mContext;

    private StringBuilder stringBuilder = new StringBuilder();
    public BlockingQueue<String> dataPacketList = new LinkedBlockingQueue<String>();
    public StringBuilder dataPacketStringBuilder = new StringBuilder();
    private BluetoothAdapter mBleAdapter;
    public BluetoothGatt mBleGatt;
    public BluetoothGattCharacteristic mBleGattCharacteristic;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private BleGattCallback mGattCallback;
    private OnWriteCallback writeCallback;

    private boolean mScanning;

    //默认扫描时间：10s
    private static final int SCAN_TIME = 10000;
    //默认连接超时时间:10s
    private static final int CONNECTION_TIME_OUT = 10000;
    //获取到所有服务的集合
    private HashMap<String, Map<String, BluetoothGattCharacteristic>> servicesMap = new HashMap<>();

    //连接请求是否ok
    public boolean isConnected = false;
    //是否是用户手动断开
    private boolean isDisconnectByHand = false;
    //连接结果的回调
    private ConnectCallback mConnectCallback;
    //读操作请求队列
    private ReceiverRequestQueue mReceiverRequestQueue = new ReceiverRequestQueue();

    //此属性一般不用修改
    private static final String BLUETOOTH_NOTIFY_D = "00002902-0000-1000-8000-00805f9b34fb";
    //TODO 以下uuid根据公司硬件改变
    public static final String DEVICE_ADDRESS = "E1:60:92:B9:77:B7";
//    public static final String DEVICE_ADDRESS = "D1:48:DA:CA:16:0F";
    public static final String UUID_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_NOTIFY = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_INDICATE = "0000000-0000-0000-8000-00805f9b0000";
    public static final String UUID_WRITE = "0000fff3-0000-1000-8000-00805f9b34fb";
    public static final String UUID_READ = "3f3e3d3c-3b3a-3938-3736-353433323130";

    public static synchronized BleController getInstance() {
        if (mBleController == null) {
            synchronized (BleController.class){
                if(mBleController==null){
                    mBleController = new BleController();
                }
            }
        }
        return mBleController;
    }


    public BleController init(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
            BluetoothManager mBlehManager = (BluetoothManager) BaseApplication.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
            mBleAdapter = mBlehManager.getAdapter();
            mGattCallback = new BleGattCallback();
        }
        return this;
    }



    /**
     * 扫描设备
     * 默认扫描10s
     *
     * @param scanCallback
     */
    public void scanBle(final boolean startScan, final BleScanCallback scanCallback) {
        scanBle(SCAN_TIME, startScan, scanCallback);
    }

    /**
     * 扫描设备
     *
     * @param time         指定扫描时间
     * @param bleScanCallback 扫描回调
     */
    public void scanBle(int time, final boolean startScan, final BleScanCallback bleScanCallback) {
        if (!isEnable()) {
            mBleAdapter.enable();
        }
//        if (null != mBleGatt) {
//            mBleGatt.close();
//        }
        reset();
        if (startScan) {
            if (mScanning){
                return;
            }
            time = time <= 0 ? SCAN_TIME : time;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    //time后停止扫描
                    mBleAdapter.stopLeScan(bleScanCallback);
                    bleScanCallback.onStopScanBle();
                }
            }, time);

            mScanning = true;
            mBleAdapter.startLeScan(bleScanCallback);
        } else {
            mScanning = false;
            mBleAdapter.stopLeScan(bleScanCallback);
        }
    }





    /**
     * 连接设备
     *
     * @param address         设备mac地址
     * @param connectCallback 连接回调
     */
    public void connect(final String address, ConnectCallback connectCallback) {
        connect(CONNECTION_TIME_OUT, address, connectCallback);
    }
    /**
     * 连接设备
     *
     * @param connectionTimeOut 指定连接超时
     * @param address           设备mac地址
     * @param connectCallback   连接回调
     */
    public void connect(final int connectionTimeOut, final String address, ConnectCallback connectCallback) {
        if (mBleAdapter == null ) {
            mBleAdapter= ((BluetoothManager) BaseApplication.getContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        }
        BluetoothDevice remoteDevice = mBleAdapter.getRemoteDevice(address);
        if (remoteDevice == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return;
        }
        this.mConnectCallback = connectCallback;
        mBleGatt = remoteDevice.connectGatt(mContext, false, mGattCallback);
        Log.e(TAG, "connecting mac-address:" + address);
        delayConnectResponse(connectionTimeOut);
    }


    /**
     * 发送数据
     *
     * @param value         指令
     * @param writeCallback 发送回调
     */
    public void WriteBuffer(String value, OnWriteCallback writeCallback) {
        this.writeCallback = writeCallback;
        if (!isEnable()) {
            writeCallback.onFailed(OnWriteCallback.FAILED_BLUETOOTH_DISABLE);
            Log.e(TAG, "FAILED_BLUETOOTH_DISABLE");
            return;
        }

        if (mBleGattCharacteristic == null) {
            mBleGattCharacteristic = getBluetoothGattCharacteristic(UUID_SERVICE, UUID_WRITE);
        }

        if (null == mBleGattCharacteristic) {
            writeCallback.onFailed(OnWriteCallback.FAILED_INVALID_CHARACTER);
            Log.e(TAG, "FAILED_INVALID_CHARACTER");
            return;
        }

        //设置数组进去
        mBleGattCharacteristic.setValue(HexUtil.hexStringToBytes(value));
        //发送

        boolean b = mBleGatt.writeCharacteristic(mBleGattCharacteristic);

        Log.e(TAG, "send:" + b + "data：" + value);
    }

    /**
     * 设置读取数据的监听
     *
     * @param requestKey
     * @param onReceiverCallback
     */
    public void RegistReciveListener(String requestKey, OnReceiverCallback onReceiverCallback) {
        mReceiverRequestQueue.set(requestKey, onReceiverCallback);
    }

    /**
     * 移除读取数据的监听
     *
     * @param requestKey
     */
    public void UnregistReciveListener(String requestKey) {
        mReceiverRequestQueue.removeRequest(requestKey);
    }

    /**
     * 手动断开Ble连接
     */
    public void closeBleConn() {
        disConnection();
        isDisconnectByHand = true;
        mBleGattCharacteristic = null;
    }


//------------------------------------分割线--------------------------------------

    /**
     * 当前蓝牙是否打开
     */
    public boolean isEnable() {
        if (mBleAdapter == null) {
            mBleAdapter= ((BluetoothManager) BaseApplication.getContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        }
        return mBleAdapter.isEnabled();
    }

    /**
     * 重置数据
     */
    private void reset() {
        isConnected = false;
        isDisconnectByHand = false;
        servicesMap.clear();
    }

    /**
     * 超时断开
     *
     * @param connectionTimeOut
     */
    private void delayConnectResponse(int connectionTimeOut) {
        mHandler.removeCallbacksAndMessages(null);
        connectionTimeOut = connectionTimeOut <= 0 ? CONNECTION_TIME_OUT : connectionTimeOut;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isConnected && !isDisconnectByHand) {
                    Log.e(TAG, "connect timeout");
                    disConnection();
//                    reConnect();
                }
            }
        }, connectionTimeOut);
    }

    /**
     * 断开连接
     */
    private void disConnection() {
        if (null == mBleAdapter || null == mBleGatt) {
            Log.e(TAG, "disconnection error maybe no init");
            return;
        }
        if(mConnectCallback != null){
            mConnectCallback.onConnFailed();
        }
        LogUtils.printLog("Ble disconnected !");
        mBleGatt.disconnect();
        reset();
    }

    /**
     * 蓝牙GATT连接及操作事件回调
     */
    private class BleGattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            LogUtils.printLog("111111");
            if (newState == BluetoothProfile.STATE_CONNECTED) { //连接成功
                isConnected = true;
                isDisconnectByHand = false;
                mBleGatt.discoverServices();

                if (mConnectCallback != null) {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mConnectCallback.onConnSuccess();
                        }
                    });
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {   //断开连接
                if (!isDisconnectByHand) {
                    reConnect();
                }
                reset();
            }
        }

        //发现新服务
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mBleGatt = gatt;
            LogUtils.printLog("22222");
            super.onServicesDiscovered(gatt, status);
            if (null != mBleGatt && status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> servicesList = mBleGatt.getServices();
                for (int i = 0; i < servicesList.size(); i++) {
                    HashMap<String, BluetoothGattCharacteristic> charMap = new HashMap<>();
                    BluetoothGattService bluetoothGattService = servicesList.get(i);
                    String serviceUuid = bluetoothGattService.getUuid().toString();
                    LogUtils.printLog("serviceUuid = "+serviceUuid);
                    List<BluetoothGattCharacteristic> characteristicsList = bluetoothGattService.getCharacteristics();
                    for (int j = 0; j < characteristicsList.size(); j++) {
                        LogUtils.printLog("characteristics.get(j).getUuid() = "+characteristicsList.get(j).getUuid().toString());
                        charMap.put(characteristicsList.get(j).getUuid().toString(), characteristicsList.get(j));
                        if(UUID_NOTIFY.equalsIgnoreCase(characteristicsList.get(j).getUuid().toString())){
                            mBleGattCharacteristic = characteristicsList.get(j);
                            enableNotification(true, characteristicsList.get(j));
                        }
                    }
                    servicesMap.put(serviceUuid, charMap);
                }
            }
        }

        //读数据
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            LogUtils.printLog("3333333");
            LogUtils.printLog("status = "+status);
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        //写数据
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            LogUtils.printLog("444444");
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (null != writeCallback) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            writeCallback.onSuccess();
                        }
                    });
                    Log.e(TAG, "Send data success!");
                } else {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            writeCallback.onFailed(OnWriteCallback.FAILED_OPERATION);
                        }
                    });
                    Log.e(TAG, "Send data failed!");
                }
            }
        }

        //通知数据
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            for(int i=0; i<characteristic.getValue().length;i++){
                stringBuilder.append((char)characteristic.getValue()[i]);
            }

//            LogUtils.printLog("55555555 string length = "+stringBuilder.toString().length()+"-- str = "+stringBuilder.toString());
            //截取以b开头，以e结尾的字符串
            while (stringBuilder.indexOf("b")>=0){
//                LogUtils.printLog("string length = "+stringBuilder.toString().length()+"-- str = "+stringBuilder.toString());
                String substring = stringBuilder.substring(stringBuilder.indexOf("b"));
//                LogUtils.printLog("substring length= "+substring.length()+"-- substring= "+substring);
                stringBuilder.delete(0, stringBuilder.length());
                stringBuilder.append(substring);
                if(stringBuilder.indexOf("e")>=0){
                    //截取以b开头，以e结尾的字符串
                    String dataStr = stringBuilder.substring(stringBuilder.indexOf("b"),stringBuilder.indexOf("e")+1);
//                    LogUtils.printLog("dataStr = "+ dataStr+"   list.size()="+dataPacketList.size());
                    if(!CalibrationStartActivity.stopCalibration){
                        try {
                            dataPacketStringBuilder.append(dataStr);
                            dataPacketList.put(dataStr);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    String str = stringBuilder.substring(stringBuilder.indexOf("e")+1);
//                    LogUtils.printLog("left str = "+str);
                    stringBuilder.delete(0, stringBuilder.length());
                    stringBuilder.append(str);
//                    LogUtils.printLog("left string length = "+stringBuilder.toString().length()+"-- left string = "+stringBuilder.toString());
                }else {
                    break;
                }
            }
//            LogUtils.printLog("------------------------------threadId = "+Thread.currentThread().getId()+"--threadName="+Thread.currentThread().getName());
//            LogUtils.printLog(HexUtil.bytesToHexString(characteristic.getValue()));
//            super.onCharacteristicChanged(gatt, characteristic);
//            printAllThread();
//            LogUtils.printLog("aaaa");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            LogUtils.printLog("66666666");
            super.onDescriptorRead(gatt, descriptor, status);
        }
    }


    public void printAllThread(){
        Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
        Set<Thread> set = stacks.keySet();
        for (Thread key : set) {
            StackTraceElement[] stackTraceElements = stacks.get(key);
            Log.d(TAG, "---- print thread: " + key.getName() + " start ----");
        }
        System.out.println("************" );
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup topGroup = group;
        // 遍历线程组树，获取根线程组
        while (group != null) {
            topGroup = group;
            group = group.getParent();
        }
        // 激活的线程数再加一倍，防止枚举时有可能刚好有动态线程生成
        int slackSize = topGroup.activeCount() * 2;
        Thread[] slackThreads = new Thread[slackSize];
        // 获取根线程组下的所有线程，返回的actualSize便是最终的线程数
        int actualSize = topGroup.enumerate(slackThreads);
        Thread[] atualThreads = new Thread[actualSize];
        // 复制slackThreads中有效的值到atualThreads
        System.arraycopy(slackThreads, 0, atualThreads, 0, actualSize);
        System.out.println("Threads size is " + atualThreads.length);
        for (Thread thread : atualThreads) {
            System.out.println("Thread name : " + thread.getName());
        }
    }

    /**
     * 设置通知
     *
     * @param enable         true为开启false为关闭
     * @param characteristic 通知特征
     * @return
     */
    public boolean enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
        if (mBleGatt == null || characteristic == null || !mBleGatt.setCharacteristicNotification(characteristic, enable)){
            return false;
        }
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BLUETOOTH_NOTIFY_D));
        if (descriptor == null){
            return false;
        }
        if (enable) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        boolean success = mBleGatt.writeDescriptor(descriptor);
//        return mBleGatt.readCharacteristic(characteristic);
        return success;
    }

    public BluetoothGattService getGattService(UUID uuid) {
        if (mBleAdapter == null || mBleGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return null;
        }
        return mBleGatt.getService(uuid);
    }


    /**
     * 根据服务UUID和特征UUID,获取一个特征{@link BluetoothGattCharacteristic}
     *
     * @param serviceUUID   服务UUID
     * @param characterUUID 特征UUID
     */
    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(String serviceUUID, String characterUUID) {
        if (!isEnable()) {
            throw new IllegalArgumentException(" Bluetooth is no enable please call BluetoothAdapter.enable()");
        }
        if (null == mBleGatt) {
            Log.e(TAG, "mBluetoothGatt is null");
            return null;
        }

        //找服务
        Map<String, BluetoothGattCharacteristic> bluetoothGattCharacteristicMap = servicesMap.get(serviceUUID);
        if (null == bluetoothGattCharacteristicMap) {
            Log.e(TAG, "Not found the serviceUUID!");
            return null;
        }

        //找特征
        Set<Map.Entry<String, BluetoothGattCharacteristic>> entries = bluetoothGattCharacteristicMap.entrySet();
        BluetoothGattCharacteristic gattCharacteristic = null;
        for (Map.Entry<String, BluetoothGattCharacteristic> entry : entries) {
            if (characterUUID.equals(entry.getKey())) {
                gattCharacteristic = entry.getValue();
                break;
            }
        }
        return gattCharacteristic;
    }


    private void runOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            if (mHandler != null) {
                mHandler.post(runnable);
            }
        }
    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    // TODO 此方法断开连接或连接失败时会被调用。可在此处理自动重连,内部代码可自行修改，如发送广播
    private void reConnect() {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                 connect(DEVICE_ADDRESS, mConnectCallback);
                }
            });
        LogUtils.printLog("Ble connect failed!, try re-connecting...");
    }
}
