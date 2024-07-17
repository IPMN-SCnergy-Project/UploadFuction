package com.springboot.springbootlogindemo.repository;

import com.springboot.springbootlogindemo.domain.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FileInfoDao extends JpaRepository<FileInfo, Long> {
    FileInfo findByFileId(long fileId);
    List<FileInfo> findAllByCreatorId(long userId);
    FileInfo findByFileNameAndCreatorId(String fileName, long creatorId);
    FileInfo findByFileName(String fileName);

    @Modifying
    @Transactional
    @Query(value = "UPDATE fileinfo SET is_finished = 1 WHERE file_name = :fileName", nativeQuery = true)
    void markFileAsFinished(@Param("fileName") String fileName);
}