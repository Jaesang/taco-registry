package com.registry.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID>{
    @Query("select role from Role role " +
            "join role.user user " +
            "join role.image image " +
            "where user.username = :username " +
            "and image.id = :imageId")
    Role getRole(@Param("username") String username, @Param("imageId") UUID imageId);

    @Query("select role from Role role " +
            "join role.image image " +
            "where image.id = :imageId")
    List<Role> getRoles(@Param("imageId") UUID imageId);

    @Query("select role from Role role " +
            "join role.image image " +
            "where image.id = :imageId")
    Page<Role> getRoles(@Param("imageId") UUID imageId, Pageable pageable);
}
