package com.registry.controller;

import com.registry.constant.CommonConstant;
import com.registry.constant.Path;
import com.registry.dto.OrganizationDto;
import com.registry.repository.common.CodeEntity;
import com.registry.repository.organization.Organization;
import com.registry.repository.user.User;
import com.registry.service.CommonService;
import com.registry.service.OrganizationService;
import com.registry.value.common.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by boozer on 2019. 6. 18
 */
@RestController
public class OrganizationController {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    protected static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    @Autowired
    private OrganizationService organizationService;

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
     * Org 등록
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(Path.ORG)
    @ApiOperation(
        value = "create org",
        notes = "Org 생성"
    )
    public Object createOrg(
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
            @RequestBody OrganizationDto.CREATE org
    ) throws Exception{
        Result result = new Result();

        try {
            organizationService.create(mapper.map(org, Organization.class));
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
     * Org 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(Path.ORG_DETAIL)
    @ApiOperation(
            value = "get org",
            notes = "Org 조회"
    )
    public Object getOrg(
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
            @PathVariable("name") String name
    ) throws Exception{
        Result result = new Result();

        try {
            result.setData(mapper.map(organizationService.getOrg(name), OrganizationDto.VIEW.class));
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
