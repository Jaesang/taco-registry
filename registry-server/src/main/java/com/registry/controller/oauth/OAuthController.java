package com.registry.controller.oauth;

import com.registry.constant.ApplicationConstant;
import com.registry.constant.CommonConstant;
import com.registry.constant.Path;
import com.registry.exception.AccessDeniedException;
import com.registry.util.RestApiUtil;
import com.registry.value.common.Result;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javassist.bytecode.ByteArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Created by boozer on 2019. 7. 15
 */
@RestController
public class OAuthController {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /** 토큰 서비스 */
    @Resource(name="tokenServices")
    private ConsumerTokenServices tokenServices;

    @Autowired
    private ApplicationConstant appConst;

    @Autowired
    private RestApiUtil restApiUtil;

    @Value("${config.oauth.jwt.key}")
    private org.springframework.core.io.Resource jwtKey;

    @Value("${config.oauth.jwt.password}")
    private String jwtPassword;

    @Value("${config.oauth.jwt.key-pair-alias}")
    private String jwtPairAlias;

    @Value("${config.oauth.jwt.key-pair-password}")
    private String jwtPairPassword;

    @Value("${config.oauth.jwt.private-key}")
    private String privateKey;

    @Value("${config.oauth.jwt.cert}")
    private String certKey;

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

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping(Path.OAUTH_REVOKE)
    @ApiOperation(
            value = "revokeToken",
            notes = "토큰제거"
    )
    public Object revokeToken(
            // 토큰
            @ApiParam(
                    defaultValue="bearer ",
                    value ="토큰",
                    required = true
            )
            @RequestHeader(name = "Authorization") String authorization ) throws Exception {

        // 토큰
        String token = authorization.substring(authorization.indexOf(" ")+1, authorization.length());

        // 토큰제거
        boolean isDelete = tokenServices.revokeToken(token);

        // 결과
        Result result = new Result();
        result.setData(isDelete);
        result.setCode(CommonConstant.CommonCode.SUCCESS);
        result.setMessage(CommonConstant.CommonMessage.SUCCESS);
        return result;
    }

    @PostMapping(Path.OAUTH_TOKEN)
    @ApiOperation(value = "dummy token", notes = "dummy token API")
    protected Object getDummyToken(
            @ApiParam(
                defaultValue = "Basic cmVnaXN0cnk6cmVnaXN0cnktc2VjcmV0",
                value = "",
                required = true) @RequestHeader(name = "Authorization") String authorization,
           @ApiParam(
                   defaultValue = "password",
                   value = "grant type",
                   required = true
           ) @RequestParam(name = "grant_type") String grantType,
           @ApiParam(
                   defaultValue = "read",
                   value = "scope",
                   required = true
           ) @RequestParam(name = "scope") String scope,
           @ApiParam(
                   defaultValue = "admin",
                   value = "username",
                   required = true
           ) @RequestParam(name = "username") String username,
           @ApiParam(
                   defaultValue = "password",
                   value = "password",
                   required = true
           ) @RequestParam(name = "password") String password ) throws Exception {
        String params = "?grant_type="+grantType+"&scope="+scope+"&username="+username+"&password="+password;
        LinkedHashMap result = (LinkedHashMap) restApiUtil.excute("http://localhost:8080/oauth/token"+params, HttpMethod.POST, null, Object.class, RestApiUtil.MEDIA_TYPE_JSON, authorization);

        if (result.get("error") != null) {
            throw new AccessDeniedException(result.get("error").toString());
        }
        return result;
    }

