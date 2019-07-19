import {Component, ElementRef, Injector, Input, OnDestroy, OnInit, Output, EventEmitter} from "@angular/core";
import {AbstractComponent} from "../abstract.component";

@Component({
  selector: '[file-upload]',
  templateUrl: 'file-upload.component.html',
})
export class FileUploadComponent extends AbstractComponent implements OnInit, OnDestroy {

  private DEFAULT_DESC = 'Please select a Dockerfile';
  private DEFAULT_SUCCESS_DESC = 'Dockerfile found and valid';
  private DEFAULT_ERROR_DESC = 'File chosen is not valid Dockerfile';

  @Input()
  public buttonLabel: string = 'Select Dockerfile';

  @Input()
  public description: string = this.DEFAULT_DESC;

  public isSuccess: boolean;
  public isError: boolean;

  private _status: boolean;
  @Input()
  public set status(status: string) {
    this.isSuccess = false;
    this.isError = false;

    if (status) {
      if (status == 'success') {
        this.isSuccess = true;
        this.description = this.DEFAULT_SUCCESS_DESC;
      } else {
        this.isError = true;
        this.description = this.DEFAULT_ERROR_DESC;
      }
    } else {
      this.description = this.DEFAULT_DESC;
    }
  }

  @Output()
  private onChange: EventEmitter<string> = new EventEmitter();

  @Output()
  private onBrowserOpen: EventEmitter<string> = new EventEmitter();

  public file: File;
  public contents: string;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector) {
    super(elementRef, injector);
  }

  ngOnInit() {
  }

  ngOnDestroy(): void {
  }

  public init() {
    if (this.file) {
      this.file = null;
    }

    this.contents = null;
    this.description = this.DEFAULT_DESC;
    this.isSuccess = false;
    this.isError = false;
  }

  /**
   * 파일 첨부
   *
   * @param event
   */
  public changeFile($event): void {

    this.contents = null;
    this.file = null;

    let input = $event.target;
    if (input.files.length > 0) {
      let reader = new FileReader();
      this.file = input.files[0];
      reader.readAsText(input.files[0]);

      reader.onload = (data) => {
        this.contents = reader.result;
        this.onChange.emit(this.contents);
      }

      reader.onerror = function () {
        alert('Unable to read ' + input.files[0]);
      };
    }
  }

  /**
   * 파일 찾기 클릭
   */
  public findClick() {
    this.onBrowserOpen.emit();
  }

}
