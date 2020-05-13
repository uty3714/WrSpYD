package com.hnyf.wrsp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;


public class JkClipboardUtils {

    private static class ClipboardUtilsHolder {
        private static JkClipboardUtils HOLDER = new JkClipboardUtils();
    }

    private JkClipboardUtils() {
    }

    public static JkClipboardUtils get() {
        return ClipboardUtilsHolder.HOLDER;
    }


    /**
     * 获取剪切板信息
     *
     * @return 剪切板
     */
    public String getClipboardContent(Context context) {
        //获取手机剪切板的内容
        String comment = "";
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context.
                    getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = null;
            if (clipboardManager != null) {
                clipData = clipboardManager.getPrimaryClip();
                if (clipboardManager.hasPrimaryClip()) {
                    if (clipData != null && clipData.getItemCount() > 0) {
                        if (clipData.getItemAt(0).getText() != null) {
                            comment = clipData.getItemAt(0).getText().toString();
                            if (comment.length() > 150) {
                                comment = comment.substring(0, 150);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            comment = "";
        }
        return comment;
    }


    /**
     * 往剪切板写数据
     * @param content 写的内容
     */
    public void copyStrToClipboard(Context context,String content) {
        // 得到剪贴板管理器
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("data", content));
            }
            Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
