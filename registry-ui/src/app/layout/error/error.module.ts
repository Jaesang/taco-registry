import {NgModule} from '@angular/core';
import {Error404Component} from "./404.component";
import {RouterModule} from "@angular/router";
import {Error500Component} from "./500.component";
import {Error403Component} from "./403.component";

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        redirectTo: '404',
        pathMatch: 'full'
      },
      {
        path: '404',
        component: Error404Component
      },
      {
        path: '500',
        component: Error500Component
      },
      {
        path: '403',
        component: Error403Component
      }
    ])
  ],
  declarations: [
    Error404Component,
    Error500Component,
    Error403Component
  ],
  providers: [
  ]
})
export class ErrorModule {
}
