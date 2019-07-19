import {NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {SharedModule} from "../../common/shared.module";
import {AdminMenuGuard} from "./admin-menu.guard";

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        redirectTo: 'user-list',
        pathMatch: 'full'
      },
      {
        path: 'user-list',
        loadChildren: 'app/layout/admin/user-list/user-list.module#UserListModule',
        canActivate: [
          AdminMenuGuard
        ]
      }
    ])
  ],
  providers: [
    AdminMenuGuard
  ]
})
export class AdminModule {
}
