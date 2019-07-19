import {NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {SharedModule} from "../../common/shared.module";
import {OrganizationMenuGuard} from "./organization-menu.guard";

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
          OrganizationMenuGuard
        ]
      },
      {
        path: 'team',
        loadChildren: 'app/layout/organization/role/role.module#RoleModule',
        canActivate: [
          OrganizationMenuGuard
        ]
      },
      {
        path: 'usage-log',
        loadChildren: 'app/layout/organization/usage-log/usage-log.module#UsageLogModule',
        canActivate: [
          OrganizationMenuGuard
        ]
      },
      {
        path: 'setting',
        loadChildren: 'app/layout/organization/setting/setting.module#SettingModule',
        canActivate: [
          OrganizationMenuGuard
        ]
      },
    ])
  ],
  providers: [
    OrganizationMenuGuard
  ]
})
export class OrganizationModule {
}
