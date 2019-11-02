import {Component, ElementRef, Injector, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {PageComponent} from '../../../common/component/page.component';
import {fromEvent} from 'rxjs/observable/fromEvent';
import {SuperUserService} from './super-user.service';
import {SuperUser} from './super-user.value';
import * as _ from 'lodash';
import {UserService} from "../../user/user.service";
import {Alert} from "../../../common/utils/alert-util";
import {ConfirmPopupService} from "../../../common/component/confirm-popup/confirm-popup.service";
import {Validate} from "../../../common/utils/validate-util";
import {Page, Sort} from '../../../common/value/result-value';
import {PaginationComponent} from '../../../common/component/pagination/pagination.component';

@Component({
  selector: '[user-list]',
  templateUrl: 'user-list.component.html',
  host: { '[class.page-user-management]': 'true' }
})
export class UserListComponent extends PageComponent implements OnInit, OnDestroy {

  public users: SuperUser.Entity[] = [];

  public searchKey: string;

  public addUser: SuperUser.Entity = new SuperUser.Entity();

  public currentSelectedUser: SuperUser.Entity;
  public currentSettingIndex: number = -1;

  public verifyNewPassword: string;

  public showCreateUserPopup: boolean = false;
  public showCreateUserConfirmPopup: boolean = false;
  public showChangeEmailPopup: boolean = false;
  public showChangePasswordPopup: boolean = false;

  @ViewChild('pagination')
  private pagination: PaginationComponent;

  public page: Page;
  public sort: Sort;

  public showSearchNodata: boolean = false;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              public userService: UserService,
              private superUserService: SuperUserService,
              private confirmPopupService: ConfirmPopupService) {
    super(elementRef, injector);
  }

  ngOnInit() {

    this.initPage();
    this.getUserList();
  }

  ngOnDestroy() {
    super.ngOnDestroy();
  }

  /**
   * 유저 등록 클릭(팝업)
   */
  public createNewUserClick() {
    this.showCreateUserPopup = true;
    this.addUser = new SuperUser.Entity();
  }

  /**
   * 유저 등록 클릭
   */
  public createUserClick() {
    this.loaderService.show.next(true);

    this.superUserService.createUser(this.addUser).then(result => {
      this.addUser = result;
      this.showCreateUserPopup = false;
      this.showCreateUserConfirmPopup = true;

      this.loaderService.show.next(false);

      this.getUserList();
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      Alert.error(body.message);

      this.loaderService.show.next(false);
    });
  }

  public search() {
    this.getUserList();
  }

  /**
   * paging
   * @param page
   */
  public pageClick(page: number) {
    this.page.number = page;

    this.getUserList();
  }

  /**
   * 정렬
   * @param property
   */
  public sortClick(property: string) {
    if (this.sort.property == property) {
      this.sort.direction = this.sort.direction == 'desc' ? 'asc' : 'desc';
    } else {
      this.sort.direction = 'desc';
    }
    this.sort.property = property;

    this.getUserList();
  }

  /**
   * setting 클릭
   * @param index
   */
  public settingClick(index: number) {
    if (this.currentSettingIndex == index) {
      this.currentSettingIndex = -1;
    } else {
      this.currentSettingIndex = index;
    }
  }

  /**
   * email 변경 클릭(팝업)
   * @param user
   */
  public changeEmailPopupClick(user: SuperUser.Entity) {
    this.currentSettingIndex = -1;
    this.showChangeEmailPopup = true;
    this.currentSelectedUser = user;
    this.currentSelectedUser.newEmail = user.email;
  }

  /**
   * email 변경 클릭
   */
  public changeUserEmailClick() {
    this.loaderService.show.next(true);

    let user = new SuperUser.Entity();
    user.username = this.currentSelectedUser.username;
    user.email = this.currentSelectedUser.newEmail;

    this.superUserService.updateUser(user).then(result => {
      this.loaderService.show.next(false);
      this.getUserList();

      this.showChangeEmailPopup = false;
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      Alert.error(body.message);

      this.loaderService.show.next(false);
    });
  }

  /**
   * 비밀번호 변경 클릭(팝업)
   * @param user
   */
  public changePasswordPopupClick(user: SuperUser.Entity) {
    this.currentSettingIndex = -1;
    this.showChangePasswordPopup = true;
    this.currentSelectedUser = user;
    this.currentSelectedUser.password = '';
    this.verifyNewPassword = '';
  }

  /**
   * 비밀번호 변경 클릭
   */
  public changeUserPasswordClick() {
    this.loaderService.show.next(true);

    let user = new SuperUser.Entity();
    user.username = this.currentSelectedUser.username;
    user.password = this.currentSelectedUser.password;

    this.superUserService.updateUser(user).then(result => {
      this.loaderService.show.next(false);
      this.getUserList();

      this.showChangePasswordPopup = false;
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      Alert.error(body.message);

      this.loaderService.show.next(false);
    });
  }

  /**
   * 유저 삭제 클릭
   * @param user
   */
  public deleteUserClick(user: SuperUser.Entity) {
    this.currentSettingIndex = -1;
    this.currentSelectedUser = user;

    this.confirmPopupService.show(
      'Delete User',
      'Continue with deletion of this User?',
      `This action cannot be undone and will delete any repositories owned by the user.`,
      () => {

      },
      () => {
        this.loaderService.show.next(true);

        this.superUserService.deleteUser(this.currentSelectedUser.username).then(result => {
          this.loaderService.show.next(false);
          this.getUserList();
        }).catch(reason => {
          let body = JSON.parse(reason._body);
          Alert.error(body.message);
        });
      },
      'Cancel',
      'Delete User'
    );
  }

  /**
   * disable 클릭
   * @param user
   */
  public disableUser(user: SuperUser.Entity) {
    this.currentSettingIndex = -1;
    this.currentSelectedUser = user;

    this.confirmPopupService.show(
      `${user.enabled ? 'Disable' : 'Enable'} User`,
      `${user.enabled ?  'Are you sure you want to disable this user? This user will be unable to login, pull or push.' : 
        'Are you sure you want to reenable this user? They will be able to login, pull or push.'}`,
      null,
      () => {

      },
      () => {
        let u = new SuperUser.Entity();
        u.username = this.currentSelectedUser.username;
        u.enabled = !user.enabled;

        this.loaderService.show.next(true);

        this.superUserService.updateUser(u).then(result => {
          this.loaderService.show.next(false);

          this.getUserList();
        }).catch(reason => {
          let body = JSON.parse(reason._body);
          Alert.error(body.message);

          this.loaderService.show.next(false);
        });
      },
      'Cancel',
      `${user.enabled ? 'Disable' : 'Enable'} User`,
    );
  }

  public validateCreateUser() {
    if (!Validate.checkValidateWithPattern('^[a-z0-9_-]+$', this.addUser.username) || Validate.isEmpty(this.addUser.username)) {
      return false;
    }

    if (Validate.isEmpty(this.addUser.email)) {
      return false;
    }

    return true;
  }

  public validateChangeEmail() {
    if (Validate.isEmpty(this.currentSelectedUser.newEmail) || this.currentSelectedUser.email == this.currentSelectedUser.newEmail) {
      return false;
    }

    return true;
  }


  public validateChangePassword() {
    if (Validate.isEmpty(this.currentSelectedUser.password) || Validate.isEmpty(this.verifyNewPassword) || this.currentSelectedUser.password != this.verifyNewPassword) {
      return false;
    }

    return true;
  }

  /**
   * page 초기화
   * @param page
   */
  private initPage(page: Page = null) {
    let result: Page = new Page(page);
    if (page) {
      result.sort = this.sort;
    } else {
      result.number = 0;
      result.size = 20;
      this.sort = new Sort();
      this.sort.property = 'createdDate';
      this.sort.direction = 'desc';
      result.sort = this.sort;
    }

    this.page = result;
  }

  private getUserList() {
    this.loaderService.show.next(true);

    this.superUserService.getUsers(this.searchKey, this.page)
      .then(result => {

        this.users = result.content;
        this.initPage(result);
        this.pagination.init(this.page);

        if (!_.isNil(this.searchKey) && !this.users.length) {
          this.showSearchNodata = true;
        } else {
          this.showSearchNodata = false;
        }

        this.loaderService.show.next(false);
      });
  }

}
