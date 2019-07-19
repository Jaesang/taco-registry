import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {RepositoryHeaderComponent} from "./repository-header.component";
import {FileUploadModule} from "../../common/component/file-upload/file-upload.module";
import {BuildPopupModule} from "./popup/build-popup.module";
import {CopyAsPopupModule} from "./popup/copy-as-popup.module";

@NgModule({
  imports: [
    CommonModule,
    FileUploadModule,
    BuildPopupModule,
    CopyAsPopupModule
  ],
  declarations: [
    RepositoryHeaderComponent
  ],
  exports: [
    RepositoryHeaderComponent
  ]
})
export class RepositoryHeaderModule {
}
