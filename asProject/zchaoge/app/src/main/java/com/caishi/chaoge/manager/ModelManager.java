package com.caishi.chaoge.manager;

import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.bean.ModuleMaterialBean;
import com.caishi.chaoge.utils.AssetsFileUtils;
import com.caishi.chaoge.utils.FileUtils;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.caishi.chaoge.ui.activity.WelcomeActivity.strCustomPath;
import static com.caishi.chaoge.utils.ConstantUtils.FILE_BASE_PATH;
import static com.caishi.chaoge.utils.ConstantUtils.FILE_MODULE_PATH;
import static com.caishi.chaoge.utils.ConstantUtils.SINGLE_TEXT_DURATION;

public class ModelManager {
    private String modelPaths = "one";
    public String[] AEPaths = new String[]{"overturn", "", "horizontal", "vertical"};
    private List<String> colorList = new ArrayList<>();
    private static ModelManager modelManager;
    private String modulePath;
    private String AEPath;
    private AssetsFileUtils assetsFileUtils;
    private String aeModulePath;
    private String bgPath;
    private String bgMusicPath;
    private String fontFilePath;
    private OnModelInitSucceed onModelInitSucceed;
    public boolean isCopy = false;
    private ArrayList<LrcBean> lrcBeanList;
    private String personMusicPath;


    public static ModelManager newInstance() {
        if (modelManager == null)
            modelManager = new ModelManager();
        return modelManager;
    }

    public void init(String personMusicPath, ArrayList<LrcBean> lrcBeanList) {
        this.personMusicPath = personMusicPath;
        this.lrcBeanList = lrcBeanList;
        for (int i = 0; i < lrcBeanList.size(); i++) {
            colorList.add("#ffffff");
        }
        assetsFileUtils = AssetsFileUtils.getInstance();
        modulePath = FILE_BASE_PATH + FILE_MODULE_PATH + modelPaths + "/";
        AEPath = getAeModulePath(0);
        initData();
        fileCopy();
    }

    private void fileCopy() {
        assetsFileUtils.copyAssetsToSD("module/" + modelPaths, modulePath, true);//assets复制到SD卡上
        assetsFileUtils.setFileOperateCallback(new AssetsFileUtils.FileOperateCallback() {
            @Override
            public void onSuccess() {
                LogUtil.d("复制成功");
                ModuleMaterialBean moduleMaterialBean = initModuleMaterialBean();
                if (onModelInitSucceed != null)
                    onModelInitSucceed.onSucceed(moduleMaterialBean);
            }

            @Override
            public void onFailed(String error) {
                LogUtil.d("复制失败===" + error);
            }
        });

    }

    private ModuleMaterialBean initModuleMaterialBean() {
        int specialFlag = 0;
        ModuleMaterialBean moduleMaterialBean = new ModuleMaterialBean();
        moduleMaterialBean.aeModulePath = aeModulePath;
        moduleMaterialBean.bgPath = bgPath;
        moduleMaterialBean.personMusicPath = personMusicPath;
        moduleMaterialBean.bgMusicPath = bgMusicPath;
        moduleMaterialBean.fontFilePath = fontFilePath;
        moduleMaterialBean.colorList = colorList;
        moduleMaterialBean.specialFlag = specialFlag;
        moduleMaterialBean.duration = lrcBeanList.get(lrcBeanList.size() - 1).getEnd() / 1000f;
        moduleMaterialBean.lrcList = lrcBeanList;
        return moduleMaterialBean;

    }


    private void initData() {
        aeModulePath = Utils.isEmpty(AEPath) ? "" : AEPath;
        bgPath = strCustomPath + "/" + modulePath + "img_bg.png";
        bgMusicPath = "";
        fontFilePath = strCustomPath + "/" + modulePath + "font.ttf";
    }


    public void clear() {
        assetsFileUtils.clearThis();
        if (modelManager != null) {
            modelManager = null;
        }

    }

    public void setOnModelInitSucceed(OnModelInitSucceed onModelInitSucceed) {
        this.onModelInitSucceed = onModelInitSucceed;

    }

    public interface OnModelInitSucceed {
        void onSucceed(ModuleMaterialBean moduleMaterialBean);
    }

    /**
     * 获取判断文件是存在 不存在复制
     *
     * @param position 对应的字效
     * @return 字效所在的位置
     */
    public String getAeModulePath(int position) {
        String path = "";
        if (position > 3 || position == 1) {
            return path;
        }
        String s = strCustomPath + "/" + FILE_BASE_PATH + FILE_MODULE_PATH +
                AEPaths[position] + "/" + "data.json";
        String image = strCustomPath + "/" + FILE_BASE_PATH + FILE_MODULE_PATH +
                AEPaths[position] + "/" + "images/";
        List<File> allFiles = FileUtils.getAllFiles(image);
        isCopy = false;
        if (allFiles.size() != 50 || !FileUtils.fileIsExists(s)) {
            isCopy = true;
            assetsFileUtils.copyAssetsToSD("module/" + AEPaths[position], FILE_BASE_PATH + FILE_MODULE_PATH + AEPaths[position] + "/", false);//assets复制到SD卡上
        }
        path = s;
        return path;
    }


}
