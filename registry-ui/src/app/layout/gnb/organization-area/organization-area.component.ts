import {Component, ElementRef, Injector, OnDestroy, OnInit} from '@angular/core';
import {AbstractComponent} from "../../../common/component/abstract.component";
import {UserService} from "../../user/user.service";
import {User} from "../../user/user.value";
import {Organization} from "../../organization/organization.value";

@Component({
  selector: '[organization-area]',
  templateUrl: 'organization-area.component.html',
  host: { '[class.organization-area]': 'true' }
})
export class OrganizationAreaComponent extends AbstractComponent implements OnInit, OnDestroy {

  public showCreateOrgPopup: boolean = false;

  public showDivider: boolean = false;

  /**
   * 조직 목록
   */
  public organizations: Array<Organization.Entity> = [];

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              public userService: UserService) {
    super(elementRef, injector);
  }

  ngOnInit() {
    const organizationWithinUser = this.userService.user.organizations;
    if (User.isOrganizationsExist(organizationWithinUser)) {
      this.organizations = organizationWithinUser;

      if (this.userService.user.superuser) {
        this.showDivider = true;
      } else {
        this.showDivider = false;
      }
    }

    this.subscriptions.push(
      this.userService.changeOrganizationList.subscribe(
        value => {
          this.userService.getUser().then(result => {
            this.userService.user = result;
            this.organizations = this.userService.user.organizations;

            if (this.userService.user.superuser && this.organizations && this.organizations.length) {
              this.showDivider = true;
            } else {
              this.showDivider = false;
            }
            return true;
          });
        }
      )
    );
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  /**
   * 조직 선택시
   *
   * @param organization
   */
  public selectOrganization(organization: Organization.Entity) {
    this.router.navigate([`app/organization/${organization.name}/image`]);
  }

  /**
   * 조직 생성 버튼 클릭
   */
  public createOrganization() {
    this.showCreateOrgPopup = true;
  }

  public createOrgPopupClose() {
    this.showCreateOrgPopup = false;
  }

}
