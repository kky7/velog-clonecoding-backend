package com.velog.backend.repository;

import com.velog.backend.entity.Likes;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    List<Likes> findAllByMember_MemberId(Long memberId);
    Likes findByMemberAndPost(Member member, Post post);
}
