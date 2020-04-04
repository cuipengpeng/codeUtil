package com.caishi.chaoge.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.caishi.chaoge.bean.AEModuleBean.AETemplateInfo;
import com.caishi.chaoge.bean.AEModuleBean.AETextLayerInfo;
import com.caishi.chaoge.bean.AEModuleBean.BackgroundMedia;
import com.caishi.chaoge.bean.AEModuleBean.DefaultMedia;
import com.rd.lib.utils.PaintUtils;
import com.rd.vecore.Music;
import com.rd.vecore.VirtualVideo;
import com.rd.vecore.models.AEFragmentInfo;
import com.rd.vecore.models.BlendEffectObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.caishi.chaoge.ui.activity.WelcomeActivity.strCustomPath;
import static com.caishi.chaoge.utils.ConstantUtils.FILE_BASE_PATH;
import static com.caishi.chaoge.utils.ConstantUtils.FILE_MODULE_PATH;

public class AEModuleUtils {

    private static final int VER_TAG = 1;
    private static String[] AEPaths = new String[]{"overturn", "horizontal", "vertical"};

    /**
     * 仿quik专用
     *
     * @param rootPath
     * @return
     * @throws JSONException
     */
    public static AETemplateInfo getConfig(String rootPath) throws JSONException {
        File config = new File(rootPath);
        String content = com.rd.lib.utils.FileUtils.readTxtFile(config.getAbsolutePath());
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        JSONObject json = new JSONObject(content);
        return getConfigImp(config, json, rootPath, false);


    }

    public static String getAEPath(int flag) {
        String s = strCustomPath + "/" + FILE_BASE_PATH + FILE_MODULE_PATH + AEPaths[flag] +
                "/" + "data.json";
        String image = strCustomPath + "/" + FILE_BASE_PATH + FILE_MODULE_PATH +
                AEPaths[flag] + "/" + "images/";
        List<File> allFiles = FileUtils.getAllFiles(image);
        if (allFiles.size() != 50 || !FileUtils.fileIsExists(s)) {
            AssetsFileUtils.getInstance().copyAssetsToSD("module/" + AEPaths[flag], FILE_BASE_PATH + FILE_MODULE_PATH + AEPaths[flag] + "/", false);//assets复制到SD卡上
        }
        return s;
    }

