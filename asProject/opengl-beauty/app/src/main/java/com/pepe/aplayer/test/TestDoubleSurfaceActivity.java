package com.pepe.aplayer.test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pepe.aplayer.R;
import com.pepe.aplayer.view.adapter.SurfaceAdapter;
import com.pepe.aplayer.view.widget.DoubleSurfaceView;


public class TestDoubleSurfaceActivity extends AppCompatActivity {

    private DoubleSurfaceView doubleSurfaceView;
    private RecyclerView recyclerView;
    private SurfaceAdapter surfaceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        setContentView(R.layout.activity_test);
        doubleSurfaceView = findViewById(R.id.sv_testActivity_double);
        recyclerView = findViewById(R.id.rv_testActivity_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));
        surfaceAdapter = new SurfaceAdapter(getApplication());
        recyclerView.setAdapter(surfaceAdapter);
        surfaceAdapter.notifyDataSetChanged();
        doubleSurfaceView.setSurfaceAdapter(surfaceAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void open(Context context){
        Intent intent = new Intent(context, TestDoubleSurfaceActivity.class);
        context.startActivity(intent);
    }
}
