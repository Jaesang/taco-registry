import {Component, ElementRef, Injector, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AbstractComponent} from "../../../common/component/abstract.component";
import {UserService} from "../../user/user.service";

@Component({
  selector: '[profile-area]',
  templateUrl: 'profile-area.component.html',
  host: { '[class.profile-area]': 'true' }
})
export class ProfileAreaComponent extends AbstractComponent implements OnInit, OnDestroy {

  /**
   * 사용자 이름
   */
  public userName = '';

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              public router: Router,
              private userService: UserService) {
    super(elementRef, injector);
  }

  ngOnInit() {
    this.userName = this.userService.user.username;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  /**
   * 로그인 페이지로 이동
   */
  public moveLoginPage() {
    this.userService.logout().then(result => {
      this.router.navigate([ 'login' ]);
    });
  }

  /**
   * 계정 설정 페이지로 이동
   */
  public moveAccountSettingsPage() {
    // TODO: ACCOUNT_SETTINGS_PAGE 설정 필요 현제 공백값으로 세팅되어 있음.
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([ `app/user/${this.userName}/user-setting` ]);
  }

  /**
   * 나의 저장소로 이동
   */
  public moveMyRepository() {
    // TODO: MY_REPOSITORY 설정 필요 현제 공백값으로 세팅되어 있음.
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate([ `app/user/${this.userName}/image` ]);
  }

}
