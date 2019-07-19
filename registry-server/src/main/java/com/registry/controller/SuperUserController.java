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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by boozer on 2019. 6. 18
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
            @RequestHeader(name = "Authorization") String authorization
//            @RequestParam String keyword,
//            @RequestParam String role,
//            @RequestParam int page,
//            @RequestParam int size,
//            @RequestParam String sortProperty,
//            @RequestParam String sortDirection
    ) throws Exception{
        Result result = new Result();
        try {
            result.setData(mapper.mapAsList(userService.getUsers(), UserDto.VIEW.class));
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
        Result result = new Result();
        try {
            User newUser = userService.saveUser(mapper.map(user, User.class), "USER", true);

            result.setData(mapper.map(newUser, UserDto.CREATE.class));
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
        Result result = new Result();
        try {
            userService.saveUser(mapper.map(user, User.class), null, false);

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
    public Object updateUser(
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization,
            @PathVariable(name = "username") String username
    ) throws Exception{
        Result result = new Result();
        try {
            userService.deleteUser(username);

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
        Result result = new Result();
        try {
            result.setData(true);
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
