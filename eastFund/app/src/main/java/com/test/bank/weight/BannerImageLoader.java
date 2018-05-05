package com.test.bank.weight;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.test.bank.R;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by 55 on 2017/12/7.
 */

public class BannerImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(R.drawable.icon_placehold)
                .error(R.drawable.icon_placehold)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }
}
