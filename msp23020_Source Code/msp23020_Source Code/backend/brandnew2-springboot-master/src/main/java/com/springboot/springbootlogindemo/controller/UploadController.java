package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.repository.FileInfoDao;
import com.springboot.springbootlogindemo.service.ContentSaveService;
import com.springboot.springbootlogindemo.service.FileService;
import com.springboot.springbootlogindemo.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import com.springboot.springbootlogindemo.Lang.Result;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.springboot.springbootlogindemo.domain.FileInfo;

@RestController
@RequestMapping("/user")
public class UploadController {
    // 设置固定的日期格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    // 将 yml 中的自定义配置注入到这里

    @Value(value = "${userfile.basedir}")
    private String filePath;

    @Value(value = "${jsonSavePath}")
    private String outputPath;

    @Resource
    private FileService fileService;

    @Resource
    UploadService uploadService;

    @Resource
    private ContentSaveService contentSaveService;

    private Logger log = LoggerFactory.getLogger("UploadController");

    @Autowired
    private FileInfoDao fileInfoDao;

    // 文件上传 （可以多文件上传）
    @PostMapping("/upload")
    public Result fileUploads(HttpServletRequest request,@RequestParam("file") MultipartFile[] files,
                              @RequestParam("uid") long uid, @RequestParam("uname") String uname) throws IOException {
        List<FileInfo> fileInfoList = new ArrayList<>();

        for (MultipartFile file : files) {
            // 得到格式化后的日期
            String format = sdf.format(new Date());
            String createDate = format;
            LocalDateTime now = LocalDateTime.now();
            String createTime = now.format(DateTimeFormatter.ofPattern("yyyy-M-d H:mm:ss"));
            String modifyTime = createTime;

            // 获取上传的文件名称
            String fileName = file.getOriginalFilename();
            long fileSize = file.getSize();

            // 时间 和 日期拼接
            //String newFileName = format + "_" + fileName;
            // 得到文件保存的位置以及新文件名
            File dest = new File(filePath + fileName);
            String realFilePath = filePath + fileName;

            FileInfo fileInfo = fileService.saveFileInfo(fileName, realFilePath, fileSize, uid, uname, createDate, createTime, modifyTime);
            fileInfoList.add(fileInfo);

            //存储json
            long fileId = fileInfo.getFileId();
            String newOutputPath = outputPath + fileId + ".json";

            try {
                // 上传的文件被保存了
                file.transferTo(dest);
                uploadService.pythonInvoker(realFilePath, newOutputPath);
                //读取output并存入表中
                contentSaveService.saveContent(fileId, (int)uid, fileName, newOutputPath);

                uploadService.sendMailToDept(fileName);
            } catch (IOException e) {
                log.error(e.toString());
                return Result.fail("Upload Error");
            }
        }

        // 自定义返回的统一的 JSON 格式的数据
        return Result.succ("Upload Success");

    }
}
