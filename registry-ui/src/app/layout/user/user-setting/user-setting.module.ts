import {NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {SharedModule} from "../../../common/shared.module";
import {UserSettingComponent} from "./user-setting.component";

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        component: UserSettingComponent
      }
    ])
  ],
  declarations: [
    UserSettingComponent
  ]
})
export class UserSettingModule {
}
