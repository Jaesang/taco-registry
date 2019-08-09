package com.registry.repository.image;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long>{
//    @Query("select tag from Tag tag " +
//            "join tag.image image " +
//            "where image.id = :imageId " +
//            "and (tag.name, tag.startTs) in (" +
//            "   select t.name, max(t.start_ts) start_ts " +
//            "   from Tag t " +
//            "   join t.image i " +
//            "   where i.id = :imageId " +
//            "   group by t.name)")
    @Query(value = "select * from tag t " +
            "where image_id = :imageId " +
            "and (t.start_time, t.name) in (select max(start_time) start_time, name from tag where image_id = :imageId group by name)", nativeQuery = true)
    List<Tag> getTags(@Param("imageId") Long imageId);

    @Query("select tag from Tag tag " +
            "join tag.image image " +
            "where image.id = :imageId")
    Page<Tag> getTags(@Param("imageId") Long imageId, Pageable pageable);
}
