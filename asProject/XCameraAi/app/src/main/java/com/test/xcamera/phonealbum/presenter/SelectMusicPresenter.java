package com.test.xcamera.phonealbum.presenter;

import android.content.Context;

import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.download.DownloadListener;
import com.test.xcamera.download.DownloadUtil;
import com.test.xcamera.phonealbum.bean.MusicResult;
import com.test.xcamera.phonealbum.bean.MusicTypeResult;
import com.test.xcamera.utils.FileUtils;


public class SelectMusicPresenter implements SelectMusicContract.Presenter{
    private SelectMusicContract.View mView;
    private Context mContext;
    public static SelectMusicPresenter getInstance(SelectMusicContract.View view, Context context) {
        return new SelectMusicPresenter(view,context);
    }
    private SelectMusicPresenter(SelectMusicContract.View view, Context context){
        this.mView=view;
        this.mContext=context;
    }

    @Override
    public void loadMusicData(int curPage,int typeId) {
        ApiImpl.getInstance().getMusicResult(curPage, 20,typeId, new CallBack<MusicResult>() {
            @Override
            public void onSuccess(MusicResult musicResult) {
                if(musicResult!=null){
                    if(mView!=null){
                        mView.showMusicData(musicResult);
                    }
                }else {
                    if(mView!=null){
                        mView.showMusicError("");
                    }
                }

            }

            @Override
            public void onFailure(Throwable e) {
                if(mView!=null){
                    mView.showMusicError("");
                }
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void loadMusicTypeData() {
        ApiImpl.getInstance().getMusicTypeResult(0, 1000, new CallBack<MusicTypeResult>() {
            @Override
            public void onSuccess(MusicTypeResult musicResult) {
                if(musicResult!=null){
                    if(mView!=null){
                        mView.showMusicTypeData(musicResult);
                    }
                }else {
                    if(mView!=null){
                        mView.showMusicError("");
                    }
                }

            }

            @Override
            public void onFailure(Throwable e) {
                if(mView!=null){
                    mView.showMusicError("");
                }
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void downMusicData(String musicId, DownloadListener listener) {
        String path= FileUtils.getMusicSelectPath(musicId,mContext).getAbsolutePath();
        DownloadUtil.downLoadFile(Long.parseLong(musicId),path,listener);
    }

    @Override
    public void loadSearchMusicData(int curPage, String keyWord) {
        ApiImpl.getInstance().getSearchMusicResult(curPage, 20,keyWord, new CallBack<MusicResult>() {
            @Override
            public void onSuccess(MusicResult musicResult) {
                if(musicResult!=null){
                    if(mView!=null){
                        mView.showMusicData(musicResult);
                    }
                }else {
                    if(mView!=null){
                        mView.showMusicError("");
                    }
                }

            }

            @Override
            public void onFailure(Throwable e) {
                if(mView!=null){
                    mView.showMusicError("");
                }
            }

            @Override
            public void onCompleted() {

            }
        });
    }
}
