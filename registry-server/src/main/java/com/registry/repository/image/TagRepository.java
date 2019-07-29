package com.registry.repository.image;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long>{
    @Query("select tag from Tag tag " +
            "join tag.image image " +
            "where image.id = :imageId")
    List<Tag> getTags(@Param("imageId") Long imageId, Pageable pageable);

    @Query("select tag from Tag tag " +
            "join tag.image image " +
            "where image.id = :imageId")
    List<Tag> getTags(@Param("imageId") Long imageId);
}
