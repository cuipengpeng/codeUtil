package com.test.xcamera.watermark;

import android.app.Application;
import android.text.TextUtils;

import com.test.xcamera.watermark.controller.MarkerController;

import java.io.Serializable;

public class WaterMarker {

    private final int width;//水印显示的宽度
    private final int height;//水印显示的高度
    private final int x;//水印显示位置x
    private final int y;//水印显示位置y

    private final int outputVideoWidth;// 输出视频的宽
    private final int outputVideoHeight;// 输出视频的高

    private final boolean isNeedExtFile;//是否双路输出 水印输出一个 拼接后的输出一个
    private final boolean showLoading;//是否显示loading

//    private final float toLx;//显示位置距离左边x
//    private final float toTy;//显示位置距离顶部y
//
//    private final float toRx;//显示位置距离右边边x
//    private  final float toBY;//显示位置距离底部y

    //    private final int resId;//水印的res id
//    private final Drawable drawable;//水印的drawable
//    private final Bitmap bitmap;//水印的Bitmap
    //    private final String[] markPaths;//打水印的视频本地地址
//    private final String[] splicePaths;//拼接视频的本地地址
//    private final String[] paths;//既打水印又拼接的视频地址
    private final String path;//  打水印的视频地址
    //    private final String splicePath; // 拼接的视频地址
    private final String outputPath;//输出目录
    private final ResultListener resultListener;
    private final WaterMarker defaultMark;

    private WaterMarker(Builder builder) {
        this.width = builder.width;
        this.height = builder.height;
        this.x = builder.x;
        this.y = builder.y;
        this.outputVideoWidth = builder.outputVideoWidth;
        this.outputVideoHeight = builder.outputVideoHeight;
        this.isNeedExtFile = builder.isNeedExtFile;
        this.showLoading = builder.showLoading;
//        this.resId = builder.resId;
//        this.drawable = builder.drawable;
//        this.bitmap = builder.bitmap;
//        this.markPaths = builder.markPaths;
//        this.splicePaths = builder.splicePaths;
//        this.paths = builder.paths;
        this.path = builder.path;
//        this.splicePath = builder.splicePath;
        this.outputPath = builder.outputPath;
        this.resultListener = builder.resultListener;
        this.defaultMark = MarkerController.getInstance().getDefaultMark();
    }

    public static void onAppCreate(Application application) {
        MarkerController.getInstance().onAppCreate(application);
    }

//    public static void start(String path, String splicePath) {
//        WaterMarker.builder().setPath(path).setSplicePath(splicePath).buildStart();
//    }

    public static void start(String path) {
        WaterMarker.builder().setPath(path).buildStart();
    }

    public static void start(String path, ResultListener listener) {
        WaterMarker.builder().setPath(path).setResultListener(listener).buildStart();
    }

