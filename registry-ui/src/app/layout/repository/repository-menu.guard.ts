import {Injectable, Injector} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs/Observable";
import {CommonService} from "../../common/service/common.service";
import {Lnb} from "../lnb/lnb.value";
import {Utils} from "../../common/utils/utils";
import {RepositoryService} from "./repository.service";

@Injectable()
export class RepositoryMenuGuard implements CanActivate {

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

	constructor(private repositoryService: RepositoryService,
              private commonService: CommonService,
              protected injector: Injector,
				private router: Router) {
	}

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Override Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/**
	 * repository check
	 *
	 * @param {ActivatedRouteSnapshot} next
	 * @param {RouterStateSnapshot} state
	 * @returns {Observable<boolean> | Promise<boolean> | boolean}
	 */
	canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
	  const org = next.params.org;
    const repo = next.params.repo;
    const path = `${org}/${repo}`;

    let lnb = new Lnb.Entity();
    lnb.lnbType = Lnb.LnbType.REPO;
    lnb.pathVariable = path;

    let url = state.url.split('?')[0];

    if (Utils.StringUtil.endsWith(url, '/info')) {
      lnb.index = 0;
    } else if (Utils.StringUtil.endsWith(url, '/tag-info')) {
      lnb.index = 1;
    } else if (Utils.StringUtil.endsWith(url, '/tag-history')) {
      lnb.index = 2;
    } else if (Utils.StringUtil.endsWith(url, '/build')) {
      lnb.index = 3;
    } else if (Utils.StringUtil.endsWith(url, '/usage-log')) {
      lnb.index = 4;
    } else if (Utils.StringUtil.endsWith(url, '/setting')) {
      lnb.index = 5;
    }

    this.commonService.lnb.next(lnb);

    return true;
	}

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

}
