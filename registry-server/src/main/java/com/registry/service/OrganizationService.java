package com.registry.service;

import com.registry.dto.OrganizationDto;
import com.registry.exception.BadRequestException;
import com.registry.repository.common.CodeEntity;
import com.registry.repository.common.CodeRepository;
import com.registry.repository.organization.Organization;
import com.registry.repository.organization.OrganizationRepository;
import com.registry.repository.user.User;
import com.registry.repository.user.UserOrganization;
import com.registry.repository.user.UserOrganizationRepository;
import com.registry.repository.user.UserRepository;
import com.registry.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boozer on 2019. 7. 15
 */
@Service
public class OrganizationService extends AbstractService {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /** Org Repo */
    @Autowired
    private OrganizationRepository _orgRepo;

    /** Org Repo */
    @Autowired
    private UserOrganizationRepository _userOrgRepo;

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
    public void create(Organization org) {
        logger.info("organization : {}", org);

        User user = new User();
        user.setId(SecurityUtil.getUser());

        org.setCreatedBy(user);
        org.setUpdatedBy(user);
        _orgRepo.save(org);


        UserOrganization userOrganization = new UserOrganization();
        userOrganization.setUser(user);
        userOrganization.setOrganization(org);
        _userOrgRepo.save(userOrganization);
    }

    public Organization getOrg(String name) {
        logger.info("getOrg name : {}", name);

        return _orgRepo.findOneByName(name);
    }

    public List<Organization> getOrgsByContainName(String name) {
        logger.info("getOrgsByContainName name : {}", name);

        return _orgRepo.findAllByNameContaining(name);
    }

    public List<UserOrganization> getMembers(String orgName) {
        logger.info("getMembers userorgNamename : {}", orgName);

        Organization org = getOrg(orgName);

        return _userOrgRepo.findAllByOrganizationId(org.getId());
    }

    /**
     * member 추가
     * @param username
     * @param namespace
     */
    public void addMember(String username, String namespace) throws Exception {
        logger.info("addMember username : {}", username);
        logger.info("addMember namespace : {}", namespace);

        User user = _userService.getUserInfo(username);
        Organization org = _orgRepo.findOneByName(namespace);

        if (_userOrgRepo.findOneByOrganizationIdAndUserId(org.getId(), user.getId()) == null) {
            UserOrganization userOrg = new UserOrganization();
            userOrg.setUser(user);
            userOrg.setOrganization(org);

            _userOrgRepo.save(userOrg);
        }
    }

    /**
     * member 삭제
     * @param username
     * @param namespace
     * @throws Exception
     */
    public void deleteMember(String username, String namespace) throws Exception {
        logger.info("deleteMember username : {}", username);
        logger.info("deleteMember namespace : {}", namespace);

        User user = _userService.getUserInfo(username);
        Organization org = _orgRepo.findOneByName(namespace);

        UserOrganization userOrg = _userOrgRepo.findOneByOrganizationIdAndUserId(org.getId(), user.getId());
        if (userOrg != null) {
            if (org.getCreatedBy().getId() != user.getId()) {
                _userOrgRepo.delete(userOrg);
            } else {
                throw new BadRequestException("Cannot remove creator.");
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
