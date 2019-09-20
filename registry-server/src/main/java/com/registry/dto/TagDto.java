package com.registry.dto;

import io.swagger.annotations.ApiModel;

import java.time.LocalDateTime;

/**
 * Tag dto
 */
public class TagDto {
    @ApiModel("Tag.CREATE")
    public static class CREATE {
    }

    @ApiModel("Tag.EDIT")
    public static class EDIT {
        public String dockerImageId;
        public Long expiration;
    }

    @ApiModel("Tag.VIEW")
    public static class VIEW {
        public String buildId;
        public String dockerImageId;
        public String lastModified;
        public String manifestDigest;
        public String name;
        public Boolean reversion;
        public String size;
        public String expiration;
        public Long startTs;
        public Long endTs;
    }

}
