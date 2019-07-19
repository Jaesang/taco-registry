import {Component, OnInit, ElementRef, Injector} from "@angular/core";
import {PageComponent} from "../../../common/component/page.component";
import {OrganizationService} from "../organization.service";
import {Team} from "../../../common/value/team.value";
import {TeamService} from "./team.service";
import {UserService} from "../../user/user.service";
import {Autocomplete} from "../../../common/component/autocomplete/autocomplete.value";
import {Member} from "./member.value";
import {CommonConstant} from "../../../common/constant/common-constant";
import {Alert} from "../../../common/utils/alert-util";

@Component({
  selector: 'team-detail',
  templateUrl: 'team-detail.component.html'
})
export class TeamDetailComponent extends PageComponent implements OnInit {

  public AutocompleteType: typeof Autocomplete.Type = Autocomplete.Type;

  public orgName: string;
  public teamName: string;

  public team: Team.Entity = new Team.Entity();

  public memberList: Member.Entity[] = [];

  public matchList: Autocomplete.Entity[] = [];

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private organizationService: OrganizationService,
              private teamService: TeamService,
              private userService: UserService) {

    super(elementRef, injector);

    // this.commonService.showLnb.next(false);
  }

  ngOnInit() {
    // parameter 가져오기
    this.subscriptions.push(
      this.activatedRoute.params
        .subscribe(params => {
          if (params[ 'org' ]) {
            this.orgName = params[ 'org' ];
          }

          if (params[ 'teamname' ]) {
            this.teamName = params[ 'teamname' ];
          }

          this.getTeamDetail();
          this.getTeamMemberList();
        })
    );
  }

  public backClick() {
    this.router.navigate([`app/organization/${this.orgName}/team`]);
  }

  public memberClick(member: Member.Entity) {
    this.router.navigate([`app/user/${member.name}/repository`]);
  }

  /**
   * 팀 정보 수정
   */
  public saveClick() {
    this.loaderService.show.next(true);

    this.teamService.updateTeam(this.orgName, this.team).then(result => {
      if (!result.new_team) {
        // already
      }
      this.loaderService.show.next(false);
    });
  }

  /**
   * quick search
   * @param text
   */
  public searchEvent(text: string) {

    this.teamService.searchMember(this.orgName, text).then(result => {
      let list = result.results.map(value => {
        return new Autocomplete.Entity(value.name, value, Autocomplete.ItemType.USER);
      });

      this.matchList = list;
    });

  }

  /**
   * enter search
   * @param text
   */
  public searchEnterEvent(text: string) {
  }

  /**
   * autocomplete item click
   * @param value
   */
  public searchAutocompleteSelect(value: Member.Entity) {
    let exist = false;
    this.memberList.forEach(v => {
      if (v.name == value.name) {
        exist = true;
      }
    });

    if (!exist) {
      this.teamService.createTeamMember(this.orgName, this.teamName, value.name).then(result => {
        this.getTeamMemberList();

        Alert.success(CommonConstant.MESSAGE.SUCCESS);
      });
    }
  }

  /**
   * 멤버 삭제 클릭
   * @param member
   */
  public deleteMemberClick(member: Member.Entity) {
    this.loaderService.show.next(true);

    this.teamService.deleteTeamMember(this.orgName, this.teamName, member.name).then(result => {
      this.getTeamMemberList();

      Alert.success(CommonConstant.MESSAGE.DELETED);

      this.loaderService.show.next(false);
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      Alert.error(body.message);

      this.loaderService.show.next(false);
    });
  }

  /**
   * team 상세 조회
   */
  private getTeamDetail() {
    this.team = this.teamService.getTeamDetail(this.organizationService.organization, this.teamName);
  }

  /**
   * team member 목록 조회
   */
  private getTeamMemberList() {
    this.loaderService.show.next(true);

    this.teamService.getTeamMemberList(this.orgName, this.teamName).then(result => {
      this.memberList = result.members;

      this.loaderService.show.next(false);
    });
  }


}
