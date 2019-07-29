package com.registry.repository.image;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuildRepository extends JpaRepository<Build, Long>{
    @Query("select build from Build build " +
            "join build.image image " +
            "where image.id = :imageId")
    List<Build> getBuilds(@Param("imageId") Long imageId, Pageable pageable);

    @Query("select build from Build build " +
            "join build.image image " +
            "where image.id = :imageId")
    List<Build> getBuilds(@Param("imageId") Long imageId);
}
