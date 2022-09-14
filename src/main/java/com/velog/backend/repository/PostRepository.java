package com.velog.backend.repository;

import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {
    Optional<Post> findByPostId(Long id);
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findAllByMember(Member member);
    List<Post> findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String titleSearch, String contentSearch);
}