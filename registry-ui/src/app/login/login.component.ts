import {Component, OnInit, ElementRef, Injector, ViewChild} from '@angular/core';
import {AbstractComponent} from "../common/component/abstract.component";
import {LoginService} from "./login.service";
import {UserService} from "../layout/user/user.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent extends AbstractComponent implements OnInit {

  @ViewChild('userIdInput')
  private userIdInput: any;

  public userId: string;
  public password: string;

  public showError: boolean;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private loginService: LoginService,
              private userService: UserService) {

    super(elementRef, injector);
  }

  ngOnInit() {
    this.userIdInput.nativeElement.focus();
  }

  public login() {
    this.loaderService.show.next(true);

    this.showError = false;

    this.loginService.login(this.userId, this.password).then(result => {
      if (result.access_token) {

        this.router.navigate(['app/main']);
      }

      this.loaderService.show.next(false);
    }).catch(reason => {
      this.showError = true;

      this.loaderService.show.next(false);
    });
  }

  public forgotClick() {
    this.router.navigate(['login/forgot']);
  }

}
