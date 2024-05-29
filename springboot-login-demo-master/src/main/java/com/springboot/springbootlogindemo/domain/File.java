package com.springboot.springbootlogindemo.domain;

import javax.persistence.*;

@Table(name = "file")
@Entity
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long file_id;

    // 用户名属性varchar对应String
    private String uname;

    // 密码属性varchar对应String
    private String password;

    public long getFile_id() {
        return file_id;
    }

    public void setFile_id(long file_id) {
        this.file_id = file_id;
    }
}
