package com.registry.service;

import com.registry.constant.Const;
import com.registry.exception.BadRequestException;
import com.registry.repository.image.Image;
import com.registry.repository.organization.Organization;
import com.registry.repository.usage.Log;
import com.registry.repository.user.*;
import com.registry.util.SecurityUtil;
import com.registry.value.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by boozer on 2019. 7. 15
 */
@Service
public class UserService extends AbstractService {

    protected static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /** 유저 Repo */
    @Autowired
    private UserRepository userRepo;

    /** 유저 롤 Repo */
    @Autowired
    private RoleRepository roleRepo;

    /** usage log Repo */
    @Autowired
    private UsageLogService logService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private OrganizationService organizationService;

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
     * 로그인 사용자정보를 반환한다.
     * @return
     * @throws Exception
     */
    public User getLoginUser() throws Exception {

        // 반환결과
        Result result = new Result();

        // 사용자정보 조회
        User user = userRepo.findById(SecurityUtil.getUser()).orElse(null);

        setOrgs(user);

        return user;
    }


    /**
     * 사용자정보를 조회한다.
     * @param username 유저 ID
     * @return
     * @throws Exception
     */
    public User getUserInfo(String username) {

        logger.info("getUserInfo username : {}", username);

        // 사용자정보 조회
        User user = userRepo.getUser(username);

        setOrgs(user);

        return user;
    }

    public List<User> getUsers() {
        return userRepo.getUsers();
    }

    public User getUser(String username) {
        logger.info("getUser username : {}", username);

        return userRepo.findById(username).orElse(null);
    }

    /**
     * 유저 검색
     * @param username
     * @return
     */
    public List<User> getUsersByContainUsername(String username) {
        logger.info("getUsersByContainUsername username : {}", username);

        return userRepo.getUserByUsernameContaining(username);
    }

    /**
     * 유저 삭제
     * @param username
     * @throws Exception
     */
    public void deleteUser(String username) throws Exception {
        logger.info("deleteUser username : {}", username);

        User user = userRepo.getUser(username);
        user.setDelYn(true);

        saveUser(user, null, false);
    }

    /**
     * 유저 저장
     */
    @Transactional
    public User saveUser(User user, String rolename, boolean isCreate) throws Exception {
        logger.info("saveUser user : {}", user);
        logger.info("saveUser rolename : {}", rolename);
        logger.info("saveUser isCreate : {}", isCreate);

        String password = isCreate ? this.passwordGenerate() : user.getPassword();
        if(password != null) {
            // 패스워드 인코더
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // 패스워드 인코딩
            String encodePassword   = passwordEncoder.encode(password);
            user.setPassword(encodePassword);
        }

        if(isCreate){
            // 신규 등록

            User preUser = userRepo.getUser(user.getUsername());
            Organization preOrg = organizationService.getOrg(user.getUsername());
            if (preUser != null || preOrg != null) {
                throw new BadRequestException("A user or organization with this name already exists");
            }

            user.setDelYn(false);
            user.setName(user.getUsername());
            user.setSuperuser(false);
            user.setEnabled(true);
            if (rolename.equals(Const.Role.ADMIN)) {
                user.setSuperuser(true);
            }
            user.setCreatedBy(user);
            user.setUpdatedBy(user);

            user = userRepo.save(user);
        }else{
            User preUser;
            if (user.getUsername() != null) {
                preUser = userRepo.getUser(user.getUsername());
            } else {
                preUser = userRepo.findById(SecurityUtil.getUser()).orElse(null);
            }

            if (rolename != null) {
                if (rolename.equals(Const.Role.ADMIN)) {
                    user.setSuperuser(true);
                } else {
                    user.setSuperuser(false);
                }
            }

            if(user.getPassword() != null) {
                preUser.setPassword(user.getPassword());

                // 로그 등록
                Log log = new Log();
                log.setKind(Const.UsageLog.ACCOUNT_CHANGE_PASSWORD);
                log.setMember(SecurityUtil.getUser());
                logService.create(log);
            }
            if(user.getName() != null) preUser.setName(user.getName());
            if(user.getEmail() != null) preUser.setEmail(user.getEmail());
            if(user.getEnabled() != null) preUser.setEnabled(user.getEnabled());

            preUser.setUpdatedBy(user);

            userRepo.save(preUser);

            user = preUser;
        }

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(password);

        return newUser;

    }

    /**
     * password 비교
     * @param password
     * @return
     * @throws Exception
     */
    public boolean passwordVerify(String password) throws Exception {
        logger.info("passwordVerify password : {}", password);

        User user = this.getLoginUser();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String p = user.getPassword();
        return bCryptPasswordEncoder.matches(password, p);
    }

    /**
     * 추가 가능한 member 목록 조회
     * @param username
     * @param namespace
     * @return
     * @throws Exception
     */
    public List<User> findMembers(String username, String namespace) throws Exception {
        logger.info("findMembers username : {}", username);
        logger.info("findMembers namespace : {}", namespace);

        List<User> users = userRepo.getUserByUsernameContaining(username);
//        List<User> results = users.stream().filter(value -> {
//            boolean exist = value.getUserOrg().stream().anyMatch(v -> {
//               return namespace.equals(v.getOrganization().getName());
//            });
//
//            return !exist;
//        }).collect(Collectors.toList());
        return users;
    }

    /**
     * 즐겨찾기 등록 / 삭제
     * @param namespace
     * @param imageName
     * @throws Exception
     */
    public void updateStarred(String namespace, String imageName, boolean starred) throws Exception {
        logger.info("updateStarred namespace : {}", namespace);
        logger.info("updateStarred imageName : {}", imageName);
        logger.info("updateStarred starred : {}", starred);

        Image image = imageService.getImage(namespace, imageName);
        Role role = roleRepo.getRole(SecurityUtil.getUser(), image.getId());
        role.setIsStarred(starred);

        roleRepo.save(role);
    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /**
     * Organizations 목록 삽입
     * @param user
     */
    private void setOrgs(User user) {
        logger.info("setOrgs user : {}", user);

        List<UserOrganization> userOrgs = user.getUserOrg();
        List<Organization> orgs = userOrgs.stream().map(value -> {
            return value.getOrganization();
        }).collect(Collectors.toList());

        user.setOrganizations(orgs);
    }

    /**
     * 임시 비밀번호 생성
     * @return
     */
    private String passwordGenerate() {
        // 6 digit
        Random generator = new Random();
        int i = generator.nextInt(1000000) % 1000000;

        DecimalFormat f = new DecimalFormat("000000");
        return f.format(i);
    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Inner Class
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
}