    private static AETemplateInfo getConfigImp(File config, JSONObject json, String rootPath, boolean ignoreText) throws JSONException {

        int ver = json.optInt("ver", 0);

        AETemplateInfo aeTemplateInfo = new AETemplateInfo();
        aeTemplateInfo.setDataPath(config.getAbsolutePath());
        parseAssets(aeTemplateInfo, rootPath, json.optJSONArray("assets"));


        JSONObject textimg = json.optJSONObject("textimg");
        if (null != textimg) {
            JSONArray jarr = textimg.optJSONArray("text");
            List<AETextLayerInfo> aeTextLayerInfos = new ArrayList<>();
            int len = jarr.length();
            for (int i = 0; i < len; i++) {
                AETextLayerInfo tmp = new AETextLayerInfo(jarr.getJSONObject(i), ignoreText);
                if (config.getAbsolutePath().toLowerCase().contains("Boxed".toLowerCase())) {
                    //demo 特别处理   （兼容boxed 9-16 ->ReplaceableText11 ）
                    if (tmp.getName().equals("ReplaceableText1.png") || tmp.getName().equals("ReplaceableText11.png")) {
                        tmp.setTextContent("世界那么大");
                    } else if (tmp.getName().equals("ReplaceableText2.png") || tmp.getName().equals("ReplaceableText12.png")) {
                        tmp.setTextContent("因为有你而与众不同");
                    } else {
                        tmp.setTextContent("");
                    }

                }

                if (!TextUtils.isEmpty(tmp.getFontSrc())) {
                    tmp.setTtfPath(config.getParent() + "/" + tmp.getFontSrc());
                }
                aeTextLayerInfos.add(tmp);
            }
            aeTemplateInfo.setAETextLayerInfos(aeTextLayerInfos);
        }


        JSONObject rdsetting = json.optJSONObject("rdsetting");
        if (rdsetting != null) {
            aeTemplateInfo.setName(rdsetting.optString("theme"));
            aeTemplateInfo.setIconPath(rdsetting.optString("icon"));
            aeTemplateInfo.setWidth(rdsetting.optInt("w"));
            aeTemplateInfo.setHeight(rdsetting.optInt("h"));
            JSONArray jsonArray = rdsetting.optJSONArray(ver >= VER_TAG ? "aeNoneEdit" : "aene");
            if (jsonArray != null) {
                ArrayList<String> listPath = new ArrayList<>();
                for (int n = 0; n < jsonArray.length(); n++) {
                    listPath.add(new File(rootPath, jsonArray.optString(n)).getAbsolutePath());
                }
                aeTemplateInfo.setAENoneEditPath(listPath);
            }
            parseMusic(aeTemplateInfo, rootPath, rdsetting.optJSONObject("music"), ver);
            parseBackground(aeTemplateInfo, rootPath, rdsetting.optJSONArray(ver >= VER_TAG ? "background" : "bg"), ver);
            parseEffect(aeTemplateInfo, rootPath, rdsetting.optJSONArray("effects"), ver);
            return aeTemplateInfo;
        }


        JSONObject settings = json.optJSONObject("settings");
        if (null != settings) {
            String mask = settings.optString("maskVideo");
            if (!TextUtils.isEmpty(mask)) {
                File file = new File(rootPath, mask);
                String backgroundVideo = settings.optString("backgroundVideo");
                String mediaPath = null;
                if (!TextUtils.isEmpty(backgroundVideo)) {
                    File bgVideo = new File(rootPath, backgroundVideo);
                    mediaPath = bgVideo.getAbsolutePath();
                    BackgroundMedia backgroundMedia = new BackgroundMedia(mediaPath);
                    backgroundMedia.setDuration((float) (settings.optDouble("duration") / 1000));
                    backgroundMedia.setBegintime(0);
                    backgroundMedia.setType("video");
                    ArrayList<BackgroundMedia> listBackground = new ArrayList<>();
                    listBackground.add(backgroundMedia);
                    aeTemplateInfo.setBackground(listBackground);
                }
                BlendEffectObject effectObject = new BlendEffectObject(mediaPath, BlendEffectObject.EffectObjectType.MASK);
                effectObject.setMaskMediaPath(file.getAbsolutePath());
                effectObject.setStartTime(0);
                effectObject.setFilterType("video");
                effectObject.setWidth((float) settings.optDouble("width"));
                effectObject.setHeight((float) settings.optDouble("height"));
                effectObject.setFPS(settings.optInt("fps"));
                effectObject.setEndTime((float) (settings.optDouble("duration") / 1000));
                ArrayList<BlendEffectObject> blendEffectObjects = new ArrayList<>();
                blendEffectObjects.add(effectObject);
                aeTemplateInfo.setBlendEffectObject(blendEffectObjects);
            }
            return aeTemplateInfo;
        }
        return aeTemplateInfo;
    }


