package com.registry.repository.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization, Long>{
    @Query("select org from Organization org " +
            "where org.id = :id ")
    Organization getOrganization(@Param("id") Long id);

    @Query("select org from Organization org " +
            "where org.name = :name " +
            "and org.delYn = false")
    Organization getOrganization(@Param("name") String name);

    @Query("select org from Organization org " +
            "where org.name like concat('%', :name, '%')" +
            "and org.delYn = false")
    List<Organization> getOrganizationsByNameContaining(@Param("name") String name);
}
