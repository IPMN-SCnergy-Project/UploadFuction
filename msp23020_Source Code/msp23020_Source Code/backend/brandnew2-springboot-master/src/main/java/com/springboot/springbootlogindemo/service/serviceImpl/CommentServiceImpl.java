package com.springboot.springbootlogindemo.service.serviceImpl;

import com.springboot.springbootlogindemo.repository.CommentDao;
import com.springboot.springbootlogindemo.service.CommentService;
import com.springboot.springbootlogindemo.domain.Comment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CommentServiceImpl implements CommentService{
    @Resource
    private CommentDao commentDao;

    @Override
    public void saveComment (int newBlockId,
                             int page,
                             String fileName,
                             long commentUid,
                             String commentUname,
                             String content,
                             String text,
                             String addTime){
        Comment comment = new Comment();
        comment.setNewBlockId(newBlockId);
        comment.setPage(page);
        comment.setFileName(fileName);
        comment.setCommentUid(commentUid);
        comment.setCommentUname(commentUname);
        comment.setContent(content);
        comment.setText(text);
        comment.setAddTime(addTime);
        commentDao.save(comment);
    }
}
