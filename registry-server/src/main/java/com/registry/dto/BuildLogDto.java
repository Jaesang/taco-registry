package com.registry.dto;

import io.swagger.annotations.ApiModel;

import java.util.UUID;

/**
 * Build dto
 */
public class BuildLogDto {

    @ApiModel("Build.VIEW")
    public static class VIEW {
        public String datetime;
        public String message;
        public String type;
    }

}
