package com.springboot.springbootlogindemo.repository;

import com.springboot.springbootlogindemo.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ContentDao extends JpaRepository<Content, Long>{
    Content findByPageAndNewBlockId(int page, int newBlockId);
    Content findByPageAndNewBlockIdAndFileName(int page, int newBlockId, String fileName);
    Content findByFileName(String fileName);
    List<Content> findByApproverDept(String approverDept);

    @Query(value = "SELECT * FROM t_content WHERE new_block_id <> -1 AND file_name = :fileName", nativeQuery = true)
    List<Content> findAllByNewBlockIdNotEqualToMinusOne(@Param("fileName") String fileName);

    //@Query(value = "SELECT page, COUNT(*) FROM t_content WHERE new_block_id <> -1 AND file_name = :fileName GROUP BY page", nativeQuery = true)
    @Query(value = "SELECT t1.page, COALESCE(t2.block_count, 0) AS block_count " +
            "FROM (SELECT DISTINCT page " +
            "     FROM t_content " +
            "     WHERE file_name = :fileName) t1 " +
            "LEFT JOIN (SELECT page, COUNT(*) AS block_count " +
            "         FROM t_content " +
            "         WHERE new_block_id <> -1 " +
            "         AND file_name = :fileName " +
            "         GROUP BY page) t2 " +
            "ON t1.page = t2.page",
            nativeQuery = true)
    List<Object[]> getPageBlockCounts(@Param("fileName") String fileName);

    @Modifying
    @Transactional
    @Query(value = "UPDATE t_content SET approver_status = :approveStatus WHERE file_name = :fileName AND page = :page AND new_block_id = :newBlockId", nativeQuery = true)
    void updateApproveStatus(@Param("fileName") String fileName, @Param("page") int page, @Param("newBlockId") int newBlockId, @Param("approveStatus") int approveStatus);

    @Query(value = "SELECT COUNT(*) FROM t_content WHERE file_name = :fileName AND new_block_id <> -1 AND approver_status = 1", nativeQuery = true)
    int countApprovedContentByFileName(@Param("fileName") String fileName);
}
