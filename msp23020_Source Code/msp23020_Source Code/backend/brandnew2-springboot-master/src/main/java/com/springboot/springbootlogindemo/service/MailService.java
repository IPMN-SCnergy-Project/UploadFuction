package com.springboot.springbootlogindemo.service;

public interface MailService {
    void sendMailWhenAddComment(String to, String uname, String fileName, String addTime);
    void sendMailWhenChangeApproveStatus(String to, String department, String approveStatus, String content);
    void sendMailWhenFinishApprove(String to, String filename);
    void sendMailWhenNewFileUpload(String to, String filename, String department);
}
