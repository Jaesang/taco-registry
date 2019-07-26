package com.registry.util;

import com.registry.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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

    public static String getIP() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // *EDIT*
                    if (addr instanceof Inet6Address) continue;

                    ip = addr.getHostAddress();
                    System.out.println(iface.getDisplayName() + " " + ip);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        return ip;
    }
}