    public static String fixAEText(AETextLayerInfo layerInfo, String text, String ttf, String color, AEFragmentInfo.LayerInfo info) {
        int width = layerInfo.getWidth();
        int height = layerInfo.getHeight();
        boolean isVer = layerInfo.isVertical();


        ArrayList<String> textList = new ArrayList<>();
        if (isVer) {
            //纵向1列显示
            int len = text.length();
            for (int i = 0; i < len; i++) {
                textList.add(text.substring(i, i + 1));
            }
        } else {
            if (text.contains("\n") || layerInfo.getFontSize() == 0) {
                //横向多行显示
                String[] arr = text.split("\n");
                int len = arr.length;
                for (int i = 0; i < len; i++) {
                    if (!TextUtils.isEmpty(arr[i])) {
                        textList.add(arr[i]);
                    }
                }
            } else {
                //根据文字多少和字体大小，自动计算换行
                int textWidthMax = width - layerInfo.getPadding().left - layerInfo.getPadding().right;
                int textHeightMax = height - layerInfo.getPadding().top - layerInfo.getPadding().bottom;
                Paint paint = getPaint(layerInfo);
                paint.setTextSize(layerInfo.getFontSize());
                textList.addAll(getText(paint, text, textWidthMax, textHeightMax));


            }


        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(bitmap);
        cv.drawColor(Color.TRANSPARENT);

        if (textList.size() > 0) {
            //文字的最大区域距离边框距离
            Rect textPadding = layerInfo.getPadding();

//        cv.drawRect(new Rect(textPadding.left, textPadding.top, width - textPadding.right, height - textPadding.bottom), paint);


            Paint paint = getPaint(layerInfo);

            String tmpTTF = ttf;
            if (TextUtils.isEmpty(ttf) || !ttf.startsWith("/")) {
                tmpTTF = layerInfo.getTtfPath();
            }
            if (!TextUtils.isEmpty(tmpTTF) && tmpTTF.startsWith("/") && FileUtils.isExist(tmpTTF)) {
                try {
                    Typeface typeface = Typeface.createFromFile(tmpTTF);
                    paint.setTypeface(typeface);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(color))
                paint.setColor(Color.parseColor(color));
            else
                paint.setColor(layerInfo.getTextColor());
            if (layerInfo.getFontSize() > 0) {
                //fontSize有效
                paint.setTextSize(layerInfo.getFontSize());
            } else {
                //单行最长的文字字符串
                String textTarget = getTarget(textList);
                paint.setTextSize(fixTextSize(textTarget, new Rect(0, 0, width, height), textPadding, paint, 1));
            }

            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            int[] tHeight = PaintUtils.getHeight(paint);
            int len = textList.size();
            if (isVer)
                len = layerInfo.getMaxNum();
            int cHeight = (height - textPadding.top - textPadding.bottom);
            int itemHeight = cHeight / len;
            paint.setAlpha((int) (Math.max(0, Math.min(1, layerInfo.getAlpha())) * 255));
            for (int i = 0; i < textList.size(); i++) {
                Rect bounds = new Rect();
                String str = textList.get(i);
                paint.getTextBounds(str, 0, str.length(), bounds);
//                Log.e(TAG, i + " fixAEText: " + bounds + "   " + bounds.width() + "*" + bounds.height() + "  " + PaintUtils.getWidth(paint, str) + "  " + Arrays.toString(tHeight) + " width:" + width + "*" + height + " " + layerInfo.getName());
//                int textWidth = bounds.width();
                int textWidth = PaintUtils.getWidth(paint, textList.get(i));
                float centerX = 0;
                float tY = textPadding.top + itemHeight * i + (itemHeight / 2 + (tHeight[0] / 2 - fontMetrics.bottom));
                if (isVer) {
                    float y = textPadding.top + (itemHeight + 20) * i + (itemHeight / 2 + (tHeight[0] / 2 - fontMetrics.bottom));
                    cv.drawText(str, textPadding.left, y, paint);
                } else if (layerInfo.getAlignment().equals("left")) {
                    //左对齐
                    cv.drawText(str, textPadding.left, tY, paint);
                } else if (layerInfo.getAlignment().equals("right")) {
                    //右对齐
                    cv.drawText(str, (width - textPadding.right - textWidth), tY, paint);
                } else if (layerInfo.getAlignment().equals("center")) {
                    //左右居中
                    centerX = textPadding.left + ((width - textPadding.left - textPadding.right - textWidth) / 2);
                    cv.drawText(str, centerX, tY, paint);
                }
                //测试
//                cv.drawLine(10, itemHeight / 2, 10 + textWidth, itemHeight / 2, paint);
//                textWidth = PaintUtils.getWidth(paint, str);
//                cv.drawLine(10, itemHeight / 2 * 1.8f, 10 + (textWidth), itemHeight / 2 * 1.8f, paint);
            }
        }
        cv.save();
        String file = null;
        if (null != info) {
            file = PathUtils.getTempFileNameForSdcard("Temp", layerInfo.getName() + ".png");
//            file = PathUtils.getTempFileNameForSdcard("Temp", layerInfo.getName() + info.getRefId() + ">>" + info.getName() + ".png");
        } else {
            file = PathUtils.getTempFileNameForSdcard("Temp", layerInfo.getName() + ".png");
        }

        BitmapUtils.saveBitmapToFile(bitmap, file, true);
        bitmap.recycle();

        return file;

    }

    /**
     * 检查path，如不存在创建之<br>
     * 并检查此路径是否存在文件.nomedia,如没有创建之
     */
    public static void checkPath(File path) {
        if (!path.exists())
            path.mkdirs();
        File fNoMedia = new File(path, ".nomedia");
        if (!fNoMedia.exists()) {
            try {
                fNoMedia.createNewFile();
            } catch (IOException ignored) {
            }
        }
    }


    private static String getTarget(ArrayList<String> textList) {
        String target = null;
        int textWidth = 0;
        int len = textList.size();
        for (int i = 0; i < len; i++) {
            int count = getWordCount(textList.get(i));
            if (count >= textWidth) {
                textWidth = count;
                target = textList.get(i);
            }
        }
        return target;
    }

    /**
     * 由于Java是基于Unicode编码的，因此，一个汉字的长度为1，而不是2。
     * 但有时需要以字节单位获得字符串的长度。例如，“123abc长城”按字节长度计算是10，而按Unicode计算长度是8。
     * 为了获得10，需要从头扫描根据字符的Ascii来获得具体的长度。如果是标准的字符，Ascii的范围是0至255，如果是汉字或其他全角字符，Ascii会大于255。
     * 因此，可以编写如下的方法来获得以字节为单位的字符串长度
     *
     * @param s
     * @return
     */
    private static int getWordCount(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;
        }
        return length;

    }


    /**
     * @param text
     * @param rect
     * @param textPadding
     * @param paint
     * @return
     */
    private static float fixTextSize(String text, Rect rect, Rect textPadding, Paint paint, int line) {
        float textSize = 200;
        int maxWidth = rect.width() - (textPadding.left + textPadding.right);
        int maxHeight = rect.height() - (textPadding.bottom + textPadding.top);
        paint.setTextSize(textSize);
        while (PaintUtils.getWidth(paint, text) >= maxWidth || PaintUtils.getHeight(paint)[0] >= (maxHeight / line)) {

//            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//            Log.e(TAG, textSize + "fixTextSize: " + fontMetrics.ascent + "  " + fontMetrics.bottom + "   " + fontMetrics.descent + " " + fontMetrics.leading + "  " + fontMetrics.top);

            if (textSize < 3) {
                break;
            }
            textSize -= 3;
            paint.setTextSize(textSize);
        }
        return textSize;

    }

    private static Paint getPaint(AETextLayerInfo layerInfo) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(layerInfo.getStrokeWidth());
        paint.setShadowLayer(0, 0, 0, layerInfo.getShadowColor());
        paint.setFakeBoldText(layerInfo.isBold());
        if (layerInfo.isItalic()) {
            //文字倾斜默认为0，官方推荐的 - 0.25f 是斜体
            paint.setTextSkewX(-0.25f);

        }
        return paint;
    }


