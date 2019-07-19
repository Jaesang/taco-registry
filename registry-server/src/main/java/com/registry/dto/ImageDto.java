package com.registry.dto;

import io.swagger.annotations.ApiModel;

/**
 * Image dto
 */
public class ImageDto {
    @ApiModel("Image.CREATE")
    public static class CREATE {
        public String name;
    }

    @ApiModel("Image.EDIT")
    public static class EDIT {
        public String name;
    }

    @ApiModel("Image.VIEW")
    public static class VIEW {
        public String name;
        public Boolean publicYn;
    }

}
