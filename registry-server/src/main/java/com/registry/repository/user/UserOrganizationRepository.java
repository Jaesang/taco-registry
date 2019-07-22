package com.registry.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Long> {
    List<UserOrganization> findAllByOrganizationId(@Param("organization_id") Long organizationId);
    UserOrganization findOneByOrganizationIdAndUserId(@Param("organization_id") Long organizationId, @Param("user_id") Long userId);
}
