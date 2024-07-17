package com.springboot.springbootlogindemo.service.serviceImpl;

import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.repository.ContentDao;
import com.springboot.springbootlogindemo.repository.UserDao;
import com.springboot.springbootlogindemo.service.MailService;
import com.springboot.springbootlogindemo.service.UploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.springboot.springbootlogindemo.domain.Content;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UploadServiceImpl implements UploadService {
    @Resource
    ContentDao contentDao;
    @Resource
    UserDao userDao;

    @Resource
    MailService mailService;

    @Value(value = "${codefilePath}")
    String codefilePath;

    @Value(value = "${rulebookPath}")
    String jsonPath;


    @Override
    public void pythonInvoker(String wordPath, String outputPath) {
        try {
            // 创建 ProcessBuilder 对象
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python", codefilePath, jsonPath, wordPath, outputPath);

            // 启动 Python 进程
            Process process = processBuilder.start();

            // 获取进程的输出流和错误流并读取
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // 读取标准输出
            String line;
            while ((line = stdoutReader.readLine()) != null) {
                System.out.println(line);
            }

            // 读取标准错误
            while ((line = stderrReader.readLine()) != null) {
                System.err.println(line);
            }

            // 等待进程结束并获取退出状态
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);

            // 如果退出状态不为0,抛出异常
            if (exitCode != 0) {
                throw new RuntimeException("Python script exited with non-zero code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while running Python script", e);
        }
    }

    @Override
    public void sendMailToDept(String fileName){
        List<Content> contents = contentDao.findAllByNewBlockIdNotEqualToMinusOne(fileName);
        Set<String> departments = new HashSet<>();
        for (Content content : contents) {
            String dept = content.getApproverDept();
            if(dept.equals("NONE")){
                dept = "LEGAL";
            }
            departments.add(dept);
        }
        for (String department : departments) {
            User user = userDao.findByDepartment(department);
            mailService.sendMailWhenNewFileUpload(user.getEmail(), fileName, department);
        }
    }
}
