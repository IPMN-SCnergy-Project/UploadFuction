package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.service.ContentSaveService;
import com.springboot.springbootlogindemo.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class ContentSaveController {
    @Resource
    private ContentSaveService contentSaveService;

    @PostMapping("/saveContent")
    public void saveCon() {
        //contentSaveService.saveContent();
    }
}
