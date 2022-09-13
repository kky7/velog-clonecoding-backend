package com.velog.backend.Repository;

import com.velog.backend.entity.Member;
import com.velog.backend.entity.PostTag;
import com.velog.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findAllByMemberAndTagOrderByPostDesc(Member member, Tag tag);
}
