package com.caishi.chaoge.rd.overturn;

import android.graphics.Color;
import android.graphics.Rect;

import com.caishi.chaoge.bean.AEModuleBean.AETextLayerInfo;
import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.bean.ModuleMaterialBean;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer1;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer10;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer11;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer12;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer13;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer14;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer15;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer16;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer17;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer18;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer19;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer2;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer20;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer21;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer22;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer23;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer24;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer25;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer26;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer27;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer28;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer29;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer3;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer30;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer31;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer32;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer33;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer34;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer35;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer36;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer37;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer38;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer39;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer4;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer40;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer41;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer42;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer43;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer44;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer45;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer46;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer47;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer48;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer49;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer5;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer50;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer6;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer7;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer8;
import com.caishi.chaoge.rd.overturn.drawtext.CustomDrawTextLayer9;
import com.caishi.chaoge.utils.LogUtil;
import com.rd.vecore.models.CanvasObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 */
public class CustomDrawTextHandler {
    private List<LrcBean> lrcList;
    private List<CustomDrawTextLayer> customDrawTextLayerList = new ArrayList<>();
    private List<Integer> textHeightList = new ArrayList<>();
    private List<Float> randomNumList = new ArrayList<>();


    public CustomDrawTextHandler(ModuleMaterialBean moduleMaterialBean) {
        int singleSize;//小于三个字以150为基数，大于以60
        if (moduleMaterialBean == null) {
            LogUtil.e("ModuleMaterialBean为null");
            return;
        }
        lrcList = moduleMaterialBean.lrcList;
        if (null != lrcList && lrcList.size() > 0)
            for (int i = 0; i < lrcList.size(); i++) {
                String text = lrcList.get(i).getLrc();
                switch (text.length()) {
                    case 1:
                        singleSize = 200;
                        break;
                    case 2:
                        singleSize = 150;
                        break;
                    case 3:
                        singleSize = 100;
                        break;
                    case 4:
                        singleSize = 90;
                        break;
                    case 5:
                        singleSize = 80;
                        break;
                    case 6:
                        singleSize = 70;
                        break;
                    default:
                        singleSize = 60;
                        break;
                }
                int width = 360;
                int height = singleSize;
                textHeightList.add(height);
                Random random = new Random();
                int randomNum = random.nextInt(14);
                float num = (randomNum + 6) / 10f;
                randomNumList.add(num);//生成5-20随机数 并保存成float。
                float timelineFrom = lrcList.get(i).getStart() / 1000f;
                float timelineTo = lrcList.get(i).getEnd() / 1000f;
                TextLayer textLayer = new TextLayer(width, height, timelineFrom, timelineTo, new Rect(6, 6, 6, 6),
                        text, Color.parseColor(moduleMaterialBean.colorList.get(i)), moduleMaterialBean.fontFilePath,
                        AETextLayerInfo.Alignment.left);
                switch (i) {
                    case 0:
                        customDrawTextLayerList.add(new CustomDrawTextLayer1(textLayer));
                        break;
                    case 1:
                        customDrawTextLayerList.add(new CustomDrawTextLayer2(textLayer));
                        break;
                    case 2:
                        customDrawTextLayerList.add(new CustomDrawTextLayer3(textLayer));
                        break;

                    case 3:
                        customDrawTextLayerList.add(new CustomDrawTextLayer4(textLayer));
                        break;

                    case 4:
                        customDrawTextLayerList.add(new CustomDrawTextLayer5(textLayer));
                        break;

                    case 5:
                        customDrawTextLayerList.add(new CustomDrawTextLayer6(textLayer));
                        break;
//
                    case 6:
                        customDrawTextLayerList.add(new CustomDrawTextLayer7(textLayer));
                        break;
//
                    case 7:
                        customDrawTextLayerList.add(new CustomDrawTextLayer8(textLayer));
                        break;
//
                    case 8:
                        customDrawTextLayerList.add(new CustomDrawTextLayer9(textLayer));
                        break;
                    case 9:
                        customDrawTextLayerList.add(new CustomDrawTextLayer10(textLayer));
                        break;
                    case 10:
                        customDrawTextLayerList.add(new CustomDrawTextLayer11(textLayer));
                        break;
                    case 11:
                        customDrawTextLayerList.add(new CustomDrawTextLayer12(textLayer));
                        break;
                    case 12:
                        customDrawTextLayerList.add(new CustomDrawTextLayer13(textLayer));
                        break;
                    case 13:
                        customDrawTextLayerList.add(new CustomDrawTextLayer14(textLayer));
                        break;
                    case 14:
                        customDrawTextLayerList.add(new CustomDrawTextLayer15(textLayer));
                        break;
                    case 15:
                        customDrawTextLayerList.add(new CustomDrawTextLayer16(textLayer));
                        break;
                    case 16:
                        customDrawTextLayerList.add(new CustomDrawTextLayer17(textLayer));
                        break;
                    case 17:
                        customDrawTextLayerList.add(new CustomDrawTextLayer18(textLayer));
                        break;
                    case 18:
                        customDrawTextLayerList.add(new CustomDrawTextLayer19(textLayer));
                        break;
                    case 19:
                        customDrawTextLayerList.add(new CustomDrawTextLayer20(textLayer));
                        break;
                    case 20:
                        customDrawTextLayerList.add(new CustomDrawTextLayer21(textLayer));
                        break;
                    case 21:
                        customDrawTextLayerList.add(new CustomDrawTextLayer22(textLayer));
                        break;
                    case 22:
                        customDrawTextLayerList.add(new CustomDrawTextLayer23(textLayer));
                        break;
                    case 23:
                        customDrawTextLayerList.add(new CustomDrawTextLayer24(textLayer));
                        break;
                    case 24:
                        customDrawTextLayerList.add(new CustomDrawTextLayer25(textLayer));
                        break;
                    case 25:
                        customDrawTextLayerList.add(new CustomDrawTextLayer26(textLayer));
                        break;
                    case 26:
                        customDrawTextLayerList.add(new CustomDrawTextLayer27(textLayer));
                        break;
                    case 27:
                        customDrawTextLayerList.add(new CustomDrawTextLayer28(textLayer));
                        break;
                    case 28:
                        customDrawTextLayerList.add(new CustomDrawTextLayer29(textLayer));
                        break;
                    case 29:
                        customDrawTextLayerList.add(new CustomDrawTextLayer30(textLayer));
                        break;
                    case 30:
                        customDrawTextLayerList.add(new CustomDrawTextLayer31(textLayer));
                        break;
                    case 31:
                        customDrawTextLayerList.add(new CustomDrawTextLayer32(textLayer));
                        break;
                    case 32:
                        customDrawTextLayerList.add(new CustomDrawTextLayer33(textLayer));
                        break;
                    case 33:
                        customDrawTextLayerList.add(new CustomDrawTextLayer34(textLayer));
                        break;
                    case 34:
                        customDrawTextLayerList.add(new CustomDrawTextLayer35(textLayer));
                        break;
                    case 35:
                        customDrawTextLayerList.add(new CustomDrawTextLayer36(textLayer));
                        break;
                    case 36:
                        customDrawTextLayerList.add(new CustomDrawTextLayer37(textLayer));
                        break;
                    case 37:
                        customDrawTextLayerList.add(new CustomDrawTextLayer38(textLayer));
                        break;
                    case 38:
                        customDrawTextLayerList.add(new CustomDrawTextLayer39(textLayer));
                        break;
                    case 39:
                        customDrawTextLayerList.add(new CustomDrawTextLayer40(textLayer));
                        break;
                    case 40:
                        customDrawTextLayerList.add(new CustomDrawTextLayer41(textLayer));
                        break;
                    case 41:
                        customDrawTextLayerList.add(new CustomDrawTextLayer42(textLayer));
                        break;
                    case 42:
                        customDrawTextLayerList.add(new CustomDrawTextLayer43(textLayer));
                        break;
                    case 43:
                        customDrawTextLayerList.add(new CustomDrawTextLayer44(textLayer));
                        break;
                    case 44:
                        customDrawTextLayerList.add(new CustomDrawTextLayer45(textLayer));
                        break;
                    case 45:
                        customDrawTextLayerList.add(new CustomDrawTextLayer46(textLayer));
                        break;
                    case 46:
                        customDrawTextLayerList.add(new CustomDrawTextLayer47(textLayer));
                        break;
                    case 47:
                        customDrawTextLayerList.add(new CustomDrawTextLayer48(textLayer));
                        break;
                    case 48:
                        customDrawTextLayerList.add(new CustomDrawTextLayer49(textLayer));
                        break;
                    case 49:
                        customDrawTextLayerList.add(new CustomDrawTextLayer50(textLayer));
                        break;
                }


            }


    }


    /**
     * 自绘
     *
     * @param canvas
     * @param currentProgress 单位：秒
     */
    public void drawText(CanvasObject canvas, float currentProgress) {
        if (customDrawTextLayerList.size() > 0) {
            for (int i = 0; i < customDrawTextLayerList.size(); i++) {
                customDrawTextLayerList.get(i).initConfigAndDraw(canvas, currentProgress, lrcList, textHeightList,randomNumList);
            }
        }

    }


}
