import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {SharedModule} from "../../../common/shared.module";
import {TagHistoryComponent} from "./tag-history.component";
import {RepositoryHeaderModule} from "../repository-header.module";
import {TagService} from "../tag-info/tag.service";
import {TagLayerPopupModule} from "../popup/tag-layer-popup.module";

@NgModule({
  imports: [
    SharedModule,
    RepositoryHeaderModule,
    TagLayerPopupModule,
    RouterModule.forChild([
      {
        path: '',
        component: TagHistoryComponent
      }
    ])
  ],
  declarations: [
    TagHistoryComponent
  ],
  providers: [
    TagService
  ]
})
export class TagHistoryModule {
}
