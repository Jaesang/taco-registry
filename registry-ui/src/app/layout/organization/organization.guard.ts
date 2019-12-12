import {Injectable, Injector} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {OrganizationService} from "./organization.service";
import {CommonService} from "../../common/service/common.service";
import {Lnb} from "../lnb/lnb.value";
import {Subscription} from "rxjs";
import {Utils} from "../../common/utils/utils";
import {UserService} from "../user/user.service";

@Injectable()
export class OrganizationGuard implements CanActivate {

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

	constructor(private organizationService: OrganizationService,
              private commonService: CommonService,
              private userService: UserService,
              protected injector: Injector,
				private router: Router) {
	}

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Override Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/**
	 * organization check
	 *
	 * @param {ActivatedRouteSnapshot} next
	 * @param {RouterStateSnapshot} state
	 * @returns {Observable<boolean> | Promise<boolean> | boolean}
	 */
	canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    const org = next.params.org;

    return this.organizationService.getOrganization(org).then(result => {
      this.organizationService.organization = result;

      if (this.userService.user.superuser) {
        this.commonService.lnbAdmin.next(true);
        this.commonService.lnbMember.next(true);
      } else {
        this.commonService.lnbAdmin.next(result.isAdmin);
        this.commonService.lnbMember.next(result.isMember);
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
