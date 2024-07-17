package com.springboot.springbootlogindemo.service.serviceImpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.springbootlogindemo.domain.Content;
import com.springboot.springbootlogindemo.domain.User;
import com.springboot.springbootlogindemo.repository.UserDao;
import com.springboot.springbootlogindemo.service.ContentSaveService;
import com.springboot.springbootlogindemo.repository.ContentDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ContentSaveServiceImpl implements ContentSaveService {
    @Resource
    private ContentDao contentDao;
    @Resource
    private UserDao userDao;

    //@Value(value = "${userfile.contentPath}")
    //String contentPath;

    @Override
    public void saveContent(long fileId, int uid, String fileName, String contentPath){
        try {
            File file = new File(contentPath);
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> contentData = objectMapper.readValue(file, List.class);
            for (Map<String, Object> contentItem : contentData){
                Content content = new Content();
                content.setBlockId((int) contentItem.get("id"));
                content.setFileId(fileId);
                content.setFileName(fileName);
                //content.setFileName(getFileName((int) contentItem.get("Document")));
                content.setCreatorId(uid);//ClientManager
                content.setNewBlockId((int) contentItem.get("Block"));
                content.setPage((int) contentItem.get("Page"));
                content.setContent((String) contentItem.get("Content"));
                content.setModifiedContent((String) contentItem.get("Modified Content 1"));
                content.setApproverDept((String) contentItem.get("Approver"));
                String department = (String) contentItem.get("Approver");
                if (!department.equals( "NONE")){
                    User user = new User();
                    user = userDao.findByDepartment(department);
                    content.setApproverId((int)user.getUid());
                }
                content.setApproverStatus((!Objects.equals((String) contentItem.get("Modification Status 1"), "null")) ? 1 : null);

                content.setLocation((String) contentItem.get("Location"));
                /*
                Map<String, Object> location = (Map<String, Object>) contentItem.get("Location");
                content.setX1((double) location.get("x1"));
                content.setY1((double) location.get("y1"));
                content.setX2((double) location.get("x2"));
                content.setY2((double) location.get("y2"));
                content.setM1((content.getX1() + content.getX2()) / 2);
                content.setM2((content.getY1() + content.getY2()) / 2);
                 */

                contentDao.save(content);
            }
        }catch (JsonParseException | JsonMappingException e) {
            System.err.println("Error parsing JSON file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
