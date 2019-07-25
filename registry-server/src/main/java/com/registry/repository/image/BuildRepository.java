package com.registry.repository.image;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuildRepository extends JpaRepository<Build, Long>{
    List<Build> findAllByImageId(@Param("image_id") Long imageId, Pageable pageable);

    List<Build> findAllByImageId(@Param("image_id") Long imageId);
}
