package com.verlif.idea.singledown.model;

import lombok.Data;

@Data
public class FileInfo {
    /**
     * 是否是文件
     */
    private boolean isFile;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件相对路径
     */
    private String path;
    /**
     * 文件大小
     */
    private long size;
    /**
     * 上传时间
     */
    private long uploadTime;
}
