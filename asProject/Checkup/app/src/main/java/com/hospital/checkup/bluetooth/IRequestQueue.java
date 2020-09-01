package com.hospital.checkup.bluetooth;


public interface IRequestQueue<T> {
    void set(String key, T t);

    T get(String key);
}
