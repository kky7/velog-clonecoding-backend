package com.velog.backend.repository;

import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import com.velog.backend.entity.PostTag;
import com.velog.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findAllByMemberAndTagOrderByPostDesc(Member member, Tag tag);
    List<PostTag> findAllByPost(Post post);
    List<PostTag> findAllByTag(Tag tag);
    List<PostTag> findAllByMember(Member member);
    Long countByMemberAndTag(Member member, Tag tag);
}
