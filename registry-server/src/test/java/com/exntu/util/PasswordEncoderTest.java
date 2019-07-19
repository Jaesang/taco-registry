package com.exntu.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by boozer on 2019. 6. 18
 */
public class PasswordEncoderTest {

    public static void main(String[] args) throws Exception {

        // 패스워드 인코더
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 패스워드
//        String password         = "test1234!";
        String password         = "password";
        System.out.println("패스워드: "+ password);

        // 패스워드 인코딩
        String encodePassword   = passwordEncoder.encode(password);
        System.out.println("인코딩 패스워드: "+ encodePassword);

        // 패스워드 확인
        boolean isMatche        = passwordEncoder.matches(password, encodePassword);
        System.out.println("인코딩 확인: "+ isMatche);
    }
}
