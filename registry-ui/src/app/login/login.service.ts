import {Injectable, Injector} from '@angular/core';
import {AbstractService} from "../common/service/abstract.service";
import {RequestOptions, Headers} from "@angular/http";
import {environment} from "../../environments/environment";
import {CookieConstant} from "../common/constant/cookie-constant";

/**
 * Login 서비스
 */
@Injectable()
export class LoginService extends AbstractService {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Constructor
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	constructor(protected injector: Injector) {
		super(injector);
	}

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Override Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  public login(userId: string, password: string): Promise<any> {

    // 헤더
    const headers = new Headers(this.DEFAULT_HEADER);
    headers.append('Authorization', 'Basic cmVnaXN0cnk6cmVnaXN0cnktc2VjcmV0');

    // 옵션
    const options = new RequestOptions({ headers: headers });

    return this.http
      .post(`${environment.apiUrl}/oauth/token?grant_type=password&scope=read&username=${userId}&password=${password}`, null, options)
      .toPromise()
      .then(response => {
        var body = JSON.parse(response['_body']);
        this.cookieService.set(CookieConstant.KEY.TOKEN, body.access_token, null, '/');
        return body;
      });
  }

  public loginCheck(): Promise<any> {
    // 호출
    return this.http
      .get(`${environment.apiUrl}/user/`, this.getDefaultRequestOptions())
      .toPromise();
  }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

}
