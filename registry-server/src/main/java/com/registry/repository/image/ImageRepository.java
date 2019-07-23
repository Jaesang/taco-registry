package com.registry.repository.image;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long>{
    Image findOneByNamespaceAndName(@Param("namespace") String namespace, @Param("name") String name);
    List<Image> findAllByNamespace(@Param("namespace") String namespace);
    Page<Image> findAllByNameContaining(@Param("name") String name, Pageable pageable);
    List<Image> findAllByNameContaining(@Param("name") String name);
}
