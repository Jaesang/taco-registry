import {Component, ElementRef, Injector, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {PageComponent} from '../../../common/component/page.component';
import {fromEvent} from 'rxjs/observable/fromEvent';
import {SuperUserService} from './super-user.service';
import {SuperUser} from './super-user.value';
import * as _ from 'lodash';
import {UserService} from "../../user/user.service";
import {Alert} from "../../../common/utils/alert-util";
import {ConfirmPopupService} from "../../../common/component/confirm-popup/confirm-popup.service";

@Component({
  selector: '[user-list]',
  templateUrl: 'user-list.component.html',
  host: { '[class.page-user-management]': 'true' }
})
export class UserListComponent extends PageComponent implements OnInit, OnDestroy {

  public users: SuperUser.Entity[] = [];

  public searchKey: string;

  public sortProperty: string = '';
  public sortDirection: string = '';

  public addUser: SuperUser.Entity = new SuperUser.Entity();

  public currentSelectedUser: SuperUser.Entity;
  public currentSettingIndex: number = -1;

  public verifyNewPassword: string;

  public showCreateUserPopup: boolean = false;
  public showCreateUserConfirmPopup: boolean = false;
  public showChangeEmailPopup: boolean = false;
  public showChangePasswordPopup: boolean = false;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              public userService: UserService,
              private superUserService: SuperUserService,
              private confirmPopupService: ConfirmPopupService) {
    super(elementRef, injector);
  }

  ngOnInit() {

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
      this.addUser = result.data;
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

  /**
   * 정렬
   * @param property
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
  }

  /**
   * email 변경 클릭
   */
  public changeUserEmailClick() {
    this.loaderService.show.next(true);

    let user = new SuperUser.Entity();
    user.username = this.currentSelectedUser.username;
    user.email = this.currentSelectedUser.email;

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

  private getUserList() {
    this.loaderService.show.next(true);

    this.superUserService.getUsers()
      .then(result => {

        const users: SuperUser.Entity[] = result.data;
        if (_.isNil(users)) {
          this.users = [];
        }

        this.users = users;

        this.loaderService.show.next(false);
      });
  }

}
