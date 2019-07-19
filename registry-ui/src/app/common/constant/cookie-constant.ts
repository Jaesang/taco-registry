/**
 * 쿠키 Key 상수
 */
export class CookieKey {

	// return url
	public RETURN_URL = 'RETURN_URL';

	// user id
	public USER_ID = 'USER_ID';

	// 로그인 아이디 저장
	public REM_USER_ID = 'REM_USER_ID';

	// token
	public TOKEN = 'TOKEN';

}

/**
 * 쿠키 관련 상수
 */
export class CookieConstant {

	// 쿠키 key
	public static KEY: CookieKey = new CookieKey();
}
