package com.test.xcamera.album;

import com.test.xcamera.bean.MoAlbumItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class MomediaWrraper {
    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    private LinkedHashMap<String, ArrayList<MoAlbumItem>> sectionInfo = new LinkedHashMap<>();
    private ArrayList<ArrayList<MoAlbumItem>> sectionList = new ArrayList<>();
    private ArrayList<MoAlbumItem> listUIitemBeans = new ArrayList<>();

    public int sectionCount() {
        return sectionInfo.size();
    }

    public int itemCountInsection(int section) {
        return sectionList.get(section).size();
    }

    public ArrayList<MoAlbumItem> getSection(int sec) {

        return sectionList.get(sec);
    }

    /**
     * zhouxuecheng
     * 用来计算每一组,一共有多少张照片包括连拍的照片
     *
     * @param sec
     * @return
     */
    public int getPhotoTotalCount(int sec) {
        int PhotoTotal = 0;
        ArrayList<MoAlbumItem> moMedia = sectionList.get(sec);
        for (int i = 0; i < moMedia.size(); i++) {
//            if (moMedia.get(i).isImageSeries()) {
//                PhotoTotal += ((MoImageSeries) moMedia.get(i)).getImageSeries().size();
//            } else {
            PhotoTotal += 1;
//            }
        }
        return PhotoTotal;
    }

    public ArrayList<MoAlbumItem> getListUIitemBeans() {
        return listUIitemBeans;
    }

    public int size() {
        return listUIitemBeans.size();
    }

    public MoAlbumItem get(int i) {
        return listUIitemBeans.get(i);
    }

    public void clear() {
        sectionInfo.clear();
        sectionList.clear();
        listUIitemBeans.clear();
    }

    public void addAll(ArrayList<MoAlbumItem> list) {
        for (int i = 0; i < list.size(); i++) {
            MoAlbumItem mmoMedia = list.get(i);
            long time = mmoMedia.getmCreateTime();
            int length = String.valueOf(time).length();
            if (length == 10) {
                time = time * 1000;
            }
            Date date = new Date(time);
            String dateStr = this.format.format(date);
            if (sectionInfo.containsKey(dateStr)) {
                sectionInfo.get(dateStr).add(mmoMedia);
            } else {
                ArrayList<MoAlbumItem> newList = new ArrayList<>();
                sectionInfo.put(dateStr, newList);
                sectionList.add(newList);
                newList.add(mmoMedia);
            }
            listUIitemBeans.add(mmoMedia);
        }
    }

    //将请求的数据,添加在六张图片的前面
    public void addAlls(ArrayList<MoAlbumItem> list, int index) {
        MoAlbumItem moMedia = getListUIitemBeans().get(getListUIitemBeans().size() - 1);
//        if (!moMedia.getmThumbnail().getmUri().startsWith("http")) {
//            for (int i = 0; i < list.size(); i++) {
//                MoAlbumItem mmoMedia = list.get(i);
//                Date date = new Date(mmoMedia.getmCreateTime());
//                String dateStr = this.format.format(date);
//                if (sectionInfo.containsKey(dateStr)) {
//                    sectionInfo.get(dateStr).add(mmoMedia);
//                } else {
//                    ArrayList<MoAlbumItem> newList = new ArrayList<>();
//                    sectionInfo.put(dateStr, newList);
//                    sectionList.add(sectionList.size() - 1, newList);
//                    newList.add(mmoMedia);
//                }
//                listUIitemBeans.add(getListUIitemBeans().size() - index, mmoMedia);
//            }
//        } else {
        addAll(list);
//        }
    }

    public void removeAll(ArrayList<MoAlbumItem> list) {
        for (int i = 0; i < list.size(); i++) {
            MoAlbumItem mmoMedia = list.get(i);
            Date date = new Date(mmoMedia.getmCreateTime());
            String dateStr = this.format.format(date);
            if (sectionInfo.containsKey(dateStr)) {
                ArrayList<MoAlbumItem> moMedia = sectionInfo.get(dateStr);
                for (int j = 0; j < moMedia.size(); j++) {
                    MoAlbumItem moMedia1 = moMedia.get(j);
                    if (moMedia1.getmThumbnail().getmUri().equals(mmoMedia.getmThumbnail().getmUri())) {
                        moMedia.remove(moMedia1);
                    }
                }
                if (moMedia.size() == 0) {
                    sectionInfo.remove(dateStr);
                    sectionList.remove(moMedia);
                }
            }
            for (int k = 0; k < listUIitemBeans.size(); k++) {
                MoAlbumItem moMedia2 = listUIitemBeans.get(k);
                if (moMedia2.getmThumbnail().getmUri().equals(mmoMedia.getmThumbnail().getmUri())) {
                    listUIitemBeans.remove(moMedia2);
                }
            }
        }
    }
}
