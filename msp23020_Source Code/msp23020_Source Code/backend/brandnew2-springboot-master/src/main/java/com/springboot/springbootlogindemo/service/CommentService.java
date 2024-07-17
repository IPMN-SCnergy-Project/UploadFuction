package com.springboot.springbootlogindemo.service;

public interface CommentService {
    void saveComment (int newBlockId,
                      int page,
                      String fileName,
                      long commentUid,
                      String commentUname,
                      String content,
                      String text,
                      String addTime);
}
