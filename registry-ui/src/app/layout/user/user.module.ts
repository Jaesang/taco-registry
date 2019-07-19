import {NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {SharedModule} from "../../common/shared.module";
import {UserMenuGuard} from "./user-menu.guard";

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        redirectTo: 'image',
        pathMatch: 'full'
      },
      {
        path: 'image',
        loadChildren: 'app/layout/organization/repository/repository.module#RepositoryModule',
        canActivate: [
          UserMenuGuard
        ]
      },
      {
        path: 'usage-log',
        loadChildren: 'app/layout/organization/usage-log/usage-log.module#UsageLogModule',
        canActivate: [
          UserMenuGuard
        ]
      },
      {
        path: 'user-setting',
        loadChildren: 'app/layout/user/user-setting/user-setting.module#UserSettingModule',
        canActivate: [
          UserMenuGuard
        ]
      }
    ])
  ],
  providers: [
    UserMenuGuard
  ]
})
export class UserModule {
}
