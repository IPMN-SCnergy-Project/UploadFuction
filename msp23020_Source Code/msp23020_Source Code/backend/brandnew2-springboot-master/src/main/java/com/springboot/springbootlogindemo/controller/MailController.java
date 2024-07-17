package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.service.serviceImpl.MailServer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class MailController {
    @Resource
    private MailServer mailServer;

    //just for test
    @PostMapping("/sendEmail")
    public void sendEmail() {
        System.out.println("SendingEmail...");
        //mailServer.sendMail("sleex@connect.hku.hk","!Notification","A new contract is pending approval.");
        mailServer.sendMail("sleex@connect.hku.hk","!Notification","LegalTeam@gmail.com has added a comment in contract1 at Thu Jul 112024 13:38:44 GMT+0800.");
    }
}
