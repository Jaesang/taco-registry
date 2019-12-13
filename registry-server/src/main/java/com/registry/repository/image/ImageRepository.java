package com.registry.repository.image;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID>{
    @Query("select image from Image image " +
            "where image.namespace = :namespace " +
            "and image.name = :name " +
            "and image.delYn = false")
    Image getImage(@Param("namespace") String namespace, @Param("name") String name);

    @Query("select image from Image image " +
            "where image.id = :id ")
    Image getImage(@Param("id") UUID id);

    @Query("select image from Image image " +
            "where image.namespace = :namespace " +
            "and image.delYn = false")
    List<Image> getImages(@Param("namespace") String namespace);

    @Query("select image from Image image " +
            "where image.delYn = false " +
            "and image.name like concat('%', :name, '%') " +
            "order by image.createdDate desc")
    Page<Image> getImagesByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("select image from Image image " +
            "where image.delYn = false " +
            "and image.name like concat('%', :name, '%') " +
            "order by image.createdDate desc")
    List<Image> getImagesByNameContaining(@Param("name") String name);

    @Query("select image from Image image " +
            "where image.delYn = false")
    List<Image> getAllImages();
}
