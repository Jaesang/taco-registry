import {NgModule} from '@angular/core';
import {LnbComponent} from "./lnb.component";
import {SharedModule} from "../../common/shared.module";

@NgModule({
  imports: [
    SharedModule
  ],
  declarations: [
    LnbComponent,
  ],
  exports: [
    LnbComponent
  ]
})
export class LnbModule {
}
