import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {BuildDetailPopupComponent} from "./build-detail-popup.component";
import {BuildHistoryService} from "../build-history/build-history.service";
import {SharedModule} from "../../../common/shared.module";

@NgModule({
  imports: [
    SharedModule
  ],
  declarations: [
    BuildDetailPopupComponent
  ],
  exports: [
    BuildDetailPopupComponent
  ],
  providers: [
    BuildHistoryService
  ]
})
export class BuildDetailPopupModule {
}
