package com.caishi.chaoge.bean;


import com.caishi.chaoge.base.BaseBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChangdaChen on 2018/10/11.
 * Redis保存 用户信息内嵌
 */
public class MineDataBean extends BaseBean implements Serializable {

        //userId")
        public String userId;//		userId
        public String credential;        //token
        public int enable;        //是否封禁
        public int isFirst;        //是否第一次登陆
        //朝歌号")
        public String userNo;    //	朝歌号
        //昵称")
        public String nickname;        //昵称
        //个人主页封面图")
        public String userBackGround; //个人主页封面图
        //sex")
        public String sex;
        //age")
        public int age;
        //头像")
        public String avatar;        //头像
        //手机号")
        public String mobile;        //手机号
        //生日 时间戳")
        public long birthday;    //Long	生日
        //所在地区")
        public String area;        //所在地区
        //大头像")
        public String bigAvatar;        //大头像
        //关注数")
        public long follows;        //关注数
        //关注状态 0 未关 1已关")
        public int followStatus;    //int	关注状态 0 未关 1已关
        //粉丝数")
        public long fans;        //粉丝数
        //获赞数")
        public long numberOfPraise;        //获赞数
        //动态数")
        public long moments; //动态数
        //收藏数")
        public long likes; //收藏数
        //认证状态")
        public int identifyStatus;        //认证状态
        //个人简介")
        public String remark;        //个人简介
        //第三方账户映射 partnerTypeId , PartnerName")
        public PartnerInfoMapBean partnerInfoMap;
        //第三方账户映射 partnerTypeId , PartnerId")
        public PartnerIdMap partnerIdMap;
        //星座")// 星座
        public String constellation = "";
        //行业")// 星座
        public List<String> industry = new ArrayList<>();
        //喜好(用户标签)")
        public List<String> character = new ArrayList<>();


   public static class PartnerIdMap implements Serializable {
        private String QQ;
        private String WebChat;
        private String WeiBo;
    }

    public static class PartnerInfoMapBean implements Serializable {
        /**
         * QQ : 测试专用
         */

        private String QQ;
        private String WebChat;
        private String WeiBo;

        public String getQQ() {
            return QQ;
        }

        public void setQQ(String QQ) {
            this.QQ = QQ;
        }

        public String getWebChat() {
            return WebChat;
        }

        public void setWebChat(String webChat) {
            WebChat = webChat;
        }

        public String getWeiBo() {
            return WeiBo;
        }

        public void setWeiBo(String weiBo) {
            WeiBo = weiBo;
        }
    }
}
