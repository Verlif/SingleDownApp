package com.verlif.idea.singledown.model.map;

import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.manager.ThumbnailManager;
import com.verlif.idea.singledown.model.FileInfo;
import com.verlif.idea.singledown.util.PrintUtil;

import lombok.Data;

@Data
public class FileInDraw implements ShowIt {

    private FileInfo fileInfo;

    public FileInDraw(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    @Override
    public int getForegroundResId() {
        if (fileInfo.isFile()) {
            if (ThumbnailManager.isPicture(fileInfo.getFileName()))
                return R.drawable.img_file_picture;
            int index = fileInfo.getFileName().lastIndexOf(".");
            if (index != -1) {
                String s = fileInfo.getFileName().substring(index + 1);
                switch (s.toLowerCase()) {
                    case "mp3":
                    case "cda":
                    case "wav":
                    case "aac":
                    case "flac":
                        return R.drawable.img_file_music;
                    case "mp4":
                    case "avi":
                    case "mov":
                    case "flv":
                    case "wmv":
                        return R.drawable.img_file_video;
                    case "zip":
                    case "rar":
                    case "7z":
                        return R.drawable.img_file_zip;
                    case "txt":
                        return R.drawable.img_file_text;
                    default:
                        return R.drawable.img_file;
                }
            } else return R.drawable.img_file;
        } else {
            return R.drawable.img_floder;
        }
    }

    @Override
    public String getType() {
        int index = fileInfo.getFileName().lastIndexOf(".");
        if (index > 0) {
            return fileInfo.getFileName().substring(index + 1).toLowerCase();
        } else return null;
    }

}
