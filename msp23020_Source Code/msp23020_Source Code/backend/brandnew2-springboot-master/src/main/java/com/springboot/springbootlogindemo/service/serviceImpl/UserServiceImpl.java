package com.springboot.springbootlogindemo.service.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.springbootlogindemo.domain.Content;
import com.springboot.springbootlogindemo.domain.FileInfo;
import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.repository.ContentDao;
import com.springboot.springbootlogindemo.repository.UserDao;
import com.springboot.springbootlogindemo.service.UserService;
import com.springboot.springbootlogindemo.repository.FileInfoDao;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private FileInfoDao fileInfoDao;
    @Resource
    private ContentDao contentDao;

    @Override
    public User loginService(String uname, String password) {
        // 如果账号密码都对则返回登录的用户对象，若有一个错误则返回null
        User user = userDao.findByUnameAndPassword(uname, password);
        // 重要信息置空
        if(user != null){
            user.setPassword("");
        }
        return user;
    }

    @Override
    public User registService(User user) {
        //当新用户的用户名已存在时
        if(userDao.findByUname(user.getUname())!=null){
            // 无法注册
            return null;
        }else{
            //返回创建好的用户对象(带uid)
            User newUser = userDao.save(user);
            if(newUser != null){
                newUser.setPassword("");
            }
            return newUser;
        }
    }

    @Override
    public JsonNode getUserFileList(long userId) {
        User user = userDao.findByUid(userId);
        int status = user.getStatus();
        List<FileInfo> fileInfos = new ArrayList<>();
        if(status == 1){
            fileInfos = fileInfoDao.findAllByCreatorId(userId);
        } else if (status == 2) {
            List<Content> contents = contentDao.findByApproverDept(user.getDepartment());
            Set<String> fileNames = new HashSet<String>();
            for(Content content : contents){
                if(content.getNewBlockId() != -1) {
                    fileNames.add(content.getFileName());
                }
            }
            for(String fileName : fileNames){
                if(fileInfoDao.findByFileName(fileName)==null){
                    return new ObjectMapper().createArrayNode();
                }
                fileInfos.add(fileInfoDao.findByFileName(fileName));
            }
        }


        if (fileInfos == null) {
            // If fileInfoDao is null, return an empty JsonNode
            return new ObjectMapper().createArrayNode();
        }

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jsonArray = mapper.createArrayNode();

        for (FileInfo fileInfo : fileInfos) {
            ObjectNode jsonObject = mapper.createObjectNode();
            jsonObject.put("file_id", fileInfo.getFileId());
            jsonObject.put("file_name", fileInfo.getFileName());
            jsonObject.put("file_size", fileInfo.getFileSize());
            jsonObject.put("create_time", fileInfo.getCreateTime());
            jsonObject.put("is_finished", fileInfo.getIsFinished());
            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

}
