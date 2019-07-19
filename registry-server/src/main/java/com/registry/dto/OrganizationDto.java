package com.registry.dto;

import io.swagger.annotations.ApiModel;

/**
 * Organization dto
 */
public class OrganizationDto {
    @ApiModel("OrgDto.CREATE")
    public static class CREATE {
        public String name;
    }

    @ApiModel("OrgDto.EDIT")
    public static class EDIT {
        public String name;
    }

    @ApiModel("OrgDto.VIEW")
    public static class VIEW {
        public String name;
        public Boolean publicYn;
    }

}
