package com.registry.service;

import com.registry.repository.image.Image;
import com.registry.repository.image.ImageRepository;
import com.registry.repository.organization.Organization;
import com.registry.repository.organization.OrganizationRepository;
import com.registry.repository.user.Role;
import com.registry.repository.user.RoleRepository;
import com.registry.repository.user.User;
import com.registry.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boozer on 2019. 7. 15
 */
@Service
public class ImageService extends AbstractService {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /** Image Repo */
    @Autowired
    private ImageRepository _imageRepo;

    /** Role Repo */
    @Autowired
    private RoleRepository _roleRepo;

    @Autowired
    private UserService _userService;

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
     * organization 등록
     */
    @Transactional
    public void create(Image image) {
        logger.info("create : {}", image);

        User user = _userService.getUser(SecurityUtil.getUser());

        image.setCreatedBy(user);
        image.setUpdatedBy(user);
        _imageRepo.save(image);

        Role role = new Role();
        role.setImage(image);
        role.setUser(user);
        role.setName("ADMIN");
        role.setStarred(false);

        _roleRepo.save(role);
    }

    public Image getImage(String namespace, String name) {
        logger.info("getImage namespace : {}", namespace);
        logger.info("getImage name : {}", name);

        return _imageRepo.findOneByNamespaceAndName(namespace, name);
    }

    public List<Image> getImages(String namespace) {
        logger.info("getImages namespace : {}", namespace);

        return _imageRepo.findAllByNamespace(namespace);
    }

    public Page<Image> getImagesByContainName(String name, Pageable pageable) {
        logger.info("getImagesByContainName name : {}", name);
        logger.info("getImagesByContainName pageable : {}", pageable);

        return _imageRepo.findAllByNameContainingOrderByCreatedDateDesc(name, pageable);
    }

    public List<Image> getImagesByContainName(String name) {
        logger.info("getImagesByContainName name : {}", name);

        return _imageRepo.findAllByNameContaining(name);
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
