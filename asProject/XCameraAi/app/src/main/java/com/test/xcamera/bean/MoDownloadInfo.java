package com.test.xcamera.bean;

import com.test.xcamera.managers.DataManager;
import com.test.xcamera.utils.BytesUtil;

/**
 * Created by zll on 2019/7/22.
 */

public class
MoDownloadInfo {
    private static final int URI_LENGTH = 512;
    private String mUri;
    private int mPackageID;
    private long mOffset;
    private byte[] mData;

//    static String testPath = Environment.getExternalStorageDirectory().getPath() + "/MoTest";
//    static String testName = "receive.txt";

    public MoDownloadInfo(byte[] data, int len) {
        int length = BytesUtil.byteArrayToInt(data, DataManager.HEAD_COUNT, false);
        System.out.println("MoDownloadInfo" + length);
        byte[] uriData = BytesUtil.subBytes(data, DataManager.HEAD_COUNT + 4, length);
        mUri = new String(uriData);

        mPackageID = BytesUtil.byteArrayToInt(data, DataManager.HEAD_COUNT + URI_LENGTH + 4, false);
//        byte[] bytes = BytesUtil.subBytes(data, DataManager.HEAD_COUNT + URI_LENGTH + 4 + 4, 8);
//        String b = Arrays.toString(bytes);
        mOffset = BytesUtil.longFrom8Bytes(data, DataManager.HEAD_COUNT + URI_LENGTH + 4 + 4, true);
        mData = BytesUtil.subBytes(data, DataManager.HEAD_COUNT + URI_LENGTH + 8 + 8, len - (DataManager.HEAD_COUNT + URI_LENGTH + 8 + 8) - 4);


//        String s = "package id：" + mPackageID + ", offset：" + mOffset + ", byte：" + b;
//        FileUtil.writeFileToSDCard(testPath, s.getBytes(), testName, true, true, false);
    }

    public String getmUri() {
        return mUri;
    }

    public void setmUri(String mUri) {
        this.mUri = mUri;
    }

    public int getmPackageID() {
        return mPackageID;
    }

    public void setmPackageID(int mPackageID) {
        this.mPackageID = mPackageID;
    }

    public long getmOffset() {
        return mOffset;
    }

    public void setmOffset(long mOffset) {
        this.mOffset = mOffset;
    }

    public byte[] getData() {
        return mData;
    }

    public void setData(byte[] data) {
        this.mData = data;
    }

    @Override
    public String toString() {
        return "MoDownloadInfo{" +
                "mUri='" + mUri + '\'' +
                ", mPackageID=" + mPackageID +
                ", mOffset=" + mOffset +
                ", mData=" + mData.length +
                '}';
    }
}
