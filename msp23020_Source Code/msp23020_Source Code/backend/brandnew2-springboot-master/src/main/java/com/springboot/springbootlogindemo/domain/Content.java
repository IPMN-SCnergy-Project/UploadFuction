package com.springboot.springbootlogindemo.domain;

import javax.persistence.*;

@Entity
@Table(name = "t_content")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "block_id")
    private int blockId;

    @Column(name = "new_block_id")
    private int newBlockId;

    @Column(name = "page")
    private int page;

    @Column(name = "file_id")
    private long fileId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "picture_path")
    private String picturePath;

    @Column(name = "creator_id")
    private int creatorId;

    @Column(name = "approver_id")
    private int approverId;

    @Column(name = "approver_dept")
    private String approverDept;

    @Column(name = "approver_status")
    private int approverStatus;

    @Column(name = "content")
    private String content;

    @Column(name = "modified_content")
    private String modifiedContent;

    @Column(name = "location")
    private String location;

    public static final String TABLE_NAME_PREFIX = "t_content_";

    public String getTableName(int index) {
        return TABLE_NAME_PREFIX + index;
    }

    /*
    private double x1;

    private double y1;

    private double x2;

    private double y2;

    private double m1;

    private double m2;

     */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getApproverId() {
        return approverId;
    }

    public void setApproverId(int approverId) {
        this.approverId = approverId;
    }

    public String getApproverDept() {
        return approverDept;
    }

    public void setApproverDept(String approverDept) {
        this.approverDept = approverDept;
    }

    public int getApproverStatus() {
        return approverStatus;
    }

    public void setApproverStatus(int approverStatus) {
        this.approverStatus = approverStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getModifiedContent() {
        return modifiedContent;
    }

    public void setModifiedContent(String modifiedContent) {
        this.modifiedContent = modifiedContent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /*
    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public double getM1() {
        return m1;
    }

    public void setM1(double m1) {
        this.m1 = m1;
    }

    public double getM2() {
        return m2;
    }

    public void setM2(double m2) {
        this.m2 = m2;
    }

 */

    public int getNewBlockId() { return newBlockId;}

    public void setNewBlockId(int newBlockId) { this.newBlockId = newBlockId; }
}
