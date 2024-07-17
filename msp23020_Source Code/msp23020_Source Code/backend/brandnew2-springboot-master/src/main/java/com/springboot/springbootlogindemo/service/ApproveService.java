package com.springboot.springbootlogindemo.service;

public interface ApproveService {
    void getApproveSignal(String fileName, int page, int newBlockId, int approveStatus, String department, String content);
    void changeApproveStatus(String fileName, int page, int newBlockId, int approveStatus);
    boolean ifFileFinish(String fileName);
    void sendEmailToFileCreator(String fileName, int page, int newBlockId, int approveStatus, String department, String content);
    void sendEmailWhenFinish(String fileName);
}
