package com.registry.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Long> {
    @Query("select userOrg from UserOrganization userOrg " +
            "join userOrg.organization org " +
            "where org.id = :organizationId")
    Page<UserOrganization> getUserOrgs(@Param("organizationId") Long organizationId, Pageable pageable);

    @Query("select userOrg from UserOrganization userOrg " +
            "join userOrg.user user " +
            "join userOrg.organization org " +
            "where org.id = :organizationId " +
            "and user.username = :username")
    UserOrganization getUserOrg(@Param("organizationId") Long organizationId, @Param("username") String username);
}
