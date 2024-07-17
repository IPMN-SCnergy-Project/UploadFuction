package com.springboot.springbootlogindemo.controller;

import com.springboot.springbootlogindemo.Lang.Result;
import com.springboot.springbootlogindemo.domain.Content;
import com.springboot.springbootlogindemo.domain.FileInfo;
import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.repository.CommentDao;
import com.springboot.springbootlogindemo.repository.ContentDao;
import com.springboot.springbootlogindemo.repository.FileInfoDao;
import com.springboot.springbootlogindemo.repository.UserDao;
import com.springboot.springbootlogindemo.service.CommentService;
import com.springboot.springbootlogindemo.service.MailService;
import com.springboot.springbootlogindemo.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class NewCommentController {
    @Resource
    private CommentService commentService;

    @Resource
    private MailService mailService;

    @Resource
    private UserDao userDao;

    @Resource
    private ContentDao contentDao;

    @Resource
    private FileInfoDao fileInfoDao;

    @PostMapping("/newComment")
    public void dealWithNewComment(@RequestParam("fileName") String fileName,
                                   @RequestParam("page") int page,
                                   @RequestParam("blockID") int newBlockId,
                                   @RequestParam("uid") long uid,
                                   @RequestParam("author") String author,
                                   @RequestParam("content") String content,
                                   @RequestParam("text") String text,
                                   @RequestParam("timestamp") String addTime){
        commentService.saveComment(newBlockId, page, fileName, uid, author, content, text, addTime);

        User user = userDao.findByUid(uid);
        if(user.getStatus() == 1){ // Client Manager
            //Send Email to Approver
            //Content contentTable = contentDao.findByPageAndNewBlockId(page, newBlockId);
            Content contentTable = contentDao.findByPageAndNewBlockIdAndFileName(page, newBlockId, fileName);
            String approverDept = contentTable.getApproverDept();
            if (approverDept.equals("NONE")) approverDept = "LEGAL";
            User sendTo = userDao.findByDepartment(approverDept);
            mailService.sendMailWhenAddComment(sendTo.getEmail(), user.getUname(), fileName, addTime);
        } else if (user.getStatus() == 2) {//Department
            FileInfo fileInfo = fileInfoDao.findByFileName(fileName);
            User sendTo = userDao.findByUid(fileInfo.getCreatorId());//2是ClientManagerUid
            mailService.sendMailWhenAddComment(sendTo.getEmail(), user.getUname(), fileName, addTime);
        }
    }
}
