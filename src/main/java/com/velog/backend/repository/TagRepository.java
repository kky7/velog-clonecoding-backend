package com.velog.backend.repository;

import com.velog.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Long> {
    Tag findByTagName(String tagName);
}
