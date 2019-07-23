package com.registry.repository.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization, Long>{
    Organization findOneByName(@Param("name") String name);
    List<Organization> findAllByNameContaining(@Param("name") String name);
}
