import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {BuildPopupComponent} from "./build-popup.component";
import {DockerService} from "../../../common/service/docker.service";
import {FileUploadModule} from "../../../common/component/file-upload/file-upload.module";
import {FormsModule} from "@angular/forms";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    FileUploadModule
  ],
  declarations: [
    BuildPopupComponent
  ],
  exports: [
    BuildPopupComponent
  ],
  providers: [
    DockerService
  ]
})
export class BuildPopupModule {
}
