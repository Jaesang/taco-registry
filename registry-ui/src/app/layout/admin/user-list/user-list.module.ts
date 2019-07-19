import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../../../common/shared.module';
import {UserListComponent} from './user-list.component';
import {SuperUserService} from './super-user.service';

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        component: UserListComponent
      }
    ])
  ],
  declarations: [
    UserListComponent
  ],
  providers: [
    SuperUserService
  ]
})
export class UserListModule {
}
