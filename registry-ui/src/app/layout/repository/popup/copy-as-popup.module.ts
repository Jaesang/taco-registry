import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {BuildPopupComponent} from "./build-popup.component";
import {DockerService} from "../../../common/service/docker.service";
import {FileUploadModule} from "../../../common/component/file-upload/file-upload.module";
import {CopyAsPopupComponent} from "./copy-as-popup.component";
import {BuildPopupModule} from "./build-popup.module";
import {SharedModule} from "../../../common/shared.module";

@NgModule({
  imports: [
    SharedModule,
    FileUploadModule,
    BuildPopupModule
  ],
  declarations: [
    CopyAsPopupComponent
  ],
  exports: [
    CopyAsPopupComponent
  ],
  providers: [

  ]
})
export class CopyAsPopupModule {
}
