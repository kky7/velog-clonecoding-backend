package com.velog.backend.repository;

import com.velog.backend.entity.Comment;
import com.velog.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostOrderByCreatedAtDesc(Post post);
    Long countByPost(Post post);
}
