package com.registry.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(@Param("username") String username);

    List<User> findAllByDelYn(@Param("delYn") boolean delYn);

    List<User> findAllByUsernameContaining(@Param("username") String username);
}
