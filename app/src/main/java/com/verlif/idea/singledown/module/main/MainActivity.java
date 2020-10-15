package com.verlif.idea.singledown.module.main;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.manager.DownloadManager;
import com.verlif.idea.singledown.manager.ServerConnManager;
import com.verlif.idea.singledown.manager.ServerInfoManager;
import com.verlif.idea.singledown.manager.UserManager;
import com.verlif.idea.singledown.model.FileInfo;
import com.verlif.idea.singledown.model.map.FileInDraw;
import com.verlif.idea.singledown.module.base.BaseActivity;
import com.verlif.idea.singledown.module.login.LoginActivity;
import com.verlif.idea.singledown.module.setting.SettingActivity;
import com.verlif.idea.singledown.tools.FileTool;
import com.verlif.idea.singledown.ui.dialog.confirm.ConfirmDialog;
import com.verlif.idea.singledown.ui.dialog.file.FileInfoDialog;
import com.verlif.idea.singledown.ui.dialog.folder.NewFolderDialog;
import com.verlif.idea.singledown.ui.dialog.message.MessageDialog;
import com.verlif.idea.singledown.ui.dialog.one.OneDialog;
import com.verlif.idea.singledown.ui.dialog.progress.DownloadPgDialog;
import com.verlif.idea.singledown.util.UriUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MainActivity extends BaseActivity {

    /**
     * 选择文件结果码
     */
    private static final int OPEN_FILE_CODE = 1000;

    private ServerConnManager serverConnManager;

    private SmartRefreshLayout smartRefreshLayout;
    private MainFileManager mainFileManager;
    private TextView nowPathView;
    private Button uploadFile;

    private String nowFilePath;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setListener();
        serverConnManager.checkUser(new ServerConnManager.InnerCallBack() {
            @Override
            public void stateOnTrue(String data) {
                runOnUiThread(() -> {
                    userManager.saveToken(JSON.parseObject(data).getString("token"));
                    toastManager.setTextAndShow("欢迎回来");
                    refreshMap();
                });
            }

            @Override
            public void stateOnFalse(String message) {
                runOnUiThread(() -> {
                    toastManager.setTextAndShow(message);
                });
                if (userManager.getToken().equals("")) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
                }
            }
        });
    }

    private void init() {
        nowFilePath = "/";
        serverConnManager = ServerConnManager.newInstance(
                ServerInfoManager.newInstance(this).getRootUrl()
        );
        userManager = UserManager.newInstance(this);
        mainFileManager = new MainFileManager(this, R.id.main_recycler);

        smartRefreshLayout = findViewById(R.id.main_refresh);
        uploadFile = findViewById(R.id.main_upload);
        nowPathView = findViewById(R.id.main_path);

        nowPathView.setText(nowFilePath);
    }

    private void setListener() {
        nowPathView.setOnClickListener(v -> {
            upPath();
        });
        uploadFile.setOnClickListener(v -> {
            new FileTool().pickFiles(this, OPEN_FILE_CODE);
        });
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> refreshMap());
        findViewById(R.id.main_menuButton).setOnClickListener(v -> {
            View view = findViewById(R.id.main_menu);
            view.setVisibility(view.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        });
        findViewById(R.id.main_newFolder).setOnClickListener(v -> new NewFolderDialog(MainActivity.this) {
            @Override
            public void newFolder(String folderName) {
                serverConnManager.newFolder(nowFilePath, folderName, new ServerConnManager.InnerCallBack() {
                    @Override
                    public void stateOnTrue(String data) {
                        runOnUiThread(MainActivity.this::refreshMap);
                        cancel();
                    }

                    @Override
                    public void stateOnFalse(String message) {
                        runOnUiThread(() -> new MessageDialog(MainActivity.this).setMessageAndShow(message));
                    }
                });
            }
        }.show());
        findViewById(R.id.main_setting).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingActivity.class)));
    }

    /**
     * 回到上一层路径
     */
    private boolean upPath() {
        if (nowFilePath.length() > 1) {
            nowFilePath = nowFilePath.substring(0, nowFilePath.substring(0, nowFilePath.length() - 1).lastIndexOf(File.separator) + 1);
            refreshMap();
            return true;
        } else return false;
    }

    /**
     * 刷新列表视图
     */
    private void refreshMap() {
        smartRefreshLayout.autoRefresh();
        nowPathView.setText(nowFilePath);
        serverConnManager.listFile(nowFilePath, new ServerConnManager.InnerCallBack() {
            @Override
            public void stateOnTrue(String data) {
                List<FileInfo> fileInfoList = JSON.parseObject(data).getJSONArray("fileInfo").toJavaList(FileInfo.class);
                // 将文件夹置首
                List<FileInfo> sortList = new ArrayList<>();
                for (FileInfo fileInfo : fileInfoList) {
                    if (fileInfo.isFile()) {
                        sortList.add(fileInfo);
                    } else sortList.add(0, fileInfo);
                }
                runOnUiThread(() -> {
                    mainFileManager.init(sortList, new MainFileManager.OnClicked() {
                        @Override
                        public void onClick(FileInDraw fileInDraw) {
                            if (fileInDraw.getFileInfo().isFile()) {
                                showFileDialog(fileInDraw);
                            } else {
                                nowFilePath += fileInDraw.getFileInfo().getFileName() + File.separator;
                                refreshMap();
                            }
                        }

                        @Override
                        public void onLongTouch(FileInDraw fileInDraw) {
                            showFileDialog(fileInDraw);
                        }
                    });
                    smartRefreshLayout.finishRefresh();
                });
            }

            @Override
            public void stateOnFalse(String message) {
                runOnUiThread(() -> {
                    new MessageDialog(MainActivity.this).setMessageAndShow(message);
                    smartRefreshLayout.finishRefresh();
                });
            }
        });
    }

    private void showFileDialog(FileInDraw fileInDraw) {
        new FileInfoDialog(MainActivity.this, fileInDraw) {
            @Override
            public void download(FileInfo fileInfo) {
                FileTool fileTool = new FileTool();
                // 如果本地已存在文件
                if (fileTool.getStandardFile(fileInfo.getFileName()).exists()) {
                    ConfirmDialog dialog = new ConfirmDialog(MainActivity.this);
                    dialog.setBuildItem(
                            ConfirmDialog.BuildItem.builder()
                                    .message("本地已存在文件:\n" + fileInfo.getFileName())
                                    .leftButtonMsg("重新下载")
                                    .leftClickedLis(v -> {
                                        downloadFile(fileInfo);
                                        dialog.cancel();
                                    })
                                    .rightButtonMsg("取消")
                                    .rightClickedLis(v -> dialog.cancel())
                                    .build()
                    );
                    dialog.show();
                } else {
                    downloadFile(fileInfo);
                }
            }

            @Override
            public void delete(FileInfo fileInfo) {
                new DeleteConfirmDialog(MainActivity.this, fileInfo).show();
                cancel();
            }

            @Override
            public void rename(FileInfo fileInfo, String name) {
                serverConnManager.renameFile(nowFilePath, fileInfo.getFileName(), name, new ServerConnManager.InnerCallBack() {
                    @Override
                    public void stateOnTrue(String data) {
                        runOnUiThread(() -> {
                            refreshMap();
                            new MessageDialog(MainActivity.this).setMessageAndShow("重命名成功:\n" + fileInfo.getFileName() + "\n->\n" + name);
                        });
                        cancel();
                    }

                    @Override
                    public void stateOnFalse(String message) {
                        runOnUiThread(() -> {
                            new MessageDialog(MainActivity.this).setMessageAndShow(message);
                        });
                    }
                });
            }
        }.show();
    }

    private void downloadFile(FileInfo fileInfo) {
        DownloadPgDialog dialog = new DownloadPgDialog(MainActivity.this);
        dialog.setMax(100);
        serverConnManager.downloadFile(fileInfo, new DownloadManager.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(() -> {
                    dialog.cancel();
                    new MessageDialog(MainActivity.this).setMessageAndShow("下载完成:\n" + fileInfo.getFileName());
                });
            }

            @Override
            public void onDownloading(int progress, long sizeNow) {
                runOnUiThread(() -> {
                    dialog.setProgress(progress);
                    dialog.setSizeNow(sizeNow);
                });
            }

            @Override
            public void onDownloadFailed() {
                runOnUiThread(() -> {
                    dialog.cancel();
                    new MessageDialog(MainActivity.this).setMessageAndShow("下载失败!!!");
                });
            }
        });
        dialog.show();
        DownloadPgDialog.ProgressStandard standard = new DownloadPgDialog.ProgressStandard();
        standard.setTotal(fileInfo.getSize());
        dialog.setProgressStandard(standard);
        // 通过轮询，向下载进度弹窗添加取消操作，轮询次数为3，间隔为200
        new Thread(() -> {
            DownloadManager downloadManager = DownloadManager.newInstance();
            try {
                for (int i = 0; i < 3; i++) {
                    DownloadManager.DownloadTask downloadTask = downloadManager.getDownloadTask(fileInfo.getFileName());
                    if (downloadTask != null) {
                        dialog.setDownloadTask(downloadTask);
                        break;
                    } else {
                        Thread.sleep(200);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (!upPath()) {
            super.onBackPressed();
        }
    }

    // 获取文件的真实路径
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_FILE_CODE) {
            if (data == null) {
                // 用户未选择任何文件，直接返回
                return;
            }
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    uploadFile(uri);
                }
            } else {
                Uri uri = data.getData();
                uploadFile(uri);
            }
        }
    }

    /**
     * 上传文件
     *
     * @param uri 文件uri
     */
    private void uploadFile(Uri uri) {
        OneDialog getFileDialog = new OneDialog(this);
        getFileDialog.setMessageAndShow("正在复制文件");
        // 当文件过大时复制会消耗过多时间
        new Thread(() -> {
            String path = UriUtil.getFileAbsolutePath(this, uri);
            runOnUiThread(() -> {
                getFileDialog.cancel();
                if (path != null) {
                    String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
                    File file = new File(path);
                    ConfirmDialog uploadingDialog = new ConfirmDialog(this);
                    getFileDialog.setCanceledOnTouchOutside(false);
                    Call call = serverConnManager.upload(file, nowFilePath, new ServerConnManager.InnerCallBack() {
                        @Override
                        public void stateOnTrue(String data) {
                            runOnUiThread(() -> {
                                new MessageDialog(MainActivity.this).setMessageAndShow("上传成功:\n" + fileName);
                                new File(path).delete();
                                uploadingDialog.cancel();
                                refreshMap();
                            });
                        }

                        @Override
                        public void stateOnFalse(String message) {
                            runOnUiThread(() -> new MessageDialog(MainActivity.this).setMessageAndShow("上传失败:\n" + message));
                            uploadingDialog.cancel();
                            new File(path).delete();
                        }
                    });
                    uploadingDialog.setBuildItem(
                            ConfirmDialog.BuildItem.builder()
                                    .leftButtonMsg("隐藏")
                                    .leftClickedLis(v -> {
                                        uploadingDialog.cancel();
                                    })
                                    .rightButtonMsg("取消")
                                    .rightClickedLis(v -> {
                                        call.cancel();
                                        uploadingDialog.cancel();
                                    })
                                    .message("正在上传文件:\n" + fileName)
                                    .build()
                    );
                    uploadingDialog.setCancelable(false);
                    uploadingDialog.show();
                } else {
                    new MessageDialog(this).setMessageAndShow("文件未找到:\n" + uri.getPath());
                }
            });
        }).start();
    }

    private void deleteFile(FileInfo fileInfo) {
        serverConnManager.deleteFile(nowFilePath, fileInfo.getFileName(), new ServerConnManager.InnerCallBack() {
            @Override
            public void stateOnTrue(String data) {
                runOnUiThread(MainActivity.this::refreshMap);
            }

            @Override
            public void stateOnFalse(String message) {
                runOnUiThread(() -> new MessageDialog(MainActivity.this).setMessageAndShow(message));
            }
        });
    }

    class DeleteConfirmDialog extends ConfirmDialog {

        public DeleteConfirmDialog(@NonNull Context context, FileInfo fileInfo) {
            super(context);
            BuildItem buildItem = BuildItem.builder()
                    .leftButtonMsg("删除")
                    .rightButtonMsg("取消")
                    .message("是否删除该文件:\n" + fileInfo.getFileName())
                    .leftClickedLis(v -> {
                        deleteFile(fileInfo);
                        cancel();
                    })
                    .rightClickedLis(v -> cancel())
                    .build();
            setBuildItem(buildItem);
        }
    }
}