package com.registry.service;

import com.registry.exception.BadRequestException;
import com.registry.repository.image.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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

        tag.setStartTime(LocalDateTime.now());
        tagRepo.save(tag);
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
        List<Tag> preTags = tagRepo.getTagByDockerImageId(image.getId(), dockerImageId);

        if (preTags == null && preTags.size() == 0) {
            throw new BadRequestException("not exists");
        }

        Tag preTag = preTags.get(0);
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
