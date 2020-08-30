package com.test.xcamera.dymode.utils;


import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import com.google.gson.stream.JsonReader;
import com.ss.android.ugc.effectmanager.common.listener.IJsonConverter;

import java.io.InputStream;
import java.io.InputStreamReader;



public class ImplJsonConverter implements IJsonConverter {
    @Override
    public <T> T convertJsonToObj(InputStream json, Class<T> cls) {
        Gson gson = new Gson();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(json));
            T object = Primitives.wrap(cls).cast(gson.fromJson(reader, cls));
            reader.close();
            json.close();
            return object;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> String convertObjToJson(T object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }
}