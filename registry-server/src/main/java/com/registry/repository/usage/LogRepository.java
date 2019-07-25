package com.registry.repository.usage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long>{
    List<Log> findAllByImageId(@Param("image_id") Long imageId);
    List<Log> findAllByOrganizationId(@Param("organization_id") Long organizationId);
    List<Log> findAllByUsername(@Param("username") String username);
}
