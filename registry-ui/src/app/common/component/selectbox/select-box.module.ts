import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SelectBoxComponent} from './select-box.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    SelectBoxComponent
  ],
  exports: [
    SelectBoxComponent
  ]
})
export class SelectBoxModule {
}
