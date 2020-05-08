package com.hnyf.wrsp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hnyf.wrsp.adapter.AccountListAdapter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

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


    private void readFile(boolean su){

        String readUser = "cat /data/data/com.ss.android.ugc.aweme/shared_prefs/aweme_user.xml";

        //1、 删除5个文件
        String[] rmFile = new String[]{
                "rm -rf /data/data/com.ss.android.ugc.aweme/cache/*",
                "rm -rf /data/data/com.ss.android.ugc.aweme/app_*",
                "rm -rf /data/data/com.ss.android.ugc.aweme/databases",
                "rm -rf /data/data/com.ss.android.ugc.aweme/files",
                "rm -rf /data/data/com.ss.android.ugc.aweme/shared_prefs/*",
        };

        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(readUser, su, true);
        Log.i("TAG","删除之前获取user:"+ commandResult.toString());

        ShellUtils.CommandResult commandResult3 = ShellUtils.execCommand(rmFile, su, true);
        Log.i("TAG","删除之后"+ commandResult3.toString());


        String[] makeDirs = new String[]{
                "mkdir /data/data/com.ss.android.ugc.aweme/shared_prefs",
        };

        ShellUtils.CommandResult commandResult4 = ShellUtils.execCommand(makeDirs, su, true);
        Log.i("TAG","创建文件夹结果:"+commandResult4.toString());

        File shared_prefsFile = new File("/data/data/com.ss.android.ugc.aweme/shared_prefs/");
        if(!shared_prefsFile.exists()){
            Log.i("TAG","文件不存在，" + shared_prefsFile.isDirectory());
            boolean mkdirs = shared_prefsFile.mkdirs();
            Log.i("TAG","mkdirs = " + mkdirs);
        }else{
            Log.i("TAG","文件存在");
        }



        String[] addFile = new String[]{
                //复制
                "cp /storage/emulated/0/360/test/aweme_user.xml /data/data/com.ss.android.ugc.aweme/shared_prefs/",
                "cp /storage/emulated/0/360/test/token_shared_preference.xml /data/data/com.ss.android.ugc.aweme/shared_prefs/",
                "cp /storage/emulated/0/360/test/LoginSharePreferences.xml /data/data/com.ss.android.ugc.aweme/shared_prefs/",
                "cp /storage/emulated/0/360/test/com.bytedance.sdk.account_setting.xml /data/data/com.ss.android.ugc.aweme/shared_prefs/",
                "cp /storage/emulated/0/360/test/MainTabPreferences.xml /data/data/com.ss.android.ugc.aweme/shared_prefs/",
        };

        ShellUtils.CommandResult commandResult1 = ShellUtils.execCommand(addFile, su, true);
        Log.i("TAG","替换之后"+ commandResult1.toString());

        //修改权限
        String[] chmod = new String[]{
                "chmod 777 /data/data/com.ss.android.ugc.aweme/shared_prefs",
                "chmod -R 777 /data/data/com.ss.android.ugc.aweme/shared_prefs/*",
        };
        ShellUtils.execCommand(chmod,su);

        ShellUtils.CommandResult commandResult2 = ShellUtils.execCommand(readUser, su, true);
        Log.i("TAG","替换之后获取user:"+ commandResult2.toString());












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

