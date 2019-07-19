import {NgModule} from "@angular/core";
import {RepositoryComponent} from "./repository.component";
import {RepositoryCardComponent} from "./repository-card.component";
import {RepositoryListComponent} from "./repository-list.component";
import {RouterModule} from "@angular/router";
import {RepositoryService} from "../../repository/repository.service";
import {SharedModule} from "../../../common/shared.module";

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        component: RepositoryComponent
      }
    ])
  ],
  declarations: [
    RepositoryComponent,
    RepositoryCardComponent,
    RepositoryListComponent
  ],
  providers: [
    RepositoryService
  ]
})
export class RepositoryModule {
}
