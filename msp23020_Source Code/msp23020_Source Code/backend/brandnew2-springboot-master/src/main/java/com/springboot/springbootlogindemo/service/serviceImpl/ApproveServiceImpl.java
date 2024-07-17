package com.springboot.springbootlogindemo.service.serviceImpl;

import com.springboot.springbootlogindemo.domain.FileInfo;
import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.repository.ContentDao;
import com.springboot.springbootlogindemo.repository.FileInfoDao;
import com.springboot.springbootlogindemo.repository.UserDao;
import com.springboot.springbootlogindemo.service.ApproveService;
import com.springboot.springbootlogindemo.service.MailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ApproveServiceImpl implements ApproveService {
    @Resource
    MailService mailService;

    @Resource
    FileInfoDao fileInfoDao;

    @Resource
    UserDao userDao;

    @Resource
    ContentDao contentDao;

    @Override
    public void getApproveSignal(String fileName, int page, int newBlockId, int approveStatus, String department, String content){
        changeApproveStatus(fileName, page, newBlockId, approveStatus);
        sendEmailToFileCreator(fileName, page, newBlockId, approveStatus, department, content);
        if(ifFileFinish(fileName)){
            fileInfoDao.markFileAsFinished(fileName);
            sendEmailWhenFinish(fileName);
        }
    }

    @Override
    public void changeApproveStatus(String fileName, int page, int newBlockId, int approveStatus){
        contentDao.updateApproveStatus(fileName, page, newBlockId, approveStatus);
    }

    @Override
    public boolean ifFileFinish(String fileName){
        int approvedCount = contentDao.countApprovedContentByFileName(fileName);
        return approvedCount == 0;
    }

    @Override
    public void sendEmailToFileCreator(String fileName, int page, int newBlockId, int approveStatus, String department, String content){
        String approveStatusContent = getApproveStatus(approveStatus);
        FileInfo fileInfo = fileInfoDao.findByFileName(fileName);
        User user = userDao.findByUid(fileInfo.getCreatorId());
        mailService.sendMailWhenChangeApproveStatus(user.getEmail(),department,approveStatusContent,content);
    }

    @Override
    public void sendEmailWhenFinish(String fileName) {
        FileInfo fileInfo = fileInfoDao.findByFileName(fileName);
        User user = userDao.findByUid(fileInfo.getCreatorId());
        mailService.sendMailWhenFinishApprove(user.getEmail(), fileName);
    }

    public String getApproveStatus(int approveStatus) {
        switch (approveStatus) {
            case 2:
                return "approved";
            case 3:
                return "rejected";
            default:
                return "pending";
        }
    }
}
