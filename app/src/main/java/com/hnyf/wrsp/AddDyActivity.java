package com.hnyf.wrsp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class AddDyActivity extends AppCompatActivity {


    private EditText mEt;

    private boolean hasSu = false;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dy);
        hasSu = SuUtils.hasRootAccess(this);
        mEt = findViewById(R.id.et_input_dy);
        mProgressBar = findViewById(R.id.progress_bar);

        mProgressBar.setVisibility(View.GONE);

    }


    /**
     * 提交抖音账号
     *
     * @param view
     */
    public void commitDyAccount(View view) {

        mProgressBar.setVisibility(View.VISIBLE);

        String etInput = mEt.getText().toString();
        if ("".equals(etInput)) {
            Toast.makeText(this, "输入的抖音号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //保存数据到本地
        saveFileToLocal(etInput);


    }

    /**
     * 保存数据到本地
     *
     * @param dyAccount 抖音名
     */
    private void saveFileToLocal(String dyAccount) {


        File file = new File(FileUtils.SD_PATH + "/" + dyAccount + "/");
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            copyDataFileToMySDCard(file);
        } else {
            //文件存在，就切换
            copyDataFileToMySDCard(file);
        }

    }

    /**
     * 切换账号
     *
     * @param localFile file
     */
    private void switchAccount(File localFile) {

        //删除本地
        //1、 删除5个文件
        String[] rmFiles = new String[]{
                "rm -rf /data/data/com.ss.android.ugc.aweme/cache/*",
                "rm -rf /data/data/com.ss.android.ugc.aweme/app_*",
                "rm -rf /data/data/com.ss.android.ugc.aweme/databases",
                "rm -rf /data/data/com.ss.android.ugc.aweme/files",
                "rm -rf /data/data/com.ss.android.ugc.aweme/shared_prefs/*",
        };

        ShellUtils.CommandResult commandResult3 = ShellUtils.execCommand(rmFiles, hasSu, true);
        Log.i("TAG", "删除之后" + commandResult3.toString());

        //判断本地是否有 shared_prefs 文件夹
        File sharePrefsFile = new File("/data/data/com.ss.android.ugc.aweme/shared_prefs/");
        if (sharePrefsFile.exists()) {

            copyMySdCardFileToData(localFile);

        } else {
            //
            String makeDir = "mkdir /data/data/com.ss.android.ugc.aweme/shared_prefs";
            ShellUtils.CommandResult commandResult = ShellUtils.execCommand(makeDir, hasSu, true);
            Toast.makeText(this, "创建 shared_prefs 文件夹结果:" + commandResult.result, Toast.LENGTH_SHORT).show();
        }

        //复制到本地
        copyMySdCardFileToData(localFile);

    }

    private void copyMySdCardFileToData(File localFile) {

        String[] copyFiles = new String[]{
                "cp " + localFile.getAbsolutePath() + "/aweme_user.xml /data/data/com.ss.android.ugc.aweme/shared_prefs/",
                "cp " + localFile.getAbsolutePath() + "/token_shared_preference.xml /data/data/com.ss.android.ugc.aweme/shared_prefs/",
                "cp " + localFile.getAbsolutePath() + "/LoginSharePreferences.xml /data/data/com.ss.android.ugc.aweme/shared_prefs/",
                "cp " + localFile.getAbsolutePath() + "/com.bytedance.sdk.account_setting.xml /data/data/com.ss.android.ugc.aweme/shared_prefs/",
                "cp " + localFile.getAbsolutePath() + "/MainTabPreferences.xml /data/data/com.ss.android.ugc.aweme/shared_prefs/"
        };

        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(copyFiles, hasSu, true);
        Log.i("TAG", "更新账号结果:" + commandResult.result);


        //修改权限
        //修改权限
        String[] chmod = new String[]{
                "chmod 777 /data/data/com.ss.android.ugc.aweme/shared_prefs",
                "chmod -R 777 /data/data/com.ss.android.ugc.aweme/shared_prefs/*",
        };
        ShellUtils.execCommand(chmod, hasSu);
        Toast.makeText(this, "更新账号结果:" + commandResult.result, Toast.LENGTH_SHORT).show();


    }

    /**
     * 保存文件
     *
     * @param file 本地文件夹
     */
    private void copyDataFileToMySDCard(File file) {

        String[] copyFiles = new String[]{
                //复制
                "cp /data/data/com.ss.android.ugc.aweme/shared_prefs/aweme_user.xml " + file.getAbsolutePath(),
                "cp /data/data/com.ss.android.ugc.aweme/shared_prefs/token_shared_preference.xml " + file.getAbsolutePath(),
                "cp /data/data/com.ss.android.ugc.aweme/shared_prefs/LoginSharePreferences.xml " + file.getAbsolutePath(),
                "cp /data/data/com.ss.android.ugc.aweme/shared_prefs/com.bytedance.sdk.account_setting.xml " + file.getAbsolutePath(),
                "cp /data/data/com.ss.android.ugc.aweme/shared_prefs/MainTabPreferences.xml " + file.getAbsolutePath(),

        };

        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(copyFiles, hasSu, true);
        Log.i("TAG", "保存到本地:" + file.getAbsolutePath() + ",结果:" + commandResult.toString());

        Toast.makeText(this, "保存结果:" + (commandResult.result == 0 ? "成功" : "失败"), Toast.LENGTH_SHORT).show();

        mProgressBar.setVisibility(View.GONE);
        if (commandResult.result == 0) {
            finish();
        }
    }


}
