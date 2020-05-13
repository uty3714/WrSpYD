package com.hnyf.wrsp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hnyf.wrsp.event.EventCheckAccont;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView mCurAccountText;

    private String mCurAccountPath = "";
    private String mCurAccountName = "";

    private boolean hasSu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hasSu = SuUtils.hasRootAccess(this);
        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCurAccountText = findViewById(R.id.tv_show_current_account);

        mCurAccountName = JkSharedPreUtils.getPreferenceString("Account", "");
        mCurAccountPath = JkSharedPreUtils.getPreferenceString("Path", "");
        mCurAccountText.setText(mCurAccountName);
        Log.i("TAG","su? = " + hasSu);
        long startTime = System.currentTimeMillis();
        boolean hasSu = SuUtils.hasRootAccess(this);
        long endTime = System.currentTimeMillis();
        Log.i("TAG", "su?: " + (endTime - startTime));
        Log.i("TAG", "hasSu = " + hasSu);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);
        }


    }

    /**
     * 添加抖音号
     *
     * @param view
     */
    public void addDy(View view) {

        Intent intent = new Intent(this, AddDyActivity.class);
        startActivity(intent);

    }

    /**
     * 去抖音列表
     *
     * @param view
     */
    public void toList(View view) {


        Intent intent = new Intent(this, DyAccountListActivity.class);
        startActivity(intent);

    }

    /**
     * 手动切换
     * @param view
     */
    public void toAllList(View view) {

        Intent intent = new Intent(this, SDDyAccountListActivity.class);
        startActivity(intent);

    }

    /**
     * 更新账号
     *
     * @param view
     */
    public void updateAccount(View view) {

        if (TextUtils.isEmpty(mCurAccountPath) || TextUtils.isEmpty(mCurAccountName)) {
            //切换
            Toast.makeText(this, "本地账号数据异常", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(mCurAccountPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                switchAccount(file);
            }
        }).start();


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

        ShellUtils.CommandResult commandResult3 = ShellUtils.execCommand(rmFiles, SuUtils.hasRootAccess(this), true);
        Log.i("TAG", "删除之后" + commandResult3.toString());

        //判断本地是否有 shared_prefs 文件夹
        File sharePrefsFile = new File("/data/data/com.ss.android.ugc.aweme/shared_prefs/");
        if (sharePrefsFile.exists()) {

            copyMySdCardFileToData(localFile);

        } else {
            //
            String makeDir = "mkdir /data/data/com.ss.android.ugc.aweme/shared_prefs";
            ShellUtils.CommandResult commandResult = ShellUtils.execCommand(makeDir, SuUtils.hasRootAccess(this), true);
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

        final ShellUtils.CommandResult commandResult = ShellUtils.execCommand(copyFiles, SuUtils.hasRootAccess(this), true);
        Log.i("TAG", "更新账号结果:" + commandResult.result);

        //修改权限
        String[] chmod = new String[]{
                "chmod 777 /data/data/com.ss.android.ugc.aweme/shared_prefs",
                "chmod -R 777 /data/data/com.ss.android.ugc.aweme/shared_prefs/*",
        };
        ShellUtils.execCommand(chmod, SuUtils.hasRootAccess(this));

        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "更新账号结果:" + ((commandResult.result == 0) ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveCheckAccountEvent(EventCheckAccont eventCheckAccont) {
        Log.i("TAG", "收到了切换账号的消息," + eventCheckAccont.getAccountName() + "," +
                " path = " + eventCheckAccont.getAccountPath());
        mCurAccountPath = eventCheckAccont.getAccountPath();
        mCurAccountName = eventCheckAccont.getAccountName();

        JkSharedPreUtils.setPreferenceString("Account", mCurAccountName);
        JkSharedPreUtils.setPreferenceString("Path", mCurAccountPath);

        mCurAccountText.setText("" + mCurAccountName);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

