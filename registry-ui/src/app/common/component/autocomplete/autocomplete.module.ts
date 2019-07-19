import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {AutocompleteComponent} from "./autocomplete.component";

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    AutocompleteComponent
  ],
  exports: [
    AutocompleteComponent
  ]
})
export class AutocompleteModule {
}
