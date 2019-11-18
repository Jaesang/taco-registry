import {Injectable, Injector} from '@angular/core';
import {AbstractService} from "../../common/service/abstract.service";
import {CommonResult} from "../../common/value/result-value";
import {environment} from "../../../environments/environment";
import {User} from "./user.value";
import {BehaviorSubject} from "rxjs";

/**
 * User 서비스
 */
@Injectable()
export class UserService extends AbstractService {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  public changeOrganizationList: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

	public user: User.Entity = new User.Entity();

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

	/**
	 * User Info
	 * @returns {Promise<any>}
	 */
	public getUser(): Promise<User.Entity> {
		return this.get(`${environment.apiUrl}/user`);
	}

  /**
   * User Info
   * @returns {Promise<any>}
   */
  public getUserById(userId: string): Promise<User.Entity> {
    return this.get(`${environment.apiUrl}/users/${userId}`);
  }

  /**
   * 패스워드 변경
   * @param password
   * @returns {Promise<any>}
   */
  public updatePassword(password: string): Promise<CommonResult> {
    let data = {
      password: password
    };

    return this.put(`${environment.apiUrl}/user`, data);
  }

  /**
   * 현재 패스워드 확인
   * @param password
   * @returns {Promise<any>}
   */
  public verifyPassword(password: string): Promise<CommonResult> {
    let data = {
      password: password
    };

    return this.post(`${environment.apiUrl}/signin/verify`, data);
  }

  /**
   * 유저 삭제
   */
  public deleteUser(): Promise<any> {
    return this.delete(`${environment.apiUrl}/user`);
  }

  /**
   * minio enable / disable
   */
  public changeMinioEnable(enable: boolean, password: string): Promise<any> {
    let data = {
      enable: enable,
      password: password
    };

    return this.put(`${environment.apiUrl}/user/minio`, data);
  }

  public getMinioUrl(user: User.Entity) {
    if (user.minioEnabled) {
      return `http://${user.minioHost}:${user.minioPort}/minio`;
    }

    return null;
  }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

}
