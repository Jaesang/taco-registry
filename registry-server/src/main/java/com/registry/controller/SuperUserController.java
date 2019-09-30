package com.registry.controller;

import com.registry.constant.CommonConstant;
import com.registry.constant.Path;
import com.registry.dto.UserDto;
import com.registry.repository.user.User;
import com.registry.service.UserService;
import com.registry.value.common.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by boozer on 2019. 7. 15
 */
@RestController
public class SuperUserController {


    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    protected static final Logger logger = LoggerFactory.getLogger(SuperUserController.class);

    @Autowired
    private UserService userService;

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
     * User 목록 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(Path.SUPERUSER_USER)
    @ApiOperation(
            value = "get user",
            notes = "user 조회"
    )
    @ResponseBody
    public Object getUserList(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @ApiParam(
                    defaultValue=" ",
                    value ="Pageable"
            )
            @PageableDefault(sort = {"createdDate"}, direction = Sort.Direction.DESC, size = 200) Pageable pageable
    ) throws Exception{
        JSONObject result = new JSONObject();

        List<User> userList = userService.getUsers(pageable);
        userList = userList == null ? new ArrayList<>() : userList;
        result.put("users", mapper.mapAsList(userList, UserDto.VIEW.class));

        return result;
    }

    /**
     * User 등록
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(Path.SUPERUSER_USER)
    @ApiOperation(
            value = "save user",
            notes = "user 저장"
    )
    @ResponseBody
    public Object createUser(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @RequestBody UserDto.CREATE user
    ) throws Exception{
        User newUser = userService.saveUser(mapper.map(user, User.class), "USER", true);
        return mapper.map(newUser, UserDto.CREATE.class);
    }

    /**
     * User 저장
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(Path.SUPERUSER_USER_DETAIL)
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(Path.SUPERUSER_USER_DETAIL)
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
            @RequestHeader(name = "Authorization") String authorization,
            @PathVariable(name = "username") String username
    ) throws Exception{
        userService.deleteUser(username);

        return true;
    }

    /**
     * admin verify
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(Path.SUPERUSER_VERIFY)
    @ApiOperation(
            value = "admin verify",
            notes = "admin 확인"
    )
    @ResponseBody
    public Object adminVerify(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization
    ) throws Exception{
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
