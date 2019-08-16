package com.registry.repository.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BuildLogRepository extends JpaRepository<BuildLog, BuildLogPK>{
    @Query("select log from BuildLog log " +
            "join log.pk.build build " +
            "where build.id = :buildId " +
            "order by log.seq")
    List<BuildLog> getBuildLogsByBuildId(@Param("buildId") UUID buildId);

//    @Query("delete from BuildLod log " +
//            "where log IN " +
//            "(SELECT a FROM BuildLod a " +
//            "join a.pk.build build " +
//            "where build.id = :buildId)")
//    void deleteBuildLogs(@Param("buildId") UUID buildId);
}
