package com.registry.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface UserOrganizationRepository extends JpaRepository<UserOrganization, UUID> {
    @Query("select userOrg from UserOrganization userOrg " +
            "join userOrg.organization org " +
            "join userOrg.user user " +
            "where org.id = :organizationId")
    Page<UserOrganization> getUserOrgs(@Param("organizationId") UUID organizationId, Pageable pageable);

    @Query("select userOrg from UserOrganization userOrg " +
            "join userOrg.organization org " +
            "join userOrg.user user " +
            "where org.id = :organizationId")
    List<UserOrganization> getUserOrgs(@Param("organizationId") UUID organizationId);

    @Query("select userOrg from UserOrganization userOrg " +
            "join userOrg.user user " +
            "join userOrg.organization org " +
            "where org.id = :organizationId " +
            "and user.username = :username")
    UserOrganization getUserOrg(@Param("organizationId") UUID organizationId, @Param("username") String username);
}
