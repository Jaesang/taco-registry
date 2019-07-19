import {NgModule} from '@angular/core';
import {HttpModule} from '@angular/http';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {TableSortPipe} from "./pipe/table-sort.pipe";
import {FilterPipe} from './pipe/filter.pipe';
import {SelectBoxModule} from "./component/selectbox/select-box.module";
import {PaginationModule} from "./component/pagination/pagination.module";
import {FileUploadModule} from "./component/file-upload/file-upload.module";
import {AutocompleteModule} from "./component/autocomplete/autocomplete.module";
import {FileSizePipe} from "./pipe/filesize.pipe";
import {CodemirrorComponent} from "./component/codemirror/codemirror.component";
import {DateTimeFormatPipe} from "./pipe/date-time.pipe";

export const MODULES = [
	CommonModule,
	FormsModule,
	HttpModule,
	RouterModule,
  SelectBoxModule,
  PaginationModule,
  FileUploadModule,
  AutocompleteModule
];

export const COMPONENTS = [
  TableSortPipe,
  FilterPipe,
  FileSizePipe,
  DateTimeFormatPipe,
  CodemirrorComponent
];

/**
 * Common Modules and Components
 */
@NgModule({
	imports: [
		...MODULES
	],
	declarations: [
		...COMPONENTS
	],
	exports: [
		...MODULES,
		...COMPONENTS
	]
})
export class SharedModule {
}
