package com.registry.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.registry.constant.Path;
import com.registry.dto.BuildDto;
import com.registry.dto.BuildLogDto;
import com.registry.repository.image.Build;
import com.registry.repository.image.BuildLog;
import com.registry.service.BuildService;
import com.registry.service.ExternalAPIService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by boozer on 2019. 7. 15
 */
@RestController
public class BuildController {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    protected static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    @Autowired
    private BuildService buildService;

    @Autowired
    private ExternalAPIService externalService;

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

    /**d
     * Build 목록 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.IMAGE_BUILD)
    @ApiOperation(
            value = "get build list",
            notes = "Build 목록 조회"
    )
    public Object getBuilds(
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
                    name = "name",
                    required = true
            )
            @PathVariable("name") String name,
            @ApiParam(
                    name = "limit"
            )
            @RequestParam("limit") int limit
    ) throws Exception{
        JSONObject result = new JSONObject();

        List<Build> buildList = buildService.getBuilds(namespace, name, limit);
        buildList = buildList == null ? new ArrayList<>() : buildList;
        result.put("builds", mapper.mapAsList(buildList, BuildDto.VIEW.class));

        return result;
    }

    /**
     * Build
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping(Path.IMAGE_BUILD)
    @ApiOperation(
            value = "create build",
            notes = "Build"
    )
    public Object createBuild(
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
                    name = "name",
                    required = true
            )
            @PathVariable("name") String name,
            @ApiParam(
                    name = "build"
            )
            @RequestBody BuildDto.CREATE buildDto
    ) throws Exception{
        Build build = buildService.createBuild(namespace, name, buildDto);

        // builder build 요청
        externalService.createBuild(build, buildDto.noCache);

        return build;
    }

    /**
     * Build 상세 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.IMAGE_BUILD_DETAIL)
    @ApiOperation(
            value = "get build detail",
            notes = "Build 상세 조회"
    )
    public Object getBuild(
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
                    name = "name",
                    required = true
            )
            @PathVariable("name") String name,
            @ApiParam(
                    name = "buildId",
                    required = true
            )
            @PathVariable("buildId") String buildId
    ) throws Exception{
        return buildService.getBuild(namespace, name, buildId);
    }

    /**
     * Build 취소
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping(Path.IMAGE_BUILD_DETAIL)
    @ApiOperation(
            value = "cancel build",
            notes = "Build 취소"
    )
    public Object cancelBuild(
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
                    name = "name",
                    required = true
            )
            @PathVariable("name") String name,
            @ApiParam(
                    name = "buildId",
                    required = true
            )
            @PathVariable("buildId") String buildId
    ) throws Exception{
        buildService.cancelBuild(namespace, name, buildId);

        return true;
    }

    /**d
     * Build log 조회
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.BUILD_LOGS)
    @ApiOperation(
            value = "get build logs",
            notes = "Build log 조회"
    )
    public Object getBuildLogs(
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
                    name = "name",
                    required = true
            )
            @PathVariable("name") String name,
            @ApiParam(
                    name = "buildId",
                    required = true
            )
            @PathVariable("buildId") String buildId
    ) throws Exception{
        JSONObject result = new JSONObject();

        result.put("logs", mapper.mapAsList(buildService.getBuildLogs(buildId), BuildLogDto.VIEW.class));

        return result;
    }

    /**
     * Build Log download
     * @return
     * @throws Exception
     */
    @GetMapping(Path.BUILD_LOGS_FILE)
    @ApiOperation(
            value = "download build logs",
            notes = "Build Log 다운로드"
    )
    public void getBuildLogFile(
            HttpServletResponse response,
            HttpServletRequest request,
            @ApiParam(
                    name = "buildId",
                    required = true
            )
            @PathVariable("buildId") String buildId
    ) throws Exception{
        List<BuildLog> logs = buildService.getBuildLogs(buildId);
        List<BuildLogDto.VIEW> logList = mapper.mapAsList(logs, BuildLogDto.VIEW.class);
        // list to jsonString
        String jsonString = new Gson().toJson(logList);

        BufferedInputStream ins = null;
        ServletOutputStream outs = null;
        try {
            response.setContentType("application/octet-stream");

            if (request.getHeader("User-Agent").indexOf("MSIE 5.5") != -1) {
                response.setHeader("Content-Type", "doesn/matter;");
                response.setHeader("Content-Disposition", "filename=" + buildId);
            } else {
                response.setHeader("Content-Type", "application/octet-stream;");
                response.setHeader("Content-Disposition", "attachment;filename=" + buildId);
            }

            response.setHeader("Content-Transfer-Encoding", "binary;");

            response.setContentLength((int) jsonString.getBytes().length);

            outs = response.getOutputStream();
            outs.write(jsonString.getBytes());
            outs.flush();

            outs.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (ins != null)
                try {
                    ins.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            if (outs != null) {
                try {
                    outs.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
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
