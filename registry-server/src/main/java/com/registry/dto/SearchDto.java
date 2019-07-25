package com.registry.dto;

import io.swagger.annotations.ApiModel;

/**
 * Search dto
 */
public class SearchDto {

    @ApiModel("Search.VIEW")
    public static class VIEW {
        public String name;
        public String kind;
        public VIEW namespace;
        public String description;
        public Long stars;
        public Long lastModified;
        public Boolean isPublic;
        public Long popularity;
    }

}
