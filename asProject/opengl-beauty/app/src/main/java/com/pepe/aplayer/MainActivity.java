package com.pepe.aplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            AFragment fragment = new AFragment();
        Camera2VideoFragment fragmentVideo = new Camera2VideoFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragmentVideo, AFragment.class.getName()).commit();
    }
}
