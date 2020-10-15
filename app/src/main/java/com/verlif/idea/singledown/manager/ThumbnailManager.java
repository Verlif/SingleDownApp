package com.verlif.idea.singledown.manager;

import android.app.Activity;
import android.content.Context;

import com.verlif.idea.singledown.tools.FileTool;
import com.verlif.idea.singledown.model.FileInfo;
import com.verlif.idea.singledown.util.PrintUtil;

import java.io.File;

public class ThumbnailManager {

    private static ThumbnailManager instance;

    private static final String PATH = "/.thumbnail/";
    private static String thumbnailPath;

    private ServerConnManager serverConnManager;

    private ThumbnailManager(String rootUrl) {
        FileTool fileTool = new FileTool();
        serverConnManager = ServerConnManager.newInstance(rootUrl);
        thumbnailPath = fileTool.getOrCreateUsedDirectory().getPath() + PATH;
        if (!new File(thumbnailPath).exists()) {
            new File(thumbnailPath).mkdir();
        }
    }

    public static ThumbnailManager newInstance(String rootUrl) {
        if (instance == null) {
            synchronized (ThumbnailManager.class) {
                if (instance == null) {
                    instance = new ThumbnailManager(rootUrl);
                }
            }
        }
        return instance;
    }

    /**
     * 获取图片的缩略图
     * 优先从已下载文件中搜索
     * 后从缩略图缓存文件中搜索
     * 最后从服务器获取
     *
     * @param fileInfo 图片文件信息
     * @return 缩略图文件
     */
    public File getThumbnail(FileInfo fileInfo) {
        File file = new File(thumbnailPath + fileInfo.getFileName());
        if (file.exists()) {
            return file;
        } else {
            serverConnManager.getThumbnail(fileInfo, new DownloadManager.OnDownloadListener() {
                @Override
                public void onDownloadSuccess() {
                }

                @Override
                public void onDownloading(int progress, long sizeNow) {
                }

                @Override
                public void onDownloadFailed() {
                }
            });
            return null;
        }
    }

    public void clear() {
        File file = new File(thumbnailPath);
        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File f : file.listFiles()) {
                f.delete();
            }
        }
    }

    public static boolean isPicture(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            switch (fileName.substring(index + 1)) {
                case "png":
                case "jpg":
                case "gif":
                case "jpeg":
                case "bmp":
                case "ico":
                    return true;
                default:
                    return false;
            }
        } else return false;
    }

    public static String getThumbnailPath() {
        return thumbnailPath;
    }
}
