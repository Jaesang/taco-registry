import {
  Component, OnInit, ElementRef, Injector, ViewChild, ViewChildren, QueryList,
} from '@angular/core';
import {PageComponent} from "../../../common/component/page.component";
import {OrganizationService} from "../organization.service";
import {Team} from "../../../common/value/team.value";
import {SelectBoxComponent} from "../../../common/component/selectbox/select-box.component";
import {Select} from "../../../common/component/selectbox/select.value";
import {TeamService} from "./team.service";
import {Organization} from "../organization.value";
import {Validate} from "../../../common/utils/validate-util";

@Component({
  selector: 'role-setting',
  templateUrl: 'role-setting.component.html'
})
export class RoleSettingComponent extends PageComponent implements OnInit {

  @ViewChildren(SelectBoxComponent)
  private selectboxs: QueryList<SelectBoxComponent>;

  public org: Organization.Entity;
  public orgName: string;

  public teamList: Team.Entity[] = [];

  public sortProperty: string;
  public sortDirection: string;

  public createTeam: Team.Entity = new Team.Entity();

  public showCreateTeamPopup = false;

  public errorMsg: string = '';

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private organizationService: OrganizationService,
              private teamService: TeamService) {

    super(elementRef, injector);
  }

  ngOnInit() {
    // parameter 가져오기
    this.subscriptions.push(
      this.activatedRoute.params
        .subscribe(params => {
          if (params[ 'org' ]) {
            this.orgName = params[ 'org' ];
          }

          this.getTeamList();
        })
    );
  }

  /**
   * Team 목록 조회
   */
  public getTeamList() {
    this.loaderService.show.next(true);

    this.organizationService.getOrganization(this.orgName).then(result => {
      this.org = result;
      this.teamList = this.teamService.getTeamList(result);

      this.loaderService.show.next(false);
    });
  }

  /**
   * 정렬
   * @param colName
   * @param sort
   */
  public sortClick(property: string) {
    if (this.sortProperty == property) {
      this.sortDirection = this.sortDirection == 'desc' ? 'asc' : 'desc';
    } else {
      this.sortDirection = 'desc';
    }
    this.sortProperty = property;
  }

  /**
   * role 변경
   * @param item
   * @param selectValue
   */
  public roleChange(item: Team.Entity, selectValue: Select.Value) {
    item.role = selectValue.value;

    this.teamService.updateTeam(this.orgName, item).then(result => {
      this.getTeamList();
    }).
    catch(reason => {
      this.getTeamList();
    });
  }

  /**
   * team role 삭제
   * @param item
   */
  public deleteTeamRole(item: Team.Entity) {
    this.teamService.deleteTeamRole(this.orgName, item.name).then(result => {
      this.getTeamList();
    });
  }

  /**
   * select role 목록
   * @param selectedRole
   * @returns {Select.Value[]}
   */
  public getRoleList(selectedRole: string) {
    let roleNames = ['member', 'creator', 'admin'];
    let roleList = roleNames.map(value => {
      return new Select.Value(value, value, selectedRole == value ? true : false);
    });

    return roleList;
  }

  /**
   * Create New Team 클릭
   */
  public popupCreateTeamClick() {
    this.showCreateTeamPopup = true;
    this.createTeam = new Team.Entity();
    this.createTeam.role = Team.RoleType.admin;
    this.errorMsg = '';
  }

  /**
   * Create Team 클릭
   */
  public createTeamClick() {
    this.teamService.updateTeam(this.orgName, this.createTeam).then(result => {
      if (!result.new_team) {
        // already
      }

      this.getTeamList();
      this.showCreateTeamPopup = false;
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      this.errorMsg = body.message;
    });


  }

  /**
   * 상세 이동
   */
  public moveToDetail(item: Team.Entity) {
    this.router.navigate([`app/organization/${this.orgName}/team/${item.name}`]);
  }

}
