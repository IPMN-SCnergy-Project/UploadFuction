package com.springboot.springbootlogindemo.repository;

import com.springboot.springbootlogindemo.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentDao extends JpaRepository<Comment, Long>{
    List<Comment> findCommentByFileName(String fileName);
}
