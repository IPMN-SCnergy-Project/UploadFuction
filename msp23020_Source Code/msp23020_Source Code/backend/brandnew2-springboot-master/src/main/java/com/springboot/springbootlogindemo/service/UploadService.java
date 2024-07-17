package com.springboot.springbootlogindemo.service;

public interface UploadService {
    void pythonInvoker(String wordPath, String outputPath);
    void sendMailToDept(String fileName);
}
