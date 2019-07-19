import {NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {SharedModule} from "../../common/shared.module";
import {RepositoryMenuGuard} from "./repository-menu.guard";
import {BuildHistoryService} from "./build-history/build-history.service";
import {BuildPopupModule} from "./popup/build-popup.module";

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        redirectTo: 'info',
        pathMatch: 'full'
      },
      {
        path: 'info',
        loadChildren: 'app/layout/repository/info/info.module#InfoModule',
        canActivate: [
          RepositoryMenuGuard
        ]
      },
      {
        path: 'tag-info',
        loadChildren: 'app/layout/repository/tag-info/tag-info.module#TagInfoModule',
        canActivate: [
          RepositoryMenuGuard
        ]
      },
      {
        path: 'tag-history',
        loadChildren: 'app/layout/repository/tag-history/tag-history.module#TagHistoryModule',
        canActivate: [
          RepositoryMenuGuard
        ]
      },
      {
        path: 'build',
        loadChildren: 'app/layout/repository/build-history/build-history.module#BuildHistoryModule',
        canActivate: [
          RepositoryMenuGuard
        ]
      },
      {
        path: 'usage-log',
        loadChildren: 'app/layout/repository/usage-log/usage-log.module#UsageLogModule',
        canActivate: [
          RepositoryMenuGuard
        ]
      },
      {
        path: 'setting',
        loadChildren: 'app/layout/repository/setting/setting.module#SettingModule',
        canActivate: [
          RepositoryMenuGuard
        ]
      }
    ])
  ],
  providers: [
    RepositoryMenuGuard,
    BuildHistoryService
  ]
})
export class RepositoryModule {
}
