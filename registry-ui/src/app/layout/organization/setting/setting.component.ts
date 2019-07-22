import {Component, OnInit, ElementRef, Injector} from '@angular/core';
import {PageComponent} from "../../../common/component/page.component";
import {ConfirmPopupService} from "../../../common/component/confirm-popup/confirm-popup.service";
import {Organization} from "../organization.value";
import {TeamService} from "../role/team.service";
import {RepositoryService} from "../../repository/repository.service";
import {OrganizationService} from "../organization.service";
import {Alert} from "../../../common/utils/alert-util";
import {UserService} from "../../user/user.service";

@Component({
  selector: 'setting',
  templateUrl: 'setting.component.html'
})
export class SettingComponent extends PageComponent implements OnInit {

  public org: Organization.Entity = new Organization.Entity();

  public repoCount: number = 0;
  public memberCount: number = 0;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private userService: UserService,
              private confirmPopupService: ConfirmPopupService,
              private organizationService: OrganizationService,
              private teamService: TeamService,
              private repositoryService: RepositoryService) {

    super(elementRef, injector);
  }

  ngOnInit() {
    this.org = this.organizationService.organization;

    // member list 조회
    this.teamService.getTeamMemberList(this.org.name, 'owners').then(result => {
      this.memberCount = result.members.length;
    });

    // repository list 조회
    this.repositoryService.getRepositoryList(this.org.name).then(result => {
      this.repoCount = result.images.length;
    });
  }

  /**
   * repo 클릭
   */
  public repoClick() {
    this.router.navigate([`app/organization/${this.org.name}/image`]);
  }

  /**
   * member 클릭
   */
  public memberClick() {
    this.router.navigate([`app/organization/${this.org.name}/team/owners`]);
  }

  /**
   * 삭제 클릭
   */
  public deleteOrgClick() {
    this.confirmPopupService.show(
      'Delete Organization',
      'Are you sure you want to delete the organization?',
      `Deleting an organization is non-reversable and will delete all of the organization's data`,
      () => {

      },
      () => {
        this.loaderService.show.next(true);

        this.organizationService.deleteOrganization(this.org.name).then(result => {
          this.userService.changeOrganizationList.next(true);
          this.router.navigate(['app/main']);

          this.loaderService.show.next(false);
        }).catch(reason => {
          let body = JSON.parse(reason._body);
          Alert.error(body.detail);
        });
      },
      'Cancel',
      'Delete Organization'
    );
  }

}
