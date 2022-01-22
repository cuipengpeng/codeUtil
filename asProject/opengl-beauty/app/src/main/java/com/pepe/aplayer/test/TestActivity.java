package com.pepe.aplayer.test;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.pepe.aplayer.R;
import com.pepe.aplayer.opengl.BeautyRenderer;


public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public static void open(Context context){
        Intent intent = new Intent(context, TestActivity.class);
        context.startActivity(intent);
    }
}
