package com.velog.backend.repository;

import com.velog.backend.entity.PostTag;
import com.velog.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findAllByTagAndRefMemberIdOrderByCreatedAtDesc(Tag tag, Long memberId);
}
