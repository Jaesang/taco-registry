package com.registry.service;

import com.registry.constant.Const;
import com.registry.dto.BuildDto;
import com.registry.dto.BuildLogDto;
import com.registry.repository.image.*;
import com.registry.repository.organization.Organization;
import com.registry.repository.usage.Log;
import com.registry.util.SecurityUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.UUID;

/**
 * Created by boozer on 2019. 7. 15
 */
@Service
public class BuildService extends AbstractService {

    protected static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /** Build Repo */
    @Autowired
    private BuildRepository buildRepo;

    /** Build Log Repo */
    @Autowired
    private BuildLogRepository buildLogRepo;

    /** Image Service */
    @Autowired
    private ImageService imageService;

    /** Tag Service */
    @Autowired
    private TagService tagService;

    /** Org Service */
    @Autowired
    private OrganizationService organizationService;

    /** Log Service */
    @Autowired
    private UsageLogService logService;

    @Autowired
    private ExternalAPIService externalService;

    @Autowired
    private FileService fileService;

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
     * build 목록 조회
     * @param namespace
     * @param name
     * @param limit
     * @return
     */
    public List<Build> getBuilds(String namespace, String name, int limit) {
        logger.info("getBuilds namespace : {}", namespace);
        logger.info("getBuilds name : {}", name);
        logger.info("getBuilds limit : {}", limit);

        Image image = imageService.getImage(namespace, name);

        Pageable pageable = PageRequest.of(0, limit, new Sort(Sort.Direction.DESC, "createdDate"));
        return buildRepo.getBuilds(image.getId(), pageable);
    }

    /**
     * build 상세 조회
     * @param namespace
     * @param name
     * @param id
     * @return
     */
    public Build getBuild(String namespace, String name, String id) {
        logger.info("getBuild namespace : {}", namespace);
        logger.info("getBuild name : {}", name);
        logger.info("getBuild id : {}", id);

        UUID uuid = UUID.fromString(id);
        return buildRepo.findById(uuid).orElse(null);
    }

    /**
     * build 생성
     * @param buildDto
     * @throws Exception
     */
    @Transactional
    public Build createBuild(String namespace, String name, BuildDto.CREATE buildDto) throws Exception {
        logger.info("createBuild namespace : {}", namespace);
        logger.info("createBuild name : {}", name);
        logger.info("createBuild build type : {}", !StringUtils.isEmpty(buildDto.dockerfile) ? "dockerfile" : (!StringUtils.isEmpty(buildDto.gitPath) ? "git" : "minio"));

        // 권한 체크
        imageService.checkAuth(namespace, name);

        Build build = new Build();

        Image image = imageService.getImage(namespace, name);
        build.setImage(image);
        build.setPhase(Const.Build.PHASE.WAITING);

        byte[] targetBytes;
        byte[] encodedBytes;
        Encoder encoder = Base64.getEncoder();

        if (!StringUtils.isEmpty(buildDto.dockerfile)) {
            targetBytes = buildDto.dockerfile.getBytes();
            encodedBytes = encoder.encode(targetBytes);
            build.setDockerfile(new String(encodedBytes));
        } else if (!StringUtils.isEmpty(buildDto.gitPassword)) {
            targetBytes = buildDto.gitPassword.getBytes();
            encodedBytes = encoder.encode(targetBytes);
            build.setGitPassword(new String(encodedBytes));
        }
        build.setGitPath(buildDto.gitPath);
        build.setGitUsername(buildDto.gitUsername);
        build.setMinioPath(buildDto.minioPath);

        build = buildRepo.save(build);

        Tag tag = new Tag();
        tag.setImage(image);
        tag.setBuildId(build.getId());
        tag.setName("latest");
        tag.setDockerImageId(UUID.randomUUID().toString());
        tagService.createTag(tag);

        // 로그 등록
        Organization org = organizationService.getOrg(image.getNamespace());
        Log log = new Log();
        log.setKind(Const.UsageLog.BUILD_DOCKERFILE);
        if (image.getIsOrganization()) {
            log.setOrganizationId(org.getId());
        } else {
            log.setUsername(SecurityUtil.getUser());
        }
        log.setImageId(image.getId());
        log.setNamespace(image.getNamespace());
        log.setImage(image.getName());
        logService.create(log);

        return build;
    }

    /**
     * build 취소
     * @param namespace
     * @param name
     * @param buildId
     */
    public void cancelBuild(String namespace, String name, String buildId) {
        logger.info("cancelBuild namespace : {}", namespace);
        logger.info("cancelBuild name : {}", name);
        logger.info("cancelBuild build id : {}", buildId);

        // 권한 체크
        imageService.checkAuth(namespace, name);

        Build build = getBuild(namespace, name, buildId);

        if (Const.Build.PHASE.WAITING.equals(build.getPhase()) || Const.Build.PHASE.PULLING.equals(build.getPhase()) || Const.Build.PHASE.BUILDING.equals(build.getPhase())) {
            //todo builder에 build 취소 요청

            build.setPhase(Const.Build.PHASE.CANCELLED);
            buildRepo.save(build);
        }
    }

    /**
     * build log 조회
     * @param buildId
     * @return
     */
    public List<BuildLog> getBuildLogs(String buildId) {
        logger.info("getBuildLogs buildId : {}", buildId);

        UUID uuid = UUID.fromString(buildId);

        return buildLogRepo.getBuildLogsByBuildId(uuid);
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
