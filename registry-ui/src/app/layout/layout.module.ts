import {NgModule} from '@angular/core';
import {LayoutComponent} from './layout.component';
import {RouterModule} from '@angular/router';
import {GnbModule} from "./gnb/gnb.module";
import {LnbModule} from "./lnb/lnb.module";
import {SharedModule} from "../common/shared.module";
import {OrganizationService} from "./organization/organization.service";
import {OrganizationGuard} from "./organization/organization.guard";
import {UserGuard} from "./user/user.guard";
import {RepositoryGuard} from "./repository/repository.guard";
import {RepositoryService} from "./repository/repository.service";
import {AdminGuard} from "./admin/admin.guard";
import {CreateRepoPopupComponent} from "./repository/popup/create-popup.component";
import {BuildHistoryService} from "./repository/build-history/build-history.service";

@NgModule({
  imports: [
    SharedModule,
    GnbModule,
    LnbModule,
    RouterModule.forChild([
      {
        path: '',
        component: LayoutComponent,
        children: [
          {
            path: '',
            redirectTo: 'main',
            pathMatch: 'full'
          },
          {
            path: 'main',
            loadChildren: 'app/layout/main/main.module#MainModule',
            canActivate: [
              // OrganizationGuard
            ]
          },
          {
            path: 'organization/:org',
            loadChildren: 'app/layout/organization/organization.module#OrganizationModule',
            canActivate: [
              OrganizationGuard
            ]
          },
          {
            path: 'user/:user',
            loadChildren: 'app/layout/user/user.module#UserModule',
            canActivate: [
              UserGuard
            ]
          },
          {
            path: 'image/:org/:repo',
            loadChildren: 'app/layout/repository/repository.module#RepositoryModule',
            canActivate: [
              RepositoryGuard
            ]
          },
          {
            path: 'admin',
            loadChildren: 'app/layout/admin/admin.module#AdminModule',
            canActivate: [
              AdminGuard
            ]
          },
          {
            path: 'error',
            loadChildren: 'app/layout/error/error.module#ErrorModule'
          },
        ]
      }
    ])
  ],
  declarations: [
    LayoutComponent,
    CreateRepoPopupComponent
  ],
  providers: [
    OrganizationService,
    OrganizationGuard,
    UserGuard,
    RepositoryService,
    RepositoryGuard,
    AdminGuard,
    BuildHistoryService
  ]
})
export class LayoutModule {
}
