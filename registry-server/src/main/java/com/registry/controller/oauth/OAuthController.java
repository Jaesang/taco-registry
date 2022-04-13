package com.registry.controller.oauth;

import com.registry.constant.ApplicationConstant;
import com.registry.constant.CommonConstant;
import com.registry.constant.Path;
import com.registry.service.OAuthService;
import com.registry.util.RestApiUtil;
import com.registry.value.common.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Map;

/**
 * Created by boozer on 2019. 7. 15
 */
@RestController
public class OAuthController {

    protected static final Logger logger = LoggerFactory.getLogger(OAuthController.class);

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /** 토큰 서비스 */
    @Resource(name="tokenServices")
    private ConsumerTokenServices tokenServices;

    @Autowired
    private ApplicationConstant appConst;

    @Autowired
    private RestApiUtil restApiUtil;

    @Autowired
    private OAuthService oAuthService;

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

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping(Path.OAUTH_REVOKE)
    @ApiOperation(
            value = "revokeToken",
            notes = "토큰제거"
    )
    public Object revokeToken(
            // 토큰
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization ) throws Exception {

        // 토큰
        String token = authorization.substring(authorization.indexOf(" ")+1, authorization.length());

        // 토큰제거
        boolean isDelete = tokenServices.revokeToken(token);

        // 결과
        Result result = new Result();
        result.setData(isDelete);
        result.setCode(CommonConstant.CommonCode.SUCCESS);
        result.setMessage(CommonConstant.CommonMessage.SUCCESS);
        return result;
    }

    @PostMapping(Path.OAUTH_TOKEN)
    @ApiOperation(value = "dummy token", notes = "dummy token API")
    protected Object getDummyToken(
            @ApiParam(
                    defaultValue = "Basic cmVnaXN0cnk6cmVnaXN0cnktc2VjcmV0",
                    value = "",
                    required = true) @RequestHeader(name = "Authorization") String authorization,
            @ApiParam(
                    defaultValue = "admin",
                    value = "username",
                    required = true
            ) @RequestParam(name = "username") String username,
            @ApiParam(
                    defaultValue = "password",
                    value = "password",
                    required = true
            ) @RequestParam(name = "password") String password ) throws Exception {

        Map result = oAuthService.getToken(username, password);
        return result;
    }

    @GetMapping(Path.OAUTH_TOKEN)
    @ApiOperation(value = "get token", notes = "get token API")
    protected Object getToken(
            @ApiParam(
                    defaultValue = "Basic YWRtaW46cGFzc3dvcmQ=",
                    value = "",
                    required = true) @RequestHeader(name = "Authorization") String authorization,
            @RequestParam(name = "service", required = false) String service,
            @RequestParam(name = "scope", required = false) String scope,
            @RequestParam(name = "offline_token", required = false) String offline_token,
            @RequestParam(name = "client_id", required = false) String client_id,
            HttpServletRequest request
    ) throws Exception {

        int index = authorization.toLowerCase().indexOf("basic ");

        String username = null;
        String password = null;

        if (index == 0) {
            // header 에서 username, password 가져오기
            String userPassString = authorization.substring(6);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decodedBytes = decoder.decode(userPassString.getBytes());
            String decodeString = new String(decodedBytes);

            String[] userPass = decodeString.split(":");
            username = userPass[0];
            password = userPass[1];
        }

        JSONObject result = oAuthService.getJWTToken(username, password, service, scope);
        return result;
    }

}
