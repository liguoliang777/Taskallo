package com.android.taskallo.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/31 0031.
 */

public class FileListInfo implements Serializable {
    public String fileId;
    public String fileName;
    public String fileUrl;
    public long fileSize;
    public long createTime;

    public FileListInfo(String fileId, String fileName, String fileUrl, long fileSize, long
            createTime) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.createTime = createTime;
    }
}
