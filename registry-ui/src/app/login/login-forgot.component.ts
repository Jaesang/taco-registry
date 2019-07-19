import {Component, OnInit, ElementRef, Injector} from '@angular/core';
import {AbstractComponent} from "../common/component/abstract.component";

@Component({
  selector: 'app-login-forgot',
  templateUrl: './login-forgot.component.html'
})
export class LoginForgotComponent extends AbstractComponent implements OnInit {

  constructor(protected elementRef: ElementRef,
              protected injector: Injector) {

    super(elementRef, injector);
  }

  ngOnInit() {

  }

  public signInClick() {
    this.router.navigate(['login']);
  }
}
