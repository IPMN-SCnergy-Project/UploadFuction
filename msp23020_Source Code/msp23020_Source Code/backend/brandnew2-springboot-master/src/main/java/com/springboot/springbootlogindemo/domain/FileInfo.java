package com.springboot.springbootlogindemo.domain;

import javax.persistence.*;

@Table(name = "fileinfo")
@Entity
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fileId;

    private String fileName;

    private String filePath;

    private long fileSize;

    private long creatorId;

    private String creatorName;

    private String createDate;

    private String createTime;

    private String modifyTime;

    private int isFinished;

    public FileInfo() {
        this.fileSize = -1;
        this.creatorId = 0;
        this.creatorName = "";
        this.createDate = "2000-01-01";
        this.createTime = "2000-01-01 00:00:00";
        this.modifyTime = "2000-01-01 00:00:00";
        this.isFinished = 0;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getIsFinished() { return isFinished;}

    public void setIsFinished(int isFinished) { this.isFinished = isFinished;}
}