import {Injectable, Injector} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs/Observable";
import {CommonService} from "../../common/service/common.service";
import {Lnb} from "../lnb/lnb.value";
import {Utils} from "../../common/utils/utils";
import {RepositoryService} from "./repository.service";
import {UserService} from "../user/user.service";

@Injectable()
export class RepositoryGuard implements CanActivate {

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
              private userService: UserService,
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

    return this.repositoryService.getRepository(path).then(result => {
      this.repositoryService.repository = result;

      if (this.userService.user.superuser) {
        this.commonService.lnbAdmin.next(true);
        this.commonService.lnbWriter.next(true);
      } else {
        this.commonService.lnbAdmin.next(result.canAdmin);
        this.commonService.lnbWriter.next(result.canWrite);
      }

      return true;
    }).catch(reason => {
      return false;
    });
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
