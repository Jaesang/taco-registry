import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {TagSecurityPopupComponent} from "./tag-security-popup.component";
import {TagSecurityPackagesComponent} from "./tag-security-packages.component";
import {TagSecurityVulnerabilityComponent} from "./tag-security-vulnerability.component";
import {SharedModule} from "../../../common/shared.module";

@NgModule({
  imports: [
    SharedModule
  ],
  declarations: [
    TagSecurityPopupComponent,
    TagSecurityPackagesComponent,
    TagSecurityVulnerabilityComponent
  ],
  exports: [
    TagSecurityPopupComponent
  ],
  providers: [
  ]
})
export class TagSecurityPopupModule {
}
