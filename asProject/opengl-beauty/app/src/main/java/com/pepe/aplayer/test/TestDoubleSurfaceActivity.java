package com.pepe.aplayer.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pepe.aplayer.R;


public class TestDoubleSurfaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public static void open(Context context){
        Intent intent = new Intent(context, TestDoubleSurfaceActivity.class);
        context.startActivity(intent);
    }
}
