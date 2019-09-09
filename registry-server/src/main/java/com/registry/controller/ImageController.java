package com.registry.controller;

import com.registry.constant.Path;
import com.registry.dto.ImageDto;
import com.registry.dto.LogDto;
import com.registry.repository.image.Image;
import com.registry.repository.usage.Log;
import com.registry.repository.user.Role;
import com.registry.service.ExternalAPIService;
import com.registry.service.ImageService;
import com.registry.service.TagService;
import com.registry.service.UsageLogService;
import com.registry.util.SecurityUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by boozer on 2019. 7. 15
 */
@RestController
public class ImageController {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    protected static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    private UsageLogService usageLogService;

    @Autowired
    private TagService tagService;

    @Autowired
    private ExternalAPIService externalAPIService;

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

    /**
     * Image 등록
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping(Path.IMAGE)
    @ApiOperation(
        value = "create image",
        notes = "Image 생성"
    )
    public Object createImage(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @ApiParam(
                    name = "image",
                    required = true
            )
            @RequestBody ImageDto.CREATE image
    ) throws Exception{
        Image i = mapper.map(image, Image.class);
        imageService.create(i);

        return true;
    }

    /**
     * Image 수정
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping(Path.IMAGE_DETAIL)
    @ApiOperation(
            value = "update image",
            notes = "Image 수정"
    )
    public Object updateImage(
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
            @PathVariable String namespace,
            @ApiParam(
                    name = "name",
                    required = true
            )
            @PathVariable String name,
            @ApiParam(
                    name = "image",
                    required = true
            )
            @RequestBody ImageDto.CREATE image
    ) throws Exception{
        Image i = mapper.map(image, Image.class);
        imageService.update(i);

        return true;
    }

    /**
     * Image 삭제
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping(Path.IMAGE_DETAIL)
    @ApiOperation(
            value = "delete image",
            notes = "Image 삭제"
    )
    public Object deleteImage(
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
            @PathVariable String namespace,
            @ApiParam(
                    name = "name",
                    required = true
            )
            @PathVariable String name
    ) throws Exception{
        imageService.deleteImage(namespace, name);
        return true;
    }

    /**
     * Image 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.IMAGE_DETAIL)
    @ApiOperation(
            value = "get image",
            notes = "Image 조회"
    )
    public Object getImage(
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
                    name = "includeStats"
            )
            @RequestParam(value = "includeStats", required = false) boolean includeStats
    ) throws Exception{
        Image image = imageService.getImage(namespace, name);
        image.setTags(tagService.getTags(namespace, name));
        ImageDto.VIEW imageDto = mapper.map(image, ImageDto.VIEW.class);
        if (includeStats) {
            List<Map<String, Object>> list = imageService.getStats(namespace, name);
            imageDto.stats = mapper.mapAsList(list, ImageDto.STAT.class);
        }
        return imageDto;
    }

    /**
     * Image 목록 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.IMAGE)
    @ApiOperation(
            value = "get image",
            notes = "Image 조회"
    )
    public Object getImages(
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
            @RequestParam("namespace") String namespace,
            @ApiParam(
                    defaultValue=" ",
                    value ="Pageable"
            )
            @PageableDefault(sort = {"createdDate"}, direction = Sort.Direction.DESC) Pageable pageable
    ) throws Exception{
        Page<Image> result = imageService.getImages(namespace, pageable);
        // 형 변환
        List<ImageDto.VIEW> collect = new ArrayList<>();
        result.getContent()
                .stream()
                .forEach(value -> {
                    List<Role> roles = value.getRole().stream().filter(v -> v.getUser().getUsername().equals(SecurityUtil.getUser())).collect(Collectors.toList());
                    if ((roles != null && roles.size() > 0) || value.getIsPublic()) {
                        ImageDto.VIEW item = mapper.map(value, ImageDto.VIEW.class);
                        item.popularity = imageService.getPopularityCount(namespace, value.getName());
                        collect.add(item);
                    }
                });

        return new PageImpl<>(collect, pageable, result.getTotalElements());
    }

    /**
     * Image member 목록 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.IMAGE_MEMBER)
    @ApiOperation(
            value = "get image",
            notes = "Image 조회"
    )
    public Object getImages(
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
            @PageableDefault(sort = {"createdDate"}, direction = Sort.Direction.DESC) Pageable pageable
    ) throws Exception{
        Page<Role> result = imageService.getMembers(namespace, name, pageable);

        // 형 변환
        List<ImageDto.MEMBER> collect = result.getContent()
                .stream()
                .map(value -> {
                    ImageDto.MEMBER item = new ImageDto.MEMBER();
                    item.name = value.getUser().getUsername();
                    item.role = value.getName().toLowerCase();
                    return item;
                }).collect(Collectors.toList());

        return new PageImpl<>(collect, pageable, result.getTotalElements());
    }

    /**
     * Image member 등록
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping(Path.IMAGE_MEMBER_DETAIL)
    @ApiOperation(
            value = "create image member",
            notes = "Image member 등록"
    )
    public Object createImages(
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
                    name = "username",
                    required = true
            )
            @PathVariable("username") String username,
            @RequestBody ImageDto.MEMBER role
    ) throws Exception{
        imageService.updateRole(username, namespace, name, role.role);
        return true;
    }

    /**
     * Image member 삭제
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping(Path.IMAGE_MEMBER_DETAIL)
    @ApiOperation(
            value = "delete image member",
            notes = "Image member 삭제"
    )
    public Object deleteMember(
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
                    name = "username",
                    required = true
            )
            @PathVariable("username") String username
    ) throws Exception{
        imageService.deleteRole(username, namespace, name);
        return true;
    }

    /**
     * 공개 / 비공개 수정
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping(Path.IMAGE_VISIBILITY)
    @ApiOperation(
            value = "change visibility",
            notes = "공개여부 변경"
    )
    public Object changeVisibility(
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
            @RequestBody Map<String, Object> body
    ) throws Exception{
        Image image = imageService.updateVisibility(namespace, name, "public".equals(body.get("visibility")));
        ImageDto.VIEW imageDto = mapper.map(image, ImageDto.VIEW.class);
        return imageDto;
    }

    /**
     * Image logs
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.IMAGE_LOGS)
    @ApiOperation(
            value = "get logs",
            notes = "Image logs"
    )
    public Object getLogs(
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
                    name = "starttime",
                    required = true
            )
            @RequestParam("starttime") String starttime,
            @ApiParam(
                    name = "endtime",
                    required = true
            )
            @RequestParam("endtime") String endtime,
            @ApiParam(
                    defaultValue=" ",
                    value ="Pageable"
            )
            @PageableDefault(sort = {"datetime"}, direction = Sort.Direction.DESC) Pageable pageable
    ) throws Exception{
        Page<Log> result = usageLogService.getImageLogs(namespace, name, starttime, endtime, pageable);

        // 형 변환
        List<LogDto.VIEW> collect = result.getContent()
                .stream()
                .map(value -> {
                    LogDto.VIEW item = mapper.map(value, LogDto.VIEW.class);
                    return item;
                }).collect(Collectors.toList());

        return new PageImpl<>(collect, pageable, result.getTotalElements());
    }

    /**
     * Docker Image detail
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.DOCKER_IMAGE)
    @ApiOperation(
            value = "get docker image detail",
            notes = "Docker image detail"
    )
    public Object getDockerImage(
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
                    name = "docker image id",
                    required = true
            )
            @PathVariable("dockerImageId") String dockerImageId
    ) throws Exception{
        return externalAPIService.getManifests(namespace, name, dockerImageId);
    }

    /**
     * Docker Image security
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.IMAGE_SECURITY)
    @ApiOperation(
            value = "get security",
            notes = "Image security"
    )
    public Object getSecurities(
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
                    name = "docker image id",
                    required = true
            )
            @PathVariable("dockerImageId") String dockerImageId,
            @ApiParam(
                    name = "vulnerabilities"
            )
            @RequestParam(value = "vulnerabilities", required = false) Boolean vulnerabilities
    ) throws Exception{
        return externalAPIService.getSecurity(namespace, name, dockerImageId);
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
