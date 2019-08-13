package com.registry.dto;

import io.swagger.annotations.ApiModel;

import java.util.UUID;

/**
 * Build dto
 */
public class BuildLogDto {

    @ApiModel("Build.VIEW")
    public static class VIEW {
        public DATA data;
        public String message;
        public String type;
    }

    public static class DATA {
        public String datetime;
    }

}
