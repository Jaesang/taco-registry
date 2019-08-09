package com.registry.service;

import com.registry.repository.image.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
