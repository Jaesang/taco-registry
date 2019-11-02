import {Injectable, Injector} from '@angular/core';
import {AbstractService} from '../../../common/service/abstract.service';
import {environment} from '../../../../environments/environment';
import * as _ from 'lodash';
import {SuperUser} from './super-user.value';
import {CommonResult, Page} from '../../../common/value/result-value';

@Injectable()
export class SuperUserService extends AbstractService {

  constructor(protected injector: Injector) {
    super(injector);
  }

  public getUsers(searchKey: string, page: Page): Promise<Page> {
    let url = `${environment.apiUrl}/superuser/users/?page=${page.number}&size=${page.size}&sort=${page.sort.property},${page.sort.direction}`;
    if (!_.isNil(searchKey)) {
      url += `&searchKey=${searchKey}`;
    }

    return this.get(url);
  }

  /**
   * get superuser verify
   * @returns {Promise<any>}
   */
  public getSuperuserVerify(): Promise<CommonResult> {
    return this.get(`${environment.apiUrl}/superuser/verify`);
  }

  /**
   * create user
   * @param user
   * @returns {Promise<any>}
   */
  public createUser(user: SuperUser.Entity): Promise<SuperUser.Entity> {
    return this.post(`${environment.apiUrl}/superuser/users/`, user);
  }

  /**
   * update user
   * @param user
   * @returns {Promise<any>}
   */
  public updateUser(user: SuperUser.Entity): Promise<SuperUser.Entity> {
    return this.put(`${environment.apiUrl}/superuser/users/${user.username}`, user);
  }

  /**
   * delete user
   * @param username
   * @returns {Promise<any>}
   */
  public deleteUser(username: string): Promise<any> {
    return this.delete(`${environment.apiUrl}/superuser/users/${username}`);
  }


}
