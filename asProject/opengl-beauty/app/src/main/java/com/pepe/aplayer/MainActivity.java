package com.pepe.aplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//            AFragment fragmentVideo = new AFragment();
//        Camera2VideoFragment fragmentVideo = new Camera2VideoFragment();
        TestCamera2Fragment fragmentVideo = new TestCamera2Fragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragmentVideo, TestCamera2Fragment.class.getName()).commit();
    }
}
