package com.test.xcamera.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by wangchunhui on 2019/7/30.
 */

public class MoCameraRequestHandler extends RequestHandler {

//    private Uri mUri = null;
//    private byte[] mData = null;
    private Stats mStats;

    public MoCameraRequestHandler(Stats stats) {
        mStats = stats;
    }

    @Override
    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return ("mox".equals(scheme));
    }

    @Nullable
    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        Uri uri = request.uri;

        if (uri == null) throw new NullPointerException("uri is null");
        String strSize = uri.getQueryParameter("size");
        if (TextUtils.isEmpty(strSize)) throw new MoCameraResponseException();

        int size = 0;
        try {
            size = Integer.parseInt(strSize);
        } catch (Exception e) {
            throw new MoCameraResponseException();
        }
        if (size == 0) throw new MoCameraResponseException();
//        byte[] data = new byte[size];
//        mRequestCount = 0;
//        dividePackages(size, uri, data);
        byte[] data = new MoCameraRequest().request(uri, size);
        if (data != null) {
            mStats.dispatchDownloadFinished(data.length);
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return new Result(bitmap, Picasso.LoadedFrom.NETWORK);
    }




    static final class MoCameraResponseException extends IllegalArgumentException {

        MoCameraResponseException() {
            super("size is 0");

        }
    }
}
