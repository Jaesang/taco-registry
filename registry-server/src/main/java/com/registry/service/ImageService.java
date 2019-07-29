package com.registry.service;

import com.registry.constant.LogConstant;
import com.registry.exception.AccessDeniedException;
import com.registry.exception.BadRequestException;
import com.registry.repository.image.BuildRepository;
import com.registry.repository.image.Image;
import com.registry.repository.image.ImageRepository;
import com.registry.repository.image.TagRepository;
import com.registry.repository.organization.Organization;
import com.registry.repository.usage.Log;
import com.registry.repository.usage.LogRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /** Build Repo */
    @Autowired
    private BuildRepository _buildRepo;

    /** Tag Repo */
    @Autowired
    private TagRepository _tagRepo;

    /** Log Repo */
    @Autowired
    private LogRepository _logRepo;

    @Autowired
    private UserService _userService;

    @Autowired
    private OrganizationService _organizationService;

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
    public void create(Image image) throws Exception {
        logger.info("create : {}", image);

        // 권한 체크
        if (image.getIsOrganization()) {
            _organizationService.checkAuth(image.getNamespace());
        }

        if (image.getName().length() > 40) {
            throw new BadRequestException("Be max 40 characters in length");
        }

        Image preImage = _imageRepo.getImage(image.getNamespace(), image.getName());
        if (preImage != null) {
            throw new BadRequestException("Already exists");
        }

        //todo builder에서 image 존재 체크

        Pattern p = Pattern.compile("^[a-z0-9_-]+$");
        Matcher m = p.matcher(image.getName());
        if (!m.find()) {
            throw new BadRequestException("Image names must match [a-z0-9_-]+");
        }
        //todo builder에 image 생성 요청

        User user = _userService.getUser(SecurityUtil.getUser());

        _imageRepo.save(image);

        List<Role> members = new ArrayList<>();
        Role role = new Role();
        role.setImage(image);
        role.setUser(user);
        role.setName("ADMIN");
        role.setIsStarred(false);
        members.add(role);

        if (image.getIsOrganization()) {
            Organization org = _organizationService.getOrg(image.getNamespace());
            org.getUserOrg().stream().forEach(value -> {
                if (!value.getUser().getUsername().equals(user.getUsername())) {
                    Role r = new Role();
                    r.setImage(image);
                    r.setUser(user);
                    r.setName("ADMIN");
                    r.setIsStarred(false);
                    members.add(r);
                }
            });
        }

        _roleRepo.saveAll(members);

        // 로그 등록
        Organization org = _organizationService.getOrg(image.getNamespace());
        Log log = new Log(LogConstant.CREATE_IMAGE);
        log.setPerformer(user);
        if (image.getIsOrganization()) {
            log.setOrganizationId(org.getId());
        } else {
            log.setUsername(SecurityUtil.getUser());
        }
        log.setImageId(image.getId());
        log.setNamespace(image.getNamespace());
        log.setImage(image.getName());
        _logRepo.save(log);
    }

    /**
     * image 삭제
     * @param namespace
     * @param name
     */
    @Transactional
    public void deleteImage(String namespace, String name) {
        checkAuth(namespace, name);

        // image 삭제
        Image image = _imageRepo.getImage(namespace, name);
        image.setDelYn(true);

        _imageRepo.save(image);

        // 로그 등록
        Organization org = _organizationService.getOrg(image.getNamespace());
        User performer = _userService.getUser(SecurityUtil.getUser());
        Log log = new Log(LogConstant.DELETE_IMAGE);
        log.setPerformer(performer);
        log.setOrganizationId(org.getId());
        log.setImageId(image.getId());
        log.setImage(image.getName());
        _logRepo.save(log);
    }

    /**
     * image 조회
     * @param namespace
     * @param name
     * @return
     */
    public Image getImage(String namespace, String name) {
        logger.info("getImage namespace : {}", namespace);
        logger.info("getImage name : {}", name);

        return _imageRepo.getImage(namespace, name);
    }

    /**
     * image 목록 조회
     * @param namespace
     * @return
     */
    public List<Image> getImages(String namespace) {
        logger.info("getImages namespace : {}", namespace);

        return _imageRepo.getImages(namespace);
    }

    /**
     * image 검색
     * @param name
     * @param pageable
     * @return
     */
    public Page<Image> getImagesByContainName(String name, Pageable pageable) {
        logger.info("getImagesByContainName name : {}", name);
        logger.info("getImagesByContainName pageable : {}", pageable);

        return _imageRepo.getImagesByNameContaining(name, pageable);
    }

    /**
     * image 검색
     * @param name
     * @return
     */
    public List<Image> getImagesByContainName(String name) {
        logger.info("getImagesByContainName name : {}", name);

        return _imageRepo.getImagesByNameContaining(name);
    }

    /**
     * image member 목록 조회
     * @param name
     * @return
     */
    public List<Role> getMembers(String namespace, String name) {
        logger.info("getMembers name : {}", name);

        Image image = _imageRepo.getImage(namespace, name);

        return image.getRole();
    }

    /**
     * role 등록
     * @param username
     * @param namespace
     * @param name
     * @param roleName
     * @throws Exception
     */
    @Transactional
    public void updateRole(String username, String namespace, String name, String roleName) throws Exception {
        // 권한 체크
        this.checkAuth(namespace, name);

        Image image = _imageRepo.getImage(namespace, name);

        if (image.getCreatedBy().getUsername().equals(username)) {
            throw new BadRequestException("Namespace owner must always be admin.");
        }

        Role preRole = _roleRepo.getRole(username, image.getId());

        if (preRole != null) {
            preRole.setName(roleName.toUpperCase());
            _roleRepo.save(preRole);
        } else {
            User user = _userService.getUser(username);
            Role role = new Role();
            role.setUser(user);
            role.setImage(image);
            role.setName(roleName.toUpperCase());
            role.setIsStarred(false);

            _roleRepo.save(role);
        }

        // 로그 등록
        Organization org = _organizationService.getOrg(image.getNamespace());
        User performer = _userService.getUser(SecurityUtil.getUser());
        Log log = new Log(LogConstant.CHANGE_IMAGE_PERMISSION);
        log.setPerformer(performer);
        log.setOrganizationId(org.getId());
        log.setImageId(image.getId());
        log.setNamespace(image.getNamespace());
        log.setImage(image.getName());
        log.setRole(roleName.toUpperCase());
        log.setUsername(username);
        _logRepo.save(log);
    }

    /**
     * role 삭제
     * @param username
     * @param namespace
     * @param name
     * @throws Exception
     */
    @Transactional
    public void deleteRole(String username, String namespace, String name) throws Exception {
        // 권한 체크
        this.checkAuth(namespace, name);

        Image image = _imageRepo.getImage(namespace, name);

        if (image.getCreatedBy().getUsername().equals(username)) {
            throw new BadRequestException("Namespace owner must always be admin.");
        }

        Role role = _roleRepo.getRole(username, image.getId());

        _roleRepo.delete(role);

        // 로그 등록
        Organization org = _organizationService.getOrg(image.getNamespace());
        User performer = _userService.getUser(SecurityUtil.getUser());
        Log log = new Log(LogConstant.DELETE_IMAGE_PERMISSION);
        log.setPerformer(performer);
        log.setOrganizationId(org.getId());
        log.setImageId(image.getId());
        log.setNamespace(image.getNamespace());
        log.setImage(image.getName());
        log.setUsername(username);
        _logRepo.save(log);
    }

    /**
     * 공개여부 변경
     * @param namespace
     * @param name
     * @param visibility
     */
    @Transactional
    public Image updateVisibility(String namespace, String name, boolean visibility) {
        // 권한 체크
        this.checkAuth(namespace, name);

        Image image = _imageRepo.getImage(namespace, name);
        image.setIsPublic(visibility);

        _imageRepo.save(image);

        // 로그 등록
        Organization org = _organizationService.getOrg(image.getNamespace());
        User performer = _userService.getUser(SecurityUtil.getUser());
        Log log = new Log(LogConstant.CHANGE_IMAGE_VISIBILITY);
        log.setPerformer(performer);
        log.setOrganizationId(org.getId());
        log.setImageId(image.getId());
        log.setNamespace(image.getNamespace());
        log.setImage(image.getName());
        log.setVisibility(visibility ? "public" : "private");
        _logRepo.save(log);

        return image;
    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /**
     * 권한 체크
     * @param namespace
     * @param name
     * @return
     */
    private boolean checkAuth(String namespace, String name) {
        Image image = _imageRepo.getImage(namespace, name);
        Role role = _roleRepo.getRole(SecurityUtil.getUser(), image.getId());
        User user = _userService.getUser(SecurityUtil.getUser());

        if (!user.getSuperuser() && !"ADMIN".equals(role.getName()) && !"WRITE".equals(role.getName())) {
            throw new AccessDeniedException("Has not permission");
        }

        return true;
    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Inner Class
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
}
