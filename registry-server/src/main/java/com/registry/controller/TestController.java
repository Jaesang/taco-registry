package com.registry.controller;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by boozer on 2019. 7. 15
 */
@RestController
public class TestController {


    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    protected static final Logger logger = LoggerFactory.getLogger(TestController.class);

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

    @Autowired
    private ExternalAPIService externalAPIService;

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valOperations;

    @GetMapping("/api/test")
    public Object getTest(
    ) throws Exception{
        String buildersJSON = valOperations.get("builderList");
        JSONObject buildersObj;
        JSONArray builders;

        JSONParser parser = new JSONParser();
        try {
            buildersObj = (JSONObject) parser.parse(buildersJSON);
            builders = (JSONArray) buildersObj.get("builders");
        } catch (Exception e) {
            buildersObj = new JSONObject();
            builders = new JSONArray();
        }

        JSONObject obj = new JSONObject();
        obj.put("host", (int) Math.ceil(Math.random() * 255) + "." + (int) Math.ceil(Math.random() * 255) + "." + (int) Math.ceil(Math.random() * 255) + "." + (int) Math.ceil(Math.random() * 255));
        obj.put("port", (int) Math.ceil(Math.random() * 9999));
        builders.add(obj);
        buildersObj.put("builders", builders);

        String server = buildersObj.toJSONString();
        valOperations.set("builderList", server);

        return null;
    }

    @GetMapping("/api/test2")
    public Object getTest2(
    ) throws Exception{
        return externalAPIService.getBuilderUri();
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
