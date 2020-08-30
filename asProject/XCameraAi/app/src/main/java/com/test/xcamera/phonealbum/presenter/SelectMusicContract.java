package com.test.xcamera.phonealbum.presenter;


import com.test.xcamera.download.DownloadListener;
import com.test.xcamera.phonealbum.bean.MusicResult;
import com.test.xcamera.phonealbum.bean.MusicTypeResult;

public class SelectMusicContract {
    public interface View{
        /**
         * 音乐分类
         * @param dataResult
         */
        void showMusicTypeData(MusicTypeResult dataResult);

        void showMusicTypeError(String msg);

        /**
         * 显示音数据
         * @param dataResult
         */
          void showMusicData(MusicResult dataResult);
        /**
         * 数据加载失败
         * @param msg
         */
          void showMusicError(String msg);
    }
    public interface Presenter{
        /**
         * 加载音乐数据
         */
        void loadMusicData(int curPage,int typeId);

        /**
         * 音乐分类
         */
        void loadMusicTypeData();

        /**
         * 音乐下载
         * @param musicId
         * @param listener
         */
        void downMusicData(String musicId, DownloadListener listener);
        /**
         * 音乐搜索
         */
        void loadSearchMusicData(int curPage,String keyWord);
    }

}
