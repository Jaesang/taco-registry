package com.registry.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long>{
    Role findOneByUserUsernameAndImageId(@Param("username") String username, @Param("image_id") Long imageId);
    List<Role> findAllByImageId(@Param("image_id") Long imageId);
}
