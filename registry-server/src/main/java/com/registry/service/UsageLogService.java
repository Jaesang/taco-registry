package com.registry.service;

import com.registry.repository.image.Image;
import com.registry.repository.organization.Organization;
import com.registry.repository.usage.Log;
import com.registry.repository.usage.LogRepository;
import com.registry.util.CommonUtil;
import com.registry.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by boozer on 2019. 7. 15
 */
@Service
public class UsageLogService extends AbstractService {

    protected static final Logger logger = LoggerFactory.getLogger(UsageLogService.class);

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /** Org Repo */
    @Autowired
    private LogRepository logRepo;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

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
     * usage log 등록
     */
    public void create(Log log) {
        logger.info("create log: {}", log);

        log.setPerformer(userService.getUser(SecurityUtil.getUser()));
        log.setIp(CommonUtil.getIP());
        log.setDatetime(LocalDateTime.now());

        logRepo.save(log);
    }

    /**
     * user log 목록 조회
     * @param username
     * @return
     */
    public Page<Log> getUserLogs(String username, String startTime, String endTime, Pageable pageable) {
        logger.info("username username : {}", username);
        logger.info("username startTime : {}", startTime);
        logger.info("username endTime : {}", endTime);

        LocalDateTime start = LocalDateTime.parse(startTime + " 00:00:00", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endTime + " 23:59:59", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        return logRepo.getLogsByUsername(username, start, end, pageable);
    }

    /**
     * org log 목록 조회
     * @param namespace
     * @return
     */
    public Page<Log> getOrganizationLogs(String namespace, String startTime, String endTime, Pageable pageable) {
        logger.info("username namespace : {}", namespace);
        logger.info("username startTime : {}", startTime);
        logger.info("username endTime : {}", endTime);

        LocalDateTime start = LocalDateTime.parse(startTime + " 00:00:00", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endTime + " 23:59:59", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        Organization org = organizationService.getOrg(namespace);
        return logRepo.getLogsByOrganizationId(org.getId(), start, end, pageable);
    }

    /**
     * image log 목록 조회
     * @param namespace
     * @param imageName
     * @return
     */
    public Page<Log> getImageLogs(String namespace, String imageName, String startTime, String endTime, Pageable pageable) {
        logger.info("username image name : {}", imageName);
        logger.info("username startTime : {}", startTime);
        logger.info("username endTime : {}", endTime);

        LocalDateTime start = LocalDateTime.parse(startTime + " 00:00:00", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endTime + " 23:59:59", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        Image image = imageService.getImage(namespace, imageName);
        return logRepo.getLogsByImageId(image.getId(), start, end, pageable);
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
