package com.hnyf.wrsp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
                Log.i("TAG","当前账号的路径："+ file.getAbsolutePath());
                if (!file.exists()) {
                    file.mkdirs();
                }

                if(file.listFiles() != null && file.listFiles().length >0){
                    for (File listFile : file.listFiles()) {
                        boolean deleteResult = listFile.delete();
                        Log.i("TAG","删除结果result:"+deleteResult);
                    }
                }

                Log.i("TAG","删除后的文件列表:" + file.listFiles().length);

                //重新保存一份
                copyDataFileToMySDCard(file);


            }
        }).start();


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

        final ShellUtils.CommandResult commandResult = ShellUtils.execCommand(copyFiles, hasSu, true);
        Log.i("TAG", "保存到本地:" + file.getAbsolutePath() + ",结果:" + commandResult.toString());

       new Handler(Looper.getMainLooper()).post(new Runnable() {
           @Override
           public void run() {
               Toast.makeText(MainActivity.this, "更新结果:" + (commandResult.result == 0 ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
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

