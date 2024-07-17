package com.springboot.springbootlogindemo.service.serviceImpl;

import com.springboot.springbootlogindemo.service.MailService;
import com.springboot.springbootlogindemo.service.serviceImpl.MailServer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MailServiceImpl implements MailService {
    @Resource
    private MailServer mailServer;

    @Override
    public void sendMailWhenAddComment(String to, String uname, String fileName, String addTime){
        String content = String.format("%s commented on %s at %s", uname, fileName, addTime);
        mailServer.sendMail(to, "!Notification: New Comment Added", content);
    }

    @Override
    public void sendMailWhenChangeApproveStatus(String to, String department, String approveStatus, String content){
        String mailContent;
        if(department.equals("NONE")){
            department = "A";
        }
        if(!content.isEmpty()){
            mailContent = String.format("%s department %s the item: \n %s", department, approveStatus, content);
        }else{
            mailContent = String.format("%s department %s an item", department, approveStatus);
        }
        mailServer.sendMail(to, "!Notification: Approve Status Changed", mailContent);
    }

    @Override
    public void sendMailWhenFinishApprove(String to, String filename){
        String mailContent = String.format("%s finished.", filename);
        mailServer.sendMail(to, "!Notification: A Contract Finished", mailContent);
    }

    @Override
    public void sendMailWhenNewFileUpload(String to, String filename, String department){
        String mailContent = String.format("Hello %s, a new contract named %s awaits review.", department, filename);
        mailServer.sendMail(to, "!Notification: A New Contract Uploaded", mailContent);
    }
}
