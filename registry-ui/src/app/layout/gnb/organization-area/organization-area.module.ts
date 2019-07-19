import {NgModule} from '@angular/core';
import {OrganizationAreaComponent} from './organization-area.component';
import {SharedModule} from "../../../common/shared.module";
import {CreatePopupComponent} from "../../organization/popup/create-popup.component";

@NgModule({
  imports: [
    SharedModule
  ],
  declarations: [
    OrganizationAreaComponent,
    CreatePopupComponent
  ],
  exports: [
    OrganizationAreaComponent
  ]
})
export class OrganizationAreaModule {
}
