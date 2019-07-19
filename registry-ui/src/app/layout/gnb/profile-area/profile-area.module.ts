import {NgModule} from '@angular/core';
import {ProfileAreaComponent} from './profile-area.component';
import {SharedModule} from "../../../common/shared.module";

@NgModule({
  imports: [
    SharedModule
  ],
  declarations: [
    ProfileAreaComponent
  ],
  exports: [
    ProfileAreaComponent
  ]
})
export class ProfileAreaModule {
}
