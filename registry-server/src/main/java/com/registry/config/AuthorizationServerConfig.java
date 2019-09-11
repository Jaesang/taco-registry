package com.registry.config;

import com.registry.config.props.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;

/**
 * Created by LEE on 2017. 1. 13..
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    private static String CLIENT_ID = "registry";

    private static String CLIENT_SECRET = "{noop}registry-secret";

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        // oauth client 정보 설정
        clients.inMemory()
            // client id
            .withClient(CLIENT_ID)
                // 사용하고자 하는 client가 인가받은 허가 유형
                .authorizedGrantTypes("password") // "authorization_code", "refresh_token", "implicit"
                // client 허가 받은 인가 설정
                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                // client 제한 범위 설정
                .scopes("read", "write", "trust")
                // resource token 입력 시 resourceId 설정
                .resourceIds(CLIENT_ID)
                // client 계정 암호
                .secret(CLIENT_SECRET)
                // 토큰만료시간
                .accessTokenValiditySeconds(86400);

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
            .tokenStore(tokenStore)
            .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer securityConfigurer) throws Exception {
        securityConfigurer
                .checkTokenAccess("isAuthenticated()");
    }

//    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//    @Autowired
//    private TokenStore tokenStore;
//
//    @Autowired
//    private JwtAccessTokenConverter jwtAccessTokenConverter;
//
//    @Autowired
//    private SecurityProperties securityProperties;
//
//    @Autowired
//    DataSource dataSource;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    @Qualifier("authenticationManagerBean")
//    private AuthenticationManager authenticationManager;
//
//    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
//    | Public Method
//    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
//
//    @Override
//    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.jdbc(dataSource);
//    }
//
//    @Bean
//    public TokenStore tokenStore() {
//        if (tokenStore == null) {
//            tokenStore = new JwtTokenStore(jwtAccessTokenConverter);
//        }
//        return tokenStore;
//    }
//
//    @Bean
//    public DefaultTokenServices tokenServices(final TokenStore tokenStore,
//                                              final ClientDetailsService clientDetailsService) {
//        DefaultTokenServices tokenServices = new DefaultTokenServices();
//        tokenServices.setSupportRefreshToken(true);
//        tokenServices.setTokenStore(tokenStore);
//        tokenServices.setClientDetailsService(clientDetailsService);
//        tokenServices.setAuthenticationManager(this.authenticationManager);
//        return tokenServices;
//    }
//
//    @Bean
//    public JwtAccessTokenConverter jwtAccessTokenConverter() {
//        if (jwtAccessTokenConverter != null) {
//            return jwtAccessTokenConverter;
//        }
//
//        SecurityProperties.JwtProperties jwtProperties = securityProperties.getJwt();
//        KeyPair keyPair = keyPair(jwtProperties, keyStoreKeyFactory(jwtProperties));
//
//        jwtAccessTokenConverter = new JwtAccessTokenConverter();
//        jwtAccessTokenConverter.setKeyPair(keyPair);
//        return jwtAccessTokenConverter;
//    }
//
//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        endpoints.authenticationManager(this.authenticationManager)
//                .accessTokenConverter(jwtAccessTokenConverter())
//                .userDetailsService(this.userDetailsService)
//                .tokenStore(tokenStore());
//    }
//
//    @Override
//    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) {
//        oauthServer.passwordEncoder(this.passwordEncoder).tokenKeyAccess("permitAll()")
//                .checkTokenAccess("isAuthenticated()");
//    }
//
//    private KeyPair keyPair(SecurityProperties.JwtProperties jwtProperties, KeyStoreKeyFactory keyStoreKeyFactory) {
//        return keyStoreKeyFactory.getKeyPair(jwtProperties.getKeyPairAlias(), jwtProperties.getKeyPairPassword().toCharArray());
//    }
//
//    private KeyStoreKeyFactory keyStoreKeyFactory(SecurityProperties.JwtProperties jwtProperties) {
//        return new KeyStoreKeyFactory(jwtProperties.getKeyStore(), jwtProperties.getKeyStorePassword().toCharArray());
//    }
}
