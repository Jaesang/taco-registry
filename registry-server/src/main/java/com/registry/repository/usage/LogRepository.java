package com.registry.repository.usage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

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
    List<Log> getLogsByImageId(@Param("imageId") Long imageId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("select log from Log log " +
            "where log.organizationId is not null " +
            "and log.organizationId = :organizationId " +
            "and log.datetime between :startTime and :endTime")
    List<Log> getLogsByOrganizationId(@Param("organizationId") Long organizationId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("select log from Log log " +
            "where log.username is not null " +
            "and log.username = :username " +
            "and log.datetime between :startTime and :endTime")
    List<Log> getLogsByUsername(@Param("username") String username, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
