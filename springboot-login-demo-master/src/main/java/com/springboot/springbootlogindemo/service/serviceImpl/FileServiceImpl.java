package com.springboot.springbootlogindemo.service.serviceImpl;

import com.springboot.springbootlogindemo.domain.FileInfo;
import com.springboot.springbootlogindemo.repository.FileInfoDao;
import com.springboot.springbootlogindemo.service.FileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FileServiceImpl  implements FileService{
    @Resource
    private FileInfoDao fileInfoDao;


    @Override
    public FileInfo saveFileInfo(String fileName,
                                 String filePath,
                                 long fileSize,
                                 long creatorId,
                                 String creatorName,
                                 String createDate,
                                 String createTime,
                                 String modifyTime) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(fileName);
        fileInfo.setFilePath(filePath);
        fileInfo.setFileSize(fileSize);
        fileInfo.setCreatorId(creatorId);
        fileInfo.setCreatorName(creatorName);
        fileInfo.setCreateDate(createDate);
        fileInfo.setCreateTime(createTime);
        fileInfo.setModifyTime(modifyTime);
        fileInfoDao.save(fileInfo);
        return fileInfo;
    }

    public String getFilePathByUidAndFilename(int uid, String filename){
        FileInfo fileInfo = fileInfoDao.findByFileNameAndCreatorId(filename, uid);
        return fileInfo.getFilePath();
    }
}
