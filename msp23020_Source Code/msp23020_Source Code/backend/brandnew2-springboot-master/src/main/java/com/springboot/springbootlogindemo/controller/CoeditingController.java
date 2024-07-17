package com.springboot.springbootlogindemo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.springbootlogindemo.domain.CoeditingData.CoeditingData;
import com.springboot.springbootlogindemo.repository.ContentDao;
import com.springboot.springbootlogindemo.service.IntoCoeditingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.springbootlogindemo.utils.Result;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class CoeditingController {
    @Resource
    private IntoCoeditingService intoCoeditingService;

    @Resource
    private ContentDao contentDao;

    @PostMapping("/coedit")
    public Result<CoeditingData> intoCoediting(HttpServletRequest request, @RequestParam("uid") long uid, @RequestParam("filename") String fileName){
        System.out.println("Coediting Request received: " + uid + " " + fileName);
        CoeditingData coeditingData = intoCoeditingService.getCoeditingData(uid, fileName);
        return Result.success(coeditingData, "Into Co-editing Platform");
    }
}
