package com.registry.repository.usage;

import com.registry.dto.ImageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface LogRepository extends JpaRepository<Log, Long>{
    @Query("select log from Log log " +
            "where log.imageId = :imageId")
    List<Log> getLogsByImageId(@Param("imageId") Long imageId);

    @Query("select log from Log log " +
            "where log.organizationId = :organizationId")
    List<Log> getLogsByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("select log from Log log " +
            "where log.username = :username")
    List<Log> getLogsByUsername(@Param("username") String username);

    @Query("select log from Log log " +
            "where log.imageId is not null " +
            "and log.imageId = :imageId " +
            "and log.datetime between :startTime and :endTime")
    Page<Log> getLogsByImageId(@Param("imageId") Long imageId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);

    @Query("select log from Log log " +
            "where log.organizationId is not null " +
            "and log.organizationId = :organizationId " +
            "and log.datetime between :startTime and :endTime")
    Page<Log> getLogsByOrganizationId(@Param("organizationId") Long organizationId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);

    @Query("select log from Log log " +
            "where log.username is not null " +
            "and log.username = :username " +
            "and log.datetime between :startTime and :endTime")
    Page<Log> getLogsByUsername(@Param("username") String username, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);

    @Query("select new map (count(log.id) as count, function('to_char', log.datetime,'YYYY-MM-DD') as date) from Log log " +
            "where log.imageId = :imageId " +
            "and log.datetime between :startDate and :endDate " +
            "group by function('to_char', log.datetime,'YYYY-MM-DD') ")
    List<Map<String, Object>> getStats(@Param("imageId") Long imageId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("select count(log.id) from Log log " +
            "where log.imageId = :imageId")
    Long getStatsCount(@Param("imageId") Long imageId);
}
