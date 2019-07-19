import {NgModule} from '@angular/core';
import {GnbComponent} from "./gnb.component";
import {OrganizationAreaModule} from './organization-area/organization-area.module';
import {ProfileAreaModule} from './profile-area/profile-area.module';
import {SharedModule} from "../../common/shared.module";
import {MainService} from "../main/main.service";

@NgModule({
  imports: [
    SharedModule,
    OrganizationAreaModule,
    ProfileAreaModule
  ],
  declarations: [
    GnbComponent,
  ],
  exports: [
    GnbComponent
  ],
  providers: [
    MainService
  ]
})
export class GnbModule {
}
