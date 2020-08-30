package com.test.xcamera.managers;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by zll on 2019/7/4.
 */

public class CommandHandler {
    private static final String TAG = "CommandHandler";

    private static CommandHandler instance = null;
    private static Object lock = new Object();
    private HashMap<String, BaseCommandProcessor> table;

    public static CommandHandler getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new CommandHandler();
            }
        }
        return instance;
    }

    public CommandHandler() {
        table = new HashMap<>();

        table.put("start_preview_resp", new StartPreviewProcessor());
        table.put("stop_preview_resp", new StopPreviewProcessor());
        table.put("switch_mode_resp", new SwitchModeProcessor());
        table.put("take_photo_resp", new TakePhotoProcessor());
        table.put("take_photo_setting_resp", new TakePhotoSettingProcessor());
        table.put("take_video_start_resp", new StartTakeVideoProcessor());
        table.put("take_video_stop_resp", new StopTakeVideoProcessor());
        table.put("take_video_setting_resp", new TakeVideoSettingProcessor());
        table.put("album_list_resp", new AlbumListProcessor());
        table.put("sync_system_info_resp", new SyncSystemInfoProcessor());
        table.put("navi_control_resp", new NaviControlProcessor());
        table.put("video_mark_resp", new VideoMarkProcessor());
        table.put("select_filter_resp", new SelectFilterProcessor());
        table.put("switch_beauty_resp", new SwitchBeautyProcessor());
        table.put("download_file_resp", new DownLoadFileProcessor());
        table.put("update_direction_resp", new UpdateDirectionProcessor());
    }

    public abstract class BaseCommandProcessor {
        public abstract void execute(JSONObject object);
    }

    public BaseCommandProcessor getHandler(String action) {
        if (TextUtils.isEmpty(action)) return null;
        return table.get(action);
    }

    /**
     * 打开预览
     */
    private class StartPreviewProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 关闭预览
     */
    private class StopPreviewProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 切换拍照模式
     */
    private class SwitchModeProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 拍照
     */
    private class TakePhotoProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 设置照片拍照参数
     */
    private class TakePhotoSettingProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 开始录视频
     */
    private class StartTakeVideoProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 结束录视频
     */
    private class StopTakeVideoProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 设置视频拍摄参数
     */
    private class TakeVideoSettingProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 获取相册列表
     */
    private class AlbumListProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 获取系统消息
     */
    private class SyncSystemInfoProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 云台控制
     */
    private class NaviControlProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * Mark视频
     */
    private class VideoMarkProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 滤镜开关
     */
    private class SelectFilterProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 美颜开关
     */
    private class SwitchBeautyProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 更新相机朝向
     */
    private class UpdateDirectionProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }

    /**
     * 下载
     */
    private class DownLoadFileProcessor extends BaseCommandProcessor {

        @Override
        public void execute(JSONObject object) {

        }
    }
}
