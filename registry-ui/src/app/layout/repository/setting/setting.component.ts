import {Component, OnInit, ElementRef, Injector, ViewChild} from '@angular/core';
import {PageComponent} from "../../../common/component/page.component";
import {ConfirmPopupService} from "../../../common/component/confirm-popup/confirm-popup.service";
import {Repository} from "../repository.value";
import {Alert} from "../../../common/utils/alert-util";
import {CommonConstant} from "../../../common/constant/common-constant";
import {Member} from "../../organization/role/member.value";
import {TeamService} from "../../organization/role/team.service";
import {Autocomplete} from "../../../common/component/autocomplete/autocomplete.value";
import {Select} from "../../../common/component/selectbox/select.value";
import {RepositoryService} from "../repository.service";
import {SelectBoxComponent} from "../../../common/component/selectbox/select-box.component";
import * as _ from 'lodash';

@Component({
  selector: 'setting',
  templateUrl: 'setting.component.html'
})
export class SettingComponent extends PageComponent implements OnInit {

  @ViewChild('addMemberSelect')
  private addMemberSelect: SelectBoxComponent;

  public AutocompleteType: typeof Autocomplete.Type = Autocomplete.Type;

  public sortProperty: string = '';
  public sortDirection: string = '';

  public repo: Repository.Entity = new Repository.Entity();

  public orgName: string;
  public repoName: string;

  public matchList: Autocomplete.Entity[] = [];

  public memberList: Member.Entity[] = [];

  public addMember: Member.Entity = new Member.Entity();
  public addMemberEnable: boolean = false;

  public selectData: Select.Value[] = [
    new Select.Value('Read', 'read', true, 'Can view and pull from the repository', 'type-read'),
    new Select.Value('Write', 'write', false, 'Can view, pull and push to the repository', 'type-write'),
    new Select.Value('Admin', 'admin', false, 'Full admin access, pull and push on the repository', 'type-admin')
  ];

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private confirmPopupService: ConfirmPopupService,
              private repositoryService: RepositoryService,
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

          if (params[ 'repo' ]) {
            this.repoName = params[ 'repo' ];
          }

          this.repo = this.repositoryService.repository;

          this.addMemberEnable = false;
          this.addMemberSelect.init(_.cloneDeep(this.selectData));
          this.getMemberList();
        })
    );
  }

  /**
   * quick search
   * @param text
   */
  public searchEvent(text: string) {
    this.addMemberEnable = false;

    this.teamService.searchMember(this.orgName, text).then(result => {
      let list = result.content.map(value => {
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
    this.addMember.name = value.name;
    this.addMemberEnable = true;
  }

  /**
   * add member 클릭
   */
  public addMemberClick() {
    if (!this.addMember.name) {
      return;
    }

    let exist = false;
    this.memberList.forEach(v => {
      if (v.name == this.addMember.name) {
        exist = true;
      }
    });

    if (!exist) {

      if (!this.addMember.role) {
        this.addMember.role = this.selectData[0].value;
      }

      this.repositoryService.addMember(this.orgName, this.repoName, this.addMember.name, this.addMember.role).then(result => {
        this.getMemberList();

        this.addMemberEnable = false;
        this.addMember = new Member.Entity();
        this.addMemberSelect.init(_.cloneDeep(this.selectData));

        Alert.success(CommonConstant.MESSAGE.SUCCESS);
      });
    }
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
   * 멤버 클릭
   * @param member
   */
  public memberClick(member: Member.Entity) {
    this.router.navigate([`app/user/${member.name}/image`]);
  }

  /**
   * Role change
   * @param role
   * @param member
   */
  public roleChange(role: Select.Value, member: Member.Entity = null) {
    if (member) {
      this.repositoryService.addMember(this.orgName, this.repoName, member.name, role.value).then(result => {
        Alert.success(CommonConstant.MESSAGE.SUCCESS);
      }).catch(reason => {
        let body = JSON.parse(reason._body);
        Alert.error(body.message);

        this.loaderService.show.next(false);
      });
    } else {
      this.addMember.role = role.value;
    }
  }

  /**
   * member 삭제 클릭
   * @param member
   */
  public deleteMemberClick(member: Member.Entity) {
    this.loaderService.show.next(true);

    this.repositoryService.deleteMember(this.orgName, this.repoName, member.name).then(result => {
      this.getMemberList();

      Alert.success(CommonConstant.MESSAGE.DELETED);

      this.loaderService.show.next(false);
    });
  }

  /**
   * Repository 삭제 클릭
   */
  public deleteRepoClick() {
    this.confirmPopupService.show(
      'Delete Image',
      'Continue with deletion of this image?',
      `This action cannot be undone.`,
      () => {

      },
      () => {
        this.loaderService.show.next(true);

        this.repositoryService.deleteRepository(this.orgName, this.repoName).then(result => {
          Alert.success(CommonConstant.MESSAGE.DELETED);

          this.router.navigate([`app/${this.repo.isOrganization ? 'organization' : 'user'}/${this.orgName}/image`]);

          this.loaderService.show.next(false);
        });
      },
      'Cancel',
      'Delete Image'
    );
  }

  /**
   * make public/private 클릭
   */
  public changeVisibilityClick() {
    let title = this.repo.isPublic ? 'Private' : 'Public';

    this.confirmPopupService.show(
      `Make ${title}`,
      this.repo.isPublic ? `This Repository is currently public. Are you sure you want to make this image private?` : `This Repository is currently private. Are you sure you want to make this image public?`,
      null,
      () => {

      },
      () => {
        this.loaderService.show.next(true);

        this.repositoryService.changeVisibility(this.orgName, this.repoName, !this.repo.isPublic).then(result => {
          this.repo.isPublic = !this.repo.isPublic;

          Alert.success(CommonConstant.MESSAGE.SUCCESS);

          this.loaderService.show.next(false);
        });
      },
      'Cancel',
      `Make ${title}`
    );
  }

  public getSelectData(member: Member.Entity = null) {
    let data = [
      new Select.Value('Read', 'read', false, 'Can view and pull from the repository', 'type-read'),
      new Select.Value('Write', 'write', false, 'Can view, pull and push to the repository', 'type-write'),
      new Select.Value('Admin', 'admin', false, 'Full admin access, pull and push on the repository', 'type-admin')
    ];
    if (member) {
      data.forEach(value => {
        if (value.value == member.role) {
          value.checked = true;
        }
      })
    }

    return data;
  }

  private getMemberList() {
    // this.memberList = [];
    this.loaderService.show.next(true);

    this.repositoryService.getMemberList(this.orgName, this.repoName).then(result => {
      this.memberList = result.members;

      this.loaderService.show.next(false);
    });
  }

}
