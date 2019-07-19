package com.registry.service;

import com.registry.repository.AbstractEntity;
import com.registry.repository.user.User;
import com.registry.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Created by boozer on 2019. 6. 18
 */
public abstract class AbstractService {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    protected static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

    @Autowired
    private UserService userService;

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

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /**
     * entity 등록시 생성자/수정자 생성시간/수정시간 설정
     * @param param
     * @throws Exception
     */
    protected void setCreatedInfo(AbstractEntity param) {

        User user = userService.getUser(SecurityUtil.getUser());

        // 유저 정보가 존재한다면
        if( user != null ){

            // 등록자/수정자 세팅
            param.setCreatedBy(user);
            param.setUpdatedBy(user);

            // 등록시간/수정시간 세팅
            param.setCreatedDate(LocalDateTime.now());
            param.setUpdatedDate(param.getCreatedDate());
        }
    }

    /**
     * entity 수정시 수정자 수정시간 설정
     * @param param
     * @throws Exception
     */
    protected void setUpdatedInfo(AbstractEntity param) {

        User user = userService.getUser(SecurityUtil.getUser());

        // 유저 정보가 존재한다면
        if( user != null ){

            // 등록자/수정자 세팅
            param.setUpdatedBy(user);

            // 등록시간/수정시간 세팅
            param.setUpdatedDate(LocalDateTime.now());
        }
    }


    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Inner Class
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
}
