package com.springboot.springbootlogindemo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.domain.UserWithFiles;
import com.springboot.springbootlogindemo.repository.UserDao;
import com.springboot.springbootlogindemo.service.UserService;
import com.springboot.springbootlogindemo.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class ExitCoeditingPlatformController {
    @Resource
    private UserService userService;
    @Resource
    private UserDao userDao;

    @PostMapping("/exitCoedit")
    public Result<UserWithFiles> loginController(@RequestParam("uid") long uid) {
        User user = userDao.findByUid(uid);
        if (user != null) {
            JsonNode fileList = userService.getUserFileList(uid);
            UserWithFiles userWithFiles = new UserWithFiles(user, fileList);
            return Result.success(userWithFiles, "Exit Platform Success");
        }
        else {
            return Result.error("123", "Wrong uid!");
        }
    }
}
