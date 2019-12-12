import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {SharedModule} from "../../../common/shared.module";
import {InfoComponent} from "./info.component";
import {RepositoryHeaderModule} from "../repository-header.module";
import {BuildDetailPopupModule} from "../popup/build-detail-popup.module";
import {BuildPopupModule} from "../popup/build-popup.module";
import {TagSecurityPopupModule} from "../popup/tag-security-popup.module";
import {TagService} from "../tag-info/tag.service";

@NgModule({
  imports: [
    SharedModule,
    RepositoryHeaderModule,
    BuildDetailPopupModule,
    TagSecurityPopupModule,
    BuildPopupModule,
    RouterModule.forChild([
      {
        path: '',
        component: InfoComponent
      }
    ])
  ],
  declarations: [
    InfoComponent
  ],
  providers: [
    TagService
  ]
})
export class InfoModule {
}
