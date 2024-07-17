package com.springboot.springbootlogindemo.service;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.springbootlogindemo.domain.User;

public interface UserService {
    /**
     * @param uname
     * @param password
     * @return
     */
    User loginService(String uname, String password);

    /**
     * @param user
     * @return
     */
    User registService(User user);

    JsonNode getUserFileList(long userId);

}
