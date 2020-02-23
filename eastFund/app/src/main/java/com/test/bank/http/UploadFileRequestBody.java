package com.test.bank.http;

import android.os.Handler;
import android.os.Looper;

import com.test.bank.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class UploadFileRequestBody extends RequestBody {

    private RequestBody mRequestBody;
    private UploadFileRequestBody.ProgressListener  mProgressListener;
    private File mFile;
    private int mFilePosition;
    private int mFileCount;
    private Handler mHandler;
    private float mProgress;

    public UploadFileRequestBody(File file, int filePosition, int fileCount, UploadFileRequestBody.ProgressListener progressListener) {
        this.mFile = file;
        this.mFilePosition = filePosition;
        this.mFileCount = fileCount;
        this.mProgressListener = progressListener;
        this.mRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if(mFilePosition==0){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgressListener.onUploadStart();
                }
            });
        }
        long currentLength = 0;
        long totalLength = mFile.length();
        FileInputStream fileInputStream = new FileInputStream(mFile);
        int len;
        byte[] data = new byte[1024*20];
        while ((len = fileInputStream.read(data)) != -1) {
            sink.write(data, 0, len);
            if (mProgressListener != null) {
                currentLength += len;
                float singleFilePercent = currentLength * 1.0f / totalLength;
                if (singleFilePercent > 1) singleFilePercent = 1;
                if (singleFilePercent < 0) singleFilePercent = 0;

                mProgress = (mFilePosition+singleFilePercent)*100f*(1.0f/mFileCount);
                if (mProgress > 100){
                    mProgress = 100;
                }
                if (mProgress < 0){
                    mProgress = 0;
                }
                LogUtils.printLog("totalProgress="+mProgress+"--mFileCount="+mFileCount+"-----mFilePosition="+mFilePosition+"----the current " + currentLength + "----" + totalLength + "-----" + (currentLength * 1.0f/totalLength));
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressListener.onProgressUpdate((int) mProgress);
                    }
                });

            }
        }
        sink.flush();
        fileInputStream.close();

        if(mFilePosition==(mFileCount-1)){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgressListener.onUploadFinish();
                }
            });

        }
    }

    public interface ProgressListener extends HttpRequest.HttpResponseCallBack{
        void onUploadStart();
        void onProgressUpdate(int progress);
        void onUploadFinish();
    }
}