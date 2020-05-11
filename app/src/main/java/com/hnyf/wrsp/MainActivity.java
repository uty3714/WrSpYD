package com.hnyf.wrsp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean hasSu = SuUtils.hasRootAccess(this);
        Log.i("TAG","hasSu = " + hasSu);

        if(Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1024);
        }


    }

    /**
     * 添加抖音号
     * @param view
     */
    public void addDy(View view) {

        Intent intent = new Intent(this,AddDyActivity.class);
        startActivity(intent);

    }

    /**
     * 去抖音列表
     * @param view
     */
    public void toList(View view) {


        Intent intent = new Intent(this, DyAccountListActivity.class);
        startActivity(intent);

    }
}

