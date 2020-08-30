package com.test.xcamera.utils.glide;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.test.xcamera.picasso.MoCameraRequest;


public class OWLBitmapFetcher implements DataFetcher<Bitmap> {
    private OWLImageFid mImageFid;

    public OWLBitmapFetcher(OWLImageFid imageFid) {
        mImageFid = imageFid;
    }

    /**
     * get image stream ( run in glide background thread)
     */
    @Override
    public void loadData(Priority priority, DataCallback<? super Bitmap> callback) {
        synchronized (Thread.currentThread()) {
            try {
                Uri uri = Uri.parse(mImageFid.gtmUrl());

                if (uri == null) throw new NullPointerException("uri is null");
                String strSize = uri.getQueryParameter("size");

                int size = 0;
                try {
                    size = Integer.parseInt(strSize);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byte[] data = new MoCameraRequest().request(uri, size);

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                if (bitmap != null) {
//                    if (bitmap.getHeight() == 0 || bitmap.getWidth() == 0 || bitmap.getByteCount() == 0)
//                        Logcat.v().tag("THUMBNAIL_MOALBUMLIST_MEDIAREADTASK").msg(" 缩略图 bitmap 出问题了  bitmap.getHeight() " + bitmap.getHeight()
//                                + "  bitmap.getWidth() == 0 " + bitmap.getWidth() + " bitmap.getByteCount()  =  " + bitmap.getByteCount()).out();
//                }

                callback.onDataReady(bitmap);

            } catch (Exception e) {
                callback.onDataReady(null);
                e.printStackTrace();
            }

        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public Class<Bitmap> getDataClass() {
        return Bitmap.class;
    }

    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;

    }
}

