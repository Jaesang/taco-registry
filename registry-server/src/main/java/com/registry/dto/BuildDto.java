package com.registry.dto;

import io.swagger.annotations.ApiModel;

import java.util.UUID;

/**
 * Build dto
 */
public class BuildDto {
    @ApiModel("Build.CREATE")
    public static class CREATE {
        public String dockerfile;
        public String gitPath;
        public String gitUsername;
        public String gitPassword;
        public String minioPath;
        public boolean noCache;
    }

    @ApiModel("Build.EDIT")
    public static class EDIT {
    }

    @ApiModel("Build.VIEW")
    public static class VIEW {
        public UUID id;
        public String displayName;
        public String status;
        public String error;
        public String subdirectory;
        public String started;
        public String manualUser;
        public String archiveUrl;
        public String trigger;
        public String triggerMetadata;
        public String context;
        public String phase;
        public String resourceKey;
        public String dockerfilePath;
        public String dockerfile;
        public String gitPath;
        public String minioPath;
        public String[] tags = {"latest"};
    }

}
