package com.springboot.springbootlogindemo.domain.CoeditingData;

import com.fasterxml.jackson.databind.JsonNode;

public class CoeditingData {
    private long uid;
    private String fileName;
    private String uploadTime;
    private String uploadName;
    private JsonNode location;
    private JsonNode blockNum;
    private JsonNode blockStatus;
    private JsonNode comment;


    public CoeditingData(long uid,
                         String fileName,
                         String uploadTime,
                         String uploadName,
                         JsonNode location,
                         JsonNode blockNum,
                         JsonNode blockStatus,
                         JsonNode comment){
        this.uid = uid;
        this.fileName = fileName;
        this.uploadTime = uploadTime;
        this.uploadName = uploadName;
        this.location = location;
        this.blockNum = blockNum;
        this.blockStatus = blockStatus;
        this.comment = comment;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public JsonNode getLocation() {
        return location;
    }

    public void setLocation(JsonNode location) {
        this.location = location;
    }

    public JsonNode getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(JsonNode blockNum) {
        this.blockNum = blockNum;
    }

    public JsonNode getBlockStatus() {
        return blockStatus;
    }

    public void setBlockStatus(JsonNode blockStatus) {
        this.blockStatus = blockStatus;
    }

    public JsonNode getComment() {
        return comment;
    }

    public void setComment(JsonNode comment) {
        this.comment = comment;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadName() {
        return uploadName;
    }

    public void setUploadName(String uploadName) {
        this.uploadName = uploadName;
    }
}
