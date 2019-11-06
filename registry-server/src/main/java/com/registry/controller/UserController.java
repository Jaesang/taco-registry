package com.registry.controller;

import com.netflix.loadbalancer.Server;
import com.registry.constant.Path;
import com.registry.dto.ImageDto;
import com.registry.dto.LogDto;
import com.registry.dto.UserDto;
import com.registry.repository.usage.Log;
import com.registry.repository.user.User;
import com.registry.service.ExternalAPIService;
import com.registry.service.UsageLogService;
import com.registry.service.UserService;
import com.registry.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by boozer on 2019. 7. 15
 */
@RestController
public class UserController {


    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    protected static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private @Value("${config.registry.url}") String registryUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private UsageLogService usageLogService;

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
     * User 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.USER)
    @ApiOperation(
            value = "get user",
            notes = "user 조회"
    )
    public Object getUser(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization
    ) throws Exception{
        UserDto.VIEW user = mapper.map(userService.getLoginUser(), UserDto.VIEW.class);
        user.registryUrl = registryUrl;
        return user;
    }

    /**
     * User 상세 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.USER_DETAIL)
    @ApiOperation(
            value = "get user",
            notes = "user 상세 조회"
    )
    public Object getUser(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @PathVariable("username") String username
    ) throws Exception{

        User user = userService.getUserInfo(URLDecoder.decode(username, "utf-8"));
        return mapper.map(user, UserDto.VIEW.class);
    }

    /**
     * User 저장
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping(Path.USER)
    @ApiOperation(
            value = "save user",
            notes = "user 저장"
    )
    @ResponseBody
    public Object updateUser(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @RequestBody UserDto.EDIT user
    ) throws Exception{
        userService.saveUser(mapper.map(user, User.class), null, false);

        return true;
    }

    /**
     * User 삭제
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping(Path.USER)
    @ApiOperation(
            value = "delete user",
            notes = "user 삭제"
    )
    @ResponseBody
    public Object deleteUser(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization
    ) throws Exception{
        userService.deleteUser(SecurityUtil.getUser());

        return true;
    }

    /**
     * password verify
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping(Path.PASSWORD_VERIFY)
    @ApiOperation(
            value = "password verify",
            notes = "패스워드 확인"
    )
    @ResponseBody
    public Object passwordVerify(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @RequestBody Map<String, Object> map
    ) throws Exception{
        return userService.passwordVerify((String) map.get("password"));
    }

    /**
     * add starred
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping(Path.USER_STARRED)
    @ApiOperation(
            value = "add starred",
            notes = "즐겨찾기 등록"
    )
    @ResponseBody
    public Object addStarred(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @RequestBody ImageDto.CREATE image
    ) throws Exception{
        userService.updateStarred(image.namespace, image.name, true);
        return true;
    }

    /**
     * delete starred
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping(Path.USER_STARRED_DETAIL)
    @ApiOperation(
            value = "delete starred",
            notes = "즐겨찾기 삭제"
    )
    @ResponseBody
    public Object deleteStarred(
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
                    name = "imageName",
                    required = true
            )
            @PathVariable("imageName") String imageName
    ) throws Exception{
        userService.updateStarred(namespace, imageName, false);
        return true;
    }

    /**
     * User logs
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.USER_LOGS)
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
            @PageableDefault(sort = {"datetime"}, direction = Sort.Direction.DESC, size = 200) Pageable pageable
    ) throws Exception{
        Page<Log> result = usageLogService.getUserLogs(SecurityUtil.getUser(), starttime, endtime, pageable);

        // 형 변환
        List<LogDto.VIEW> collect = result.getContent()
                .stream()
                .map(value -> {
                    LogDto.VIEW item = mapper.map(value, LogDto.VIEW.class);
                    return item;
                }).collect(Collectors.toList());

        return new PageImpl<>(collect, pageable, result.getTotalElements());
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
