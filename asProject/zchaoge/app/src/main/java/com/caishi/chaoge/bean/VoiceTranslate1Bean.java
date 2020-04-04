package com.caishi.chaoge.bean;


import java.util.List;


public class VoiceTranslate1Bean  {

    public int code;
    public String message;
    public List<Data> data;

    public static class Data {
        public long beginTime;
        public long endTime;
        public String text;
    }
}
