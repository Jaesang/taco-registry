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
public class UserController {


    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    protected static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
     * User 조회
     * @return
     * @throws Exception
     */
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
        Result result = new Result();

        try {
            // 반환
            result.setData(mapper.map(userService.getLoginUser(), UserDto.VIEW.class));
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
     * User 상세 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAuthority('USER')")
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
        Result result = new Result();

        try {
            User user = userService.getUserInfo(URLDecoder.decode(username, "utf-8"));

            result.setData(mapper.map(user, UserDto.VIEW.class));
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
    @PreAuthorize("hasAuthority('USER')")
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
     * password verify
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAuthority('USER')")
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
        Result result = new Result();
        try {
            result.setData(userService.passwordVerify((String) map.get("password")));
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
