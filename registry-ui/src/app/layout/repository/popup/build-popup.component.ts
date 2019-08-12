import {
  Component, OnInit, ElementRef, Injector, Input, Output, EventEmitter, OnChanges,
  SimpleChanges, ViewChild
} from "@angular/core";
import {AbstractComponent} from "../../../common/component/abstract.component";
import {DockerService} from "../../../common/service/docker.service";
import {FileService} from "../../../common/service/file.service";
import {FileUploadComponent} from "../../../common/component/file-upload/file-upload.component";
import {BuildHistoryService} from "../build-history/build-history.service";
import {RepositoryService} from "../repository.service";
import {OrganizationService} from "../../organization/organization.service";
import {CommonConstant} from "../../../common/constant/common-constant";
import {Alert} from "../../../common/utils/alert-util";
import {Build} from "../build-history/build-history.value";

@Component({
  selector: '[build-popup]',
  templateUrl: 'build-popup.component.html'
})
export class BuildPopupComponent extends AbstractComponent implements OnInit, OnChanges {

  @ViewChild(FileUploadComponent)
  private fileUploader: FileUploadComponent;

  @Input()
  public show: boolean = false;

  @Output()
  public onClose: EventEmitter<any> = new EventEmitter();

  public fileStatus: string;

  public orgName: string;
  public repoName: string;
  public errorMsg: string = '';

  private dockerFileContent: string;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private repositoryService: RepositoryService,
              private organizationService: OrganizationService,
              private dockerService: DockerService,
              private fileService: FileService,
              private buildService: BuildHistoryService) {

    super(elementRef, injector);
  }

  ngOnInit() {
  }

  public ngOnChanges(changes: SimpleChanges): void {
    for (let propName in changes) {
      if (propName === 'show') {
        this.errorMsg = '';
        this.orgName = '';
      }
    }
  }

  public close() {
    this.fileUploader.init();
    this.fileStatus = null;
    this.show = false;
    this.onClose.emit();
  }

  /**
   * 파일 선택
   * @param text
   */
  public fileChange(text: string) {
    if (this.dockerService.validDockerFile(text)) {
      this.dockerFileContent = text;
      this.fileStatus = 'success';
    } else {
      this.fileStatus = 'error';
    }
  }

  /**
   * start build 클릭
   */
  public startBuildClick() {
    this.orgName = this.repositoryService.repository.namespace;
    this.repoName = this.repositoryService.repository.name;

    let build: Build.Entity = new Build.Entity();
    build.dockerfile = this.dockerFileContent;

    this.buildService.build(this.orgName, this.repoName, build).then(result => {
      this.buildService.newBuild.next(result);

      this.close();

      Alert.success(CommonConstant.MESSAGE.SUCCESS);
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      Alert.error(body.message);
    });
  }

}