    public static void startWithoutLoading(String path, ResultListener listener) {
        WaterMarker.builder().setPath(path).setShowLoading(false).setResultListener(listener).buildStart();
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getWidth() {
        if (width != 0 || defaultMark == null) {
            return width;
        } else {
            return defaultMark.getWidth();
        }
    }

    public int getHeight() {
        if (height != 0 || defaultMark == null) {
            return height;
        } else {
            return defaultMark.getHeight();
        }
    }

    public int getX() {
        if (x != 0 || defaultMark == null) {
            return x;
        } else {
            return defaultMark.getX();
        }
    }

    public int getY() {
        if (y != 0 || defaultMark == null) {
            return y;
        } else {
            return defaultMark.getY();
        }
    }

    public boolean isNeedExtFile() {
        if (isNeedExtFile || defaultMark == null) {
            return isNeedExtFile;
        } else {
            return defaultMark.isNeedExtFile();
        }
    }

    public int getOutputVideoWidth() {
        if (outputVideoWidth != 0 || defaultMark == null) {
            return outputVideoWidth;
        } else {
            return defaultMark.getOutputVideoWidth();
        }
    }

    public int getOutputVideoHeight() {
        if (outputVideoHeight != 0 || defaultMark == null) {
            return outputVideoHeight;
        } else {
            return defaultMark.getOutputVideoHeight();
        }
    }

    public boolean getShowLoading() {
        return showLoading;
    }

    //    public int getResId() {
//        if (resId != 0 || defaultMark == null) {
//            return resId;
//        } else {
//            return defaultMark.getResId();
//        }
//    }
//
//    public Drawable getDrawable() {
//        if (drawable != null || defaultMark == null) {
//            return drawable;
//        } else {
//            return defaultMark.getDrawable();
//        }
//    }

//    public Bitmap getBitmap() {
//        if (bitmap != null || defaultMark == null) {
//            return bitmap;
//        } else {
//            return defaultMark.getBitmap();
//        }
//    }

//    public String[] getMarkPaths() {
//        return markPaths;
//    }
//
//    public String[] getSplicePaths() {
//        return splicePaths;
//    }

//    public String[] getPaths() {
//        return paths;
//    }

    public String getPath() {
        if (!TextUtils.isEmpty(path) || defaultMark == null) {
            return path;
        } else {
            return defaultMark.getPath();
        }
    }

//    public String getSplicePath() {
//        if (!TextUtils.isEmpty(splicePath) || defaultMark == null) {
//            return splicePath;
//        } else {
//            return defaultMark.getSplicePath();
//        }
//    }

    public String getOutputPath() {
        if (!TextUtils.isEmpty(outputPath) || defaultMark == null) {
            return outputPath;
        } else {
            return defaultMark.getOutputPath();
        }
    }

    public ResultListener getResultListener() {
        if (resultListener != null || defaultMark == null) {
            return resultListener;
        } else {
            return defaultMark.getResultListener();
        }
    }

    public void start() {
        MarkerController.getInstance().start(this);
    }

    public static class Builder implements Serializable{

        private int width;//水印显示的宽度
        private int height;//水印显示的高度
        private int outputVideoWidth;// 输出视频的宽
        private int outputVideoHeight;// 输出视频的高

        private int x;//水印显示位置x
        private int y;//水印显示位置y
        private boolean isNeedExtFile;//是否双路输出 水印输出一个 拼接后的输出一个
        private boolean showLoading = true;//是否显示loading

//    private float toLx;//显示位置距离左边x
//    private float toTy;//显示位置距离顶部y
//    private float toRx;//显示位置距离右边边x
//    private float toBY;//显示位置距离底部y

        //        private int resId;//水印的res id
//        private Drawable drawable;//水印的drawable
//        private Bitmap bitmap;//水印的Bitmap
        private String path;
        //        private String splicePath;
        // private String[] markPaths;//打水印的视频本地地址
        // private String[] splicePaths;//拼接视频的本地地址
        // private String[] paths;//既打水印又拼接的视频地址
        private String outputPath;//输出目录
        private ResultListener resultListener;

//        public WaterMarker.Builder setIcon(int resId) {
//            this.resId = resId;
//            return this;
//        }
//
//        public WaterMarker.Builder setIcon(Drawable drawable) {
//            this.drawable = drawable;
//            return this;
//        }
//
//        public WaterMarker.Builder setIcon(Bitmap bitmap) {
//            this.bitmap = bitmap;
//            return this;
//        }

        public WaterMarker.Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public WaterMarker.Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public WaterMarker.Builder setX(int x) {
            this.x = x;
            return this;
        }

        public WaterMarker.Builder setY(int y) {
            this.y = y;
            return this;
        }

        public WaterMarker.Builder setOutputVideoWidth(int outputVideoWidth) {
            this.outputVideoWidth = outputVideoWidth;
            return this;
        }

        public WaterMarker.Builder setOutputVideoHeight(int outputVideoHeight) {
            this.outputVideoHeight = outputVideoHeight;
            return this;
        }

        //        public WaterMark.Builder setMarkPaths(String... markPaths){
//            this.markPaths = markPaths;
//            return this;
//        }
//
//        public WaterMark.Builder setSplicePaths(String... splicePaths){
//            this.splicePaths = splicePaths;
//            return this;
//        }

//        public WaterMark.Builder setPaths(String... paths){
//            this.paths = paths;
//            return this;
//        }

        public WaterMarker.Builder setPath(String path) {
            this.path = path;
            return this;
        }

//        public WaterMarker.Builder setSplicePath(String splicePath) {
//            this.splicePath = splicePath;
//            return this;
//        }

        public WaterMarker.Builder setOutputPath(String outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public WaterMarker.Builder setNeedExtFile(boolean isNeedExtFile) {
            this.isNeedExtFile = isNeedExtFile;
            return this;
        }

        public WaterMarker.Builder setShowLoading(boolean showLoading) {
            this.showLoading = showLoading;
            return this;
        }

        public WaterMarker.Builder setResultListener(ResultListener resultListener) {
            this.resultListener = resultListener;
            return this;
        }

        public WaterMarker build() {
            return new WaterMarker(this);
        }

        public WaterMarker buildStart() {
            WaterMarker waterMarker = new WaterMarker(this);
            waterMarker.start();
            return waterMarker;
        }

        public WaterMarker buildDefault() {
            WaterMarker waterMarker = new WaterMarker(this);
            MarkerController.getInstance().initDefault(waterMarker);
            return waterMarker;
        }
    }

    public interface ResultListener {
        void onSuccess(String path);

        void onFailure(String err);

        default void onProgress(int progress, int type){};
    }
}
