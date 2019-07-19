import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {UsageLogComponent} from "./usage-log.component";
import {SharedModule} from "../../../common/shared.module";
import {LogsModule} from "./logs.module";
import {UsageLogService} from "./usage-log.service";

@NgModule({
  imports: [
    SharedModule,
    LogsModule,
    RouterModule.forChild([
      {
        path: '',
        component: UsageLogComponent
      }
    ])
  ],
  declarations: [
    UsageLogComponent
  ],
  providers: [
    UsageLogService
  ]
})
export class UsageLogModule {
}
