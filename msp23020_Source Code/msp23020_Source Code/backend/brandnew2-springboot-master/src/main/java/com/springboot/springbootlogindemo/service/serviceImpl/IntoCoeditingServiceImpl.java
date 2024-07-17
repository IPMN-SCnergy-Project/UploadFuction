package com.springboot.springbootlogindemo.service.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springboot.springbootlogindemo.domain.CoeditingData.CoeditingData;
import com.springboot.springbootlogindemo.domain.Comment;
import com.springboot.springbootlogindemo.domain.Content;
import com.springboot.springbootlogindemo.domain.FileInfo;
import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.repository.CommentDao;
import com.springboot.springbootlogindemo.repository.ContentDao;
import com.springboot.springbootlogindemo.repository.FileInfoDao;
import com.springboot.springbootlogindemo.repository.UserDao;
import com.springboot.springbootlogindemo.service.IntoCoeditingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class IntoCoeditingServiceImpl implements IntoCoeditingService {
    @Resource
    private ContentDao contentDao;

    @Resource
    private CommentDao commentDao;
    @Autowired
    private FileInfoDao fileInfoDao;
    @Autowired
    private UserDao userDao;

    @Override
    public CoeditingData getCoeditingData(long uid, String fileName){
        JsonNode location = getLocation(fileName);
        JsonNode blockNum = getBlockNum(fileName);
        JsonNode blockStatus = getBlockStatus(fileName);
        JsonNode comment = getComment(fileName);

        FileInfo fileInfo = fileInfoDao.findByFileName(fileName);
        User user = userDao.findByUid(uid);

        CoeditingData coeditingData = new CoeditingData(uid, fileName, fileInfo.getCreateTime(), user.getUname(), location, blockNum, blockStatus, comment);
        coeditingData.setUid(uid);
        coeditingData.setFileName(fileName);

        return coeditingData;
    }

    @Override
    public JsonNode getLocation(String fileName) {
        List<Content> contents = contentDao.findAllByNewBlockIdNotEqualToMinusOne(fileName);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jsonArray = mapper.createArrayNode();

        for (Content content : contents) {
            ObjectNode jsonObject = mapper.createObjectNode();
            jsonObject.put("page", content.getPage());
            jsonObject.put("blockId", content.getBlockId());
            jsonObject.put("content", content.getContent());
            jsonObject.put("modifiedContent", content.getModifiedContent());
            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

    @Override
    public JsonNode getBlockNum(String fileName){
        List<Object[]> queryResult = contentDao.getPageBlockCounts(fileName);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jsonArray = mapper.createArrayNode();

        for (Object[] row : queryResult) {
            ObjectNode jsonObject = mapper.createObjectNode();
            jsonObject.put("page", ((Number) row[0]).intValue());
            if(row[1] == null) {
                jsonObject.put("blockNum", 0);
            }
            else {
                jsonObject.put("blockNum", ((Number) row[1]).intValue());
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JsonNode getBlockStatus(String fileName){
        List<Content> contents = contentDao.findAllByNewBlockIdNotEqualToMinusOne(fileName);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jsonArray = mapper.createArrayNode();

        for (Content content : contents) {
            ObjectNode jsonObject = mapper.createObjectNode();
            jsonObject.put("page", content.getPage());
            jsonObject.put("newBlockId", content.getNewBlockId());
            jsonObject.put("department", content.getApproverDept());
            jsonObject.put("approvedStatus", content.getApproverStatus());
            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

    @Override
    public JsonNode getComment(String fileName){
        List<Comment> comments = commentDao.findCommentByFileName(fileName);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jsonArray = mapper.createArrayNode();

        for (Comment comment : comments) {
            ObjectNode jsonObject = mapper.createObjectNode();
            jsonObject.put("fileName", comment.getFileName());
            jsonObject.put("page", comment.getPage());
            jsonObject.put("blockId", comment.getNewBlockId());
            jsonObject.put("author", comment.getCommentUname());
            jsonObject.put("content", comment.getContent());
            jsonObject.put("text", comment.getText());
            jsonObject.put("timestamp", comment.getAddTime());
            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }
}
