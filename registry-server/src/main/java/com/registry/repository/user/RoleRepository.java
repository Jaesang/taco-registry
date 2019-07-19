package com.registry.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long>{

	// 유저가 가진 Role 목록
	List<Role> findByUserIdOrderByNameDesc(@Param("userId") Long userId);
}
