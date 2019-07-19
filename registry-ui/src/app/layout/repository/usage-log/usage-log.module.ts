import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {SharedModule} from "../../../common/shared.module";
import {UsageLogComponent} from "./usage-log.component";
import {RepositoryHeaderModule} from "../repository-header.module";
import {LogsModule} from "../../organization/usage-log/logs.module";
import {UsageLogService} from "../../organization/usage-log/usage-log.service";

@NgModule({
  imports: [
    SharedModule,
    RepositoryHeaderModule,
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
