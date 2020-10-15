package com.verlif.idea.singledown.ui.dialog.progress;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.manager.DownloadManager;
import com.verlif.idea.singledown.ui.dialog.confirm.ConfirmDialog;
import com.verlif.idea.singledown.util.PrintUtil;

import java.text.DecimalFormat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DownloadPgDialog extends Dialog {

    private ProgressBar progressBar;
    private TextView sizeNowView;

    private ProgressStandard standard;

    public DownloadPgDialog(@NonNull Context context) {
        super(context, R.style.BottomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_download_progress);
        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }

        progressBar = findViewById(R.id.dialog_downloadPg_progressbar);
        sizeNowView = findViewById(R.id.dialog_downloadPg_sizeNow);
        findViewById(R.id.dialog_downloadPg_hide).setOnClickListener(v -> cancel());

        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public void setDownloadTask(DownloadManager.DownloadTask downloadTask) {
        if (downloadTask != null) {
            findViewById(R.id.dialog_downloadPg_cancel).setOnClickListener(v -> {
                ConfirmDialog dialog = new ConfirmDialog(getContext());
                dialog.setBuildItem(
                        ConfirmDialog.BuildItem.builder()
                                .message("是否删除当前下载记录与本地源文件?")
                                .leftButtonMsg("删除")
                                .leftClickedLis(v1 -> {
                                    dialog.cancel();
                                    downloadTask.cancel();
                                    DownloadPgDialog.this.cancel();
                                })
                                .rightButtonMsg("取消")
                                .rightClickedLis(v1 -> cancel())
                                .build()
                );
                dialog.show();
            });
        } else {
            PrintUtil.println("downloadTask is null");
        }
    }

    @SuppressLint("SetTextI18n")
    public void setProgressStandard(ProgressStandard standard) {
        this.standard = standard;
        ((TextView) findViewById(R.id.dialog_downloadPg_sizeTotal)).setText((standard.total / standard.scale) + standard.standard);
    }

    public void setProgress(int progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
            if (progress == progressBar.getMax()) {
                cancel();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void setSizeNow(long sizeNow) {
        if (standard != null) {
            double size = sizeNow * 1.0d / standard.scale;
            DecimalFormat df = new DecimalFormat("#.00");
            String str = df.format(size);
            if (str.startsWith("."))
                str = "0" + str;
            sizeNowView.setText(str + standard.standard);
        } else {
            sizeNowView.setText(String.valueOf(sizeNow));
        }
    }

    public void setMax(int max) {
        if (progressBar != null) {
            progressBar.setMax(max);
        }
    }

    public int getMax() {
        return progressBar.getMax();
    }

    public int getProgress() {
        if (progressBar != null) {
            return progressBar.getProgress();
        } else return 0;
    }

    @Override
    public void show() {
        if (isShowing()) {
            cancel();
        }
        super.show();
        setProgress(0);
    }

    @Data
    public static class ProgressStandard {

        private static final String[] STANDARD = new String[]{"b", "k", "m", "g"};
        /**
         * 进度单位
         */
        private String standard;
        /**
         * 进度进制
         */
        private long scale;
        /**
         * 总大小（未转换）
         */
        private long total;

        public void setTotal(long total) {
            this.total = total;
            scale = 1;
            for (int i = 0; i < 4; i++) {
                if (total / scale < 1024) {
                    standard = STANDARD[i];
                    return;
                }
                scale *= 1024;
            }
        }
    }
}
