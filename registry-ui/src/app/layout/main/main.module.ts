import {NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {MainComponent} from "./main.component";
import {SharedModule} from "../../common/shared.module";
import {RepositoryService} from "../repository/repository.service";
import {OrganizationAreaModule} from "../gnb/organization-area/organization-area.module";
import {ProfileAreaModule} from "../gnb/profile-area/profile-area.module";
import {MainService} from "./main.service";
import {OrganizationService} from "../organization/organization.service";
import {TagSecurityPopupModule} from "../repository/popup/tag-security-popup.module";
import {TagService} from "../repository/tag-info/tag.service";

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        component: MainComponent
      }
    ]),
    OrganizationAreaModule,
    ProfileAreaModule,
    TagSecurityPopupModule
  ],
  declarations: [
    MainComponent
  ],
  exports: [
    MainComponent
  ],
  providers: [
    RepositoryService,
    OrganizationService,
    MainService,
    TagService
  ]
})
export class MainModule {
}
