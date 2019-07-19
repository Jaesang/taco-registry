package com.registry.dto;

import io.swagger.annotations.ApiModel;

/**
 * Usage dto
 */
public class UsageDto {
    @ApiModel("Usage.CREATE")
    public static class CREATE {
        public String name;
    }

    @ApiModel("Usage.EDIT")
    public static class EDIT {
        public String name;
    }

    @ApiModel("Usage.VIEW")
    public static class VIEW {
        public String name;
        public Boolean publicYn;
    }

}
