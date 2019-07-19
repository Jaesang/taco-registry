import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {RoleSettingComponent} from "./role-setting.component";
import {SharedModule} from "../../../common/shared.module";
import {TeamService} from "./team.service";
import {TeamDetailComponent} from "./team-detail.component";

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        redirectTo: ':teamname',
        pathMatch: 'full'
      },
      // {
      //   path: '',
      //   component: RoleSettingComponent
      // },
      {
        path: ':teamname',
        component: TeamDetailComponent
      }
    ])
  ],
  declarations: [
    RoleSettingComponent,
    TeamDetailComponent
  ],
  providers: [
    TeamService
  ]
})
export class RoleModule {
}
