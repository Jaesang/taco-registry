import {Injectable, Injector} from '@angular/core';
import {AbstractService} from "../../common/service/abstract.service";
import {CommonResult} from "../../common/value/result-value";
import {environment} from "../../../environments/environment";
import {Repository} from "./repository.value";
import {Build} from "./build-history/build-history.value";
import {Member} from "../organization/role/member.value";

/**
 * Repository 서비스
 */
@Injectable()
export class RepositoryService extends AbstractService {

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  public repository: Repository.Entity;

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
   * Repository 목록 조회
   * @returns {Promise<any>}
   */
  public getRepositoryList(namespace: string): Promise<Repository.Entity[]> {
    let url = `${environment.apiUrl}/image?namespace=${namespace}`;

    return this.get(url);
  }

  /**
   * Repository 상세 조회
   * @returns {Promise<any>}
   */
  public getRepository(repoPath: string, includeStats: boolean = false): Promise<Repository.Entity> {
    let url = `${environment.apiUrl}/image/${repoPath}`;

    if (includeStats) {
      url += `?includeStats=${includeStats}`;
    }

    return this.get(url);
  }

  /**
   * repository 생성
   * @param repo
   * @returns {Promise<any>}
   */
  public createRepository(repo: Repository.Entity): Promise<Repository.Entity> {
    return this.post(`${environment.apiUrl}/image`, repo);
  }

  /**
   * repository 저장
   * @param namespace
   * @param repository
   * @param repo
   * @returns {Promise<any>}
   */
  public updateRepository(namespace: string, repository: string, repo: Repository.Entity): Promise<any> {
    return this.put(`${environment.apiUrl}/image/${namespace}/${repository}`, repo);
  }

  /**
   * repository 삭제
   * @param namespace
   * @param repository
   * @param repo
   * @returns {Promise<any>}
   */
  public deleteRepository(namespace: string, repository: string): Promise<any> {
    return this.delete(`${environment.apiUrl}/image/${namespace}/${repository}`);
  }

  /**
   * 별 등록
   * @param namespace
   * @param repository
   * @returns {Promise<any>}
   */
  public starredRepository(namespace: string, repository: string): Promise<any> {
    let data = {
      namespace: namespace,
      name: repository
    }
    return this.post(`${environment.apiUrl}/user/starred`, data);
  }

  /**
   * 별 삭제
   * @param namespace
   * @param repository
   * @returns {Promise<any>}
   */
  public deleteStarredRepository(namespace: string, repository: string): Promise<any> {
    return this.delete(`${environment.apiUrl}/user/starred/${namespace}/${repository}`);
  }

  /**
   * 빌드 히스토리 조회
   * @param namespace
   * @param repository
   * @param limit
   * @returns {Promise<any>}
   */
  public getBuildHistory(namespace: string, repository:string, limit: number = 10): Promise<Build.Result> {
    return this.get(`${environment.apiUrl}/image/${namespace}/${repository}/build/?limit=${limit}`);
  }

  /**
   * Repo member 목록 조회
   * @param namespace
   * @param repository
   * @returns {Promise<any>}
   */
  public getMemberList(namespace: string, repository: string): Promise<Member.Entity[]> {
    return this.get(`${environment.apiUrl}/image/${namespace}/${repository}/permissions/user/`);
  }

  /**
   * Repo member 추가
   * @param namespace
   * @param repository
   * @param username
   * @param role
   * @returns {Promise<any>}
   */
  public addMember(namespace: string, repository: string, username: string, role: string): Promise<Member.Permissions> {
    let data = {
      role: role
    };

    return this.put(`${environment.apiUrl}/image/${namespace}/${repository}/permissions/user/${username}`, data);
  }

  /**
   * Repo member 삭제
   * @param namespace
   * @param repository
   * @param username
   * @param role
   * @returns {Promise<any>}
   */
  public deleteMember(namespace: string, repository: string, username: string): Promise<any> {
    return this.delete(`${environment.apiUrl}/image/${namespace}/${repository}/permissions/user/${username}`);
  }

  /**
   * visibility 변경
   * @param namespace
   * @param repository
   * @param isPublic
   * @returns {Promise<any>}
   */
  public changeVisibility(namespace: string, repository: string, isPublic: boolean): Promise<any> {
    let data = {
      visibility: isPublic ? 'public' : 'private'
    };

    return this.post(`${environment.apiUrl}/image/${namespace}/${repository}/changevisibility`, data);
  }

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Protected Method
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

}
