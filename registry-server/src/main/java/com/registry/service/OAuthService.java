package com.registry.service;

import com.registry.constant.Const;
import com.registry.exception.AccessDeniedException;
import com.registry.exception.InvalidTokenException;
import com.registry.repository.image.Image;
import com.registry.repository.user.Role;
import com.registry.repository.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base32;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by boozer on 2019. 7. 15
 */
@Service
public class OAuthService extends AbstractService {

    protected static final Logger logger = LoggerFactory.getLogger(OAuthService.class);

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Value("${security.oauth2.auth-server-uri}")
    private String authServerUri;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Value("${security.oauth2.keycloak.auth-server-uri}")
    private String keycloakAuthServerUri;

    @Value("${security.oauth2.keycloak.client-id}")
    private String keycloakClientId;

    @Value("${security.oauth2.keycloak.realm}")
    private String keycloakRealm;

    @Value("${config.oauth.jwt.issuer}")
    private String jwtIssuer;

    @Value("${config.oauth.jwt.key}")
    private org.springframework.core.io.Resource jwtKey;

    @Value("${config.oauth.jwt.password}")
    private String jwtPassword;

    @Value("${config.oauth.jwt.key-pair-alias}")
    private String jwtPairAlias;

    @Value("${config.oauth.jwt.key-pair-password}")
    private String jwtPairPassword;

    @Value("${builder.username}")
    private String builderUsername;

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

    public Map getToken(String username, String password) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("client_id", keycloakClientId);
        map.add("grant_type", "password");
        map.add("username", username);
        map.add("password", password);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        // get keycloak token
        String url = MessageFormat.format("{0}/realms/{1}/protocol/openid-connect/token", keycloakAuthServerUri, keycloakRealm);
        RestTemplate restTemplate = new RestTemplate();
        Map result = null;
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity( url, request , Map.class );
            result = response.getBody();
        } catch (Exception e) {
            logger.error("get keycloak token error");
            logger.error(e.getMessage());
        }

        if (result == null || result.get("access_token") == null) {
            throw new AccessDeniedException("Access denied");
        }

        User user = userService.getUser(username);

        // keycloak token parsing
        String jwtToken = (String) result.get("access_token");
        String[] split_string = jwtToken.split("\\.");
        String base64EncodedHeader = split_string[0];
        String base64EncodedBody = split_string[1];
        String base64EncodedSignature = split_string[2];

        logger.debug("============ JWT Header ============");
        String header = new String(Base64.getUrlDecoder().decode(base64EncodedHeader));
        logger.debug("JWT Header : " + header);

        logger.debug("============ JWT Body ============");
        String body = new String(Base64.getUrlDecoder().decode(base64EncodedBody));
        logger.debug("JWT Body : "+body);
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(body);
        String name = (String) obj.get("name");
        String email = (String) obj.get("email");

        boolean isCreate = false;
        if (user == null) {
            // 유저 없을 경우 생성

            isCreate = true;
            user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setName(name);
        } else {
            // 유저 있을 경우 업데이트

            user.setEmail(email);
            user.setName(name);
            user.setPassword(password);
        }
        userService.saveUser(user, "USER", isCreate);

        // get registry app token
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        map= new LinkedMultiValueMap<String, String>();
        map.add("scope", "read");
        map.add("grant_type", "password");
        map.add("username", username);
        map.add("password", password);
        request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        url = authServerUri;
        restTemplate = new RestTemplate();
        result = null;
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity( url, request , Map.class );
            result = response.getBody();
        } catch (Exception e) {
            logger.error("get token error");
            logger.error(e.getMessage());
        }

        if (result == null || result.get("error") != null) {
            throw new AccessDeniedException(result.get("error").toString());
        }

        return result;
    }

    /**
     * get jwt token
     * @param username
     * @param password
     * @param service
     * @param scope
     * @return
     */
    public JSONObject getJWTToken(String username, String password, String service, String scope) {
        logger.info("getJWTToken username : {}", username);
        logger.info("getJWTToken password : {}", password);
        logger.info("getJWTToken service : {}", service);
        logger.info("getJWTToken scope : {}", scope);

        User user = userService.getUser(username);
        // password check
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            if (!builderUsername.equals(username)) {
                throw new InvalidTokenException("unauthorized");
            }
        }

        // load jks file
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(this.jwtKey, this.jwtPassword.toCharArray());
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(jwtPairAlias, jwtPairPassword.toCharArray());

