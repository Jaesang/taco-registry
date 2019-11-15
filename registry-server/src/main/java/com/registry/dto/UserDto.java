package com.registry.dto;

import com.registry.repository.organization.Organization;
import com.registry.repository.user.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;

import java.util.List;

/**
 * 앱 리뷰
 */
public class UserDto {
    @ApiModel("UserDto.CREATE")
    public static class CREATE {
        public String username;
        public String password;
        public String email;
    }

    @ApiModel("UserDto.EDIT")
    public static class EDIT {
        public String password;
        public String name;
        public String username;
        public String email;
        public Boolean enabled;
    }

    @ApiModel("UserDto.VIEW")
    public static class VIEW {
        public String username;
        public String name;
        public String email;
        public Boolean enabled;
        public Boolean superuser;
        public String registryUrl;
        public Boolean minioEnabled;
        public String minioHost;
        public String minioPort;
        public List<Role> roles;
        public List<OrganizationDto.VIEW> organizations;
    }

}
