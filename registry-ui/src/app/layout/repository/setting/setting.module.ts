import {NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {SettingComponent} from "./setting.component";
import {SharedModule} from "../../../common/shared.module";
import {TeamService} from "../../organization/role/team.service";
import {RepositoryHeaderModule} from "../repository-header.module";

@NgModule({
  imports: [
    SharedModule,
    RepositoryHeaderModule,
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
