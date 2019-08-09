package com.registry.dto;

import com.registry.repository.image.Tag;
import io.swagger.annotations.ApiModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
        public String lastModified;
        public String description;
        public Boolean isPublic;
        public Boolean canAdmin;
        public Boolean canWrite;
        public Boolean isOrganization;
        public Boolean isStarred;
        public Long popularity;
        public List<TagDto.VIEW> tags;
        public List<STAT> stats;
    }

    public static class STAT {
        public String date;
        public Long count;
    }

    @ApiModel("Image.MEMBER")
    public static class MEMBER {
        public String name;
        public String kind;
        public String role;
    }

}
