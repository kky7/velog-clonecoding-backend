package com.velog.backend.repository;

import com.velog.backend.entity.Image;
import com.velog.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query(value = "SELECT i.imgUrl FROM Image i WHERE i.post = :post")
    List<String> findAllByPostJPQL(@Param("post") Post post);

    @Query(value = "SELECT i.img_url FROM image i WHERE i.post_id = :post_id limit 1", nativeQuery = true)
    String findAllByPost_PostId(@Param("post_id") Long post_id);

    List<Image> findAllByPost(Post post);
}
