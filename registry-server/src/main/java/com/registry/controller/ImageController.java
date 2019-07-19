package com.registry.controller;

import com.registry.constant.CommonConstant;
import com.registry.constant.Path;
import com.registry.dto.ImageDto;
import com.registry.dto.OrganizationDto;
import com.registry.repository.image.Image;
import com.registry.repository.organization.Organization;
import com.registry.service.ImageService;
import com.registry.value.common.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by boozer on 2019. 6. 18
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
    @PreAuthorize("hasAuthority('USER')")
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
                    name = "organization",
                    required = true
            )
            @RequestBody ImageDto.CREATE image
    ) throws Exception{
        Result result = new Result();

        try {
            imageService.create(mapper.map(image, Image.class));
            result.setCode(CommonConstant.CommonCode.SUCCESS);
            result.setMessage(CommonConstant.CommonMessage.SUCCESS);
        } catch(Exception e) {
            logger.error(e.getMessage());
            result.setCode(CommonConstant.CommonCode.FAIL);
            result.setMessage(CommonConstant.CommonMessage.FAIL);
        }
        return result;
    }

    /**
     * Image 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(Path.IMAGE_DETAIL)
    @ApiOperation(
            value = "get image",
            notes = "Image 조회"
    )
    public Object getOrg(
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
            @PathVariable("name") String namespace,
            @ApiParam(
                    name = "name",
                    required = true
            )
            @PathVariable("name") String name
    ) throws Exception{
        Result result = new Result();

        try {
            result.setData(mapper.map(imageService.getImage(namespace, name), ImageDto.VIEW.class));
            result.setCode(CommonConstant.CommonCode.SUCCESS);
            result.setMessage(CommonConstant.CommonMessage.SUCCESS);
        } catch(Exception e) {
            logger.error(e.getMessage());
            result.setCode(CommonConstant.CommonCode.FAIL);
            result.setMessage(CommonConstant.CommonMessage.FAIL);
        }
        return result;
    }

    /**
     * Image 목록 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(Path.IMAGE)
    @ApiOperation(
            value = "get image",
            notes = "Image 조회"
    )
    public Object getOrg(
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
            @RequestParam("namespace") String namespace
    ) throws Exception{
        Result result = new Result();

        try {
            result.setData(mapper.mapAsList(imageService.getImages(namespace), ImageDto.VIEW.class));
            result.setCode(CommonConstant.CommonCode.SUCCESS);
            result.setMessage(CommonConstant.CommonMessage.SUCCESS);
        } catch(Exception e) {
            logger.error(e.getMessage());
            result.setCode(CommonConstant.CommonCode.FAIL);
            result.setMessage(CommonConstant.CommonMessage.FAIL);
        }
        return result;
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
