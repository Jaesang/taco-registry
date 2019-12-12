package com.registry.controller;

import com.registry.constant.Path;
import com.registry.dto.SearchDto;
import com.registry.repository.image.Image;
import com.registry.repository.organization.Organization;
import com.registry.repository.user.Role;
import com.registry.repository.user.User;
import com.registry.service.ImageService;
import com.registry.service.OrganizationService;
import com.registry.service.UserService;
import com.registry.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by boozer on 2019. 7. 15
 */
@RestController
public class SearchController {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    protected static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

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
     * 찾기 (user, org, image)
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.SEARCH_ALL)
    @ApiOperation(
            value = "find all",
            notes = "찾기 (user, org, image)"
    )
    public Object getAll(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @ApiParam(
                    name = "query"
            )
            @RequestParam("query") String query
    ) throws Exception{
        JSONObject result = new JSONObject();
        List<User> users = userService.getUsersByContainUsername(query);
        List<Organization> orgs = organizationService.getOrgsByContainName(query);
        List<Image> images = imageService.getImagesByContainName(query);

        List<SearchDto.VIEW> results = new ArrayList<>();
        users.stream().forEach(value -> {
            SearchDto.VIEW item = new SearchDto.VIEW();
            item.kind = "user";
            item.name = value.getUsername();
            results.add(item);
        });
        orgs.stream().forEach(value -> {
            SearchDto.VIEW item = new SearchDto.VIEW();
            item.kind = "organization";
            item.name = value.getName();
            results.add(item);
        });
        images.stream().forEach(value -> {
            // 권한이 있거나 public Image 만 결과로 보여줌
            List<Role> roles = value.getRole().stream().filter(v -> v.getUser().getUsername().equals(SecurityUtil.getUser())).collect(Collectors.toList());
            if ((roles != null && roles.size() > 0) || value.getIsPublic()) {
                SearchDto.VIEW item = new SearchDto.VIEW();
                item.kind = "image";
                item.name = value.getName();

                SearchDto.VIEW namespace = new SearchDto.VIEW();
                namespace.name = value.getNamespace();
                namespace.kind = value.getIsOrganization() ? "organization" : "user";
                item.namespace = namespace;

                results.add(item);
            }
        });

        result.put("content", results);

        return result;
    }

    /**
     * Image 찾기
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.SEARCH_IMAGE)
    @ApiOperation(
            value = "find images",
            notes = "Image 찾기"
    )
    public Object getImages(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @ApiParam(
                    name = "query"
            )
            @RequestParam("query") String query,
            @ApiParam(
                    name = "page",
                    required = true
            )
            @RequestParam("page") int page
    ) throws Exception{
        Pageable pageable = PageRequest.of(page, 10);
        Page<Image> result = imageService.getImagesByContainName(query, pageable);

        // 형 변환
        List<SearchDto.VIEW> collect = new ArrayList<>();
        result.getContent()
                .stream()
                .forEach(value -> {
                    // 권한이 있거나 public Image 만 결과로 보여줌
                    List<Role> roles = value.getRole().stream().filter(v -> v.getUser().getUsername().equals(SecurityUtil.getUser())).collect(Collectors.toList());
                    if ((roles != null && roles.size() > 0) || value.getIsPublic()) {
                        SearchDto.VIEW item = mapper.map(value, SearchDto.VIEW.class);
                        item.kind = "image";
                        item.stars = value.getStarreds().stream().filter(v -> v.getIsStarred()).count();
                        item.popularity = imageService.getPopularityCount(value.getNamespace(), value.getName());

                        SearchDto.VIEW namespace = new SearchDto.VIEW();
                        namespace.name = value.getNamespace();
                        namespace.kind = value.getIsOrganization() ? "organization" : "user";
                        item.namespace = namespace;
                        collect.add(item);
                    }
                });

        return new PageImpl<>(collect, pageable, result.getTotalElements());
    }

    /**
     * 추가할 멤버 찾기
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.SEARCH_MEMBER)
    @ApiOperation(
            value = "find new member",
            notes = "멤버 찾기"
    )
    public Object getOrg(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @ApiParam(
                    name = "username",
                    required = true
            )
            @PathVariable("username") String username,
            @ApiParam(
                    name = "namespace",
                    required = true
            )
            @RequestParam("namespace") String namespace
    ) throws Exception{
        JSONObject result = new JSONObject();

        result.put("content", userService.findMembers(username, namespace));
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
