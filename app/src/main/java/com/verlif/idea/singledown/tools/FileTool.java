package com.verlif.idea.singledown.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Environment;

import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;

public class FileTool {

    public FileTool() {
    }

    public static final String directory = "SingleDown";
    private static final String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    public static final int CODE_PERMISSION = 100;

    /**
     * 获取并创建统一的外部储存的目录
     *
     * @return 返回规定的外存目录，用于存放各种文件，如果没有此文件目录，且创建失败，返回null
     */
    public File getOrCreateUsedDirectory() {
        File file = new File(Environment.getExternalStorageDirectory(), directory);
        if (!file.exists()) {
            if (!file.mkdirs())
                return null;
        }
        return file;
    }

    /**
     * 获取在程序的根目录下的子集文件或目录
     *
     * @param filePathInRoot 相对路径
     * @return 文件实例
     */
    public File getStandardFile(String filePathInRoot) {
        return new File(getOrCreateUsedDirectory(), filePathInRoot);
    }

    /**
     * 获取读写权限
     *
     * @return 检查结果
     */
    public boolean getPermission(Activity activity) {
        if (activity == null) {
            return false;
        }
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(activity, perms)) {
            return true;
        } else {
            EasyPermissions.requestPermissions(activity, "请求本地存储权限", CODE_PERMISSION, perms);
            return false;
        }
    }

    /**
     * 选择文件
     *
     * @param activity   当前活动
     * @param resultCode 结果码
     */
    public void pickFiles(Activity activity, int resultCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("*/*");
        activity.startActivityForResult(intent, resultCode);
    }
}
