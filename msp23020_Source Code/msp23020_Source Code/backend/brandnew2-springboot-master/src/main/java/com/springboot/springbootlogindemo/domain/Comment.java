package com.springboot.springbootlogindemo.domain;

import javax.persistence.*;

@Table(name = "t_comment")
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId;

    private int newBlockId;

    private int page;

    private int fileId;

    private String fileName;

    private long commentUid;

    private String commentUname;

    private String content;

    private String text;

    private String addTime;

    public  Comment(){
        this.fileId = -1;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getNewBlockId() {
        return newBlockId;
    }

    public void setNewBlockId(int newBlockId) {
        this.newBlockId = newBlockId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getCommentUid() {
        return commentUid;
    }

    public void setCommentUid(long commentUid) {
        this.commentUid = commentUid;
    }

    public String getCommentUname() {
        return commentUname;
    }

    public void setCommentUname(String commentUname) {
        this.commentUname = commentUname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
}
