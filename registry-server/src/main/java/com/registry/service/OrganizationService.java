package com.registry.service;

import com.registry.constant.Const;
import com.registry.exception.AccessDeniedException;
import com.registry.exception.BadRequestException;
import com.registry.repository.organization.Organization;
import com.registry.repository.organization.OrganizationRepository;
import com.registry.repository.usage.Log;
import com.registry.repository.user.*;
import com.registry.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    protected static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /** Org Repo */
    @Autowired
    private OrganizationRepository orgRepo;

    /** Org Repo */
    @Autowired
    private UserOrganizationRepository userOrgRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UsageLogService logService;

    @Autowired
    private ExternalAPIService externalService;

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
        logger.info("create name : {}", org.getName());

        User user = userService.getUser(SecurityUtil.getUser());
        if (!user.getSuperuser()) {
            throw new AccessDeniedException("Organizations can only be created by administrators.");
        }

        if (org.getName().length() < 2 || org.getName().length() > 40) {
            throw new BadRequestException("Be at least 2 characters in length and max 40 characters in length");
        }

        User preUser = userService.getUser(org.getName());
        Organization preOrg = orgRepo.getOrganization(org.getName());
        if (preUser != null || preOrg != null) {
            throw new BadRequestException("A user or organization with this name already exists");
        }

        Pattern p = Pattern.compile("^[a-z0-9_-]+$");
        Matcher m = p.matcher(org.getName());
        if (m.find()) {
            user = new User();
            user.setUsername(SecurityUtil.getUser());

            org.setCreatedBy(user);
            org.setUpdatedBy(user);
            orgRepo.save(org);


            UserOrganization userOrganization = new UserOrganization();
            userOrganization.setUser(user);
            userOrganization.setOrganization(org);
            userOrgRepo.save(userOrganization);
        } else {
            throw new BadRequestException("Oraganization names must match [a-z0-9_-]+");
        }
    }

    /**
     * organization 삭제
     * @param name
     */
    @Transactional
    public void deleteOrg(String name) {
        logger.info("deleteOrg name : {}", name);

        // 권한 체크
        checkAuth(name);

        Organization org = orgRepo.getOrganization(name);
        org.setDelYn(true);

        User user = new User();
        user.setUsername(SecurityUtil.getUser());
        org.setUpdatedBy(user);

        orgRepo.save(org);
    }

    /**
     * organization 상세 조회
     * @param name
     * @return
     */
    public Organization getOrg(String name) {
        logger.info("getOrg name : {}", name);

        return orgRepo.getOrganization(name);
    }

    /**
     * organization 검색
     * @param name
     * @return
     */
    public List<Organization> getOrgsByContainName(String name) {
        logger.info("getOrgsByContainName name : {}", name);

        return orgRepo.getOrganizationsByNameContaining(name);
    }

    /**
     * member 조회
     * @param orgName
     * @return
     */
    public List<UserOrganization> getMembers(String orgName) {
        logger.info("getMembers userorgNamename : {}", orgName);

        Organization org = getOrg(orgName);

        return userOrgRepo.getUserOrgs(org.getId());
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

        User user = userService.getUserInfo(username);
        Organization org = orgRepo.getOrganization(namespace);

        if (userOrgRepo.getUserOrg(org.getId(), user.getUsername()) == null) {
            UserOrganization userOrg = new UserOrganization();
            userOrg.setUser(user);
            userOrg.setOrganization(org);

            userOrgRepo.save(userOrg);

            // user role에 멤버 추가
            List<Role> addMembers = new ArrayList<>();
            imageService.getImages(namespace).forEach(value -> {
                List<Role> roles = value.getRole().stream().filter(v -> v.getUser().getUsername().equals(username)).collect(Collectors.toList());
                if (roles.size() == 0) {
                    Role role = new Role();
                    role.setUser(user);
                    role.setImage(value);
                    role.setName(Const.Role.ADMIN);
                    role.setIsStarred(false);

                    addMembers.add(role);
                }
            });

            roleRepo.saveAll(addMembers);

            // 로그 등록
            Log log = new Log();
            log.setOrganizationId(org.getId());
            log.setMember(username);
            log.setKind(Const.UsageLog.ORG_ADD_MEMBER);
            logService.create(log);
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

        User user = userService.getUserInfo(username);
        Organization org = orgRepo.getOrganization(namespace);

        UserOrganization userOrg = userOrgRepo.getUserOrg(org.getId(), user.getUsername());
        if (userOrg != null) {
            if (org.getCreatedBy().getUsername() != user.getUsername()) {
                userOrgRepo.delete(userOrg);
            } else {
                throw new BadRequestException("Cannot remove creator.");
            }
        }

        // user role에 연결되어 있는 멤버 삭제
        List<Role> deleteMembers = new ArrayList<>();
        imageService.getImages(namespace).forEach(value -> {
            value.getRole().stream().forEach(v -> {
                if (username.equals(v.getUser().getUsername())) {
                    deleteMembers.add(v);
                }
            });
        });

        roleRepo.deleteAll(deleteMembers);

        // 로그 등록
        Log log = new Log();
        log.setKind(Const.UsageLog.ORG_REMOVE_MEMBER);
        log.setOrganizationId(org.getId());
        log.setMember(username);
        logService.create(log);
    }

    /**
     * 권한 체크
     * @param namespace
     * @return
     */
    public boolean checkAuth(String namespace) {
        Organization org = orgRepo.getOrganization(namespace);
        UserOrganization userOrg = userOrgRepo.getUserOrg(org.getId(), SecurityUtil.getUser());
        User user = userService.getUser(SecurityUtil.getUser());

        if (!user.getSuperuser() && userOrg == null) {
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
