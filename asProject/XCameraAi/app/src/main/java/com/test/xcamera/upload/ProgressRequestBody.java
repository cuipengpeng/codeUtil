package com.test.xcamera.upload;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by smz on 2019/11/6.
 */

public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private String mMediaType;
    private UploadCallbacks mListener;

    private int mEachBufferSize = 1024;

    public ProgressRequestBody(final File file, String mediaType, final UploadCallbacks listener) {
        mFile = file;
        mMediaType = mediaType;
        mListener = listener;
    }

//    public ProgressRequestBody(final File file, String mediaType, int eachBufferSize, final UploadCallbacks listener) {
//        mFile = file;
//        mMediaType = mediaType;
//        mEachBufferSize = eachBufferSize;
//        mListener = listener;
//    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(mMediaType);
    }

    @Override
    public void writeTo(BufferedSink sink) {
        FileInputStream in=null;
        try {
        long fileLength = mFile.length();
        byte[] buffer = new byte[mEachBufferSize];
         in  = new FileInputStream(mFile);
        long uploaded = 0;

            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {
                // update progress on UI thread
                handler.post(new ProgressUpdater(uploaded, fileLength));
                uploaded += read;
                sink.write(buffer, 0, read);

            }
        } catch (FileNotFoundException e) {
            mListener.onError(e);
            e.printStackTrace();
        } catch (IOException e) {
            mListener.onError(e);
            e.printStackTrace();
        } finally {
            try {
                if(in!=null){
                    in.close();
                }
                if(mListener!=null){
                    mListener.onFinish();
                }
            } catch (IOException e) {
                e.printStackTrace();
                if(mListener!=null){
                    mListener.onError(e);
                }
            }
        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;

        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate((int) (100 * mUploaded / mTotal));
        }
    }
}
