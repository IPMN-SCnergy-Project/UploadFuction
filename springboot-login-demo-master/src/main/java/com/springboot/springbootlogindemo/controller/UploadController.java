package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.service.FileService;
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

import com.springboot.springbootlogindemo.domain.FileInfo;

@RestController
@RequestMapping("/user")
public class UploadController {
    // 设置固定的日期格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    // 将 yml 中的自定义配置注入到这里

    @Value(value = "${userfile.basedir}")
    private String filePath;

    @Resource
    private FileService fileService;

    private Logger log = LoggerFactory.getLogger("UploadController");

    // 文件上传 （可以多文件上传）
    @PostMapping("/upload")
    public Result fileUploads(HttpServletRequest request,@RequestParam("file") MultipartFile file,
                              @RequestParam("uid") long uid, @RequestParam("uname") String uname) throws IOException {
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

        FileInfo fileInfo = fileService.saveFileInfo(fileName,realFilePath,fileSize,uid,uname,createDate,createTime,modifyTime);
        try {
            // 上传的文件被保存了
            file.transferTo(dest);
            // 自定义返回的统一的 JSON 格式的数据，可以直接返回这个字符串也是可以的。
            return Result.succ("Upload Success");
        } catch (IOException e) {
            log.error(e.toString());
        }
        // 待完成 —— 文件类型校验工作
        return Result.fail("Upload Error");
    }
}
