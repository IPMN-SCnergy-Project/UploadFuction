package com.springboot.springbootlogindemo.service;

import com.springboot.springbootlogindemo.domain.FileInfo;

import java.util.List;

public interface FileService {
    FileInfo saveFileInfo(String fileName,
                          String filePath,
                          long fileSize,
                          long creatorId,
                          String creatorName,
                          String createDate,
                          String createTime,
                          String modifyTime);
    String getFilePathByUidAndFilename(int uid, String filename);
}
