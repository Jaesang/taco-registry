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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    protected static final Logger logger = LoggerFactory.getLogger(TagService.class);

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

    @Autowired
    private UserService userService;

    @Autowired
    private ExternalAPIService externalService;

    @Autowired
    private ImageService imageService;

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
     * ?????? ?????? ??????
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
     * ?????? history ?????? ??????
     * @param namespace
     * @param name
     * @return
     */
    public List<Tag> getTagHistory(String namespace, String name) {
        logger.info("getTagHistory namespace : {}", namespace);
        logger.info("getTagHistory name : {}", name);

        Image image = this.imageRepo.getImage(namespace, name);

        return this.tagRepo.getAllTags(image.getId());
    }

    /**
     * Tag ??????
     * @param tag
     */
    @Transactional
    public void createTag(Tag tag) {
        logger.info("createTag tag : {}", tag);

        // ?????? ??????
        imageService.checkAuth(tag.getImage().getNamespace(), tag.getImage().getName());

        LocalDateTime now = LocalDateTime.now();

        Tag preTag = tagRepo.getTagByTagName(tag.getImage().getId(), tag.getName());
        if (preTag != null) {
            // ?????? tag update
            preTag.setBuildId(tag.getBuildId());
            preTag.setDockerImageId(tag.getDockerImageId());
            tag = preTag;
        } else {
            tag.setStartTime(now);
        }
        tagRepo.save(tag);

        // ?????? ??????
        Log log = new Log();
        log.setKind(Const.UsageLog.CREATE_TAG);
        if (tag.getImage().getIsOrganization()) {
            Organization org = organizationService.getOrg(tag.getImage().getNamespace());
            log.setOrganizationId(org.getId());
        } else {
            log.setUsername(tag.getImage().getNamespace());
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
     * Tag Expiration ??????
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

        // ?????? ??????
        imageService.checkAuth(namespace, name);

        Image image = imageRepo.getImage(namespace, name);
        Tag tag = tagRepo.getTagByTagName(image.getId(), tagName);
        Tag latestTag = tagRepo.getTagByTagName(image.getId(), "latest");

        if ("latest".equals(tagName)) {
            throw new BadRequestException("latest tag cannot set an expiration date.");
        } else if (latestTag == null || tag.getManifestDigest().equals(latestTag.getManifestDigest())) {
            throw new BadRequestException("If the 'latest' tag and the manifestDigest value are the same, you cannot set an expiration date.");
        }

        List<Tag> deleteTags = tagRepo.getTagsByManifestDigest(image.getId(), tag.getManifestDigest());

        deleteTags.stream().forEach(value -> {
            // log?????? ??????
            String oldExpiration = tag.getExpiration();

            if (expiration > 0) {
                LocalDateTime expirationDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiration), TimeZone.getDefault().toZoneId());
                tag.setExpiration(expirationDateTime);
                tag.setEndTime(null);
            } else {
                tag.setExpiration(null);
                tag.setEndTime(null);
            }
            tagRepo.save(tag);

            // ?????? ??????
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
        });
    }

    /**
     * Tag ??????
     * @param namespace
     * @param name
     * @param tagName
     */
    @Transactional
    public void deleteTag(String namespace, String name, String tagName, LocalDateTime date) {
        logger.info("deleteTag namespace : {}", namespace);
        logger.info("deleteTag name : {}", name);
        logger.info("deleteTag tagName : {}", tagName);

        // ?????? ??????
        imageService.checkAuth(namespace, name);

        Image image = imageRepo.getImage(namespace, name);
        Tag preTag = tagRepo.getTagByTagName(image.getId(), tagName);
        Tag latestTag = tagRepo.getTagByTagName(image.getId(), "latest");

        if (preTag != null) {
            if ("latest".equals(tagName)) {
                throw new BadRequestException("latest tag cannot be deleted.");
            } else if (latestTag == null || preTag.getManifestDigest().equals(latestTag.getManifestDigest())) {
                throw new BadRequestException("If the 'latest' tag and the manifestDigest value are the same, it cannot be deleted.");
            }

            // builder tag ?????? ??????
            externalService.deleteTag(preTag);

            List<Tag> deleteTags = tagRepo.getTagsByManifestDigest(image.getId(), preTag.getManifestDigest());

            deleteTags.stream().forEach(value -> {
                value.setExpiration(date);
                value.setEndTime(date);
                tagRepo.save(value);

                // ?????? ??????
                Log log = new Log();
                log.setKind(Const.UsageLog.DELETE_TAG);
                if (image.getIsOrganization()) {
                    Organization org = organizationService.getOrg(image.getNamespace());
                    log.setOrganizationId(org.getId());
                } else {
                    log.setUsername(image.getNamespace());
                }
                log.setImageId(image.getId());
                log.setNamespace(image.getNamespace());
                log.setImage(image.getName());
                log.setTag(value.getName());
                log.setMember(SecurityUtil.getUser());
                logService.create(log);
            });
        }
    }

    /**
     * ?????? ??????
     * @param namespace
     * @param name
     * @param tagName
     * @param oldTagName
     */
    public void copyTag(String namespace, String name, String tagName, String oldTagName) {
        logger.info("createTag namespace : {}", namespace);
        logger.info("createTag name : {}", name);
        logger.info("createTag oldTagName : {}", oldTagName);

        // ?????? ??????
        imageService.checkAuth(namespace, name);

        Image image = imageRepo.getImage(namespace, name);

        // ????????? tag(??????)??? ????????? ??????
        Tag preTag = tagRepo.getTagByTagName(image.getId(), oldTagName);

        if (preTag == null) {
            throw new BadRequestException("source tag not exists");
        }

        // tag ????????? ?????? ???????????? ?????? ??? builder ?????? manifest ?????? ????????????
        Tag tag = new Tag();
        tag.setImage(image);
        tag.setName(tagName);
        tag.setBuildId(preTag.getBuildId());
        tag.setDockerImageId(preTag.getDockerImageId());
        tag.setSize(preTag.getSize());

        createTag(tag);

        // builder tag ?????? ??????
        externalService.createTag(tag, preTag.getName());
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
