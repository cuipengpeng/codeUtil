package com.playppt.ppt.http.glide;

import android.content.Context;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.io.InputStream;

public class OkHttpsUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    /** * The default factory for {@link OkHttpsUrlLoader}s. */
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private static volatile okhttp3.OkHttpClient internalClient;
        private okhttp3.OkHttpClient client;

        private static okhttp3.OkHttpClient getInternalClient() {
            if (internalClient == null) {
                synchronized (Factory.class) {
                    if (internalClient == null) {
                        internalClient = OkHttpsClient.getOkHttpsClient();
                    }
                }
            }
            return internalClient;
        }

        /** * Constructor for a new Factory that runs requests using a static singleton client. */
        public Factory() {
            this(getInternalClient());
        }

        /** * Constructor for a new Factory that runs requests using given client. */
        public Factory(okhttp3.OkHttpClient client) {
            this.client = client;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new OkHttpsUrlLoader(client);
        }

        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }

    private final okhttp3.OkHttpClient client;

    public OkHttpsUrlLoader(okhttp3.OkHttpClient client) {
        this.client = client;
    }
    @Override
    public DataFetcher<InputStream> getResourceFetcher(GlideUrl model, int width, int height) {
        return new OkHttpsStreamFetcher(client, model);
    }
}