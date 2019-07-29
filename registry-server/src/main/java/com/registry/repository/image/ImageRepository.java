package com.registry.repository.image;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long>{
    @Query("select image from Image image " +
            "where image.namespace = :namespace " +
            "and image.name = :name " +
            "and image.delYn = false")
    Image getImage(@Param("namespace") String namespace, @Param("name") String name);

    @Query("select image from Image image " +
            "where image.id = :id ")
    Image getImage(@Param("id") Long id);

    @Query("select image from Image image " +
            "where image.namespace = :namespace " +
            "and image.delYn = false")
    List<Image> getImages(@Param("namespace") String namespace);

    @Query("select image from Image image " +
            "where image.name like concat('%', :name, '%') " +
            "and image.delYn = false " +
            "order by image.createdDate desc")
    Page<Image> getImagesByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("select image from Image image " +
            "where image.name like concat('%', :name, '%') " +
            "and image.delYn = false " +
            "order by image.createdDate desc")
    List<Image> getImagesByNameContaining(@Param("name") String name);
}
