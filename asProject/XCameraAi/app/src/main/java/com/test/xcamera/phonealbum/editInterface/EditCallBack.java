package com.test.xcamera.phonealbum.editInterface;

import android.graphics.PointF;

public class EditCallBack {

    //贴纸和字幕编辑对应的回调，其他素材不用
    public interface AssetEditListener {
        void onAssetDelete();

        void onAssetSelected(PointF curPoint);

        void onAssetTranstion();

        void onAssetScale();

        void onAssetAlign(int alignVal);//字幕使用

        void onAssetHorizFlip(boolean isHorizFlip);//贴纸使用
        void onTouchDown();
        void onTouchUp();
    }

    //字幕文本修改回调
    public interface VideoCaptionTextEditListener {
        void onCaptionTextEdit();
    }
}
