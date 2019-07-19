import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {LoginComponent} from './login/login.component';
import {RouterModule} from '@angular/router';
import {CookieService} from 'ng2-cookies';
import {CommonService} from './common/service/common.service';
import {LoginService} from './login/login.service';
import {SharedModule} from './common/shared.module';
import {UserService} from './layout/user/user.service';
import {ConfirmPopupComponent} from './common/component/confirm-popup/confirm-popup.component';
import {ConfirmPopupService} from './common/component/confirm-popup/confirm-popup.service';
import {SuperUserService} from "./layout/admin/user-list/super-user.service";
import {FileService} from "./common/service/file.service";
import {DockerService} from "./common/service/docker.service";
import {LoaderComponent} from "./common/component/loader/loader.component";
import {LoaderService} from "./common/component/loader/loader.service";
import {LoginForgotComponent} from "./login/login-forgot.component";
import {TestComponent} from "./test/test.component";
import {LoginGuard} from "./login/login.guard";
import {LoginCheckGuard} from "./common/guard/login-check.guard";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ConfirmPopupComponent,
    LoaderComponent,
    LoginForgotComponent,
    TestComponent
  ],
  imports: [
    BrowserModule,
    SharedModule,
    RouterModule.forRoot([
      {
        path: '',
        redirectTo: 'app',
        pathMatch: 'full'
      },
      {
        path: 'login',
        component: LoginComponent,
        canActivate: [
          LoginGuard
        ]
      },
      {
        path: 'login/forgot',
        component: LoginForgotComponent
      },
      {
        path: 'app',
        loadChildren: 'app/layout/layout.module#LayoutModule',
        canActivate: [
          LoginCheckGuard
        ]
      },
      {
        path: 'test',
        component: TestComponent
      },
      {
        path: '**',
        redirectTo: 'app/error/404'
      }
    ])
  ],
  providers: [
    CommonService,
    CookieService,
    LoginService,
    UserService,
    ConfirmPopupService,
    SuperUserService,
    FileService,
    DockerService,
    LoaderService,
    LoginGuard,
    LoginCheckGuard
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule {
}
