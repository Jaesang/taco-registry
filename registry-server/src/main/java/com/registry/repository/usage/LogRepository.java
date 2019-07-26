package com.registry.repository.usage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long>{
    List<Log> findAllByImageId(@Param("image_id") Long imageId);
    List<Log> findAllByOrganizationId(@Param("organization_id") Long organizationId);
    List<Log> findAllByUsername(@Param("username") String username);

    List<Log> findAllByImageIdAndDatetimeAfterAndDatetimeBefore(@Param("image_id") Long imageId, @Param("start_time") LocalDateTime startTime, @Param("end_time") LocalDateTime endTime);
    List<Log> findAllByOrganizationIdAndDatetimeAfterAndDatetimeBefore(@Param("organization_id") Long organizationId, @Param("start_time") LocalDateTime startTime, @Param("end_time") LocalDateTime endTime);
    List<Log> findAllByUsernameAndDatetimeAfterAndDatetimeBefore(@Param("username") String username, @Param("start_time") LocalDateTime startTime, @Param("end_time") LocalDateTime endTime);
}
