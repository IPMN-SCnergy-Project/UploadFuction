package com.springboot.springbootlogindemo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.springbootlogindemo.domain.CoeditingData.CoeditingData;

public interface IntoCoeditingService {
    CoeditingData getCoeditingData(long uid, String fileName);
    JsonNode getLocation(String fileName);
    JsonNode getBlockNum(String fileName);
    JsonNode getBlockStatus(String fileName);
    JsonNode getComment(String fileName);
}
