package com.verlif.idea.singledown.ui.dialog.file;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.manager.ServerInfoManager;
import com.verlif.idea.singledown.manager.ThumbnailManager;
import com.verlif.idea.singledown.model.FileInfo;
import com.verlif.idea.singledown.model.map.FileInDraw;
import com.verlif.idea.singledown.ui.dialog.base.BottomDialog;

import java.io.File;

public abstract class FileInfoDialog extends BottomDialog {

    private FileInDraw fileInDraw;
    private ThumbnailManager thumbnailManager;

    public FileInfoDialog(@NonNull Context context, FileInDraw fileInDraw) {
        super(context);
        this.fileInDraw = fileInDraw;
        thumbnailManager = ThumbnailManager.newInstance(
                ServerInfoManager.newInstance(getContext()).getRootUrl()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView imageView = view.findViewById(R.id.dialog_file_img);
        TextView nameView = view.findViewById(R.id.dialog_file_name);
        FileInfo fileInfo = fileInDraw.getFileInfo();

        imageView.setImageResource(fileInDraw.getForegroundResId());
        if (ThumbnailManager.isPicture(fileInfo.getFileName())) {
            File file = thumbnailManager.getThumbnail(fileInfo);
            if (file != null) {
                imageView.setImageURI(Uri.fromFile(file));
            }
        }
        nameView.setText(fileInfo.getFileName());

        View downloadButton = view.findViewById(R.id.dialog_file_download);
        if (fileInfo.isFile()) {
            downloadButton.setOnClickListener(v -> {
                download(fileInfo);
            });
        } else {
            downloadButton.setVisibility(View.INVISIBLE);
        }
        view.findViewById(R.id.dialog_file_delete).setOnClickListener(v -> {
            delete(fileInfo);
        });
        view.findViewById(R.id.dialog_file_rename).setOnClickListener(v -> {
            new RenameFileDialog(getContext(), fileInfo.getFileName()) {
                @Override
                public void nowName(String nowName) {
                    rename(fileInfo, nowName);
                    cancel();
                }
            }.show();
        });
        view.findViewById(R.id.dialog_file_button).setOnClickListener(v -> {
            cancel();
        });
    }

    @Override
    protected int layout() {
        return R.layout.dialog_file;
    }

    public abstract void download(FileInfo fileInfo);
    public abstract void delete(FileInfo fileInfo);
    public abstract void rename(FileInfo fileInfo, String name);
}