    /**
     * 对于刚从json解析的字体（没有换行符)，主动处理换行
     *
     * @param paint
     * @param str
     * @param textMaxWidth
     * @param textMaxHeight
     * @return
     */
    private static ArrayList<String> getText(Paint paint, String str, int textMaxWidth, int textMaxHeight) {
        String text = str.replace(" ", "").trim();
        ArrayList<String> list = new ArrayList<>();
        int len = text.length();
        if (len > 0) {
            int index = 0;
            int textIndexMax = len - 1;
            while (index < textIndexMax) {
                for (int i = index; i < len; i++) {
                    String tmp = str.substring(index, i);
                    int tmpWidth = PaintUtils.getWidth(paint, tmp);
                    if (tmpWidth > textMaxWidth) {
                        list.add(str.substring(index, i - 1));
                        index = i - 1;
                        break;
                    } else if (i == textIndexMax) {
                        //最后一个字已经扫描完毕  (跳出while 循环)
                        list.add(str.substring(index, len));
                        index = textIndexMax;
                        break;
                    }
                }
            }
        }
        return list;

    }


    /**
     * 解析默认资源
     *
     * @param aeTemplateInfo
     * @param rootPath
     * @param assets
     */
    private static void parseAssets(AETemplateInfo aeTemplateInfo, String rootPath, JSONArray assets)
            throws JSONException {
        List<DefaultMedia> listDefaultMedia = new ArrayList<>();

        for (int i = 0; assets != null && i < assets.length(); i++) {
            JSONObject joAssets = assets.getJSONObject(i);

            DefaultMedia defaultMedia = new DefaultMedia(joAssets.optString("id"), new File(rootPath, joAssets.optString("u") + joAssets.optString("p")).getAbsolutePath());

            defaultMedia.setHeight(joAssets.optInt("h"));
            defaultMedia.setWidth(joAssets.optInt("w"));
            listDefaultMedia.add(defaultMedia);
        }
        aeTemplateInfo.setDefaultMeida(listDefaultMedia);
    }


