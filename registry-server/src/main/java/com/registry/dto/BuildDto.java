package com.registry.dto;

import io.swagger.annotations.ApiModel;

/**
 * Build dto
 */
public class BuildDto {
    @ApiModel("Build.CREATE")
    public static class CREATE {
    }

    @ApiModel("Build.EDIT")
    public static class EDIT {
    }

    @ApiModel("Build.VIEW")
    public static class VIEW {
        public String displayName;
        public String status;
        public String error;
        public String subdirectory;
        public String started;
        public String archiveUrl;
        public String trigger;
        public String triggerMetadata;
        public String context;
        public String phase;
        public String resourceKey;
        public String dockerfilePath;
    }

}
