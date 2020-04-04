package com.caishi.chaoge.bean;

public class UpAppBean {
    public Data data;

    public class Data {


        public Result result;

        public class Result {
            public String source;//那个包 ChaoGe

            public String version;//版本

            public int type;//OS Type

            public String channel;//渠道

            public String url;//更新地址

            public String desc;//更新描述

            public boolean force;//是否强制更新

            public long time;//上传时间

        }
    }
}
