package com.hnyf.wrsp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hnyf.wrsp.adapter.AccountListAdapter;
import com.hnyf.wrsp.listener.ICustomClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DyAccountListActivity extends AppCompatActivity implements ICustomClickListener {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private List<String> mList;
    private AccountListAdapter mAdapter;

    private boolean hasSu = false;

    @Override
    protected void onResume() {
        super.onResume();
        //本地取
        String localPath = FileUtils.SD_PATH + "/";
        File localFile = new File(localPath);

        if (localFile.exists()) {
            File[] files = localFile.listFiles();
            if (files != null && files.length > 0) {
                mList.clear();
                for (File f : files) {
                    mList.add(f.getName());
                }
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dy_account_list);

        hasSu = SuUtils.hasRootAccess(this);
        mList = new ArrayList<>();

        mAdapter = new AccountListAdapter(this, mList);
        mAdapter.setICustomClickListener(this);
        mRecyclerView = findViewById(R.id.account_list_view);
        mProgressBar = findViewById(R.id.progress_bar_list);

        mProgressBar.setVisibility(View.GONE);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider_line));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onCustomClick(View view, final int position) {

        if (view.getId() == R.id.item_account_switch_btn) {
            mProgressBar.setVisibility(View.VISIBLE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //杀掉抖音
                    String killDy = "am force-stop com.ss.android.ugc.aweme";
                    ShellUtils.execCommand(killDy, hasSu);
                    //恢复文件
                    String fileName = FileUtils.SD_PATH + "/" + mList.get(position);
                    File file = new File(fileName);
                    switchAccount(file);
                }
            }).start();

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

        final ShellUtils.CommandResult commandResult = ShellUtils.execCommand(copyFiles, hasSu, true);
        Log.i("TAG", "更新账号结果:" + commandResult.result);

        //修改权限
        String[] chmod = new String[]{
                "chmod 777 /data/data/com.ss.android.ugc.aweme/shared_prefs",
                "chmod -R 777 /data/data/com.ss.android.ugc.aweme/shared_prefs/*",
        };
        ShellUtils.execCommand(chmod, hasSu);

        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DyAccountListActivity.this, "更新账号结果:" + ((commandResult.result == 0) ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);

                try {
                    Intent launchIntentForPackage = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.ss.android.ugc.aweme");
                    startActivity(launchIntentForPackage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

}