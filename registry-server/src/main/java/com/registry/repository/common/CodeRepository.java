package com.registry.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CodeRepository extends JpaRepository<CodeEntity, Long> {
    // Code 목록 조회
    List<CodeEntity> findByGroupCode(@Param("groupCode") String groupCode);

    // Code 목록 조회
    List<CodeEntity> findByGroupCodeAndEnabled(@Param("groupCode") String groupCode, @Param("enabled") boolean enabled);
}
