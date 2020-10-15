package com.verlif.idea.singledown.manager;

import com.verlif.idea.singledown.model.FileInfo;
import com.verlif.idea.singledown.tools.FileTool;
import com.verlif.idea.singledown.util.PrintUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadManager {

    private static DownloadManager instance;

    private OkHttpClient okHttpClient;
    private FileTool fileTool;
    private static HashMap<String, DownloadTask> downloadTaskHashMap;

    private DownloadManager() {
        okHttpClient = new OkHttpClient();
        fileTool = new FileTool();
        downloadTaskHashMap = new HashMap<>();
    }

    public static DownloadManager newInstance() {
        if (instance == null) {
            synchronized (DownloadManager.class) {
                if (instance == null) {
                    instance = new DownloadManager();
                }
            }
        }
        return instance;
    }

    /**
     * @param listener 下载监听
     */
    public void download(
            final DownloadInfo downloadInfo,
            final FormBody formBody, final OnDownloadListener listener) {
        Request request = new Request.Builder()
                .header("token", UserManager.token())
                .url(downloadInfo.url)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream is = response.body().byteStream();
                String fileName = downloadInfo.fileInfo.getFileName();
                File file = new File(downloadInfo.savePath, fileName);
                long total = response.body().contentLength();
                PrintUtil.println(total);

                DownloadTask downloadTask = new DownloadTask(is, file) {

                    @Override
                    public void size(long size) {
                        int progress = (int) (size * 100f / total);
                        listener.onDownloading(progress, size);
                    }

                    @Override
                    public void download() {
                        listener.onDownloadSuccess();
                    }

                    @Override
                    public void failed() {
                        listener.onDownloadFailed();
                    }
                };
                new Thread(downloadTask).start();
                // 向下载管理器中添加任务
                downloadTaskHashMap.put(fileName, downloadTask);
            }
        });
    }

    public DownloadTask getDownloadTask(String fileName) {
        return downloadTaskHashMap.get(fileName);
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress  下载进度
         * @param sizeNow   当前已下载大小
         */
        void onDownloading(int progress, long sizeNow);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }

    @Data
    public static class DownloadInfo {
        private String url;
        private String savePath;
        private FileInfo fileInfo;
    }

    public abstract static class DownloadTask implements Runnable {

        private InputStream is;
        private File targetFile;

        /**
         * 是否继续下载
         */
        private boolean c;

        public DownloadTask(InputStream is, File target) {
            this.is = is;
            this.targetFile = target;
            c = true;
        }

        @Override
        public void run() {
            byte[] buf = new byte[2048];
            int len;
            FileOutputStream fos = null;
            try {

                fos = new FileOutputStream(targetFile);
                long sum = 0;
                while ((len = is.read(buf)) != -1 && c) {
                    fos.write(buf, 0, len);
                    sum += len;
                    // 下载中
                    size(sum);
                }
                fos.flush();
                if (!c) {
                    fos.close();
                    targetFile.delete();
                    failed();
                } else {
                    // 下载完成
                    download();
                }
            } catch (Exception e) {
                failed();
            } finally {
                // 从下载管理器中移除任务
                downloadTaskHashMap.remove(targetFile.getName());
                try {
                    if (is != null)
                        is.close();
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException ignored) {
                }
            }
        }

        public void cancel() {
            c = false;
        }

        /**
         * 当前已下载大小
         * @param size  文件已下载大小bit
         */
        public abstract void size(long size);

        public abstract void download();

        public abstract void failed();
    }
}