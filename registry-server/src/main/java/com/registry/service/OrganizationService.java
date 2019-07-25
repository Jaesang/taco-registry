package com.registry.service;

import com.registry.dto.OrganizationDto;
import com.registry.exception.AccessDeniedException;
import com.registry.exception.BadRequestException;
import com.registry.repository.common.CodeEntity;
import com.registry.repository.common.CodeRepository;
import com.registry.repository.image.Image;
import com.registry.repository.organization.Organization;
import com.registry.repository.organization.OrganizationRepository;
import com.registry.repository.usage.LogRepository;
import com.registry.repository.user.*;
import com.registry.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @Autowired
    private ImageService _imageService;

    @Autowired
    private RoleRepository _roleRepo;

    @Autowired
    private LogRepository _logRepo;

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

        if (org.getName().length() < 2 || org.getName().length() > 40) {
            throw new BadRequestException("Be at least 2 characters in length and max 40 characters in length");
        }

        Organization preOrg = _orgRepo.findOneByName(org.getName());
        if (preOrg != null) {
            throw new BadRequestException("Already exists");
        }

        //todo builder에서 org 존재 체크

        Pattern p = Pattern.compile("^[a-z0-9_-]+$");
        Matcher m = p.matcher(org.getName());
        if (m.find()) {
            User user = new User();
            user.setUsername(SecurityUtil.getUser());

            org.setCreatedBy(user);
            org.setUpdatedBy(user);
            _orgRepo.save(org);


            UserOrganization userOrganization = new UserOrganization();
            userOrganization.setUser(user);
            userOrganization.setOrganization(org);
            _userOrgRepo.save(userOrganization);
        } else {
            throw new BadRequestException("Oraganization names must match [a-z0-9_-]+");
        }
    }

    @Transactional
    public void deleteOrg(String name) {
        // 권한 체크
        checkAuth(name);

        Organization org = _orgRepo.findOneByName(name);

        // image 삭제
        List<Image> images = _imageService.getImages(name);
        images.stream().forEach(value -> {
            _imageService.deleteImage(name, value.getName());
        });

        // user org rel 삭제
        _userOrgRepo.deleteAll(_userOrgRepo.findAllByOrganizationId(org.getId()));

        // usage log 삭제
        _logRepo.deleteAll(_logRepo.findAllByOrganizationId(org.getId()));

        // organization 삭제
        _orgRepo.delete(org);
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
    @Transactional
    public void addMember(String username, String namespace) throws Exception {
        logger.info("addMember username : {}", username);
        logger.info("addMember namespace : {}", namespace);

        // 권한 체크
        this.checkAuth(namespace);

        User user = _userService.getUserInfo(username);
        Organization org = _orgRepo.findOneByName(namespace);

        if (_userOrgRepo.findOneByOrganizationIdAndUserUsername(org.getId(), user.getUsername()) == null) {
            UserOrganization userOrg = new UserOrganization();
            userOrg.setUser(user);
            userOrg.setOrganization(org);

            _userOrgRepo.save(userOrg);

            // user role에 멤버 추가
            List<Role> addMembers = new ArrayList<>();
            _imageService.getImages(namespace).forEach(value -> {
                List<Role> roles = value.getRole().stream().filter(v -> v.getUser().getUsername().equals(username)).collect(Collectors.toList());
                if (roles.size() == 0) {
                    Role role = new Role();
                    role.setUser(user);
                    role.setImage(value);
                    role.setName("ADMIN");
                    role.setIsStarred(false);

                    addMembers.add(role);
                }
            });

            _roleRepo.saveAll(addMembers);
        }
    }

    /**
     * member 삭제
     * @param username
     * @param namespace
     * @throws Exception
     */
    @Transactional
    public void deleteMember(String username, String namespace) throws Exception {
        logger.info("deleteMember username : {}", username);
        logger.info("deleteMember namespace : {}", namespace);

        // 권한 체크
        this.checkAuth(namespace);

        User user = _userService.getUserInfo(username);
        Organization org = _orgRepo.findOneByName(namespace);

        UserOrganization userOrg = _userOrgRepo.findOneByOrganizationIdAndUserUsername(org.getId(), user.getUsername());
        if (userOrg != null) {
            if (org.getCreatedBy().getUsername() != user.getUsername()) {
                _userOrgRepo.delete(userOrg);
            } else {
                throw new BadRequestException("Cannot remove creator.");
            }
        }

        // user role에 연결되어 있는 멤버 삭제
        List<Role> deleteMembers = new ArrayList<>();
        _imageService.getImages(namespace).forEach(value -> {
            value.getRole().stream().forEach(v -> {
                if (username.equals(v.getUser().getUsername())) {
                    deleteMembers.add(v);
                }
            });
        });

        _roleRepo.deleteAll(deleteMembers);
    }

    /**
     * 권한 체크
     * @param namespace
     * @return
     */
    public boolean checkAuth(String namespace) {
        Organization org = _orgRepo.findOneByName(namespace);
        _userOrgRepo.findOneByOrganizationIdAndUserUsername(org.getId(), SecurityUtil.getUser());
        User user = _userService.getUser(SecurityUtil.getUser());

        if (!user.getSuperuser() && _userOrgRepo == null) {
            throw new AccessDeniedException("Has not permission");
        }

        return true;
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
