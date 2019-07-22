package com.registry.dto;

import io.swagger.annotations.ApiModel;

/**
 * Image dto
 */
public class ImageDto {
    @ApiModel("Image.CREATE")
    public static class CREATE {
        public String name;
        public String namespace;
        public boolean is_public;
        public String description;
        public boolean is_organization;
    }

    @ApiModel("Image.EDIT")
    public static class EDIT {
        public String name;
    }

    @ApiModel("Image.VIEW")
    public static class VIEW {
        public String name;
        public String namespace;
        public Boolean publicYn;
        public Boolean can_admin;
        public Boolean can_write;
        public Boolean is_organization;
    }

}
