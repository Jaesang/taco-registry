import {Injectable, Injector} from "@angular/core";
import {AbstractService} from "../../../common/service/abstract.service";
import {Build} from "./build-history.value";
import {environment} from "../../../../environments/environment";
import {BehaviorSubject} from "rxjs";
import {Validate} from "../../../common/utils/validate-util";

/**
 * Build history 서비스
 */
@Injectable()
export class BuildHistoryService extends AbstractService {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  public newBuild: BehaviorSubject<Build.Entity> = new BehaviorSubject<Build.Entity>(new Build.Entity());

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
   * Build
   * @param namespace
   * @param repo
   * @param fileId
   * @returns {Promise<any>}
   */
	public build(namespace: string, repo: string, build: Build.Entity, sourceBuild: Build.Entity = null): Promise<Build.Entity> {
	  let url = `${environment.apiUrl}/image/${namespace}/${repo}/build/`;
	  if (sourceBuild) {
	    url += `?sourceBuildId=${sourceBuild.id}`;
    }

	  if (Validate.isEmpty(build.tag)) {
	    build.tag = 'latest';
    }
    return this.post(url, build);
  }

  /**
   * build 상세 조회
   * @param namespace
   * @param repo
   * @param buildId
   * @returns {Promise<any>}
   */
  public getBuildDetail(namespace: string, repo: string, buildId: string): Promise<Build.Entity> {
    return this.get(`${environment.apiUrl}/image/${namespace}/${repo}/build/${buildId}`)
  }

  /**
   * build log url 조회
   * @param namespace
   * @param repo
   * @param buildId
   * @returns {Promise<any>}
   */
  public getBuildLogUrl(namespace: string, repo: string, buildId: string): Promise<any> {
	  return this.get(`${environment.apiUrl}/image/${namespace}/${repo}/build/${buildId}/logs?start=0`)
  }

  /**
   * build log 조회
   * @param namespace
   * @param repo
   * @param buildId
   * @returns {any}
   */
  public getBuildLogList(namespace: string, repo: string, buildId: string, start: number = 0): Promise<Build.LogResult> {
    return this.get(`${environment.apiUrl}/image/${namespace}/${repo}/build/${buildId}/logs?start=${start}`);
  }

  /**
   * build log 조회
   * @param namespace
   * @param repo
   * @param buildId
   * @returns {any}
   */
  public getCompleteBuildLogList(buildId: string): Promise<Build.LogResult> {
    return this.get(`${environment.host}/logarchive/${buildId}`);
  }

  /**
   * build cancel
   * @param namespace
   * @param repo
   * @param buildId
   * @returns {Promise<any>}
   */
  public deleteBuild(namespace: string, repo: string, buildId: string): Promise<Build.Entity> {
    return this.delete(`${environment.apiUrl}/image/${namespace}/${repo}/build/${buildId}`);
  }

  public getPhaseClass(buildHistory: Build.Phase) {
    let className = '';

    switch (buildHistory) {
      case Build.Phase.complete:
        className = 'build-success';
        break;

      case Build.Phase.error:
        className = 'build-fail';
        break;

      case Build.Phase.cancelled:
        className = 'build-cancel';
        break;

      case Build.Phase.waiting:
      case Build.Phase.pulling:
      case Build.Phase.pushing:
      case Build.Phase.building:
        className = 'component-loading';
        break;
    }

    return className;
  }

  /**
   * command log class
   * @param item
   * @returns {string}
   */
  public getClassCommandLog(commandName: string) {
    let className = '';
    if (commandName.toUpperCase() == 'FROM') {
      className += ' is-from';
    } else if (commandName.toUpperCase() == 'RUN') {
      className += ' is-run';
    } else if (commandName.toUpperCase() == 'WORKDIR') {
      className += ' is-work';
    } else if (commandName.toUpperCase() == 'LABEL' || commandName.toUpperCase() == 'MAINTAINER') {
      className += ' is-info';
    } else {
      className += ' is-basic';
    }

    return className;
  }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

}
