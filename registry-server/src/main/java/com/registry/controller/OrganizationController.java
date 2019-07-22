package com.registry.controller;

import com.registry.constant.Path;
import com.registry.dto.OrganizationDto;
import com.registry.dto.UserDto;
import com.registry.exception.BadRequestException;
import com.registry.repository.organization.Organization;
import com.registry.repository.user.User;
import com.registry.repository.user.UserOrganization;
import com.registry.service.OrganizationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by boozer on 2019. 7. 15
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
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
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
        organizationService.create(mapper.map(org, Organization.class));

        return true;
    }

    /**
     * Org 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
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
        OrganizationDto.VIEW org = mapper.map(organizationService.getOrg(name), OrganizationDto.VIEW.class);

        if (org == null) {
            throw new BadRequestException(MessageFormat.format("{0} not exist.", name));
        }
        return mapper.map(organizationService.getOrg(name), OrganizationDto.VIEW.class);
    }

    /**
     * Org 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.ORG_MEMBER)
    @ApiOperation(
            value = "get org",
            notes = "Org 조회"
    )
    public Object getOrgMembers(
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
        JSONObject result = new JSONObject();

        List<UserOrganization> users = organizationService.getMembers(name);
        List<OrganizationDto.MEMBER> members = new ArrayList<>();
        users.stream().forEach(value -> {
            OrganizationDto.MEMBER member = new OrganizationDto.MEMBER();
            member.name = value.getUser().getUsername();
            member.kind = "organization";
            member.role = "ADMIN";
            members.add(member);
        });
        result.put("members", members);
        return result;
    }

    /**
     * Org member 등록
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping(Path.ORG_MEMBER_DETAIL)
    @ApiOperation(
            value = "add member",
            notes = "Org 멤버 등록"
    )
    public Object addOrgMember(
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
            @PathVariable("name") String name,
            @ApiParam(
                    name = "username",
                    required = true
            )
            @PathVariable("username") String username
    ) throws Exception{
        organizationService.addMember(username, name);

        return true;
    }

    /**
     * Org member 삭제
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping(Path.ORG_MEMBER_DETAIL)
    @ApiOperation(
            value = "add member",
            notes = "Org 멤버 등록"
    )
    public Object deleteOrgMember(
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
            @PathVariable("name") String name,
            @ApiParam(
                    name = "username",
                    required = true
            )
            @PathVariable("username") String username
    ) throws Exception{
        organizationService.deleteMember(username, name);

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
