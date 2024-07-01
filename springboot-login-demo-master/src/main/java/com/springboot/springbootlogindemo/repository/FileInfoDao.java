package com.springboot.springbootlogindemo.repository;

import com.springboot.springbootlogindemo.domain.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoDao extends JpaRepository<FileInfo, Long> {
    FileInfo findByFileId(long fileId);
    List<FileInfo> findAllByCreatorId(long userId);
    FileInfo findByFileNameAndCreatorId(String fileName, long creatorId);
}