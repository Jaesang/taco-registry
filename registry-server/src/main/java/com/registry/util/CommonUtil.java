package com.registry.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by boozer on 2019. 7. 15
 */
public class CommonUtil {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /**
     * 유저 IP Address
     * @return
     */
    public static String getIP() {
        String ip = "";
        try {
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            ip = req.getHeader("X-FORWARDED-FOR");

            if (ip == null) {
                ip = req.getHeader("Proxy-Client-IP");
            }

            if (ip == null) {
                ip = req.getHeader("WL-Proxy-Client-IP");
            }

            if (ip == null) {
                ip = req.getHeader("HTTP_CLIENT_IP");
            }

            if (ip == null) {
                ip = req.getHeader("HTTP_X_FORWARDED_FOR");
            }

            if (ip == null) {
                ip = req.getRemoteAddr();
            }

        } catch (Exception e) {

        }

        return ip;
    }
}
