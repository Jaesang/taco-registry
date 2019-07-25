import {Injectable, Injector} from "@angular/core";
import {AbstractService} from "../../../common/service/abstract.service";
import {environment} from "../../../../environments/environment";
import {Logs} from "./logs.value";

/**
 * Usage Log 서비스
 */
@Injectable()
export class UsageLogService extends AbstractService {

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

  public getOrgLogList(org: string, startDate: string, endDate: string, nextPage: string = null): Promise<Logs.Result> {
    let url = `${environment.apiUrl}/organization/${org}/logs?starttime=${startDate}&endtime=${endDate}`;
    if (nextPage) {
      url += `&nextPage=${nextPage}`;
    }
    return this.get(url);
  }

  public getUserLogList(startDate: string, endDate: string, nextPage: string = null): Promise<Logs.Result> {
    let url = `${environment.apiUrl}/user/logs?starttime=${startDate}&endtime=${endDate}`;
    if (nextPage) {
      url += `&nextPage=${nextPage}`;
    }
    return this.get(url);
  }

  public getRepoLogList(org: string, repo: string, startDate: string, endDate: string, nextPage: string = null): Promise<Logs.Result> {
    let url = `${environment.apiUrl}/image/${org}/${repo}/logs?starttime=${startDate}&endtime=${endDate}`;
    if (nextPage) {
      url += `&nextPage=${nextPage}`;
    }
    return this.get(url);
  }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

}
