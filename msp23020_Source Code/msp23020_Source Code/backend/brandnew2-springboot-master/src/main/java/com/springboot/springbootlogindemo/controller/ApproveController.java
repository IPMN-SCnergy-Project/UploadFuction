package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.service.ApproveService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class ApproveController {
    @Resource
    private ApproveService approveService;

    @PostMapping("/newApprove")
    public void dealWithNewApprove(@RequestParam("fileName") String fileName,
                                   @RequestParam("page") int page,
                                   @RequestParam("blockID") int newBlockId,
                                   @RequestParam("approveStatus") int approveStatus,
                                   @RequestParam("content") String content,
                                   @RequestParam("department") String department){
        approveService.getApproveSignal(fileName, page, newBlockId, approveStatus, department, content);
    }
}
