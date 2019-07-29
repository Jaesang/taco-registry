package com.registry.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long>{
    @Query("select role from Role role " +
            "join role.user user " +
            "join role.image image " +
            "where user.username = :username " +
            "and image.id = :imageId")
    Role getRole(@Param("username") String username, @Param("imageId") Long imageId);

    @Query("select role from Role role " +
            "join role.image image " +
            "where image.id = :imageId")
    List<Role> getRoles(@Param("imageId") Long imageId);
}
