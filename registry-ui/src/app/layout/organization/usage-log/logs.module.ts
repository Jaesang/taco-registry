import {NgModule} from "@angular/core";
import {LogsComponent} from "./logs.component";
import {LogsService} from "./logs.service";
import {SharedModule} from "../../../common/shared.module";

@NgModule({
  imports: [
    SharedModule
  ],
  declarations: [
    LogsComponent
  ],
  exports: [
    LogsComponent
  ],
  providers: [
    LogsService
  ]
})
export class LogsModule {
}
