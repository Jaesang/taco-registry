package com.registry.service;

import com.registry.repository.image.Image;
import com.registry.repository.organization.Organization;
import com.registry.repository.usage.Log;
import com.registry.repository.usage.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by boozer on 2019. 7. 15
 */
@Service
public class UsageLogService extends AbstractService {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /** Org Repo */
    @Autowired
    private LogRepository _logRepo;

    @Autowired
    private OrganizationService _organizationService;

    @Autowired
    private ImageService _imageService;

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

        _logRepo.save(log);
    }

    /**
     * user log 목록 조회
     * @param username
     * @return
     */
    public List<Log> getUserLogs(String username, String startTime, String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime + " 00:00:00", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endTime + " 23:59:59", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        return _logRepo.getLogsByUsername(username, start, end);
    }

    /**
     * org log 목록 조회
     * @param namespace
     * @return
     */
    public List<Log> getOrganizationLogs(String namespace, String startTime, String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime + " 00:00:00", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endTime + " 23:59:59", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        Organization org = _organizationService.getOrg(namespace);
        return _logRepo.getLogsByOrganizationId(org.getId(), start, end);
    }

    /**
     * image log 목록 조회
     * @param namespace
     * @param imageName
     * @return
     */
    public List<Log> getImageLogs(String namespace, String imageName, String startTime, String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime + " 00:00:00", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endTime + " 23:59:59", DateTimeFormatter.ofPattern("M/d/y HH:mm:ss"));
        Image image = _imageService.getImage(namespace, imageName);
        return _logRepo.getLogsByImageId(image.getId(), start, end);
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
