package com.springboot.springbootlogindemo.service;

import com.springboot.springbootlogindemo.domain.Content;

public interface ContentSaveService {
    void saveContent(long fileId, int uid, String fileName, String contentPath);
}
