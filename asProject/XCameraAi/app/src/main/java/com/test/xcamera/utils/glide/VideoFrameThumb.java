package com.test.xcamera.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.load.data.DataFetcher;
import com.test.xcamera.R;
import com.meicam.sdk.NvsIconGenerator;
import com.meicam.sdk.NvsVideoFrameRetriever;

import java.util.HashMap;

/**
 * 视频缩略图
 */
public class VideoFrameThumb implements NvsIconGenerator.IconCallback {
    private static VideoFrameThumb mVideoFrameThumb;
    private NvsIconGenerator m_iconGenerator = null;
    private HashMap<Long, ImageView> imageRequestList;

    public static VideoFrameThumb getInstance() {
        if (mVideoFrameThumb != null) {
            return mVideoFrameThumb;
        } else {
            mVideoFrameThumb = new VideoFrameThumb();
        }
        return mVideoFrameThumb;
    }

    private VideoFrameThumb() {
        m_iconGenerator = new NvsIconGenerator();
        m_iconGenerator.setIconCallback(this);
        imageRequestList = new HashMap<>();
    }

    ImageView mImageView;

    public void loadVideoThumbnail(Context context, String uri, ImageView imageView, long frameTime) {
        mImageView = imageView;
        if (frameTime <= 10) {
            frameTime = 100;
        }
        Bitmap bitmap = m_iconGenerator.getIconFromCache(uri, frameTime, NvsVideoFrameRetriever.VIDEO_FRAME_HEIGHT_GRADE_360);
        if (bitmap != null & imageView != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            if (imageView != null) {
                imageView.setImageResource(R.mipmap.bank_thumbnail_local);
            }
            if (imageRequestList.size() > 40) {
                imageRequestList.clear();
            }

            long id = m_iconGenerator.getIcon(uri, frameTime, NvsVideoFrameRetriever.VIDEO_FRAME_HEIGHT_GRADE_360);

            if (imageView != null) {
                imageView.setTag(id);
                imageRequestList.put(id, imageView);
            }
        }

    }

    DataFetcher.DataCallback<? super Bitmap> mCallback;

    public void loadVideoThumbnail(String uri, final long frameTime, DataFetcher.DataCallback<? super Bitmap> callback) {
        mCallback = callback;
        Bitmap bitmap = m_iconGenerator.getIconFromCache(uri, frameTime, NvsVideoFrameRetriever.VIDEO_FRAME_HEIGHT_GRADE_360);

        if (bitmap != null) {
            mCallback.onDataReady(bitmap);
        } else {
            m_iconGenerator.getIcon(uri, frameTime, NvsVideoFrameRetriever.VIDEO_FRAME_HEIGHT_GRADE_360);


        }

    }

    public void onDetached() {
        if (imageRequestList != null) {
            imageRequestList.clear();
        }
        cancelIconTask();
        if (m_iconGenerator != null) {
            m_iconGenerator.setIconCallback(null);
            m_iconGenerator.release();
            m_iconGenerator = null;
        }
        mVideoFrameThumb = null;
    }

    private void cancelIconTask() {
        if (m_iconGenerator != null)
            m_iconGenerator.cancelTask(0);
    }

    @Override
    public void onIconReady(Bitmap bitmap, long l, long l1) {
        ImageView imageView = imageRequestList.remove(l1);
        if (imageView != null && (long) imageView.getTag() == l1) {
            imageView.setImageBitmap(bitmap);
        }
        if (mCallback != null) {
            mCallback.onDataReady(bitmap);
        }
    }
}
