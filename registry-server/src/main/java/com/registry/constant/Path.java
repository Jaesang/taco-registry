package com.registry.constant;

/**
 * URL Path
 * @author taeho
 *
 */
public class Path {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Prefix
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    // API Prefix
    public static final String API							= "/api";

    // RestAPI 상세
    public static final String API_DETAIL					= "/{id}";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Common
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    // 공통 코드 목록
    public static final String COMMON_CODE                 = API + "/code";

    public static final String COMMON_INVENTORY_VALIDATE   = API + "/inventory/validate";

    public static final String COMMON_INVENTORY_PARSE   = API + "/inventory/parse";

    // tag 목록
    public static final String COMMON_TAG                  = API + "/tag";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| User
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String OAUTH_REVOKE                 = API + "/oauth/revoke";

    public static final String OAUTH_TOKEN                  = API + "/oauth/token";

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| User
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    // 로그인 유저정보
    // (GET) 로그인 유저정보
    public static final String USER_ME					    = API + "/user/me";

    // 특정 유저정보
    // (GET) 특정 유저정보 조회
    public static final String USER_DETAIL					= API + "/users/{username}";

    // 유저
    // (GET) 유저 조회
    // (POST, PUT) 유저 저장
    public static final String USER				            = API + "/user" ;

    // 유저목록
    // (GET) 유저목록 조회
    // (POST) 유저 등록
    public static final String SUPERUSER_USER               = API + "/superuser/users/" ;

    // 유저 상세
    // (PUT) 유저 수정정
    public static final String SUPERUSER_USER_DETAIL        = API + "/superuser/users/{username}";

    // 유저 사용상태
    // (POST) 유저목록 사용상태 변경
    public static final String USER_EDIT_ENABLED		    = API + "/user/enabled" ;

    // 패스워드 확인
    public static final String PASSWORD_VERIFY              = API + "/signin/verify";

    // admin 확인
    public static final String SUPERUSER_VERIFY             = API + "/superuser/verify";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Organization
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    // organization 등록
    // (POST) 등록
    public static final String ORG                          = API + "/organization/";

    // organization 상세
    // (GET) 상세
    public static final String ORG_DETAIL                   = API + "/organization/{name}";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Image
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    // image 등록
    // (POST) 등록
    // (GET) 목록
    public static final String IMAGE                        = API + "/image";

    // image 상세
    // (GET) 상세
    public static final String IMAGE_DETAIL                 = API + "/image/{namespace}/{name}";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   | Download
   |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    // file download
    public static final String FILE_DOWNLOAD_KUBE_CONFIG    = "/download/cluster/{clusterName}/inventory/{inventoryName}/config";

}