    @GetMapping(Path.OAUTH_TOKEN)
    @ApiOperation(value = "get token", notes = "get token API")
    protected Object getToken(
            @ApiParam(
                    defaultValue = "Basic YWRtaW46cGFzc3dvcmQ=",
                    value = "",
                    required = true) @RequestHeader(name = "Authorization") String authorization
            ) throws Exception {

        int index = authorization.toLowerCase().indexOf("basic ");

        LinkedHashMap result = null;

        if (index == 0) {
            String userPassString = authorization.substring(6);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decodedBytes = decoder.decode(userPassString.getBytes());
            String decodeString = new String(decodedBytes);

            String[] userPass = decodeString.split(":");
            String username = userPass[0];
            String password = userPass[1];
            String params = "?grant_type=password&scope=read&username="+username+"&password="+password;
            authorization = "Basic Y2xpZW50SWQ6c2VjcmV0";
            result = (LinkedHashMap) restApiUtil.excute("http://localhost:9000/oauth/token"+params, HttpMethod.POST, null, Object.class, RestApiUtil.MEDIA_TYPE_JSON, authorization);
        }

//       {
//"access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjgxMDQ1MzgsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6ImQyNTYzZTJmLTBhZjAtNDU4MC1iMzg0LWNlNDYzYzdlYjkxNSIsImNsaWVudF9pZCI6ImNsaWVudElkIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl19.NJbjOcgcIUKGO4K13POAUVc60eSOGuQHhyPjdRGwQVK69c7qEOE9YS2tCtYcalW-PXp81OQ5geSuf8LE9V4uYgUWcDiePbDsMuVheo6srQWQAsib4_m1iEGPGXoxnIf-iz3bmwMwrCAJwkj-yT6v1LaDxKdgY3Yre8KoBmB5iv8ybvrAQN3j9RlmaLi2p82iWZSXHr7IXxTB1KUk59dMDVJgKqRj_gJFVI2ssNqAVIEnUFj-qG_QqmuM2ykgf8bViejK6ylJYF7bd14ZUA5xHUSnr0-VlcN6sfS8tDWbXNkjqwi1G5f2el8ViYoHsIkEf5JwtS199lFsOwN-2yABuA",
//"token_type": "bearer",
//"refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdLCJhdGkiOiJkMjU2M2UyZi0wYWYwLTQ1ODAtYjM4NC1jZTQ2M2M3ZWI5MTUiLCJleHAiOjE1NzA2OTYyMzgsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiJjNDlkYTM4MS1iODk1LTQzMzEtYmY3YS05M2Q5NWE3MzcxY2EiLCJjbGllbnRfaWQiOiJjbGllbnRJZCJ9.syiStxJeMJ7qN02GB4Tuunjd59FLmX7pBy3pV2oi6SDN0wXzp6XFkWDDz_dyxQ4CBMNArCT2W_WVSeZLHEPOLdmben2jZpBP6UYrRmvCRrJKhXGQAEdTN-QT8NC8Hbkmn8e0b761PqGajLhSf45oBNLe40EVmPng-iNTKRc8UsnMGfxEKSpLIZTtay8FEpVwR8D5FP9OBKEEKlIJSkDjMS3N-gbPWI-kyiewOxXzt_wbqr5HpOeI7phz0ccL_uiWrH8A7ET3JmK2p8EXFV0IDoOFarl8BauXy_yB46cTKwYGbqrMhR5eLfWTT4u7rujeevzJk_mBQuhiRVDAno1qOw",
//"expires_in": 299,
//"scope": "read write",
//"jti": "d2563e2f-0af0-4580-b384-ce463c7eb915"
//}

        if (result == null || result.get("error") != null) {
            throw new AccessDeniedException(result.get("error").toString());
        }

        String json = "{\"issued_at\": \"2016-04-27T15:08:10.066891Z\", \"token\": \"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1YyI6WyJNSUlGUGpDQ0F5YWdBd0lCQWdJQkJEQU5CZ2txaGtpRzl3MEJBUXNGQURDQnBERUxNQWtHQTFVRUJoTUNVbFV4XG5EekFOQmdOVkJBZ01CazF2YzJOdmR6RVBNQTBHQTFVRUJ3d0dUVzl6WTI5M01SY3dGUVlEVlFRS0RBNU9aWFJRXG5iMnhwWTJVZ1NXNW1iekVVTUJJR0ExVUVDd3dMUkdWMlpXeHZjRzFsYm5ReEdqQVlCZ05WQkFNTUVVNWxkRkJ2XG5iR2xqWlNCSmJtWnZJRU5CTVNnd0pnWUpLb1pJaHZjTkFRa0JGaGx6ZFhCd2IzSjBRRzVsZEhCdmJHbGpaV2x1XG5abTh1WTI5dE1CNFhEVEUyTURReU1UQTNOVE15TVZvWERURTVNREV4TmpBM05UTXlNVm93YlRFTE1Ba0dBMVVFXG5CaE1DVWxVeER6QU5CZ05WQkFnTUJrMXZjMk52ZHpFUE1BMEdBMVVFQnd3R1RXOXpZMjkzTVJjd0ZRWURWUVFLXG5EQTVPWlhSUWIyeHBZMlVnU1c1bWJ6RWpNQ0VHQTFVRUF3d2FUbVYwVUc5c2FXTmxJRWx1Wm04Z1JHVjJaV3h2XG5jRzFsYm5Rd2dnRWlNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0SUJEd0F3Z2dFS0FvSUJBUUM3T0NDZFBVTTBoUXhRXG5XOHM4SEJRdHUzVElmQ2tPSzA1M3VGTytpWUdnTjBDTU5IKzZWQmt5VmFjamJxTDQ0REZTQXNvUWdWcmxuV3FyXG50ZVJKU2FXNG1lRFJkaWRmWDR3c0hhSGYrY2xtd2t1UzFHYzJMeGxWVVBpTEtDeDB1VlRGWU5aOXM1OFZ6Nm1zXG5IY2Q1MERCUGJMZFZYWS9wTXQ2YXlVcGJQZ05yMGl4dXd4Nng2N0F4WXlSSVI2RHpIajc4ZEVsVkVKTjhYbjFmXG43OG9nWVg3Z2NRUmVRVFdnbDBmSHVxOVo3NkN4YmxYMEU5ejJtaE5FeEQraXVGeUViME1mNE4zcldSZVhYblNvXG4rOENrSnRxS0xVck1NOGxlUzloTDRoUmpvTDhieURoTVYzNU9jb2ZqYXBVTEY0eXdkclZyMTArZi93bFVncGQ2XG5CSE43MVhkckFnTUJBQUdqZ2JBd2dhMHdIUVlEVlIwT0JCWUVGQktBbnRZSWZna3pUbmlxSTdHeEV2VEJCTzZtXG5NQjhHQTFVZEl3UVlNQmFBRkJhc1lRMGgwYlhSS1BxOFM4Znp1cjRqVTNkaU1Ba0dBMVVkRXdRQ01BQXdDd1lEXG5WUjBQQkFRREFnV2dNQ1VHQTFVZEVRUWVNQnlDQ1d4dlkyRnNhRzl6ZElJUGNuTjFkbTl5YjNZdWFYTmhMbkoxXG5NQ3dHQ1dDR1NBR0crRUlCRFFRZkZoMVBjR1Z1VTFOTUlFZGxibVZ5WVhSbFpDQkRaWEowYVdacFkyRjBaVEFOXG5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FnRUFzZlFWRVBnWDltbHV2YnJMbmdsd1IxdFFuT3ZhY0Fsd2VZbkhzOFNXXG5xejAxc29RTTZHU2dVRmRoejVtZ1V0OEF6ZDNIS2FBVDFVVGY4cFdBMmtIN0kza3hySWtsZFF5bUFUeFB2UnlLXG5lMEF6N2FJc1p2VXZQNTJ1OEdYY2RYZDhTRS9OemFXWUp5ZmowM0cvTGptZnBiU3JrSXJ6b01EbnNFUnhRdVVMXG5pKzJ6bnFxeWIvZ0RjdEUzUDd6YW9sSE5IRUxhbW1kUmtucnFnVnF3ZjdmSmM4enU1bTd5TGtSaTYxRlNmbGYzXG53N1hCSTNEUzBndVhrMGRvWWxPS3VGVys1U3FlK1hmZFJhRFoxSkxLOXNXakw2eW96bVNDYVVPaVkxSEZNRGlKXG50SjExWXordFYybWxvOEhXK2N5UHNRamZNOFpyQ1BmclN0dUtQWE1EUWl5S084YjM5M0JZQ25CQkRBRGZKNU82XG5qYmt5MFZKOUsrZm1rbm1MMTdld0NWeUkraUdmSWdEOVl4bVl5cTFZQnB4Y3kybDZmRnc1aGhYT2YrbUUzNFRqXG5HUEFHeUU2RFlkSUZZMUZLU2oxanFkUmtFVEpTWm1USGp0OWNnOUthTlJpQ1VaZFlqMkhJR3FtOXBYUDVjY21mXG5kZnBvbGJWMVkvRVBDSndFSnhZZXpXbmZGaWtDRHVpWlRrZG80RGNjOEYwR2VrREpaS3BMTzV6bVMrYnFyZnp0XG5wYVVNZ29ubFlWRGZuQVQ3dU5uTzlRcWJsbk5wdEVqenBDcWNIVFJhZ3hLSUl0OW50UlN5UUxoSkxSQXNTYm5sXG5kYm04aXUrVCtaaEM3aTZYUnUyb1RPZmVtUGZLYmhMYllPR0RMbjVpTG9iRVZlZytENWlHbnBBRjV3RWZsSXErXG5uWVU9XG4iXX0.eyJhY2Nlc3MiOlt7InR5cGUiOiJyZXBvc2l0b3J5IiwibmFtZSI6ImhlbGxvLXdvcmxkL2hlbGxvLXdvcmxkIiwiYWN0aW9ucyI6WyJwdXNoIiwicHVsbCJdfV0sImp0aSI6ImUyN2UzYjEwMmZiMDQ1YjhhN2QwZGU4MWRjNWNhMGJlIiwic3ViIjoibnBpbmZvIiwiZXhwIjoxNDYxNzczMjkwLCJpc3MiOiJkaXN0cmliLm5ldHBvbGljZWluZm8uY29tIiwiaWF0IjoxNDYxNzY5NjkwLCJuYmYiOjE0NjE3Njk2OTAsImF1ZCI6Im5waW5mby1yZWdpc3RyeSJ9.HHTEoM8jOFee5MRbg7B0DF8g0Mal1Cyjj5KqqbjKesVBv-Tjb5tS7KlhMIOVMHRXgQyVHRf0A-XJ6cAVJAvBmT6aoZNW7zlgZ_XQRo8UbyJTzlTEKaMYoLtsoXIxbppOiURYC57iSjpBnzkETFdhPElXS-QN0nV9tbPuv1iPPjX9qRvm_SzbWYDIOcEF_rd6It4-SBggIAnE_LIe5AkHjQuBf3bcO6RqaIO-4pcZC-qDpwt0DaieCZiis-EbVs7nrzSwPi3nqd6FwCfboP1RXdGEj37gRROUFwDGROMAPzkGbo-Go72a0ko9o4GDbDZNPW56Y05GcU0Gt3EeIhKyMg\", \"expires_in\": \"3600\"}";
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(json);

//        privateKey = privateKey.replace("-----BEGIN RSA PRIVATE KEY-----", "");
//        privateKey = privateKey.replace("-----END RSA PRIVATE KEY-----", "");
//        privateKey = privateKey.replaceAll("\\s+","");
//
//        Base64.Decoder decoder = Base64.getDecoder();
//        byte[] encodedKey = decoder.decode(this.privateKey);
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PrivateKey privKey = kf.generatePrivate(keySpec);

        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(this.jwtKey, this.jwtPassword.toCharArray());
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(jwtPairAlias, jwtPairPassword.toCharArray());

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(privateKey);
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
        Key key = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Date date = new Date(System.currentTimeMillis() + 1 * (1000 * 60 * 60 * 24));
        String jwt = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("x5c", "[" + keyPair.getPublic() + "]")
                .claim("jti", UUID.randomUUID())
                .setSubject("registry")
                .setIssuer("issuer")
                .setAudience("audience")
                .setExpiration(date)
                .setIssuedAt(date)
                .setNotBefore(date)
                .signWith(SignatureAlgorithm.RS256, keyPair.getPrivate())
                .compact();

        return jwt;
    }
}
