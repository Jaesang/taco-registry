import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {SharedModule} from "../../../common/shared.module";
import {TagInfoComponent} from "./tag-info.component";
import {RepositoryHeaderModule} from "../repository-header.module";
import {TagService} from "./tag.service";
import {TagLayerPopupModule} from "../popup/tag-layer-popup.module";
import {TagSecurityPopupModule} from "../popup/tag-security-popup.module";
import {BuildHistoryService} from "../build-history/build-history.service";

@NgModule({
  imports: [
    SharedModule,
    RepositoryHeaderModule,
    TagLayerPopupModule,
    TagSecurityPopupModule,
    RouterModule.forChild([
      {
        path: '',
        component: TagInfoComponent
      }
    ])
  ],
  declarations: [
    TagInfoComponent
  ],
  providers: [
    TagService,
    BuildHistoryService
  ]
})
export class TagInfoModule {
}
