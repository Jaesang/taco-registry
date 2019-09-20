package com.registry.service;

import com.registry.constant.Const;
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
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private ImageRepository imageRepo;

    /** Role Repo */
    @Autowired
    private RoleRepository roleRepo;

    /** Build Repo */
    @Autowired
    private BuildRepository buildRepo;

    /** Tag Repo */
    @Autowired
    private TagRepository tagRepo;

    /** Log Repo */
    @Autowired
    private LogRepository logRepo;

    @Autowired
    private UsageLogService logService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

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
     * image 등록
     */
    @Transactional
    public void create(Image image) throws Exception {
        logger.info("create : {}", image);

        // 권한 체크
        if (image.getIsOrganization()) {
            organizationService.checkAuth(image.getNamespace());
        }

        if (image.getName().length() > 40) {
            throw new BadRequestException("Be max 40 characters in length");
        }

        // sync with builder
        externalService.syncWithBuilder(image.getNamespace(), image.getName());

        Image preImage = imageRepo.getImage(image.getNamespace(), image.getName());
        if (preImage != null) {
            throw new BadRequestException("Already exists");
        }

        Pattern p = Pattern.compile("^[a-z0-9_-]+$");
        Matcher m = p.matcher(image.getName());
        if (!m.find()) {
            throw new BadRequestException("Image names must match [a-z0-9_-]+");
        }

        User user = userService.getUser(SecurityUtil.getUser());

        imageRepo.save(image);

        List<Role> members = new ArrayList<>();
        Role role = new Role();
        role.setImage(image);
        role.setUser(user);
        role.setName(Const.Role.ADMIN);
        role.setIsStarred(false);
        members.add(role);

        if (image.getIsOrganization()) {
            Organization org = organizationService.getOrg(image.getNamespace());
            org.getUserOrg().stream().forEach(value -> {
                if (!value.getUser().getUsername().equals(user.getUsername())) {
                    Role r = new Role();
                    r.setImage(image);
                    r.setUser(value.getUser());
                    r.setName(Const.Role.ADMIN);
                    r.setIsStarred(false);
                    members.add(r);
                }
            });
        }

        roleRepo.saveAll(members);

        // 로그 등록
        Organization org = organizationService.getOrg(image.getNamespace());
        Log log = new Log();
        log.setKind(Const.UsageLog.CREATE_IMAGE);
        if (image.getIsOrganization()) {
            log.setOrganizationId(org.getId());
        } else {
            log.setUsername(SecurityUtil.getUser());
        }
        log.setImageId(image.getId());
        log.setNamespace(image.getNamespace());
        log.setImage(image.getName());
        logService.create(log);
    }

    /**
     * image 수정
     */
    @Transactional
    public void update(Image image) throws Exception {
        logger.info("update : {}", image);

        // 권한 체크
        checkAuth(image.getNamespace(), image.getName());

        Image preImage = imageRepo.getImage(image.getNamespace(), image.getName());
        if (preImage == null) {
            throw new BadRequestException("No image");
        }

        preImage.setDescription(image.getDescription());

        imageRepo.save(preImage);

        // builder image 삭제 요청
        externalService.deleteImage(preImage);

        // 로그 등록
        Organization org = organizationService.getOrg(preImage.getNamespace());
        Log log = new Log();
        log.setKind(Const.UsageLog.SET_IMAGE_DESCRIPTION);
        if (preImage.getIsOrganization()) {
            log.setOrganizationId(org.getId());
        } else {
            log.setUsername(SecurityUtil.getUser());
        }
        log.setImageId(preImage.getId());
        log.setNamespace(preImage.getNamespace());
        log.setImage(preImage.getName());
        logService.create(log);
    }

    /**
     * image 삭제
     * @param namespace
     * @param name
     */
    @Transactional
    public void deleteImage(String namespace, String name) {
        checkAuth(namespace, name);

        //todo builder에서 이미지 삭제

        // image 삭제
        Image image = imageRepo.getImage(namespace, name);
        image.setDelYn(true);

        imageRepo.save(image);

        // 로그 등록
        Organization org = organizationService.getOrg(image.getNamespace());
        Log log = new Log();
        log.setKind(Const.UsageLog.DELETE_IMAGE);
        if (image.getIsOrganization()) {
            log.setOrganizationId(org.getId());
        } else {
            log.setUsername(namespace);
        }
        log.setImageId(image.getId());
        log.setImage(image.getName());
        logService.create(log);
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

        return imageRepo.getImage(namespace, name);
    }

    /**
     * image 목록 전체 조회
     * @param namespace
     * @return
     */
    public List<Image> getImages(String namespace) {
        logger.info("getImages namespace : {}", namespace);

        return imageRepo.getImages(namespace);
    }

    /**
     * image 목록 조회
     * @param namespace
     * @return
     */
    public Page<Image> getImages(String namespace, Pageable pageable) {
        logger.info("getImages namespace : {}", namespace);
        logger.info("getImages pageable : {}", pageable);

        return imageRepo.getImages(namespace, pageable);
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

        return imageRepo.getImagesByNameContaining(name, pageable);
    }

    /**
     * image 검색
     * @param name
     * @return
     */
    public List<Image> getImagesByContainName(String name) {
        logger.info("getImagesByContainName name : {}", name);

        return imageRepo.getImagesByNameContaining(name);
    }

    /**
     * image member 목록 조회
     * @param name
     * @return
     */
    public List<Role> getMembers(String namespace, String name) {
        logger.info("getMembers name : {}", name);

        Image image = imageRepo.getImage(namespace, name);

        return image.getRole();
    }

    /**
     * image member 목록 조회
     * @param name
     * @return
     */
    public Page<Role> getMembers(String namespace, String name, Pageable pageable) {
        logger.info("getMembers name : {}", name);

        Image image = imageRepo.getImage(namespace, name);

        return roleRepo.getRoles(image.getId(), pageable);
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

        Image image = imageRepo.getImage(namespace, name);

        if (image.getCreatedBy().getUsername().equals(username)) {
            throw new BadRequestException("Namespace owner must always be admin.");
        }

        Role preRole = roleRepo.getRole(username, image.getId());

        if (preRole != null) {
            preRole.setName(roleName.toUpperCase());
            roleRepo.save(preRole);
        } else {
            User user = userService.getUser(username);
            Role role = new Role();
            role.setUser(user);
            role.setImage(image);
            role.setName(roleName.toUpperCase());
            role.setIsStarred(false);

            roleRepo.save(role);
        }

        // 로그 등록
        Organization org = organizationService.getOrg(image.getNamespace());
        Log log = new Log();
        log.setKind(Const.UsageLog.CHANGE_IMAGE_PERMISSION);
        if (image.getIsOrganization()) {
            log.setOrganizationId(org.getId());
        } else {
            log.setUsername(namespace);
        }
        log.setImageId(image.getId());
        log.setNamespace(image.getNamespace());
        log.setImage(image.getName());
        log.setRole(roleName.toUpperCase());
        log.setMember(username);
        logService.create(log);
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

        Image image = imageRepo.getImage(namespace, name);

        if (image.getCreatedBy().getUsername().equals(username)) {
            throw new BadRequestException("Namespace owner must always be admin.");
        }

        Role role = roleRepo.getRole(username, image.getId());

        roleRepo.delete(role);

        // 로그 등록
        Organization org = organizationService.getOrg(image.getNamespace());
        Log log = new Log();
        log.setKind(Const.UsageLog.DELETE_IMAGE_PERMISSION);
        if (image.getIsOrganization()) {
            log.setOrganizationId(org.getId());
        } else {
            log.setUsername(namespace);
        }
        log.setImageId(image.getId());
        log.setNamespace(image.getNamespace());
        log.setImage(image.getName());
        log.setMember(username);
        logService.create(log);
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

        Image image = imageRepo.getImage(namespace, name);
        image.setIsPublic(visibility);

        imageRepo.save(image);

        // 로그 등록
        Organization org = organizationService.getOrg(image.getNamespace());
        Log log = new Log();
        log.setKind(Const.UsageLog.CHANGE_IMAGE_VISIBILITY);
        if (image.getIsOrganization()) {
            log.setOrganizationId(org.getId());
        } else {
            log.setUsername(namespace);
        }
        log.setImageId(image.getId());
        log.setNamespace(image.getNamespace());
        log.setImage(image.getName());
        log.setVisibility(visibility ? "public" : "private");
        logService.create(log);

        return image;
    }

    /**
     * 날짜별 로그 개수 조회
     * @param namespace
     * @param name
     * @return
     */
    public List<Map<String, Object>> getStats(String namespace, String name) {
        Image image = imageRepo.getImage(namespace, name);

        LocalDate now = LocalDate.now();
        LocalDate startDate = LocalDate.of(now.getYear(), now.getMonthValue() - 2, 1);
        LocalDate createdDate = image.getCreatedDate().toLocalDate();
        if (startDate.compareTo(createdDate) < 0) {
            // 생성일이 3개월이 안될 경우

            startDate = createdDate;
        }

        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> stats = logRepo.getStats(image.getId(), LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(now.minusDays(1), LocalTime.MAX));

        if (stats.size() > 0) {
            DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String formattedCurrentDate = startDate.format(fm);
            String formattedNowDate = now.format(fm);

            int index = 0;
            // 전날기준 데이터까지 취합
            while (!formattedCurrentDate.equals(formattedNowDate)) {
                LocalDate currentDate = startDate.plusDays(index);

                Map<String, Object> stat = new HashedMap();
                stat.put("date", formattedCurrentDate);
                String curr = formattedCurrentDate;

                // 날짜에 해당하는 데이터 filter
                Map<String, Object> obj = stats.stream().filter(value -> curr.equals(value.get("date"))).findFirst().orElse(null);
                if (obj != null) {
                    stat.put("count", obj.get("count"));
                } else {
                    stat.put("count", 0l);
                }

                result.add(stat);

                formattedCurrentDate = currentDate.format(fm);
                index++;
            }
        }

        return result;
    }

    /**
     * 총 log 개수 조회
     * @param namespace
     * @param name
     * @return
     */
    public Long getPopularityCount(String namespace, String name) {
        Image image = imageRepo.getImage(namespace, name);

        return logRepo.getStatsCount(image.getId());
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
        Image image = imageRepo.getImage(namespace, name);
        Role role = roleRepo.getRole(SecurityUtil.getUser(), image.getId());
        User user = userService.getUser(SecurityUtil.getUser());

        if (!user.getSuperuser() && !Const.Role.ADMIN.equals(role.getName()) && !Const.Role.WRITE.equals(role.getName())) {
            throw new AccessDeniedException("Has not permission");
        }

        return true;
    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Inner Class
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
}
