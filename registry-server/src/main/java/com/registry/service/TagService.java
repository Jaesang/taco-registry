package com.registry.service;

import com.registry.constant.Const;
import com.registry.exception.BadRequestException;
import com.registry.repository.image.Image;
import com.registry.repository.image.ImageRepository;
import com.registry.repository.image.Tag;
import com.registry.repository.image.TagRepository;
import com.registry.repository.organization.Organization;
import com.registry.repository.usage.Log;
import com.registry.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by boozer on 2019. 7. 15
 */
@Service
public class TagService extends AbstractService {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    @Autowired
    private ImageRepository imageRepo;

    @Autowired
    private TagRepository tagRepo;

    /** Org Service */
    @Autowired
    private OrganizationService organizationService;

    /** Log Service */
    @Autowired
    private UsageLogService logService;

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
     * 태그 목록 조회
     * @param namespace
     * @param name
     * @return
     */
    public List<Tag> getTags(String namespace, String name) {
        logger.info("getTags namespace : {}", namespace);
        logger.info("getTags name : {}", name);

        Image image = this.imageRepo.getImage(namespace, name);

        return this.tagRepo.getTags(image.getId());
    }

    /**
     * 태그 history 목록 조회
     * @param namespace
     * @param name
     * @param pageable
     * @return
     */
    public Page<Tag> getTagHistory(String namespace, String name, Pageable pageable) {
        logger.info("getTagHistory namespace : {}", namespace);
        logger.info("getTagHistory name : {}", name);
        logger.info("getTagHistory pageable : {}", pageable);

        Image image = this.imageRepo.getImage(namespace, name);

        return this.tagRepo.getTags(image.getId(), pageable);
    }

    /**
     * Tag 등록
     * @param tag
     */
    @Transactional
    public void createTag(Tag tag) {
        logger.info("createTag tag : {}", tag);
        LocalDateTime now = LocalDateTime.now();

        Tag preTag = tagRepo.getTagByTagName(tag.getImage().getId(), tag.getName());
        if (preTag != null) {
            // 같은 이름의 태그는 삭제
            preTag.setEndTime(now);
            preTag.setExpiration(now);
            tagRepo.save(preTag);
        }

        tag.setStartTime(now);
        tagRepo.save(tag);

        // 로그 등록
        Organization org = organizationService.getOrg(tag.getImage().getNamespace());
        Log log = new Log();
        log.setKind(Const.UsageLog.CREATE_TAG);
        if (tag.getImage().getIsOrganization()) {
            log.setOrganizationId(org.getId());
        } else {
            log.setUsername(SecurityUtil.getUser());
        }
        log.setImageId(tag.getImage().getId());
        log.setNamespace(tag.getImage().getNamespace());
        log.setImage(tag.getImage().getName());
        log.setTag(tag.getName());
        log.setDockerImageId(tag.getDockerImageId());
        log.setMember(SecurityUtil.getUser());
        log.setBuildId(tag.getBuildId());
        logService.create(log);
    }

    /**
     * Tag Expiration 수정
     * @param namespace
     * @param name
     * @param tagName
     * @param expiration
     */
    @Transactional
    public void updateExpiration(String namespace, String name, String tagName, Long expiration) {
        logger.info("updateTag namespace : {}", namespace);
        logger.info("updateTag name : {}", name);
        logger.info("updateTag tagName : {}", tagName);
        logger.info("updateTag expiration : {}", expiration);

        Image image = imageRepo.getImage(namespace, name);
        Tag tag = tagRepo.getTagByTagName(image.getId(), tagName);

        // log에서 사용
        String oldExpiration = tag.getExpiration();

        if (expiration > 0) {
            LocalDateTime expirationDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiration), TimeZone.getDefault().toZoneId());
            tag.setExpiration(expirationDateTime);
            tag.setEndTime(expirationDateTime);
        } else {
            tag.setExpiration(null);
            tag.setEndTime(null);
        }
        tagRepo.save(tag);

        // 로그 등록
        Organization org = organizationService.getOrg(tag.getImage().getNamespace());
        Log log = new Log();
        log.setKind(Const.UsageLog.CHANGE_TAG_EXPIRATION);
        if (tag.getImage().getIsOrganization()) {
            log.setOrganizationId(org.getId());
        } else {
            log.setUsername(SecurityUtil.getUser());
        }
        log.setImageId(tag.getImage().getId());
        log.setNamespace(tag.getImage().getNamespace());
        log.setImage(tag.getImage().getName());
        log.setTag(tag.getName());
        log.setExpirationDate(tag.getExpiration());
        log.setOldExpirationDate(oldExpiration);
        logService.create(log);
    }

    /**
     * Tag 삭제
     * @param namespace
     * @param name
     * @param tagName
     */
    @Transactional
    public void deleteTag(String namespace, String name, String tagName) {
        logger.info("deleteTag namespace : {}", namespace);
        logger.info("deleteTag name : {}", name);
        logger.info("deleteTag tagName : {}", tagName);

        Image image = imageRepo.getImage(namespace, name);
        Tag preTag = tagRepo.getTagByTagName(image.getId(), tagName);

        if (preTag != null) {
            preTag.setExpiration(LocalDateTime.now());
            preTag.setEndTime(LocalDateTime.now());
            tagRepo.save(preTag);

            // 로그 등록
            Organization org = organizationService.getOrg(image.getNamespace());
            Log log = new Log();
            log.setKind(Const.UsageLog.DELETE_TAG);
            if (image.getIsOrganization()) {
                log.setOrganizationId(org.getId());
            } else {
                log.setUsername(SecurityUtil.getUser());
            }
            log.setImageId(image.getId());
            log.setNamespace(image.getNamespace());
            log.setImage(image.getName());
            log.setTag(tagName);
            log.setMember(SecurityUtil.getUser());
            logService.create(log);
        }
    }

    /**
     * 태그 복사
     * @param namespace
     * @param name
     * @param tagName
     * @param dockerImageId
     */
    @Transactional
    public void copyTag(String namespace, String name, String tagName, String dockerImageId) {
        logger.info("createTag namespace : {}", namespace);
        logger.info("createTag name : {}", name);
        logger.info("createTag dockerImageId : {}", dockerImageId);

        //todo builder tagging 후 digest 받아오기

        Image image = imageRepo.getImage(namespace, name);

        // 기존 살아있는 태그 중에 같은 이름이 있는지 체크
        Tag preTag = tagRepo.getTagByTagName(image.getId(), tagName);

        if (preTag != null) {
            throw new BadRequestException(MessageFormat.format("{0} is already applied to this image.", tagName));
        }

        // 복사할 tag(원본)가 있는지 체크
        List<Tag> preTags = tagRepo.getTagsByDockerImageId(image.getId(), dockerImageId);

        if (preTags == null || preTags.size() == 0) {
            throw new BadRequestException("not exists");
        }

        preTag = preTags.get(0);
        Tag tag = new Tag();
        tag.setImage(image);
        tag.setName(tagName);
        tag.setBuildId(preTag.getBuildId());
        tag.setDockerImageId(preTag.getDockerImageId());
        tag.setSize(preTag.getSize());

        createTag(tag);
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
