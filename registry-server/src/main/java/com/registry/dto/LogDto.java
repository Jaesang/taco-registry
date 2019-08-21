package com.registry.dto;

import io.swagger.annotations.ApiModel;

import java.util.UUID;

/**
 * Log dto
 */
public class LogDto {
    @ApiModel("Log.VIEW")
    public static class VIEW {
        public String ip;
        public String datetime;
        public UserDto.VIEW performer;
        public String kind;
        public UUID organizationId;
        public UUID imageId;
        public String username;
        public UUID buildId;
        public String namespace;
        public String image;
        public String role;
        public String team;
        public String member;
        public String tag;
        public String dockerImageId;
        public String originalDockerImageId;
        public String visibility;
        public String description;
        public String expirationDate;
        public String oldExpirationDate;
    }

}
