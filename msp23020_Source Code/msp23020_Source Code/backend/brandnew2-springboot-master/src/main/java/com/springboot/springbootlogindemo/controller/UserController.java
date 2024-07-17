package com.springboot.springbootlogindemo.controller;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.domain.UserWithFiles;
import com.springboot.springbootlogindemo.service.UserService;
import com.springboot.springbootlogindemo.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class    UserController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    /*
    public Result<User> loginController(@RequestParam String uname, @RequestParam String password){
        User user = userService.loginService(uname, password);
        if(user!=null){
            return Result.success(user,"Login Success");
        }else{
            return Result.error("123","The account or password is incorrect!");
        }
    }*/

    public Result<UserWithFiles> loginController(@RequestParam String uname, @RequestParam String password) {
        User user = userService.loginService(uname, password);
        if (user != null) {
            // 获取用户的文件列表
            JsonNode fileList = userService.getUserFileList(user.getUid());

            // 创建一个包含用户信息和文件列表的对象
            UserWithFiles userWithFiles = new UserWithFiles(user, fileList);

            // 返回成功登录的结果，包括用户信息和文件列表
            return Result.success(userWithFiles, "Login Success");
        } else {
            return Result.error("123", "The account or password is incorrect!");
        }
    }

    @PostMapping("/register")
    public Result<User> registController(@RequestBody User newUser){
        User user = userService.registService(newUser);
        if(user!=null){
            return Result.success(user,"Registry Success!");
        }else{
            return Result.error("456","The user name already exists!");
        }
    }
}
