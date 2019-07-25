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
        public boolean isPublic;
        public String description;
        public boolean isOrganization;
    }

    @ApiModel("Image.EDIT")
    public static class EDIT {
        public String name;
    }

    @ApiModel("Image.VIEW")
    public static class VIEW {
        public String name;
        public String namespace;
        public Boolean isPublic;
        public Boolean canAdmin;
        public Boolean canWrite;
        public Boolean isOrganization;
        public Boolean isStarred;
    }

    @ApiModel("Image.MEMBER")
    public static class MEMBER {
        public String name;
        public String kind;
        public String role;
    }

}
