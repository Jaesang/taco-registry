package com.registry.repository.image;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID>{
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
            "and t.end_time is null " +
            "and (t.start_time, t.name) in (select max(start_time) start_time, name from tag where image_id = :imageId group by name)", nativeQuery = true)
    List<Tag> getTags(@Param("imageId") UUID imageId);

    @Query("select tag from Tag tag " +
            "join tag.image image " +
            "where image.id = :imageId")
    List<Tag> getAllTags(@Param("imageId") UUID imageId);

    @Query("select tag from Tag tag " +
            "join tag.image image " +
            "where image.id = :imageId " +
            "and tag.dockerImageId = :dockerImageId")
    List<Tag> getTagsByDockerImageId(@Param("imageId") UUID imageId, @Param("dockerImageId") String dockerImageId);

    @Query("select tag from Tag tag " +
            "join tag.image image " +
            "where image.id = :imageId " +
            "and tag.buildId = :buildId")
    List<Tag> getTagsByBuildId(@Param("imageId") UUID imageId, @Param("buildId") UUID buildId);

    @Query("select tag from Tag tag " +
            "join tag.image image " +
            "where image.id = :imageId " +
            "and tag.name = :tagName " +
            "and tag.endTime is null")
    Tag getTagByTagName(@Param("imageId") UUID imageId, @Param("tagName") String tagName);

    @Query("select tag from Tag tag " +
            "where tag.endTime is null")
    List<Tag> getAllTags();

    @Query("select tag from Tag tag " +
            "join tag.image image " +
            "where image.id = :imageId " +
            "and tag.manifestDigest = :manifestDigest")
    List<Tag> getTagsByManifestDigest(@Param("imageId") UUID imageId, @Param("manifestDigest") String manifestDigest);

    @Query("select tag from Tag tag " +
            "where " +
            "tag.expiration < :date " +
            "and tag.endTime is null")
    List<Tag> getAllDeleteTagsByExpired(@Param("date") LocalDateTime date);
}
