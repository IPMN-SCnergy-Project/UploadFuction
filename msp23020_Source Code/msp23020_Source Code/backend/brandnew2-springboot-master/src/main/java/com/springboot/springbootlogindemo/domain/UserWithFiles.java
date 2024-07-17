package com.springboot.springbootlogindemo.domain;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class UserWithFiles {
    private User user;
    private JsonNode fileList;

    public UserWithFiles(User user, JsonNode fileList) {
        this.user = user;
        this.fileList = fileList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JsonNode getFileList() {
        return fileList;
    }

    public void setFileList(JsonNode fileList) {
        this.fileList = fileList;
    }
}
