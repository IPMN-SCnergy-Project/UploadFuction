package com.springboot.springbootlogindemo.controller;


import com.springboot.springbootlogindemo.service.UploadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class RunFileController {
    @Resource
    UploadService uploadService;

    @PostMapping("/runfile")
    public void runFile() {
        String wordPath = "D:/AAA_MyFiles/HKU/Project/backend/upload/Dummy T&C Dataset 1.docx";
        String outputPath = "D:/AAA_MyFiles/HKU/Project/backend/jsonSavePath/output13.json";
        uploadService.pythonInvoker(wordPath, outputPath);
    }
}
