package com.verlif.idea.singledown.manager;

import com.alibaba.fastjson.JSON;
import com.verlif.idea.singledown.model.FileInfo;
import com.verlif.idea.singledown.model.FileProgressRequestBody;
import com.verlif.idea.singledown.model.Result;
import com.verlif.idea.singledown.tools.FileTool;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerConnManager {

    private static ServerConnManager instance;

    private static final String URL_LOGIN = "/login";
    private static final String URL_CHECK = "/login/refresh";
    private static final String URL_FILE_PATH = "/query/list";
    private static final String URL_DOWNLOAD_FILE = "/file/download";
    private static final String URL_UPLOAD_FILE = "/file/upload";
    private static final String URL_NEW_FOLDER = "/file/newFolder";
    private static final String URL_DELETE = "/file/del";
    private static final String URL_RENAME = "/file/rename";
    private static final String URL_SERVER_INFO = "/server/info";
    private static final String URL_THUMBNAIL = "/picture/thumbnail";

    private OkHttpClient client;
    private String rootUrl;

    private ServerConnManager(String rootUrl) {
        client = getUnsafeOkHttpClient();
        this.rootUrl = rootUrl;
    }

    public static ServerConnManager newInstance(String rootUrl) {
        if (instance == null) {
            synchronized (ServerConnManager.class) {
                if (instance == null) {
                    instance = new ServerConnManager(rootUrl);
                }
            }
        }
        return instance;
    }

    /**
     * 获取不安全连接的客户端
     *
     * @return OkHttpClient对象
     */
    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final X509TrustManager x509TrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            };
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    x509TrustManager
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, x509TrustManager);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return new OkHttpClient();
        }
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    /**
     * 登录到服务器
     *
     * @param userName 用户名
     * @param password 密码
     * @param callBack 状态回调
     */
    public void Login(String userName, String password, InnerCallBack callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("password", password);
        defaultPost(getUrl() + URL_LOGIN, params, callBack);
    }

    /**
     * 检测并刷新用户token
     *
     * @param callBack 状态回调
     */
    public void checkUser(InnerCallBack callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("token", getToken());
        defaultPost(getUrl() + URL_CHECK, params, callBack);
    }

    /**
     * 列出当前路径的所有文件及文件夹
     *
     * @param path     路径
     * @param callBack 状态回调
     */
    public void listFile(String path, InnerCallBack callBack) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("token", getToken());
        if (path != null) {
            builder.add("path", path);
        }
        Request request = new Request.Builder()
                .header("token", getToken())
                .url(getUrl() + URL_FILE_PATH)
                .post(builder.build())
                .build();
        client.newCall(request).enqueue(callBack);
    }

    /**
     * 从服务器下载文件
     *
     * @param fileInfo           文件信息
     * @param onDownloadListener 下载回调
     */
    public void downloadFile(FileInfo fileInfo, DownloadManager.OnDownloadListener onDownloadListener) {
        File file = new FileTool().getOrCreateUsedDirectory();
        if (file != null) {
            FormBody formBody = new FormBody.Builder()
                    .add("fileName", fileInfo.getFileName())
                    .add("path", fileInfo.getPath())
                    .build();
            DownloadManager downloadManager = DownloadManager.newInstance();
            DownloadManager.DownloadInfo downloadInfo = new DownloadManager.DownloadInfo();
            downloadInfo.setUrl(getUrl() + URL_DOWNLOAD_FILE);
            downloadInfo.setSavePath(file.getPath());
            downloadInfo.setFileInfo(fileInfo);
            downloadManager.download(downloadInfo, formBody, onDownloadListener);
        } else {
            onDownloadListener.onDownloadFailed();
        }
    }

    /**
     * 上传文件
     *
     * @param file     上传的文件
     * @param path     上传的路径参数
     * @param callBack 状态回调
     */
    public Call upload(File file, String path, InnerCallBack callBack) {
        if (path == null) {
            path = "";
        }
        RequestBody fileBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("path", path)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .header("token", getToken())
                .url(getUrl() + URL_UPLOAD_FILE)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callBack);
        return call;
    }

    /**
     * 新建文件夹
     *
     * @param path       操作路径
     * @param folderName 新建文件夹名称
     * @param callBack   状态回调
     */
    public void newFolder(String path, String folderName, InnerCallBack callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("folderName", folderName);
        params.put("path", path);
        defaultPost(getUrl() + URL_NEW_FOLDER, params, callBack);
    }

    /**
     * 删除文件
     *
     * @param path     操作路径
     * @param fileName 文件名
     * @param callBack 状态回调
     */
    public void deleteFile(String path, String fileName, InnerCallBack callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("fileName", fileName);
        params.put("path", path);
        defaultPost(getUrl() + URL_DELETE, params, callBack);
    }

    /**
     * 重命名文件
     *
     * @param path     文件路径
     * @param fileName 原文件名
     * @param nowName  现命名文件
     * @param callBack 状态回调
     */
    public void renameFile(String path, String fileName, String nowName, InnerCallBack callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("fileName", fileName);
        params.put("path", path);
        params.put("nowName", nowName);
        defaultPost(getUrl() + URL_RENAME, params, callBack);
    }

    /**
     * 获取服务器信息
     *
     * @param callBack 状态回调
     */
    public void serverInfo(InnerCallBack callBack) {
        Request request = new Request.Builder()
                .url(getUrl() + URL_SERVER_INFO)
                .build();
        client.newCall(request).enqueue(callBack);
    }

    /**
     * 下载缓存图片文件
     *
     * @param fileInfo           图片信息
     * @param onDownloadListener 状态回调
     */
    public void getThumbnail(FileInfo fileInfo, DownloadManager.OnDownloadListener onDownloadListener) {
        File file = new FileTool().getOrCreateUsedDirectory();
        if (file != null) {
            FormBody formBody = new FormBody.Builder()
                    .add("fileName", fileInfo.getFileName())
                    .add("path", fileInfo.getPath())
                    .build();
            DownloadManager downloadManager = DownloadManager.newInstance();
            DownloadManager.DownloadInfo downloadInfo = new DownloadManager.DownloadInfo();
            downloadInfo.setUrl(getUrl() + URL_THUMBNAIL);
            downloadInfo.setSavePath(ThumbnailManager.getThumbnailPath());
            downloadInfo.setFileInfo(fileInfo);
            downloadManager.download(downloadInfo, formBody, onDownloadListener);
        } else onDownloadListener.onDownloadFailed();
    }

    private void defaultPost(String url, HashMap<String, String> params, InnerCallBack innerCallBack) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String s : params.keySet()) {
            builder.add(s, params.get(s));
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .header("token", getToken())
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(innerCallBack);
    }

    private String getUrl() {
        return rootUrl;
    }

    private String getToken() {
        return UserManager.token();
    }

    public abstract static class InnerCallBack implements Callback {

        public InnerCallBack() {
        }

        @Override
        public void onFailure(Call call, IOException e) {
            stateOnFalse("网络错误");
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String data = response.body().string();
                Result result = JSON.parseObject(data, Result.class);
                if (result != null) {
                    if (result.getCode() == Result.CODE_SUCCESS) {
                        stateOnTrue(result.getData());
                    } else stateOnFalse(result.getMsg());
                } else stateOnFalse("返回数据错误");
            } else {
                stateOnFalse("服务端不可用");
            }
        }

        public abstract void stateOnTrue(String data);

        public abstract void stateOnFalse(String message);
    }
}
