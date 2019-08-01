package com.registry.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * Created by boozer on 2019. 7. 15
 */
public class SecurityUtil {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /**
     * 인증 객체를 반환
     * @return
     * @throws Exception
     */
    public static String getUser() {

        // 인증정보
        Object principal    = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user           = principal instanceof User ? (User) principal : null;
        String anonymous    = principal instanceof String ? (String) principal : null;
        String username     = user != null ? user.getUsername() : anonymous;

        // 유저 ID
//        Long userId = !"anonymousUser".equals(username)
//                ? Long.parseLong(username)
//                : -1;

        // 반환
        return username;
    }

    /**
     * 인증된 유저와 ID가 같은지 체크
     * @param username
     * @return
     */
    public static boolean matchUser(String username) {

        // 반환
        return username != null && getUser() == username;
    }

}