    /**
     * 解析音乐
     *
     * @param aeTemplateInfo
     * @param rootPath
     * @param joMusic
     * @throws JSONException
     */
    private static void parseMusic(AETemplateInfo aeTemplateInfo, String rootPath, JSONObject joMusic, int ver) throws JSONException {
        if (null != joMusic) {
            String musicPath = joMusic.optString(ver >= VER_TAG ? "fileName" : "src");
            if (!TextUtils.isEmpty(musicPath)) {
                Music music = VirtualVideo.createMusic(new File(rootPath, musicPath).getAbsolutePath());
                int startTime = (int) (joMusic.optDouble("begintime") * 1000);
                int endTime = startTime + (int) (joMusic.optDouble("duration") * 1000);
                music.setTimelineRange(startTime, endTime);
                aeTemplateInfo.setMusic(music);
            }
        }
    }

    /**
     * 解析背景视频
     *
     * @param backgrounds
     * @throws JSONException
     */
    private static void parseBackground(AETemplateInfo aeTemplateInfo, String rootPath, JSONArray backgrounds, int ver)
            throws JSONException {
        ArrayList<BackgroundMedia> listBackground = new ArrayList<>();
        for (int i = 0; backgrounds != null && i < backgrounds.length(); i++) {

            JSONObject joBg = backgrounds.getJSONObject(i);
            String strPath = joBg.optString(ver >= VER_TAG ? "fileName" : "src");
            BackgroundMedia backgroundMedia = new BackgroundMedia((new File(rootPath, strPath).getAbsolutePath()));

            backgroundMedia.setBegintime((float) (joBg.optDouble("begintime")));
            backgroundMedia.setDuration((float) (joBg.optDouble("duration")));
            backgroundMedia.setType(joBg.optString("type"));
            backgroundMedia.setMusic(joBg.optString("music"));

            JSONObject crop = joBg.optJSONObject("crop");
            if (crop != null) {
                backgroundMedia.setCropRect(new RectF((float) crop.optDouble("l"), (float) crop.optDouble("t"),
                        (float) crop.optDouble("r"), (float) crop.optDouble("b")));
            }
            listBackground.add(backgroundMedia);
        }
        aeTemplateInfo.setBackground(listBackground);
    }

    /**
     * 解析 mask screen
     *
     * @param effects
     * @throws JSONException
     */
    private static void parseEffect(AETemplateInfo aeTemplateInfo, String rootPath, JSONArray effects, int ver)
            throws JSONException {
        ArrayList<BlendEffectObject> listBlendEffectObject = new ArrayList<>();
        for (int i = 0; effects != null && i < effects.length(); i++) {
            JSONObject joEffect = effects.getJSONObject(i);
            String strFilter = joEffect.getString("filter");
            if (TextUtils.isEmpty(strFilter))
                continue;
            String strPath = joEffect.optString(ver >= VER_TAG ? "fileName" : "src");
            BlendEffectObject.EffectObjectType objectType = null;
            if (strFilter.equalsIgnoreCase("mask")) {
                objectType = BlendEffectObject.EffectObjectType.MASK;
            } else if (strFilter.equalsIgnoreCase("screen")) {
                objectType = BlendEffectObject.EffectObjectType.SCREEN;
            }
            BlendEffectObject effectObject = new BlendEffectObject(new File(rootPath, strPath).getAbsolutePath(), objectType);
            effectObject.setStartTime((float) (joEffect.optDouble("begintime")));
            effectObject.setEndTime(effectObject.getStartTime() + (float) (joEffect.optDouble("duration")));
            effectObject.setRepeat(joEffect.optInt("repeat") == 1);
            effectObject.setFilterType("video");

            String srcPath = joEffect.optString("srcmask");
            if (!TextUtils.isEmpty(srcPath)) {
                File f = new File(rootPath, srcPath);
                effectObject.setMaskMediaPath(f.getAbsolutePath());
            }
            listBlendEffectObject.add(effectObject);

        }
        aeTemplateInfo.setBlendEffectObject(listBlendEffectObject);
    }
}
