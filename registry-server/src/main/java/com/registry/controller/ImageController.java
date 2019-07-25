package com.registry.controller;

import com.registry.constant.CommonConstant;
import com.registry.constant.Path;
import com.registry.dto.ImageDto;
import com.registry.dto.OrganizationDto;
import com.registry.dto.UserDto;
import com.registry.repository.image.Image;
import com.registry.repository.organization.Organization;
import com.registry.repository.user.Role;
import com.registry.repository.user.User;
import com.registry.service.ImageService;
import com.registry.value.common.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
            @PathVariable("name") String name
    ) throws Exception{
        Image image = imageService.getImage(namespace, name);
        ImageDto.VIEW imageDto = mapper.map(image, ImageDto.VIEW.class);
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
            @RequestParam("namespace") String namespace
    ) throws Exception{
        JSONObject result = new JSONObject();
//        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
//        mapperFactory.classMap(Image.class, ImageDto.VIEW.class)
//                .field("publicYn", "is_public")
//                .field("isOrganization", "is_organization")
//                .byDefault()
//                .register();
//        MapperFacade mapper = mapperFactory.getMapperFacade();
        result.put("images", mapper.mapAsList(imageService.getImages(namespace), ImageDto.VIEW.class));
        return result;
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
            @PathVariable("name") String name
    ) throws Exception{
        JSONObject result = new JSONObject();

        List<Role> roles = imageService.getMembers(namespace, name);
        List<ImageDto.MEMBER> members = new ArrayList<>();
        roles.stream().forEach(value -> {
            ImageDto.MEMBER member = new ImageDto.MEMBER();
            member.name = value.getUser().getUsername();
            member.role = value.getName().toLowerCase();
            members.add(member);
        });

        result.put("members", members);

        return result;
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
