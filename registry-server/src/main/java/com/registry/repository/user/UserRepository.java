package com.registry.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserRepository extends JpaRepository<User, String> {
    @Query("select user from User user " +
            "where user.username = :username")
    User getUser(@Param("username") String username);

    @Query("select user from User user " +
            "where user.delYn = false " +
            "and (user.username like concat('%', :searchKey, '%') or user.email like concat('%', :searchKey, '%')) ")
    Page<User> getUsers(String searchKey, Pageable pageable);

    @Query("select user from User user  " +
            "where user.username like concat('%', :username, '%') " +
            "and user.delYn = false " +
            "and user.enabled = true")
    List<User> getUserByUsernameContaining(@Param("username") String username);

}
