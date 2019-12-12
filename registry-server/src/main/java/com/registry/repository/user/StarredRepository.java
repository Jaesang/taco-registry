package com.registry.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface StarredRepository extends JpaRepository<Starred, UUID>{
    @Query("select starred from Starred starred " +
            "join starred.user user " +
            "join starred.image image " +
            "where user.username = :username " +
            "and image.id = :imageId")
    Starred getStarred(@Param("username") String username, @Param("imageId") UUID imageId);
}
