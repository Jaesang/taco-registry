import {Component, OnInit, ElementRef, Injector} from '@angular/core';
import {PageComponent} from "../../../common/component/page.component";
import {UserService} from "../user.service";
import {User} from "../user.value";
import {Alert} from "../../../common/utils/alert-util";
import {CommonConstant} from "../../../common/constant/common-constant";
import {ConfirmPopupService} from "../../../common/component/confirm-popup/confirm-popup.service";
import {RepositoryService} from "../../repository/repository.service";

@Component({
  selector: 'user-setting',
  templateUrl: 'user-setting.component.html'
})
export class UserSettingComponent extends PageComponent implements OnInit {

  public showChangePasswordPopup: boolean = false;

  public user: User.Entity;

  public repoCount: number;

  public currentPassword: string;
  public newPassword: string;
  public newPasswordConfirm: string;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private userService: UserService,
              private repositoryService: RepositoryService,
              private confirmPopupService: ConfirmPopupService) {

    super(elementRef, injector);
  }

  ngOnInit() {

    this.user = this.userService.user;

    // repository list 조회
    this.repositoryService.getRepositoryListCount(this.user.username).then(result => {
      this.repoCount = result;
    });
  }

  /**
   * 패스워드 변경 팝업 클릭
   */
  public changePasswordPopupClick() {
    this.currentPassword = '';
    this.newPassword = '';
    this.newPasswordConfirm = '';

    this.showChangePasswordPopup = true;
  }

  /**
   * 패스워드 변경 클릭
   */
  public changePasswordClick() {
    if (this.newPassword != this.newPasswordConfirm) {
      return;
    }

    this.loaderService.show.next(true);
    this.userService.verifyPassword(this.currentPassword).then(result => {
      if (result) {
        this.userService.updatePassword(this.newPassword).then(result => {
          this.showChangePasswordPopup = false;

          Alert.success(CommonConstant.MESSAGE.SUCCESS);

          this.loaderService.show.next(false);
        }).catch(reason => {
          let body = JSON.parse(reason._body);
          Alert.error(body.message);

          this.loaderService.show.next(false);
        });
      }
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      Alert.error(body.message);

      this.loaderService.show.next(false);
    });
  }

  /**
   * repo 클릭
   */
  public repoClick() {
    this.router.navigate([`app/user/${this.user.username}/image`]);
  }

  /**
   * 계정 삭제 클릭
   */
  public deleteAccountClick() {
    this.confirmPopupService.show(
      'Delete Account',
      'Are you sure you want to delete the Account?',
      `Deleting an account is non-reversable and will delete all of the account's data`,
      () => {

      },
      () => {
        this.loaderService.show.next(true);

        this.userService.deleteUser().then(result => {
          this.router.navigate(['login']);

          Alert.success(CommonConstant.MESSAGE.DELETED);

          this.loaderService.show.next(false);
        }).catch(reason => {
          let body = JSON.parse(reason._body);
          Alert.error(body.message);

          this.loaderService.show.next(false);
        });
      },
      'Cancel',
      'Delete Account'
    );
  }

}
