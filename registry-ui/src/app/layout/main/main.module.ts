import {NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {MainComponent} from "./main.component";
import {SharedModule} from "../../common/shared.module";
import {RepositoryService} from "../repository/repository.service";
import {OrganizationAreaModule} from "../gnb/organization-area/organization-area.module";
import {ProfileAreaModule} from "../gnb/profile-area/profile-area.module";
import {MainService} from "./main.service";
import {OrganizationService} from "../organization/organization.service";

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
    ProfileAreaModule
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
    MainService
  ]
})
export class MainModule {
}
