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

    public static final String HEALTH                       = API + "/health";

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

    // starred
    // (POST) add starred
    // (DELETE) delete starred
    public static final String USER_STARRED 	            = API + "/user/starred" ;

    // starred
    // (DELETE) delete starred
    public static final String USER_STARRED_DETAIL          = API + "/user/starred/{namespace}/{imageName}" ;

    // (PUT) change minio
    public static final String USER_MINIO    	            = API + "/user/minio" ;

    // logs
    // (GET) log 목록
    public static final String USER_LOGS                    = USER + "/logs";

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

    // member
    // (GET) 목록
    public static final String ORG_MEMBER                   = ORG_DETAIL + "/members";

    // member 상세
    // (POST) 등록
    public static final String ORG_MEMBER_DETAIL            = ORG_MEMBER + "/{username}";

    // logs
    // (GET) log 목록
    public static final String ORG_LOGS                     = ORG_DETAIL + "/logs";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Image
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    // image 등록
    // (POST) 등록
    // (GET) 목록
    public static final String IMAGE                        = API + "/image";

    public static final String IMAGE_COUNT                  = IMAGE + "/count";

    // image 상세
    // (GET) 상세
    public static final String IMAGE_DETAIL                 = API + "/image/{namespace}/{name}";

    // docker image detail
    public static final String MANIFESTS                    = IMAGE_DETAIL + "/tag/{tagName}/manifests";

    // image security
    public static final String IMAGE_SECURITY               = IMAGE_DETAIL + "/tag/{tagName}/security";

    // build 목록
    public static final String IMAGE_BUILD                  = IMAGE_DETAIL + "/build/";

    // build 상세
    public static final String IMAGE_BUILD_DETAIL           = IMAGE_BUILD + "{buildId}";

    // build log
    public static final String BUILD_LOGS                   = IMAGE_BUILD_DETAIL + "/logs";

    // build log file
    public static final String BUILD_LOGS_FILE              = "/logarchive/{buildId}";

    // tag 목록
    public static final String IMAGE_TAG                    = IMAGE_DETAIL + "/tag/";

    // tag 상세
    public static final String IMAGE_TAG_DETAIL             = IMAGE_TAG + "{tagName}";

    // build 목록
    public static final String IMAGE_MEMBER                 = IMAGE_DETAIL + "/permissions/user/";

    // build 목록
    public static final String IMAGE_MEMBER_DETAIL          = IMAGE_DETAIL + "/permissions/user/{username}";

    // 공개여부 변경
    // (POST) 변경
    public static final String IMAGE_VISIBILITY             = IMAGE_DETAIL + "/changevisibility";

    // logs
    // (GET) log 목록
    public static final String IMAGE_LOGS                   = IMAGE_DETAIL + "/logs";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Search
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    // 유저 찾기
    // (GET) 목록
    public static final String SEARCH_MEMBER                = API + "/entities/{username}";

    // 이미지 찾기
    // (GET) 목록
    public static final String SEARCH_IMAGE                 = API + "/find/images";

    // 찾기
    // (GET) 목록
    public static final String SEARCH_ALL                   = API + "/find/all";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Notification
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    // Noti
    // (POST) 목록
    public static final String NOTI                         = API + "/notification";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   | Download
   |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    // file download
    public static final String FILE_DOWNLOAD_KUBE_CONFIG    = "/download/cluster/{clusterName}/inventory/{inventoryName}/config";

}
