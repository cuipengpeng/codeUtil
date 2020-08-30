package com.test.xcamera.bean;

import com.test.xcamera.constants.CtrlCmdConstants;
import com.test.xcamera.enumbean.CtrlCmdType;
import com.test.xcamera.utils.BytesUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 控制命令
 * Created by zll on 2019/5/21.
 */

public class CtrlInfo {
    private static final int HEADER_SIZE = 12;
    private CtrlCmdType mCtrlCmd;
    private int mCtrlCmdSeq;
    private int mExtHeaderSize;
    private int mRespRet;
    private int mCtrlDataSize;
    private byte[] mData;

    private JSONObject jsonObject;

    public CtrlInfo(byte[] ctrlData, int dataSize) {
        byte[] content = BytesUtil.subBytes(ctrlData, HEADER_SIZE, dataSize);
        String json = new String(content);
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        int ctrl = BytesUtil.byteArrayToShort(ctrlData,8, false);
//        this.mCtrlCmd = getCtrlType(ctrl);
//        this.mCtrlCmdSeq = BytesUtil.byteArrayToShort(ctrlData, 10, false);
//        this.mExtHeaderSize = BytesUtil.byteArrayToShort(ctrlData, 12, false);
//        this.mRespRet = BytesUtil.byteArrayToShort(ctrlData, 14, false);
//        this.mCtrlDataSize = BytesUtil.byteArrayToInt(ctrlData, 16, false);
//        this.mData = BytesUtil.subBytes(ctrlData, 12 + 8, mCtrlDataSize);
//        int ctrl2 = BytesUtil.byteArrayToShort(ctrlData,8, false);
    }

    public CtrlCmdType getmCtrlCmd() {
        return mCtrlCmd;
    }

    public void setmCtrlCmd(CtrlCmdType mCtrlCmd) {
        this.mCtrlCmd = mCtrlCmd;
    }

    public int getmCtrlCmdSeq() {
        return mCtrlCmdSeq;
    }

    public void setmCtrlCmdSeq(int mCtrlCmdSeq) {
        this.mCtrlCmdSeq = mCtrlCmdSeq;
    }

    public int getmExtHeaderSize() {
        return mExtHeaderSize;
    }

    public void setmExtHeaderSize(int mExtHeaderSize) {
        this.mExtHeaderSize = mExtHeaderSize;
    }

    public int getmCtrlDataSize() {
        return mCtrlDataSize;
    }

    public void setmCtrlDataSize(int mCtrlDataSize) {
        this.mCtrlDataSize = mCtrlDataSize;
    }

    public byte[] getmData() {
        return mData;
    }

    public void setmData(byte[] mData) {
        this.mData = mData;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    private CtrlCmdType getCtrlType(int type) {
        CtrlCmdType ctrlCmdType = null;
        switch (type) {
            case CtrlCmdConstants.STREAM_IO_CTRL_CMD_TAKE_PHOTO:
                ctrlCmdType = CtrlCmdType.STREAM_IO_CTRL_CMD_SNAP;
                break;
            case CtrlCmdConstants.STREAM_IO_CTRL_CMD_START_RECORD:
                ctrlCmdType = CtrlCmdType.STREAM_IO_CTRL_CMD_START_RECORD;
                break;
            case CtrlCmdConstants.STREAM_IO_CTRL_CMD_END_RECORD:
                ctrlCmdType = CtrlCmdType.STREAM_IO_CTRL_CMD_END_RECORD;
                break;
        }
        return ctrlCmdType;
    }

    @Override
    public String toString() {
        return "CtrlInfo{" +
//                "mCtrlCmd=" + mCtrlCmd +
//                ", mCtrlCmdSeq=" + mCtrlCmdSeq +
//                ", mExtHeaderSize=" + mExtHeaderSize +
//                ", mCtrlDataSize=" + mCtrlDataSize +
//                ", mData length =" + mData.length +
                ", json =" + jsonObject.toString() +
                '}';
    }
}