//        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        keyStore.load(jwtKey.getInputStream(), jwtPassword.toCharArray());
//        Key key = keyStore.getKey(jwtPairAlias, jwtPairPassword.toCharArray());
//        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(jwtPairAlias);
//        PublicKey publicKey = certificate.getPublicKey();
//        KeyPair keyPair = new KeyPair(publicKey, (PrivateKey) key);

        JSONArray access = new JSONArray();
        if (scope != null) {
            access = this.getAccess(username, scope);
        }

        // generate jwt token
        int expiresIn = 1 * (60 * 60 * 24);
        Date expiration = new Date(System.currentTimeMillis() + expiresIn * 1000);
        Date now = new Date();
        String jwt = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("kid", this.getKID(keyPair.getPublic()))
                .claim("access", access)
                .setSubject(username)
                .setIssuer(jwtIssuer)
                .setAudience(service)
                .setExpiration(expiration)
                .setIssuedAt(now)
                .setNotBefore(now)
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.RS256, keyPair.getPrivate())
                .compact();

        JSONObject obj = new JSONObject();
        obj.put("token", jwt);
//        obj.put("issued_at", now.toString());
//        obj.put("expires_in", expiresIn);

        logger.info("getJWTToken jwt token : {}", jwt);

        return obj;
    }
    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /**
     * - Take the DER encoded public key which the JWT token was signed against.
     * - Create a SHA256 hash out of it and truncate to 240bits.
     * - Split the result into 12 base32 encoded groups with : as delimiter.
     * @param publicKey
     * @return
     */
    private String getKID(PublicKey publicKey) {
        String result = "";

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] sha256 = digest.digest(publicKey.getEncoded());
            byte[] hash = Arrays.copyOfRange(sha256, 0, 30);

            Base32 base32 = new Base32();
            result = base32.encodeAsString(hash);
            result = result.replaceAll("=", "");

            Set<String> r = new LinkedHashSet<>();
            for(int i = 0; i < result.length(); i += 4) {
                int start = i;
                int end = start + 4;

                r.add(result.substring(start, end));
            }

            result = String.join(":", r);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        logger.info("getKID kid : {}", result);

        return result;
    }

    /**
     * get Access
     * @param scope
     * @return
     */
    private JSONArray getAccess(String username, String scope) {
        JSONArray result = new JSONArray();

        String[] scopes = scope.split(":");
        String type = scopes[0];
        String name = scopes[1];
        String action = scopes[2];

        if (builderUsername.equals(username)) {
            // builder에서 요청할 경우 전부 허용

            JSONObject obj = new JSONObject();
            obj.put("type", type);
            obj.put("name", name);

            Set<String> actions = new LinkedHashSet<>();
            actions.add("*");
            obj.put("actions", actions);

            result.add(obj);
        } else {
            String[] names = name.split("/");
            if (names.length != 2) {
                return result;
            }

            String namespace = names[0];
            String imageName = names[1];

            Image image = this.imageService.getImage(namespace, imageName);
            if (image != null) {
                JSONObject obj = new JSONObject();
                obj.put("type", type);
                obj.put("name", name);

                Set<String> actions = new LinkedHashSet<>();

                if (image.getIsPublic()) {
                    // public image 전부 허용
                    if (action.contains("pull")) {
                        actions.add("pull");
                    }
                    Role role = image.getRole().stream().filter(v -> username.equals(v.getUser().getUsername())).findFirst().orElse(null);
                    if (role != null) {
                        if (action.contains("push") && (Const.Role.WRITE.equals(role.getName()) || Const.Role.ADMIN.equals(role.getName()))) {
                            actions.add("push");
                        }
                    }
                } else {
                    Role role = image.getRole().stream().filter(v -> username.equals(v.getUser().getUsername())).findFirst().orElse(null);
                    if (role != null) {
                        if (action.contains("pull")) {
                            actions.add("pull");
                        }
                        if (action.contains("push") && (Const.Role.WRITE.equals(role.getName()) || Const.Role.ADMIN.equals(role.getName()))) {
                            actions.add("push");
                        }
                    }
                }

                obj.put("actions", actions);

                result.add(obj);
            }
        }

        return result;
    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Inner Class
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
}
