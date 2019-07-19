import {NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {SharedModule} from "../../../common/shared.module";
import {BuildHistoryComponent} from "./build-history.component";
import {RepositoryHeaderModule} from "../repository-header.module";
import {BuildPopupModule} from "../popup/build-popup.module";
import {BuildDetailPopupModule} from "../popup/build-detail-popup.module";

@NgModule({
  imports: [
    SharedModule,
    RepositoryHeaderModule,
    BuildPopupModule,
    BuildDetailPopupModule,
    RouterModule.forChild([
      {
        path: '',
        component: BuildHistoryComponent
      }
    ])
  ],
  declarations: [
    BuildHistoryComponent
  ],
  providers: [
  ]
})
export class BuildHistoryModule {
}
