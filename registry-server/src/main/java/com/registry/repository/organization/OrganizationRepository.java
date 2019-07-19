package com.registry.repository.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface OrganizationRepository extends JpaRepository<Organization, Long>{
    Organization findOneByName(@Param("name") String name);
}
