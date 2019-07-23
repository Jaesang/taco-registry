package com.registry.service;

import com.registry.constant.CommonConstant;
import com.registry.repository.common.CodeEntity;
import com.registry.repository.common.CodeRepository;
import com.registry.repository.organization.Organization;
import com.registry.repository.user.*;
import com.registry.util.SecurityUtil;
import com.registry.value.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by boozer on 2019. 7. 15
 */
@Service
public class UserService extends AbstractService {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /** 유저 Repo */
    @Autowired
    private UserRepository _userRepo;

    /** 유저 롤 Repo */
    @Autowired
    private RoleRepository _roleRepo;

    /** 공통 코드 Repo */
    @Autowired
    private CodeRepository _codeRepo;

    /** user org Repo */
    @Autowired
    private UserOrganizationRepository _userOrgRepo;

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
        User user = _userRepo.findById(SecurityUtil.getUser()).orElse(null);

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
        User user = _userRepo.findUserByUsername(username);

        setOrgs(user);

        return user;
    }

    public List<User> getUsers() {
        return _userRepo.findAllByDelYn(false);
    }

    public User getUser(String username) {
        logger.info("getUser username : {}", username);

        return _userRepo.findById(username).orElse(null);
    }

    public List<User> getUsersByContainUsername(String username) {
        logger.info("getUsersByContainUsername username : {}", username);

        return _userRepo.findAllByUsernameContaining(username);
    }

    /**
     * Organizations 목록 삽입
     * @param user
     */
    public void setOrgs(User user) {
        logger.info("setOrgs user : {}", user);

        List<UserOrganization> userOrgs = user.getUserOrg();
        List<Organization> orgs = userOrgs.stream().map(value -> {
           return value.getOrganization();
        }).collect(Collectors.toList());

        user.setOrganizations(orgs);
    }

    /**
     * 유저 삭제
     * @param username
     * @throws Exception
     */
    public void deleteUser(String username) throws Exception {
        logger.info("deleteUser username : {}", username);

        User user = _userRepo.findUserByUsername(username);
        user.setDelYn(true);

        saveUser(user, null, false);
    }

    /**
     *  유저 저장
     */
    @Transactional
    public User saveUser(User user, String rolename, boolean isCreate) throws Exception {
        logger.info("saveUser user : {}", user);
        logger.info("saveUser rolename : {}", rolename);
        logger.info("saveUser isCreate : {}", isCreate);

        String password = isCreate ? UUID.randomUUID().toString() : user.getPassword();
        if(password != null) {
            // 패스워드 인코더
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // 패스워드 인코딩
            String encodePassword   = passwordEncoder.encode(password);
            user.setPassword(encodePassword);
        }

        if(isCreate){
            // 신규 등록

            user.setDelYn(false);
            user.setName(user.getUsername());
            user.setSuperuser(false);
            user.setEnabled(true);
            if (rolename.equals("ADMIN")) {
                user.setSuperuser(true);
            }

            user =  _userRepo.save(user);
        }else{
            User preUser;
            if (user.getUsername() != null) {
                preUser = _userRepo.findUserByUsername(user.getUsername());
            } else {
                preUser = _userRepo.findById(SecurityUtil.getUser()).orElse(null);
            }

            if (rolename != null) {
                if (rolename.equals("ADMIN")) {
                    user.setSuperuser(true);
                } else {
                    user.setSuperuser(false);
                }
            }

            if(user.getPassword() != null) preUser.setPassword(user.getPassword());
            if(user.getName() != null) preUser.setName(user.getName());
            if(user.getEmail() != null) preUser.setEmail(user.getEmail());
            if(user.getEnabled() != null) preUser.setEnabled(user.getEnabled());
            _userRepo.save(preUser);

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

    public List<User> findMembers(String username, String namespace) throws Exception {
        logger.info("findMembers username : {}", username);
        logger.info("findMembers namespace : {}", namespace);

        List<User> users = _userRepo.findAllByUsernameContaining(username);
        List<User> results = users.stream().filter(value -> {
            boolean exist = value.getUserOrg().stream().anyMatch(v -> {
               return namespace.equals(v.getOrganization().getName());
            });

            return !exist;
        }).collect(Collectors.toList());
        return results;
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
