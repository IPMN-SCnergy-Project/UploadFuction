package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.service.FileService;
import com.springboot.springbootlogindemo.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class OpenfileController {
    @Resource
    private FileService fileService;

    @PostMapping("/openfile")
    public Result<String> filepath(@RequestParam("filename") String filename, @RequestParam("uid") int uid) {
        String filepath = fileService.getFilePathByUidAndFilename(uid, filename);
        return Result.success(filepath);
    }

}
