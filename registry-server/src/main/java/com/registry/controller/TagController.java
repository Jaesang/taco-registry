package com.registry.controller;

import com.registry.constant.Path;
import com.registry.dto.TagDto;
import com.registry.repository.image.Tag;
import com.registry.repository.image.TagRepository;
import com.registry.service.ExternalAPIService;
import com.registry.service.TagService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by boozer on 2019. 7. 15
 */
@RestController
public class TagController {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    protected static final Logger logger = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagService tagService;

    @Autowired
    private ExternalAPIService externalService;

    @Autowired
    private TagRepository tagRepo;

    @Autowired
    private MapperFacade mapper;

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Constructor
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Getter & Setter Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /**d
     * Tag history 목록 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.IMAGE_TAG)
    @ApiOperation(
            value = "get tag history list",
            notes = "Tag history 목록 조회"
    )
    public Object getTagHistories(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @ApiParam(
                    name = "namespace",
                    required = true
            )
            @PathVariable("namespace") String namespace,
            @ApiParam(
                    name = "name",
                    required = true
            )
            @PathVariable("name") String name,
            @ApiParam(
                    defaultValue=" ",
                    value ="Pageable"
            )
            @PageableDefault(sort = {"startTime"}, direction = Sort.Direction.DESC, size = 200) Pageable pageable
    ) throws Exception{
        Page<Tag> result = tagService.getTagHistory(namespace, name, pageable);
        // 형 변환
        List<TagDto.VIEW> collect = result.getContent()
                .stream()
                .map(value -> {
                    TagDto.VIEW item = mapper.map(value, TagDto.VIEW.class);
                    return item;
                }).collect(Collectors.toList());

        return new PageImpl<>(collect, pageable, result.getTotalElements());
    }

    /**
     * Tag 수정 (복사, 변경)
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping(Path.IMAGE_TAG_DETAIL)
    @ApiOperation(
            value = "update tag",
            notes = "Tag 수정"
    )
    public Object updateTag(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @ApiParam(
                    name = "namespace",
                    required = true
            )
            @PathVariable("namespace") String namespace,
            @ApiParam(
                    name = "name",
                    required = true
            )
            @PathVariable("name") String name,
            @ApiParam(
                    name = "tagName",
                    required = true
            )
            @PathVariable("tagName") String tagName,
            @RequestBody TagDto.EDIT tag
    ) throws Exception{

        if (tag.oldTagName != null) {
            // 태그 복사
            tagService.copyTag(namespace, name, tagName, tag.oldTagName);
        } else {
            // expiration 변경
            tagService.updateExpiration(namespace, name, tagName, tag.expiration != null ? tag.expiration : 0);
        }

        return true;
    }

    /**
     * Tag 삭제
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping(Path.IMAGE_TAG_DETAIL)
    @ApiOperation(
            value = "delete tag",
            notes = "Tag 삭제"
    )
    public Object deleteTag(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @ApiParam(
                    name = "namespace",
                    required = true
            )
            @PathVariable("namespace") String namespace,
            @ApiParam(
                    name = "name",
                    required = true
            )
            @PathVariable("name") String name,
            @ApiParam(
                    name = "tagName",
                    required = true
            )
            @PathVariable("tagName") String tagName
    ) throws Exception{

        tagService.deleteTag(namespace, name, tagName, LocalDateTime.now());

        return true;
    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Inner Class
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
}
