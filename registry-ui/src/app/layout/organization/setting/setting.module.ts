import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {SettingComponent} from "./setting.component";
import {SharedModule} from "../../../common/shared.module";
import {TeamService} from "../role/team.service";

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        component: SettingComponent
      }
    ])
  ],
  declarations: [
    SettingComponent
  ],
  providers: [
    TeamService
  ]
})
export class SettingModule {
}
