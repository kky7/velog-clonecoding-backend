package com.velog.backend.repository;

import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findAllByMember(Member member);
    List<Post> findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String titleSearch, String contentSearch);
}